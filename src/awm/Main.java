package awm;

import awm.subcommand.Run;

import java.util.Arrays;

import static gblibx.Util.invariant;
import static java.util.Objects.isNull;

/**
 * Execute subcommands.
 */
public class Main {
    private Main(String[] argv) {
        __argv = argv;
        invariant(isNull(__theOne));
        __theOne = this;
    }

    public static void main(String[] argv) {
        new Main(argv).process();
    }

    private void process() {
        if (0 == __argv.length) usageError();
        //drop subcommand
        final String[] args = Arrays.copyOfRange(__argv, 1, __argv.length);
        switch (__argv[0]) {
            case "run":
                Run.execute(args);
                break;
            default:
                invariant(false);
        }
    }

    private void usageError() {
        System.err.println("Usage: awm subcommand ...");
        System.exit(1);
    }

    private static Main __theOne = null;

    private final String[] __argv;
}
