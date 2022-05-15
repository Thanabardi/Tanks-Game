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

    private List<Integer> keyCode1 = new ArrayList<Integer>();
    private List<Integer> keyCode2 = new ArrayList<Integer>();
    private List<Integer> keyList1 = List.of(KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);
    private List<Integer> keyList2 = List.of(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_ENTER);

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
        moveCommand1();
        moveCommand2();

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

        public Renderer() {
            setDoubleBuffered(true);

            imageGrass = new ImageIcon("imgs/grass.jpg").getImage();
            imageBrick = new ImageIcon("imgs/brick.png").getImage();
            imageSteel = new ImageIcon("imgs/steel.jpg").getImage();
            imageFlag = new ImageIcon("imgs/flag.png").getImage();
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            paintGrids(g);
            paintFlag(g);
            paintBullets1(g);
            paintBullets2(g);
            paintGrass(g);
            paintBricks(g);
            paintSteel(g);
            paintPlayer2(g);
            paintPlayer1(g);
            paintEnemies(g);
            paintPlayerHP(g);
            paintStage(g);
        }

        private void paintGrids(Graphics g) {
            g.setColor(Color.lightGray);
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

        private void paintSteel(Graphics g) {
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
            g.setColor(Color.blue);
            g.fillRect(x * perCell,y * perCell, perCell, perCell);
        }

        private void paintPlayer2(Graphics g) {
            int perCell = size/world.getSize();
            int x = world.getPlayer2().getX();
            int y = world.getPlayer2().getY();
            g.setColor(Color.red);
            g.fillRect(x * perCell,y * perCell, perCell, perCell);
        }

        private void paintEnemies(Graphics g) {
            int perCell = size/world.getSize();
            g.setColor(Color.red);
            for(Enemy e : world.getEnemies()) {
                int x = e.getX();
                int y = e.getY();
                g.fillRect(x * perCell,y * perCell,perCell, perCell);
            }
        }

        private void paintBullets1(Graphics g) {
            int perCell = size/world.getSize();
            g.setColor(Color.black);
            for(Bullet b : world.getBullets1()) {
                int x = b.getX();
                int y = b.getY();
                g.fillOval(x * perCell+5,y * perCell+5,perCell-10, perCell-10);
            }
        }

        private void paintBullets2(Graphics g) {
            int perCell = size/world.getSize();
            g.setColor(Color.white);
            for(Bullet b : world.getBullets2()) {
                int x = b.getX();
                int y = b.getY();
                g.fillOval(x * perCell+5,y * perCell+5,perCell-10, perCell-10);
            }
        }

        private void paintStage(Graphics g) {
            g.setColor(Color.black);
            if(world.getStage()>0){g.drawString("Stage " + world.getStage(), size-80, 20);}
        }

        private void paintPlayerHP(Graphics g) {
            int player1HP = world.getPlayer1().getHP();
            int player2HP = world.getPlayer2().getHP();
            g.setColor(Color.black);
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
            if (keyList1.contains(e.getKeyCode()) && !keyCode1.contains(e.getKeyCode())) {
                keyCode1.add(e.getKeyCode());
            }
            if (keyList2.contains(e.getKeyCode()) && !keyCode2.contains(e.getKeyCode())) {
                keyCode2.add(e.getKeyCode());
            }
        }

        public void keyReleased(KeyEvent e) {
            List<Integer> toRemoveKey1 = new ArrayList<Integer>();
            List<Integer> toRemoveKey2 = new ArrayList<Integer>();
            if(keyCode1.contains(e.getKeyCode())) {
                toRemoveKey1.add(e.getKeyCode());
            }
            if(keyCode2.contains(e.getKeyCode())) {
                toRemoveKey2.add(e.getKeyCode());
            }
            keyCode1.removeAll(toRemoveKey1);
            keyCode2.removeAll(toRemoveKey2);
        }
    }

    // class MouseController extends MouseAdapter {
    //     @Override
    //     public void mousePressed(MouseEvent e) {
    //         System.out.println(e.getX() + " " +e.getY());
    //     }
    // }

    public void moveCommand1() {
        for(Integer key : keyCode1) {
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
            }
            if(key == KeyEvent.VK_SPACE){
                world.burstPlayerBullets1(2);
            }
        }
    }

    public void moveCommand2() {
        for(Integer key : keyCode2) {
            if(key == KeyEvent.VK_UP) {
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
            }
            if(key == KeyEvent.VK_ENTER){
                world.burstPlayerBullets2(2);
            }
        }
    }

    public static void main(String[] args) {
        Window window = new Window();
        window.setVisible(true);
    }

}
