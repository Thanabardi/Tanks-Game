import java.util.ArrayList;
import java.util.List;

public class Record {
    List<Command> commands = new ArrayList<Command>();
    List<Integer> ticks = new ArrayList<Integer>();
    public Record(Command command, int tick){
        commands.add(command);
        ticks.add(tick);
    }

    public List<Command> getCommands() {
        return commands;
    }

    public List<Integer> getTicks() {
        return ticks;
    }
}
