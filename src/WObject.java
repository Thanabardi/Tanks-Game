public abstract class WObject {

    private int x;
    private int y;

    private int dx;
    private int dy;

    private int hp;
    // Random random = new Random();

    public WObject() {
    }

    public WObject(int x, int y, int dx, int dy, int hp) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.hp = hp;
    }

    public void turnNorth() {
        dy = -1;
    }

    public void turnSouth() {
        dy = 1;
    }

    public void turnWest() {
        dx = -1;
    }

    public void turnEast() {
        dx = 1;
    }

    public void move() {
        this.x += dx;
        this.y += dy;
        resetDis();
    }

    public void resetDis() {
        this.dx = 0;
        this.dy = 0;
    }

    public void moveBulletA() {
        this.x += this.dx;
        this.y += this.dy;
    }

    public void moveEnemy(int disX, int disY) {
        this.x += disX;
        this.y += disY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getdX() {
        return dx;
    }

    public int getdY() {
        return dy;
    }

    public int getHP() {
        return hp;
    }

    public void reset() {
        dx = dy = 0;
    }

    public void setObject(int x, int y, int dx, int dy, int hp) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.hp = hp;
    }

    public boolean hit(WObject wObj) {
        return x == wObj.x && y == wObj.y;
    }

    public void updateHP(int value) {
        this.hp += value;
    }
}
