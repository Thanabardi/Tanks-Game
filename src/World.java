import java.util.Observable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World extends Observable {

    private int tick;
    private int size;

    private List<Bullet> bullets;
    private BulletPool bulletPool;
    private Player player;
    private Thread thread;
    private boolean notOver;
    private int enemyCount = 10;
    private List<Enemy> enemies;
    private List<Enemy> enemiesStart;

    private int bulletdX = 0;
    private int bulletdY = -1;

    public World(int size) {
        this.size = size;
        tick = 0;
        player = new Player(size/2, size/2, 0, 0);
        enemies = new ArrayList<Enemy>();
        enemiesStart = new ArrayList<Enemy>();
        bullets = new ArrayList<Bullet>();
        bulletPool = new BulletPool();
        Random random = new Random();
        for(int i = 0; i < enemyCount; i++) {
            int x = random.nextInt(size);
            int y = random.nextInt(size);
            enemies.add(new Enemy(x, y, 0, 0));
            enemiesStart.add(new Enemy(x, y, 0, 0));
        }
        // enemies[enemies.length] = new Enemy((size/2), (size/2)+2);
    }

    public void start() {
        player.reset();
        player.setPosition(size/2, size/2, 0, 0);
        for(int i = 0; i < enemies.size(); i++) {
            enemies.get(i).setPosition(enemiesStart.get(i).getX(), enemiesStart.get(i).getY(), 0, 0);
        }
        tick = 0;
        notOver = true;
        thread = new Thread() {
            @Override
            public void run() {
                while(notOver) {
                    tick++;
                    if(player.getdX() != 0 || player.getdY() != 0) {
                        bulletdX = player.getdX();
                        bulletdY = player.getdY();
                    }
                    player.move();
                    for(int i = 0; i < enemies.size(); i++) {
                        enemies.get(i).moveStalkerEnermy(player.getX(), player.getY(), tick);
                    }
                    moveBullets();
                    bulletHit();
                    cleanupBullets();
                    reducePoolSize();
                    setChanged();
                    notifyObservers();
                }
            }
        };
        thread.start();
    }

    private void bulletHit() {
        List<Enemy> toRemove = new ArrayList<Enemy>();
        for(Bullet b : bullets) {
            for(Enemy enemy : enemies) {
                if(b.hit(enemy )) {
                    toRemove.add(enemy );
                }
            } 
        }
        for(Enemy enemy : toRemove) {
            enemies.remove(enemy );
        }
    }

    private void moveBullets() {
        for(Bullet bullet : bullets) {
            bullet.moveBullet();
        }
    }

    private void cleanupBullets() {
        List<Bullet> toRemove = new ArrayList<Bullet>();
        for(Bullet bullet : bullets) {
            if(bullet.getX() <= 0 ||
                    bullet.getX() >= size ||
                    bullet.getY() <= 0 ||
                    bullet.getY() >= size) {
                toRemove.add(bullet);
            }
        }
        for(Bullet bullet : toRemove) {
            bullets.remove(bullet);
            bulletPool.releaseBullet(bullet);
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

    public void burstBullets() {
        bullets.add(bulletPool.requestBullet(player.getX(), player.getY(), bulletdX, bulletdY));
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

    public List<Bullet> getBullets() {
        return bullets;
    }

    public boolean isGameOver() {
        return !notOver;
    }
}
