public abstract class Command {
    private Player player;

    public Command(Player player, int tick) {
        this.player = player;
    }

    public abstract void execute();

    public Player getPlayer() {
        return player;
    }
}