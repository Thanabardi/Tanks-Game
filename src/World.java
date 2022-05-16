import java.util.Observable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World extends Observable {
    private int tick;
    private int size;
    private Thread thread;
    private boolean notOver;

    private int stage = 0;
    private int enemyCount;
    private int enemyHealth;
    private int playerHealthPvP;
    private int playerHealthPvE;
    private int enemyNumGain;
    private int playerHpGain;

    private List<Bullet> bullets1;
    private List<Bullet> bullets2;
    private int [] bulletd = {0,-1,0,-1}; // direction of bullet1(0,1) and bullet2(2,3)
    private BulletPool bulletPool;
    
    private Player player1;
    private Player player2;
    private List<Enemy> enemies;

    private List<Grass> grasses;
    private List<Brick> bricks;
    private List<Steel> steels;
    private Flag flag;

    public World(int size) {
        Config config = new Config();

        int[][] treePosition = config.treePosition;
        int[][] brickPosition = config.brickPosition;
        int[][] steelPosition = config.steelPosition;

        enemyCount = config.initEnemyCount;
        enemyHealth = config.initEnemyHealth;
        playerHealthPvP = config.initPlayerHealthPvP;
        playerHealthPvE = config.initPlayerHealthPvE;
        enemyNumGain = config.enemyNumGain;
        playerHpGain = config.playerHpGain;

        this.size = size;
        tick = 0;

        flag = new Flag(12, 24);
        player1 = new Player(size/2, size/2);
        player2 = new Player(size/2, size/2);
        bulletPool = new BulletPool();
        bullets1 = new ArrayList<Bullet>();
        bullets2 = new ArrayList<Bullet>();

        createTrees(treePosition);
        createBricks(brickPosition);
        createSteels(steelPosition);
        createEnemy(enemyCount);
    }

    private void createTrees(int[][] treePosition) {
        grasses = new ArrayList<Grass>();
        for (int i = 0; i < treePosition.length; i++) {
            grasses.add(new Grass(treePosition[i][1], treePosition[i][0]));
        }
    }

    private void createBricks(int[][] brickPosition) {
        bricks = new ArrayList<Brick>();
        for (int i = 0; i < brickPosition.length; i++) {
            bricks.add(new Brick(brickPosition[i][1], brickPosition[i][0]));
        }
    }

    private void createSteels(int[][] steelPosition) {
        steels = new ArrayList<Steel>();
        for (int i = 0; i < steelPosition.length; i++) {
            steels.add(new Steel(steelPosition[i][1], steelPosition[i][0]));
        }
    }
    
    private void createEnemy(int enemyCount) {
        Random random = new Random();
        enemies = new ArrayList<Enemy>();
        int x;
        int y;
        for(int i = 0; i < enemyCount; i++) {
            enemies.add(new Enemy(0, 0));
            while (true) {
                x = random.nextInt(size);
                y = random.nextInt(size);
                if (collideCheck(x, y) && x != player1.getX() && y != player1.getY()){
                    break;
                }
            }
            enemies.get(i).setObject(x, y, 0, 0, enemyHealth);
        }
    }

    public void startpve() { // start pve game mode
        stage++;
        player1.setObject(size/2, size/2, 0, 0, playerHealthPvE);
        player2.setObject(-998, -998, 0, 0, -1);
        tick = 0;
        notOver = true;
        thread = new Thread() {
            @Override
            public void run() {
                while (notOver) {
                    tick++;
                    moveBullets(bullets1, 1); // move player bullets
                    moveBullets(bullets2, 5); // move enemy bullets
                    pveBulletHit();
                    updateBulletDis1();
                    movePlayer(player1);
                    moveEnemy(20, 0);
                    burstEnemyBullets(20);
                    reducePoolSize();
                    setChanged();
                    notifyObservers();
                }
            }
        };
        thread.start();
    }

    public void startpvp() { // start pve game mode
        for(Enemy e : enemies) {
            e.setObject(-997, -997, 0, 0, -1);
        }
        player1.setObject(0, size/2, 0, 0, playerHealthPvP);
        player2.setObject(size-1, size/2, 0, 0, playerHealthPvP);
        tick = 0;
        notOver = true;
        thread = new Thread() {
            @Override
            public void run() {
                while (notOver) {
                    tick++;
                    moveBullets(bullets1, 1); // move player 1 bullets
                    moveBullets(bullets2, 1); // move player 2 bullets
                    pvpBulletHit(player1, player2, bullets1, bullets2); // bullet hit check for player 1
                    pvpBulletHit(player2, player1, bullets2, bullets1); // bullet hit check for player 2
                    reducePoolSize();
                    updateBulletDis1();
                    updateBulletDis2();
                    movePlayer(player1);
                    movePlayer(player2);
                    setChanged();
                    notifyObservers();
                }
            }
        };
        thread.start();
    }

    private void movePlayer(Player p) {
        if (collideCheck(p.getX()+p.getdX(), p.getY()+p.getdY())) {
            p.move();
        }
        p.reset();
    }

    private void moveEnemy(int delayed, int offset) {
        if (tick % delayed == 0) {
            int playerX = player1.getX();
            int playerY = player1.getY();
            for(Enemy enemy : enemies) {
                int enemyX = enemy.getX();
                int enemyY = enemy.getY();
                boolean move = true;
                if (enemyX < playerX-offset && enemyY < playerY-offset) {
                    enemyX += 1;
                    enemyY += 1;
                } else if (enemyX < playerX-offset && enemyY > playerY+offset) {
                    enemyX += 1;
                    enemyY -= 1;
                } else if (enemyX > playerX+offset && enemyY < playerY-offset) {
                    enemyX -= 1;
                    enemyY += 1;
                } else if (enemyX > playerX+offset && enemyY > playerY+offset) {
                    enemyX -= 1;
                    enemyY -= 1;
                } else if (enemyX == playerX && enemyY > playerY+offset) {
                    enemyY -= 1;
                } else if (enemyX == playerX && enemyY < playerY-offset) {
                    enemyY += 1;
                } else if (enemyX > playerX+offset && enemyY == playerY) {
                    enemyX -= 1;
                } else if (enemyX < playerX-offset && enemyY == playerY) {
                    enemyX += 1;
                }
                if (collideCheck(enemyX, enemyY)) { // collide brick steel and map border
                    if (enemyX != player1.getX()|| enemyY != player1.getY()) { // collide player
                        for(Enemy e : enemies) { // collide other enemy
                            if (enemyX == e.getX() && enemyY == e.getY()) {
                                move = !move;
                                break;
                            }
                        }
                        if (move) {enemy.moveEnemy(enemyX, enemyY);}
                    }
                }
            }
        }
    }

    private boolean collideCheck(int x, int y) {
        if (x <= -1 || x >= size || y <= -1 || y >= size) {
            return false;
        }
        for(Brick brick : bricks) {
            if (x == brick.getX() && y == brick.getY()) {
                return false;
            }
        }
        for(Steel steel : steels) {
            if (x == steel.getX() && y == steel.getY()) {
                return false;
            }
        }
        return true;
    }

    private void updateBulletDis1() { // set bullet1 direction
        if(player1.getdX() != 0 || player1.getdY() != 0) {
            bulletd[0] = player1.getdX();
            bulletd[1] = player1.getdY();
        }
    }

    private void updateBulletDis2() { // set bullet2 direction
        if(player2.getdX() != 0 || player2.getdY() != 0) {
            bulletd[2] = player2.getdX();
            bulletd[3] = player2.getdY();
        }
    }

    private void pveBulletHit() {
        List<Enemy> toRemoveEnemy = new ArrayList<Enemy>();
        List<Brick> toRemoveBrick = new ArrayList<Brick>();
        List<Bullet> toRemoveBullet1 = new ArrayList<Bullet>();
        List<Bullet> toRemoveBullet2 = new ArrayList<Bullet>();
        // player bullets
        for(Bullet b1 : bullets1) {
            for(Enemy enemy : enemies) { // hit enemy
                if(b1.hit(enemy)) {
                    enemy.updateHP(-b1.getHP());
                    if(enemy.getHP() == 0) {
                        toRemoveEnemy.add(enemy);
                    }
                    toRemoveBullet1.add(b1);
                    break;
                }
            }
            for(Bullet b2 : bullets2) { // hit enemy bullet
                if(b1.hit(b2)) {
                    b2.updateHP(-b1.getHP());
                    if(b2.getHP() == 0) {
                        toRemoveBullet2.add(b2);
                    }
                    toRemoveBullet1.add(b1);
                    break;
                }
            }
            for(Brick brick : bricks) { // hit brick
                if(b1.hit(brick)) {
                    toRemoveBrick.add(brick);
                    break;
                }
            }
            if(!collideCheck(b1.getX(), b1.getY())) { // hit brick steel and map border
                toRemoveBullet1.add(b1);
                break;
            }
        }
        // enemy bullets
        for(Bullet b2 : bullets2) {
            if(b2.hit(player1)) { // hit player
                player1.updateHP(-b2.getHP());
                if(player1.getHP() == 0) {
                    notOver = false;
                }
                toRemoveBullet2.add(b2);
                break;
            }
            if(!collideCheck(b2.getX(), b2.getY())) { // hit brick steel and map border
                toRemoveBullet2.add(b2);
                break;
            }
        }
        enemies.removeAll(toRemoveEnemy);
        bricks.removeAll(toRemoveBrick);
        bullets1.removeAll(toRemoveBullet1);
        bullets2.removeAll(toRemoveBullet2);
        // next stage
        if(enemies.size() == 0) {
            enemyCount += enemyNumGain;
            player1.updateHP(playerHpGain);
            createEnemy(enemyCount);
            stage++;
        }
    }

    private void pvpBulletHit(Player me, Player other, List<Bullet> myBullets, List<Bullet> otherBullets) {
        List<Brick> toRemoveBrick = new ArrayList<Brick>();
        List<Bullet> toRemoveBullet1 = new ArrayList<Bullet>();
        List<Bullet> toRemoveBullet2 = new ArrayList<Bullet>();
        // mybullets
        for(Bullet b1 : myBullets) {
            if(b1.hit(other)) { // hit other player
                other.updateHP(-b1.getHP());
                if(other.getHP() == 0) {
                    notOver = false;
                }
                toRemoveBullet1.add(b1);
                break;
            }
            for(Bullet b2 : otherBullets) { // hit other bullet
                if(b1.hit(b2)) {
                    b2.updateHP(-b1.getHP());
                    if(b2.getHP() == 0) {
                        toRemoveBullet2.add(b2);
                    }
                    toRemoveBullet1.add(b1);
                    break;
                }
            }
            for(Brick brick : bricks) { // hit brick
                if(b1.hit(brick)) {
                    toRemoveBrick.add(brick);
                    break;
                }
            }
            if(!collideCheck(b1.getX(), b1.getY())) { // hit brick steel and map border
                toRemoveBullet1.add(b1);
                break;
            }
        }
        bricks.removeAll(toRemoveBrick);
        myBullets.removeAll(toRemoveBullet1);
        otherBullets.removeAll(toRemoveBullet2);
    }

    private void moveBullets(List<Bullet> bullets, int delayed) {
        if (tick % delayed == 0) {
            for(Bullet bullet : bullets) {
                bullet.move();
            }
        }
    }

    private void reducePoolSize() {
        while (bulletPool.bullets.size() > 30) {
            if (System.currentTimeMillis() - bulletPool.getTime() >= 30000) {
                bulletPool.bullets.remove(0);
            }
            else { break; }
        }
    }

    public void burstPlayerBullets1(int delayed) {
        if (tick % delayed == 0) {
            bullets1.add(bulletPool.requestBullet(player1.getX(), player1.getY(), bulletd[0], bulletd[1], 1));
        }
    }

    public void burstPlayerBullets2(int delayed) {
        if (tick % delayed == 0) {
            bullets2.add(bulletPool.requestBullet(player2.getX(), player2.getY(), bulletd[2], bulletd[3], 1));
        }
    }

    private void burstEnemyBullets(int delayed) {
        if (tick % delayed == 0) {
            int playerX = player1.getX();
            int playerY = player1.getY();
            for(Enemy e : enemies) {
                int enemyX = e.getX();
                int enemyY = e.getY();
                int dx = 0;
                int dy = 0;
                if (enemyX < playerX && enemyY < playerY) {
                    dx = 1;
                    dy = 1;
                } else if (enemyX < playerX && enemyY > playerY) {
                    dx = 1;
                    dy = -1;
                } else if (enemyX > playerX && enemyY < playerY) {
                    dx = -1;
                    dy = 1;
                } else if (enemyX > playerX && enemyY > playerY) {
                    dx = -1;
                    dy = -1;
                } else if (enemyX == playerX && enemyY > playerY) {
                    dy = -1;
                } else if (enemyX == playerX && enemyY < playerY) {
                    dy = 1;
                } else if (enemyX > playerX && enemyY == playerY) {
                    dx = -1;
                } else if (enemyX < playerX && enemyY == playerY) {
                    dx = 1;
                }
                bullets2.add(bulletPool.requestBullet(e.getX(), e.getY(), dx, dy, 1));
            }
        }
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public List<Bullet> getBullets1() {
        return bullets1;
    }

    public List<Bullet> getBullets2() {
        return bullets2;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public int getTick() {
        return tick;
    }

    public int getSize() {
        return size;
    }

    public int getStage() {
        return stage;
    }

    public boolean isGameOver() {
        return !notOver;
    }

    public List<Grass> getGrasses() {
        return grasses;
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public List<Steel> getSteels() {
        return steels;
    }

    public Flag getFlag() {
        return flag;
    }
}
