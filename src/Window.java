import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Window extends JFrame implements Observer {

    private int size = 800;
    private long delayed = 100;
    private World world;
    private Renderer renderer;
    private Gui gui;
    private int worldSize = 26;

    private List<Integer> keyCode = new ArrayList<Integer>();
    private List<Integer> keyList = List.of(KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D, KeyEvent.VK_SPACE);

    List<Command> replays = new ArrayList<Command>();

    public Window() {
        super();
        addKeyListener(new KeyController());
        // addMouseListener(new MouseController());
        setLayout(new BorderLayout());
        renderer = new Renderer();
        add(renderer, BorderLayout.CENTER);
        gui = new Gui();
        add(gui, BorderLayout.SOUTH);
        world = new World(worldSize);
        world.addObserver(this);
        setSize(size - 9, size + 52);
//        setResizable(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void update(Observable o, Object arg) {
        renderer.repaint();
        gui.updateTick(world.getTick());
        moveCommand();

        for (Command c: replays){
            if (c.getTick() == world.getTick()){
                c.execute();
            }
        }
        if(world.isGameOver()) {
            gui.showGameOverLabel();
            gui.enableReplayButton();
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
            paintPlayer(g);
            paintEnemies(g);
            paintBullets1(g);
            paintBullets2(g);
            paintGlass(g);
            paintBricks(g);
            paintSteel(g);
            paintFlag(g);
        }

        private void paintGrids(Graphics g) {
            g.setColor(Color.lightGray);
            g.fillRect(0, 0, size, size);
        }

        private void paintGlass(Graphics g) {
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

        private void paintPlayer(Graphics g) {
            int perCell = size/world.getSize();
            int x = world.getPlayer().getX();
            int y = world.getPlayer().getY();
            g.setColor(Color.blue);
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
    }

    class Gui extends JPanel {

        private JLabel tickLabel;
        private JButton startButton;
        private JButton replayButton;
        private JLabel gameOverLabel;

        public Gui() {
            setLayout(new FlowLayout());
            tickLabel = new JLabel("Tick: 0");
            add(tickLabel);
            startButton = new JButton("Start");
            startButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    world.start();
                    startButton.setEnabled(false);
                    Window.this.requestFocus();
                }
            });
            add(startButton);
            replayButton = new JButton("Replay");
            replayButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    world.start();
                    replayButton.setEnabled(false);
                    Window.this.requestFocus();
                }
            });
            replayButton.setEnabled(false);
            add(replayButton);
            gameOverLabel = new JLabel("GAME OVER");
            gameOverLabel.setForeground(Color.red);
            gameOverLabel.setVisible(false);
            add(gameOverLabel);
        }

        public void updateTick(int tick) {
            tickLabel.setText("Tick: " + tick);
        }

        public void showGameOverLabel() {
            gameOverLabel.setVisible(true);
        }

        public void enableReplayButton() {
            replayButton.setEnabled(true);
        }
    }

    class KeyController extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (keyList.contains(e.getKeyCode()) && !keyCode.contains(e.getKeyCode())) {
                keyCode.add(e.getKeyCode());
            }
        }

        public void keyReleased(KeyEvent e) {
            if(keyCode.contains(e.getKeyCode())) {
                keyCode.remove(Integer.valueOf(e.getKeyCode()));
            }
        }
    }

    // class MouseController extends MouseAdapter {
    //     @Override
    //     public void mousePressed(MouseEvent e) {
    //         System.out.println(e.getX() + " " +e.getY());
    //     }
    // }

    public void moveCommand() {
        for(Integer key : keyCode) {
            if(key == KeyEvent.VK_W) {
                Command c = new CommandTurnNorth(world.getPlayer(), world.getTick());
                c.execute();
                replays.add(c);
            } else if(key == KeyEvent.VK_S) {
                Command c = new CommandTurnSouth(world.getPlayer(), world.getTick());
                c.execute();
                replays.add(c);
            } else if(key == KeyEvent.VK_A) {
                Command c = new CommandTurnWest(world.getPlayer(), world.getTick());
                c.execute();
                replays.add(c);
            } else if(key == KeyEvent.VK_D) {
                Command c = new CommandTurnEast(world.getPlayer(), world.getTick());
                c.execute();
                replays.add(c);
            }
            if(key == KeyEvent.VK_SPACE){
                world.burstPlayerBullets1();
            }
        }
    }

    public static void main(String[] args) {
        Window window = new Window();
        window.setVisible(true);
    }

}
