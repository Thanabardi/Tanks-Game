import java.util.Observable;
import java.util.Random;

public class World extends Observable {

    private int tick;
    private int size;

    private Player player;
    private Thread thread;
    private boolean notOver;
    private long delayed = 500;
    private int enemyCount = 10;
    private Enemy [] enemies;
    private Enemy [] enemiesStart;

    public World(int size) {
        this.size = size;
        tick = 0;
        player = new Player(size/2, size/2);
        enemies = new Enemy[enemyCount];
        enemiesStart = new Enemy[enemyCount];
        Random random = new Random();
        for(int i = 0; i < enemies.length; i++) {
            int x = random.nextInt(size);
            int y = random.nextInt(size);
            enemies[i] = new Enemy(x, y);
            enemiesStart[i] = new Enemy(x, y);
        }
        // enemies[enemies.length] = new Enemy((size/2), (size/2)+2);
    }

    public void start() {
        player.reset();
        player.setPosition(size/2, size/2);
        for(int i = 0; i < enemies.length; i++) {
            enemies[i].setPosition(enemiesStart[i].getX(), enemiesStart[i].getY());
        }
        tick = 0;
        notOver = true;
        thread = new Thread() {
            @Override
            public void run() {
                while(notOver) {
                    tick++;
                    player.move();
                    for(int i = 0; i < enemies.length; i++) {
                        enemies[i].moveStalkerEnermy(player.getX(), player.getY(), tick);
                    }
                    checkCollisions();
                    setChanged();
                    notifyObservers();
                    waitFor(delayed);
                }
            }
        };
        thread.start();
    }

    private void checkCollisions() {
        for(Enemy e : enemies) {
            if(e.hit(player)) {
                notOver = false;
            }
        }
    }

    private void waitFor(long delayed) {
        try {
            Thread.sleep(delayed);
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    public Enemy[] getEnemies() {
        return enemies;
    }

    public boolean isGameOver() {
        return !notOver;
    }
}
