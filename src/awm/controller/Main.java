package awm.controller;

import gblibx.Util;
import gblibx.yaap.Option;
import gblibx.yaap.Parser;

import java.util.List;

import static gblibx.Util.invariant;
import static gblibx.Util.join;
import static gblibx.Util.toList;
import static java.util.Objects.isNull;

/**
 * Main for controller.
 */
public class Main {
    public static void main(String[] argv) {
        new Main(argv).initialize();
    }

    private Main(String[] args) {
        invariant(isNull(__theOne));
        __args = args;
        __theOne = this;
    }

    private static Main __theOne = null;
    private final String[] __args;

    private Main initialize() {
        return setupUsageOptions()
                .checkOptions();
    }

    private Main setupUsageOptions() {
        class Sideband {
            final List<String> VALID_VERBOSITY = toList("D", "I", "W", "E");
            final String VALID_VERBOSITY_OPT = join(VALID_VERBOSITY, "|");

            String verbosityError(String arg) {
                return String.format("found '%s': expected one of %s", arg, VALID_VERBOSITY_OPT);
            }

        }
        Sideband sb = new Sideband();
        __options = new Parser("awmController", "Start AWM controller.");
        __options.add(new Option(
                "-v|--verbosity", "D|I|W|E", "D",
                String.format("Log verbosity (one of: %s)", sb.VALID_VERBOSITY_OPT),
                '?', (_arg) -> {
            final String arg = _arg.toString();
            if (1 != arg.length()
                    || !sb.VALID_VERBOSITY.stream().anyMatch((e) -> e.equals(arg))) {
                return new Util.Pair<>(null, sb.verbosityError(arg));
            }
            return new Util.Pair<>(arg, null);
        }))
                .add(new Option(
                        "-l|--logfile", "logfile", Config.LOG,
                        "Logfile for controller messages", '?'
                ))
                .add(new Option("-c|--console", "Log to console (too)"));
        return this;
    }

    private Main checkOptions() {
        if (!__options.parse(__args, false)) {
            System.exit(1);
        }
        String verbosity = __options.getValue("verbosity").asScalar();
        return this;
    }

    private Parser __options;
}
