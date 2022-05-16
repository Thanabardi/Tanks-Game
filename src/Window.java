import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
// import java.awt.event.MouseAdapter;
// import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Window extends JFrame implements Observer {

    private int size = 800;
    private long delayed = 100; // game update delay
    private World world;
    private Renderer renderer;
    private Gui gui;
    private int worldSize = 26;

    private List<Integer> keyCode = new ArrayList<Integer>();
    private List<Integer> keyList = List.of(KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE,
                                            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);

    public Window() {
        super();
        addKeyListener(new KeyController());
        setLayout(new BorderLayout());
        renderer = new Renderer();
        add(renderer, BorderLayout.CENTER);
        gui = new Gui();
        add(gui, BorderLayout.SOUTH);
        world = new World(worldSize);
        world.addObserver(this);
        setSize(size-4, size + 55);
        setResizable(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void update(Observable o, Object arg) {
        renderer.repaint();
        moveCommand();

        if(world.isGameOver()) {
            JOptionPane.showMessageDialog(Window.this,
            "Replay?",
            "Game Over",
            JOptionPane.INFORMATION_MESSAGE);
            world = new World(worldSize);
            world.addObserver(this);
            addKeyListener(new KeyController());
            gui.pveButton.setEnabled(true);
            gui.pvpButton.setEnabled(true);
            repaint();

        }
        waitFor(delayed);
    }

    private void waitFor(long delayed) {
        try {
            Thread.sleep(delayed);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class Renderer extends JPanel {
        public final Image imageGrass;
        public final Image imageBrick;
        public final Image imageSteel;
        public final Image imageFlag;
        public final Image imageTank1Up;
        public final Image imageTank1Down;
        public final Image imageTank1Right;
        public final Image imageTank1Left;
        public final Image imageTank2Up;
        public final Image imageTank2Down;
        public final Image imageTank2Right;
        public final Image imageTank2Left;
        public final Image imageEnemy;

        public Renderer() {
            setDoubleBuffered(true);

            imageGrass = new ImageIcon("imgs/grass.png").getImage();
            imageBrick = new ImageIcon("imgs/brick.png").getImage();
            imageSteel = new ImageIcon("imgs/steel.jpg").getImage();
            imageFlag = new ImageIcon("imgs/flag.png").getImage();
            imageTank1Up = new ImageIcon("imgs/tank-up.png").getImage();
            imageTank1Down = new ImageIcon("imgs/tank-down.png").getImage();
            imageTank1Right = new ImageIcon("imgs/tank-right.png").getImage();
            imageTank1Left = new ImageIcon("imgs/tank-left.png").getImage();
            imageTank2Up = new ImageIcon("imgs/tank2-up.png").getImage();
            imageTank2Down = new ImageIcon("imgs/tank2-down.png").getImage();
            imageTank2Right = new ImageIcon("imgs/tank2-right.png").getImage();
            imageTank2Left = new ImageIcon("imgs/tank2-left.png").getImage();
            imageEnemy = new ImageIcon("imgs/enemy.png").getImage();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            paintGrids(g);
            paintFlag(g);
            paintBullets1(g);
            paintBullets2(g);
            paintBricks(g);
            paintSteels(g);
            paintEnemies(g);
            paintPlayer2(g);
            paintPlayer1(g);
            paintGrass(g);
            paintPlayerHP(g);
            paintStage(g);
        }

        private void paintGrids(Graphics g) {
            g.setColor(Color.black);
            g.fillRect(0, 0, size, size);
        }

        private void paintGrass(Graphics g) {
            int perCell = size / world.getSize();
            for (Grass grass : world.getGrasses()) {
                int x = grass.getX();
                int y = grass.getY();
                g.drawImage(imageGrass, x * perCell, y * perCell, perCell, perCell, null, null);
            }
        }

        private void paintBricks(Graphics g) {
            int perCell = size / world.getSize();
            for (Brick b : world.getBricks()) {
                int x = b.getX();
                int y = b.getY();
                g.drawImage(imageBrick, x * perCell, y * perCell, perCell, perCell, null, null);
            }
        }

        private void paintSteels(Graphics g) {
            int perCell = size / world.getSize();
            for (Steel s : world.getSteels()) {
                int x = s.getX();
                int y = s.getY();
                g.drawImage(imageSteel, x * perCell, y * perCell, perCell, perCell, null, null);
            }
        }

        private void paintFlag(Graphics g) {
            int perCell = size / world.getSize();
            int x = world.getFlag().getX();
            int y = world.getFlag().getY();
            g.drawImage(imageFlag, x * perCell, y * perCell, perCell * 2, perCell * 2, null, null);
        }

        private void paintPlayer1(Graphics g) {
            int perCell = size/world.getSize();
            int x = world.getPlayer1().getX();
            int y = world.getPlayer1().getY();
            switch (world.getPlayer1().getDirection()) {
                case "NORTH":
                    g.drawImage(imageTank1Up, x * perCell, y * perCell, perCell, perCell, null, null);
                    break;
                case "SOUTH":
                    g.drawImage(imageTank1Down, x * perCell, y * perCell, perCell , perCell, null, null);
                    break;
                case "EAST":
                    g.drawImage(imageTank1Right, x * perCell, y * perCell, perCell , perCell, null, null);
                    break;
                case "WEST":
                    g.drawImage(imageTank1Left, x * perCell, y * perCell, perCell , perCell, null, null);
                    break;
            }
        }

        private void paintPlayer2(Graphics g) {
            int perCell = size/world.getSize();
            int x = world.getPlayer2().getX();
            int y = world.getPlayer2().getY();
            switch (world.getPlayer2().getDirection()) {
                case "NORTH":
                    g.drawImage(imageTank2Up, x * perCell, y * perCell, perCell, perCell, null, null);
                    break;
                case "SOUTH":
                    g.drawImage(imageTank2Down, x * perCell, y * perCell, perCell , perCell, null, null);
                    break;
                case "EAST":
                    g.drawImage(imageTank2Right, x * perCell, y * perCell, perCell , perCell, null, null);
                    break;
                case "WEST":
                    g.drawImage(imageTank2Left, x * perCell, y * perCell, perCell , perCell, null, null);
                    break;
            }
        }

        private void paintEnemies(Graphics g) {
            int perCell = size/world.getSize();
            for(Enemy e : world.getEnemies()) {
                int x = e.getX();
                int y = e.getY();
                g.drawImage(imageEnemy, x * perCell, y * perCell, perCell, perCell, null, null);
            }
        }

        private void paintBullets1(Graphics g) {
            int perCell = size/world.getSize();
            g.setColor(Color.orange);
            for(Bullet b : world.getBullets1()) {
                int x = b.getX();
                int y = b.getY();
                g.fillOval(x * perCell+10,y * perCell+10,perCell-20, perCell-20);
            }
        }

        private void paintBullets2(Graphics g) {
            int perCell = size/world.getSize();
            g.setColor(Color.red);
            for(Bullet b : world.getBullets2()) {
                int x = b.getX();
                int y = b.getY();
                g.fillOval(x * perCell+10,y * perCell+10,perCell-20, perCell-20);
            }
        }

        private void paintStage(Graphics g) {
            g.setColor(Color.white);
            if(world.getStage()>0){g.drawString("Stage " + world.getStage(), size-80, 20);}
        }

        private void paintPlayerHP(Graphics g) {
            int player1HP = world.getPlayer1().getHP();
            int player2HP = world.getPlayer2().getHP();
            g.setColor(Color.white);
            if (player1HP>=0) {g.drawString("Player 1 HP: " + player1HP , 10, 20);}
            if (player2HP>=0) {g.drawString("Player 2 HP: " + player2HP , 10, 40);}
        }
    }

    class Gui extends JPanel {

        private JButton pveButton;
        private JButton pvpButton;

        public Gui() {
            setLayout(new FlowLayout());
            pvpButton = new JButton("PvP");
            pvpButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    world.startpvp();
                    pveButton.setEnabled(false);
                    pvpButton.setEnabled(false);
                    Window.this.requestFocus();
                }
            });
            add(pvpButton);
            pveButton = new JButton("Survival");
            pveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    world.startpve();
                    pveButton.setEnabled(false);
                    pvpButton.setEnabled(false);
                    Window.this.requestFocus();
                }
            });
            add(pveButton);
        }
    }

    class KeyController extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (keyList.contains(e.getKeyCode()) && !keyCode.contains(e.getKeyCode())) {
                keyCode.add(e.getKeyCode());
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            List<Integer> toRemoveKey = new ArrayList<Integer>();
            if(keyCode.contains(e.getKeyCode())) {
                toRemoveKey.add(e.getKeyCode());
            }
            keyCode.removeAll(toRemoveKey);
        }
    }

    // class MouseController extends MouseAdapter {
    //     @Override
    //     public void mousePressed(MouseEvent e) {
    //         System.out.println(e.getX() + " " +e.getY());
    //     }
    // }

    private void moveCommand() {
        for(Integer key : keyCode) {
            if(key == KeyEvent.VK_W) {
                Command c = new CommandTurnNorth(world.getPlayer1(), world.getTick());
                c.execute();
            } else if(key == KeyEvent.VK_S) {
                Command c = new CommandTurnSouth(world.getPlayer1(), world.getTick());
                c.execute();
            } else if(key == KeyEvent.VK_A) {
                Command c = new CommandTurnWest(world.getPlayer1(), world.getTick());
                c.execute();
            } else if(key == KeyEvent.VK_D) {
                Command c = new CommandTurnEast(world.getPlayer1(), world.getTick());
                c.execute();
            } else if(key == KeyEvent.VK_SPACE){
                world.burstPlayerBullets1(2);
            } else if(key == KeyEvent.VK_UP) {
                Command c = new CommandTurnNorth(world.getPlayer2(), world.getTick());
                c.execute();
            } else if(key == KeyEvent.VK_DOWN) {
                Command c = new CommandTurnSouth(world.getPlayer2(), world.getTick());
                c.execute();
            } else if(key == KeyEvent.VK_LEFT) {
                Command c = new CommandTurnWest(world.getPlayer2(), world.getTick());
                c.execute();
            } else if(key == KeyEvent.VK_RIGHT) {
                Command c = new CommandTurnEast(world.getPlayer2(), world.getTick());
                c.execute();
            }else if(key == KeyEvent.VK_ENTER){
                world.burstPlayerBullets2(2);
            }
        }
    }

    public static void main(String[] args) {
        Window window = new Window();
        window.setVisible(true);
    }

}
