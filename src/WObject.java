public abstract class WObject {

    private int x;
    private int y;

    private int dx;
    private int dy;
    // Random random = new Random();

    public WObject() {
    }

    public WObject(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void turnNorth() {
        dx = 0;
        dy = -1;
    }

    public void turnSouth() {
        dx = 0;
        dy = 1;
    }

    public void turnWest() {
        dx = -1;
        dy = 0;
    }

    public void turnEast() {
        dx = 1;
        dy = 0;
    }

    public void move() {
        this.x += dx;
        this.y += dy;
    }

    public void moveStalkerEnermy(int disX, int disY, int tick) {
        if (tick % 3 == 0) {
            if (this.x < disX && this.y < disY) {
                this.x += 1;
                this.y += 1;
            } else if (this.x < disX && this.y > disY) {
                this.x += 1;
                this.y -= 1;
            } else if (this.x > disX && this.y < disY) {
                this.x -= 1;
                this.y += 1;
            } else if (this.x > disX && this.y > disY) {
                this.x -= 1;
                this.y -= 1;
            } else if (this.x == disX && this.y > disY) {
                this.y -= 1;
            } else if (this.x == disX && this.y < disY) {
                this.y += 1;
            } else if (this.x > disX && this.y == disY) {
                this.x -= 1;
            } else if (this.x < disX && this.y == disY) {
                this.x += 1;
            }
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void reset() {
        dx = dy = 0;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean hit(WObject wObj) {
        return x == wObj.x && y == wObj.y;
    }
}
