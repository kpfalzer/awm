package awm.controller;

import awm.Logger;
import awm.Message;
import gblibx.GbLogger;
import gblibx.Util;
import gblibx.yaap.Option;
import gblibx.yaap.Parser;

import java.io.File;
import java.util.List;

import static awm.Util.usageError;
import static gblibx.Util.invariant;
import static gblibx.Util.isNonNull;
import static gblibx.Util.join;
import static gblibx.Util.toList;
import static java.util.Objects.isNull;

/**
 * Main for controller.
 */
public class Main {
    public static int addJob(PendingJob pj) {
        return theOne().__pendingJobs.insert(pj).size();
    }

    public static void main(String[] argv) {
        try {
            new Main(argv).initialize();
        } catch (Util.FileException e) {
            awm.Util.fatal(e);
        }
    }

    private Main(String[] args) {
        invariant(isNull(__theOne));
        __args = args;
        __theOne = this;
    }

    private static Main __theOne = null;
    private final String[] __args;

    private Main initialize() throws Util.FileException {
        return setupUsageOptions()
                .checkOptions()
                .startLogging()
                .startServer()
                ;
    }

    private Main startServer() {
        logger().message("SERVER-1", Config.HOST, Config.PORT);
        __server = Server.start();
        return this;
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
                'v', __VERBOSITY_OPT, "D|I|W|E", "D",
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
                        'l', __LOGFILE_OPT, null, Config.LOG.getAbsolutePath(),
                        "Logfile for controller messages", '?', null
                ))
                .add(new Option('c', __CONSOLE_OPT, "Log to console (too)"));
        return this;
    }

    private static final String __LOGFILE_OPT = "logfile";
    private static final String __CONSOLE_OPT = "console";
    private static final String __VERBOSITY_OPT = "verbosity";

    private Main startLogging() throws Util.FileException {
        File f = Util.createFile(getOption(__LOGFILE_OPT).getOptAsString());
        GbLogger logger = new GbLogger(f, getOption(__CONSOLE_OPT).isTrue(), true);
        logger.setLevel(getOption(__VERBOSITY_OPT).getOptAsString().charAt(0));
        logger.addNamedLogger(__HTTP_LOGGER, Message.getLogRecordMessage());
        __logger = new Logger(logger);
        __logger.info("LOG-2", f);
        return this;
    }

    private Option getOption(String optNm) {
        return __options.getValue(optNm);
    }

    private Main checkOptions() {
        if (!__options.parse(__args, false, (ex) -> {
            usageError(this.getClass(), ex);
        })) {
            usageError(this.getClass(), null);
        }
        String verbosity = __options.getValue("verbosity").asScalar();
        return this;
    }

    //package
    static Main theOne() {
        assert isNonNull(__theOne);
        return __theOne;
    }

    static public Logger logger() {
        return theOne().__logger;
    }

    private Parser __options;
    private Logger __logger;
    private Server __server;
    private PendingQueue __pendingJobs = new PendingQueue();

    private static final String __HTTP_LOGGER = "com.sun.net.httpserver";
}
