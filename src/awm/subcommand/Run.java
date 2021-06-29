package awm.subcommand;

import java.util.Map;

import static awm.Util.spacify;
import static gblibx.Util.invariant;

public class Run {
    /**
     * Execute run.
     * 1) send request to controller.
     * 2) Could take LONG time for controller to respond.
     * 3) controller responds with run parameters (and node).
     * 3.1) or details why cannot be run.
     * 4) collect response and send request to (designated) node.
     */
    private Run execute(Object... kvs) {
        Map<String, Object> cntlReponse = ControllerRequest.request(CMD, kvs);
        //todo
        return this;
    }

    public static void execute(String[] argv) {
        invariant(2 == argv.length);
        final Run run = new Run().execute("command", spacify(argv[1]), "user", __USER);
        boolean todo = true;
    }

    public static final String CMD = "run";
    private static String __USER = System.getProperty("user.name");
}
