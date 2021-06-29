package awm.subcommand;

import java.util.Map;

import static awm.Util.spacify;
import static gblibx.Util.invariant;

public class Run extends ControllerRequest {
    private Run() {
        super("run");
    }

    private Map<String, Object> execute(Object... kvs) {
        return super.request(kvs);
    }

    public static void execute(String[] argv) {
        invariant(2 == argv.length);
        final Map<String, Object> result = new Run().execute("command", spacify(argv[1]));
        boolean todo = true;
    }
}
