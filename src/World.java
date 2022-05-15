import java.util.Observable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World extends Observable {

    private int tick;
    private int size;
    private int enemyCount = 4;
    private int enemyHealth = 2;

    private List<Bullet> bullets1;
    private List<Bullet> bullets2;
    private BulletPool bulletPool;

    private int[][] treePosition = {
            { 2, 2 }, { 2, 3 }, { 2, 4 }, { 2, 5 },
            { 3, 2 }, { 3, 3 }, { 3, 4 }, { 3, 5 },
            { 4, 0 }, { 4, 1 }, { 4, 2 }, { 4, 3 }, { 4, 4 }, { 4, 5 }, { 4, 6 }, { 4, 7 }, { 4, 8 }, { 4, 9 }, { 4, 10 }, { 4, 11 }, { 4, 12 }, { 4, 13 }, { 4, 14 }, { 4, 15 },
            { 5, 0 }, { 5, 1 }, { 5, 2 }, { 5, 3 }, { 5, 4 }, { 5, 5 }, { 5, 6 }, { 5, 7 }, { 5, 8 }, { 5, 9 }, { 5, 10 }, { 5, 11 }, { 5, 12 }, { 5, 13 }, { 5, 14 }, { 5, 15 },
            { 6, 0 }, { 6, 1 }, { 6, 6 }, { 6, 7 }, { 6, 14 }, { 6, 15 }, { 6, 16 }, { 6, 17 }, { 6, 18 }, { 6, 19 }, { 6, 20 }, { 6, 21 },
            { 7, 0 }, { 7, 1 }, { 7, 6 }, { 7, 7 }, { 7, 14 }, { 7, 15 }, { 7, 16 }, { 7, 17 }, { 7, 18 }, { 7, 19 }, { 7, 20 }, { 7, 21 },
            { 8, 0 }, { 8, 1 }, { 8, 2 }, { 8, 3 }, { 8, 6 }, { 8, 7 }, { 8, 8 }, { 8, 9 }, { 8, 10 }, { 8, 11 }, { 8, 14 }, { 8, 15 }, { 8, 16 }, { 8, 17 },
            { 9, 0 }, { 9, 1 }, { 9, 2 }, { 9, 3 }, { 9, 6 }, { 9, 7 }, { 9, 8 }, { 9, 9 }, { 9, 10 }, { 9, 11 }, { 9, 14 }, { 9, 15 }, { 9, 16 }, { 9, 17 },
            { 10, 2 }, { 10, 3 }, { 10, 4 }, { 10, 5 }, { 10, 10 }, { 10, 11 }, { 10, 12 }, { 10, 13 }, { 10, 14 }, { 10, 15 }, { 10, 16 }, { 10, 17 },
            { 11, 2 }, { 11, 3 }, { 11, 4 }, { 11, 5 }, { 11, 10 }, { 11, 11 }, { 11, 12 }, { 11, 13 }, { 11, 14 }, { 11, 15 }, { 11, 16 }, { 11, 17 },
            { 12, 12 }, { 12, 13 }, { 12, 14 }, { 12, 15 }, { 12, 22 }, { 12, 23 }, { 12, 24 }, { 12, 25 },
            { 13, 12 }, { 13, 13 }, { 13, 14 }, { 13, 15 }, { 13, 22 }, { 13, 23 }, { 13, 24 }, { 13, 25 },
            { 14, 24 }, { 14, 25 },
            { 15, 24 }, { 15, 25 },
            { 16, 16 }, { 16, 17 }, { 16, 18 }, { 16, 19 }, { 16, 24 }, { 16, 25 },
            { 17, 16 }, { 17, 17 }, { 17, 18 }, { 17, 19 }, { 17, 24 }, { 17, 25 },
            { 18, 14 }, { 18, 15 }, { 18, 16 }, { 18, 17 }, { 18, 24 }, { 18, 25 },
            { 19, 14 }, { 19, 15 }, { 19, 16 }, { 19, 17 }, { 19, 24 }, { 19, 25 },
            { 20, 12 }, { 20, 13 }, { 20, 14 }, { 20, 15 }, { 20, 18 }, { 20, 19 }, { 20, 22 }, { 20, 23 }, { 20, 24 }, { 20, 25 },
            { 21, 12 }, { 21, 13 }, { 21, 14 }, { 21, 15 }, { 21, 18 }, { 21, 19 }, { 21, 22 }, { 21, 23 }, { 21, 24 }, { 21, 25 },
            { 22, 8 }, { 22, 9 }, { 22, 18 }, { 22, 19 }, { 22, 22 }, { 22, 23 },
            { 23, 8 }, { 23, 9 }, { 23, 18 }, { 23, 19 }, { 23, 22 }, { 23, 23 },
            { 24, 18 }, { 24, 19 }, { 24, 20 }, { 24, 21 }, { 24, 22 }, { 24, 23 },
            { 25, 18 }, { 25, 19 }, { 25, 20 }, { 25, 21 }, { 25, 22 }, { 25, 23 }
    };
    private int[][] brickPosition = {
            { 0, 8 }, { 0, 9 }, { 0, 10 }, { 0, 11 }, { 0, 16 }, { 0, 17 },
            { 1, 8 }, { 1, 9 }, { 1, 10 }, { 1, 11 }, { 1, 16 }, { 1, 17 },
            { 2, 6 }, { 2, 7 }, { 2, 8 }, { 2, 9 }, { 2, 16 }, { 2, 17 },
            { 3, 6 }, { 3, 7 }, { 3, 8 }, { 3, 9 }, { 3, 16 }, { 3, 17 },
            { 4, 16 }, { 4, 17 }, { 4, 18 }, { 4, 19 },
            { 5, 16 }, { 5, 17 }, { 5, 18 }, { 5, 19 },
            { 6, 4 }, { 6, 5 }, { 6, 8 }, { 6, 9 }, { 6, 10 }, { 6, 11 }, { 6, 22 }, { 6, 23 },
            { 7, 4 }, { 7, 5 }, { 7, 8 }, { 7, 9 }, { 7, 10 }, { 7, 11 }, { 7, 12 }, { 7, 13 }, { 7, 22 }, { 7, 23 },
            { 8, 4 }, { 8, 5 }, { 8, 18 }, { 8, 19 }, { 8, 22 }, { 8, 23 },
            { 9, 4 }, { 9, 5 }, { 9, 18 }, { 9, 19 }, { 9, 22 }, { 9, 23 },
            { 10, 6 }, { 10, 7 }, { 10, 18 }, { 10, 19 }, { 10, 22 }, { 10, 23 },
            { 11, 6 }, { 11, 7 }, { 11, 18 }, { 11, 19 }, { 11, 22 }, { 11, 23 },
            { 12, 2 }, { 12, 3 }, { 12, 4 }, { 12, 5 }, { 12, 6 }, { 12, 7 }, { 12, 8 }, { 12, 9 }, { 12, 10 }, { 12, 11 }, { 12, 16 }, { 12, 17 }, { 12, 18 }, { 12, 19 }, { 12, 20 },
            { 13, 2 }, { 13, 3 }, { 13, 4 }, { 13, 5 }, { 13, 6 }, { 13, 7 }, { 13, 8 }, { 13, 9 }, { 13, 10 }, { 13, 11 }, { 13, 16 }, { 13, 17 }, { 13, 18 }, { 13, 19 }, { 13, 20 },
            { 14, 14 }, { 14, 15 }, { 14, 16 }, { 14, 17 },
            { 15, 14 }, { 15, 15 },
            { 16, 6 }, { 16, 14 }, { 16, 15 }, { 16, 20 }, { 16, 21 }, { 16, 22 },
            { 17, 6 }, { 17, 12 }, { 17, 13 }, { 17, 20 }, { 17, 21 }, { 17, 22 },
            { 18, 2 }, { 18, 3 }, { 18, 9 }, { 18, 10 }, { 18, 11 }, { 18, 12 }, { 18, 13 }, { 18, 18 }, { 18, 19 },
            { 19, 2 }, { 19, 3 }, { 19, 9 }, { 19, 10 }, { 19, 11 }, { 19, 18 }, { 19, 19 },
            { 20, 2 }, { 20, 3 }, { 20, 4 }, { 20, 5 }, { 20, 6 }, { 20, 9 }, { 20, 10 }, { 20, 11 }, { 20, 20 }, { 20, 21 },
            { 21, 2 }, { 21, 3 }, { 21, 4 }, { 21, 5 }, { 21, 6 }, { 21, 9 }, { 21, 16 }, { 21, 17 }, { 21, 20 }, { 21, 21 },
            { 22, 4 }, { 22, 5 }, { 22, 16 }, { 22, 17 }, { 22, 20 }, { 22, 21 },
            { 23, 4 }, { 23, 5 }, { 23, 11 }, { 23, 12 }, { 23, 13 }, { 23, 14 }, { 23, 16 }, { 23, 17 },
            { 24, 4 }, { 24, 5 }, { 24, 11 }, { 24, 14 },
            { 25, 11 }, { 25, 14 }
    };
    private int[][] steelPosition = {
            { 6, 2 }, { 6, 3 }, { 6, 24 }, { 6, 25 },
            { 7, 2 }, { 7, 24 }, { 7, 25 },
            { 8, 12 }, { 8, 13 }, { 8, 20 },
            { 9, 20 },
            { 11, 8 }, { 11, 9 },
            { 14, 1 }, { 14, 2 }, { 14, 3 },
            { 15, 1 },
            { 17, 10 }, { 17, 11 }
    };
    private Player player;
    private Thread thread;
    private boolean notOver;
    private List<Enemy> enemies;

    private int bulletdX = 0;
    private int bulletdY = -1;

    private List<Grass> grasses;
    private List<Brick> bricks;
    private List<Steel> steels;
    private Flag flag;

    public World(int size) {
        this.size = size;
        tick = 0;
        flag = new Flag(12, 24, 0, 0, 1);
        
        player = new Player(size/2, size/2, 0, 0, 4);
        enemies = new ArrayList<Enemy>();

        bullets1 = new ArrayList<Bullet>();
        bullets2 = new ArrayList<Bullet>();
        bulletPool = new BulletPool();

        initTrees();
        initBricks();
        initSteels();
        createEnemy(enemyCount);
    }

    public void initTrees() {
        grasses = new ArrayList<Grass>();
        for (int i = 0; i < treePosition.length; i++) {
            grasses.add(new Grass(treePosition[i][1], treePosition[i][0], 0, 0, 1));
        }
    }

    public void initBricks() {
        bricks = new ArrayList<Brick>();
        for (int i = 0; i < brickPosition.length; i++) {
            bricks.add(new Brick(brickPosition[i][1], brickPosition[i][0], 0, 0, 1));
        }
    }

    public void initSteels() {
        steels = new ArrayList<Steel>();
        for (int i = 0; i < steelPosition.length; i++) {
            steels.add(new Steel(steelPosition[i][1], steelPosition[i][0], 0, 0, 1));
        }
    }
    
    private void createEnemy(int enemyCount) {
        Random random = new Random();
        int x;
        int y;
        for(int i = 0; i < enemyCount; i++) {
            while (true) {
                x = random.nextInt(size);
                y = random.nextInt(size);
                if (collideCheck(x, y)){
                    break;
                }
            }
            enemies.add(new Enemy(x, y, 0, 0, enemyHealth));
        }
    }

    public void startpve() {
        player.reset();
        player.setObject(size/2, size/2, 0, 0, 4);
        flag.setObject(12, 24, 0, 0, 1);
        for (int j = 0; j < grasses.size(); j++) {
            grasses.get(j).setObject(grasses.get(j).getX(), grasses.get(j).getY(), 0, 0, 1);
        }
        for (int k = 0; k < bricks.size(); k++) {
            bricks.get(k).setObject(bricks.get(k).getX(), bricks.get(k).getY(), 0, 0, 1);
        }
        for (int l = 0; l < steels.size(); l++) {
            steels.get(l).setObject(steels.get(l).getX(), steels.get(l).getY(), 0, 0, 1);
        }
        tick = 0;
        notOver = true;
        thread = new Thread() {
            @Override
            public void run() {
                while (notOver) {
                    tick++;
                    updateBulletDis();
                    movePlayer(player);
                    moveEnemy(20, 0);
                    moveBullets(bullets1);
                    pveBulletHit();
                    enemyMoveBullets(5);
                    // bulletHit2();
                    burstEnemyBullets(20);
                    reducePoolSize();
                    setChanged();
                    notifyObservers();
                }
            }
        };
        thread.start();
    }

    public void startpvp() {
        player.reset();
        player.setObject(size/2, size/2, 0, 0, 4);
        flag.setObject(12, 24, 0, 0, 1);
        for (int j = 0; j < grasses.size(); j++) {
            grasses.get(j).setObject(grasses.get(j).getX(), grasses.get(j).getY(), 0, 0, 1);
        }
        for (int k = 0; k < bricks.size(); k++) {
            bricks.get(k).setObject(bricks.get(k).getX(), bricks.get(k).getY(), 0, 0, 1);
        }
        for (int l = 0; l < steels.size(); l++) {
            steels.get(l).setObject(steels.get(l).getX(), steels.get(l).getY(), 0, 0, 1);
        }
        tick = 0;
        notOver = true;
        thread = new Thread() {
            @Override
            public void run() {
                while (notOver) {
                    tick++;
                    updateBulletDis();
                    movePlayer(player);
                    moveEnemy(20, 0);
                    moveBullets(bullets1);
                    pveBulletHit();
                    enemyMoveBullets(5);
                    // bulletHit2();
                    burstEnemyBullets(20);
                    reducePoolSize();
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
        } else {
            p.resetDis();
        }
    }

    private void moveEnemy(int delayed, int offset) {
        if (tick % delayed == 0) {
            int playerX = player.getX();
            int playerY = player.getY();
            for(Enemy enemy : enemies) {
                int enemyX = enemy.getX();
                int enemyY = enemy.getY();
                int newdX = 0;
                int newdY = 0;
                if (enemyX < playerX-offset && enemyY < playerY-offset) {
                    newdX = 1;
                    newdY = 1;
                } else if (enemyX < playerX-offset && enemyY > playerY+offset) {
                    newdX = 1;
                    newdY = -1;
                } else if (enemyX > playerX+offset && enemyY < playerY-offset) {
                    newdX = -1;
                    newdY = 1;
                } else if (enemyX > playerX+offset && enemyY > playerY+offset) {
                    newdX = -1;
                    newdY = -1;
                } else if (enemyX == playerX && enemyY > playerY+offset) {
                    newdY = -1;
                } else if (enemyX == playerX && enemyY < playerY-offset) {
                    newdY = 1;
                } else if (enemyX > playerX+offset && enemyY == playerY) {
                    newdX = -1;
                } else if (enemyX < playerX-offset && enemyY == playerY) {
                    newdX = 1;
                }
                if (collideCheck(enemy.getX()+newdX, enemy.getY()+newdY)) {
                    enemy.moveEnemy(newdX, newdY);
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

    private void updateBulletDis() {
        if(player.getdX() != 0 || player.getdY() != 0) {
            bulletdX = player.getdX();
            bulletdY = player.getdY();
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
                    brick.updateHP(-b1.getHP());
                    if(brick.getHP() == 0) {
                        toRemoveBrick.add(brick);
                    }
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
            if(b2.hit(player)) { // hit player
                player.updateHP(-b2.getHP());
                if(player.getHP() == 0) {
                    // isGameOver();
                    System.out.println("you are already dead");
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
            enemyCount += 2;
            player.updateHP(2);
            createEnemy(enemyCount);
            System.out.println(player.getHP());
        }
    }

    // private void pvpBulletHit(Player me, Player other, List<Bullet> myBullets, List<Bullet> otherBullets) {

    // }

    private void moveBullets(List<Bullet> bullets) {
        for(Bullet bullet : bullets) {
            bullet.moveBulletA();
        }
    }

    private void enemyMoveBullets(int delayed) {
        if (tick % delayed == 0) {
            for(Bullet bullet : bullets2) {
                bullet.moveBulletA();
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

    public void burstPlayerBullets1() {
        bullets1.add(bulletPool.requestBullet(player.getX(), player.getY(), bulletdX, bulletdY, 1));
    }

    public void burstPlayerBullets2() {
        bullets2.add(bulletPool.requestBullet(player.getX(), player.getY(), bulletdX, bulletdY, 1));
    }

    public void burstEnemyBullets(int delayed) {
        if (tick % delayed == 0) {
            int playerX = player.getX();
            int playerY = player.getY();
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

    public int getTick() {
        return tick;
    }

    public int getSize() {
        return size;
    }

    public Player getPlayer() {
        return player;
    }

    public void turnPlayerNorth() {
        player.turnNorth();
    }

    public void turnPlayerSouth() {
        player.turnSouth();
    }

    public void turnPlayerWest() {
        player.turnWest();
    }

    public void turnPlayerEast() {
        player.turnEast();
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Bullet> getBullets1() {
        return bullets1;
    }

    public List<Bullet> getBullets2() {
        return bullets2;
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
