
public class CommandTeleport extends Command{

    private int size;

    public CommandTeleport(Player player, int tick, int size) {
        super(player, tick);
        this.size  = size;
    }

    @Override
    public void execute() {
        getPlayer().reset();
        getPlayer().setPosition(this.size/2, this.size/2);
    }
}
