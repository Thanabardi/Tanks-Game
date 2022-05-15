import java.util.Observable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World extends Observable {

    private int tick;
    private int size;
    private int enemyCount = 2;
    private int enemyHealth = 2;

    private List<Bullet> bullets1;
    private List<Bullet> bullets2;
    private BulletPool bulletPool;

    private Player player;
    private Thread thread;
    private boolean notOver;
    private List<Enemy> enemies;
    private List<Enemy> enemiesStart;

    private int bulletdX = 0;
    private int bulletdY = -1;

    public World(int size) {
        this.size = size;
        tick = 0;
        player = new Player(size/2, size/2, 0, 0, 4);
        enemies = new ArrayList<Enemy>();
        enemiesStart = new ArrayList<Enemy>();

        bullets1 = new ArrayList<Bullet>();
        bullets2 = new ArrayList<Bullet>();
        bulletPool = new BulletPool();

        createEnemy(enemyCount);
    }
    
    private void createEnemy(int enemyCount) {
        Random random = new Random();
        for(int i = 0; i < enemyCount; i++) {
            int x = random.nextInt(size);
            int y = random.nextInt(size);
            enemies.add(new Enemy(x, y, 0, 0, enemyHealth));
            enemiesStart.add(new Enemy(x, y, 0, 0, enemyHealth));
        }
    }

    public void start() {
        player.reset();
        player.setObject(size/2, size/2, 0, 0, 4);
        for(int i = 0; i < enemies.size(); i++) {
            enemies.get(i).setObject(enemiesStart.get(i).getX(), enemiesStart.get(i).getY(), 0, 0, enemyHealth);
        }
        tick = 0;
        notOver = true;
        thread = new Thread() {
            @Override
            public void run() {
                while(notOver) {
                    tick++;
                    updateBulletDis();
                    player.move();
                    moveEnermy();
                    moveBulletsA(bullets1);
                    pveBulletHit();
                    enemyMoveBulletsA();
                    // bulletHit2();
                    burstEnemyBullets(tick);
                    clearBullets();
                    reducePoolSize();
                    setChanged();
                    notifyObservers();
                }
            }
        };
        thread.start();
    }

    private void moveEnermy() {
        for(int i = 0; i < enemies.size(); i++) {
            enemies.get(i).moveEnermyA(player.getX(), player.getY(), tick, 1);
        }
    }

    private void updateBulletDis() {
        if(player.getdX() != 0 || player.getdY() != 0) {
            bulletdX = player.getdX();
            bulletdY = player.getdY();
        }
    }

    private void pveBulletHit() {
        List<Enemy> toRemoveEnemy = new ArrayList<Enemy>();
        List<Bullet> toRemoveBullet1 = new ArrayList<Bullet>();
        List<Bullet> toRemoveBullet2 = new ArrayList<Bullet>();
        for(Bullet b1 : bullets1) {
            for(Enemy enemy : enemies) {
                if(b1.hit(enemy)) {
                    enemy.updateHP(-b1.getHP());
                    if(enemy.getHP() == 0) {
                        toRemoveEnemy.add(enemy);
                    }
                    toRemoveBullet1.add(b1);
                }
            }
            for(Bullet b2 : bullets2) {
                if(b1.hit(b2)) {
                    b2.updateHP(-b1.getHP());
                    if(b2.getHP() == 0) {
                        toRemoveBullet2.add(b2);
                    }
                }
            }
        }
        for(Bullet b2 : bullets2) {
            if(b2.hit(player)) {
                player.updateHP(-b2.getHP());
                if(player.getHP() == 0) {
                    // isGameOver();
                    System.out.println("you are already dead");
                }
                toRemoveBullet2.add(b2);
            }
        }
        enemies.removeAll(toRemoveEnemy);
        bullets1.removeAll(toRemoveBullet1);
        bullets2.removeAll(toRemoveBullet2);
        if(enemies.size() == 0) {
            enemyCount += 2;
            player.updateHP(2);
            createEnemy(enemyCount);
            System.out.println(player.getHP());
        }
    }

    // private void pvpBulletHit(Player me, Player other, List<Bullet> myBullets, List<Bullet> otherBullets) {
    //     List<Enemy> toRemoveEnemy = new ArrayList<Enemy>();
    //     List<Bullet> toRemoveBullet = new ArrayList<Bullet>();
    //     for(Bullet b : bullets1) {
    //         for(Enemy enemy : enemies) {
    //             if(b.hit(enemy)) {
    //                 enemy.updateHP(-1);
    //                 if(enemy.getHP() == 0) {
    //                     toRemoveEnemy.add(enemy);
    //                 }
    //                 toRemoveBullet.add(b);
    //             }
    //         }
    //         if (b.getX() <= 0 ||
    //             b.getX() >= size ||
    //             b.getY() <= 0 ||
    //             b.getY() >= size) {
    //             toRemoveBullet.add(b);
    //         }
    //     }
    //     for(Enemy enemy : toRemoveEnemy) {
    //         enemies.remove(enemy);
    //     }
    //     for(Bullet bullet : toRemoveBullet) {
    //         bullets1.remove(bullet);
    //         // bulletPool.releaseBullet(bullet);
    //     }
    //     if(enemies.size() == 0) {
    //         enemyCount += 2;
    //         enemyHealth += 1;
    //         createEnemy(enemyCount);
    //     }
    // }

    private void moveBulletsA(List<Bullet> bullets) {
        for(Bullet bullet : bullets) {
            bullet.moveBulletA();
        }
    }

    private void enemyMoveBulletsA() {
        if (tick%5 == 0) {
            for(Bullet bullet : bullets2) {
                bullet.moveBulletA();
            }
        }
    }

    private void clearBullets() {
        List<Bullet> toRemoveBullet1 = new ArrayList<Bullet>();
        List<Bullet> toRemoveBullet2 = new ArrayList<Bullet>();
        for(Bullet b : bullets1) {
            if (b.getX() <= 0 ||
                b.getX() >= size ||
                b.getY() <= 0 ||
                b.getY() >= size) {
                toRemoveBullet1.add(b);
            }
        }
        for(Bullet b : bullets2) {
            if (b.getX() <= 0 ||
                b.getX() >= size ||
                b.getY() <= 0 ||
                b.getY() >= size) {
                toRemoveBullet2.add(b);
            }
        }
        bullets1.removeAll(toRemoveBullet1);
        bullets2.removeAll(toRemoveBullet2);
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

    public void burstEnemyBullets(int tick) {
        if (tick%20 == 0) {
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
}
