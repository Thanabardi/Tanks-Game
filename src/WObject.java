public abstract class WObject {

    private int x;
    private int y;

    private int dx;
    private int dy;
    // Random random = new Random();

    public WObject() {
    }

    public WObject(int x, int y, int dx, int dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
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
        dx = 0;
        dy = 0;
    }

    public void moveBullet() {
        this.x += this.dx;
        this.y += this.dy;
    }

    public void moveEnermyA(int disX, int disY, int tick) {
        if (tick % 4 == 0) {
            if (this.x < disX-5 && this.y < disY-5) {
                this.x += 1;
                this.y += 1;
            } else if (this.x < disX-5 && this.y > disY+5) {
                this.x += 1;
                this.y -= 1;
            } else if (this.x > disX+5 && this.y < disY-5) {
                this.x -= 1;
                this.y += 1;
            } else if (this.x > disX+5 && this.y > disY+5) {
                this.x -= 1;
                this.y -= 1;
            } else if (this.x == disX && this.y > disY+5) {
                this.y -= 1;
            } else if (this.x == disX && this.y < disY-5) {
                this.y += 1;
            } else if (this.x > disX+5 && this.y == disY) {
                this.x -= 1;
            } else if (this.x < disX-5 && this.y == disY) {
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

    public int getdX() {
        return dx;
    }

    public int getdY() {
        return dy;
    }

    public void reset() {
        dx = dy = 0;
    }

    public void setPosition(int x, int y, int dx, int dy) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public boolean hit(WObject wObj) {
        return x == wObj.x && y == wObj.y;
    }
}
