import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class Window extends JFrame implements Observer {

    private int size = 700;
    private World world;
    private Renderer renderer;
    private Gui gui;
    private int worldSize = 26;

    List<Command> replays = new ArrayList<Command>();

    public Window() {
        super();
        addKeyListener(new Controller());
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

        for (Command c: replays){
            if (c.getTick() == world.getTick()){
                c.execute();
            }
        }
        if(world.isGameOver()) {
            gui.showGameOverLabel();
            gui.enableReplayButton();
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
//            paintPlayer(g);
//            paintEnemies(g);
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

    class Controller extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode() == KeyEvent.VK_UP) {
                Command c = new CommandTurnNorth(world.getPlayer(), world.getTick());
                c.execute();
                replays.add(c);
            } else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                Command c = new CommandTurnSouth(world.getPlayer(), world.getTick());
                c.execute();
                replays.add(c);
            } else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
                Command c = new CommandTurnWest(world.getPlayer(), world.getTick());
                c.execute();
                replays.add(c);
            } else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
                Command c = new CommandTurnEast(world.getPlayer(), world.getTick());
                c.execute();
                replays.add(c);
            } else if(e.getKeyCode() == KeyEvent.VK_Z){
                Command c = new CommandTeleport(world.getPlayer(), world.getTick(), world.getSize());
                c.execute();
                replays.add(c);
            }
        }
    }

    public static void main(String[] args) {
        Window window = new Window();
        window.setVisible(true);
    }

}
