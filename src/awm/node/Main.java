package awm.node;

import awm.Logger;
import gblibx.Util;

import static gblibx.Util.invariant;
import static java.util.Objects.isNull;

/**
 * Main for node.
 */
public class Main {
    public Main(boolean logToConsoleToo) {
        invariant(isNull(__theOne));
        __logToConsole = logToConsoleToo;
        __theOne = this;
    }

    private Main initialize() throws Util.FileException {
        //todo: add more
        return setLogger();
    }

    private Main setLogger() throws Util.FileException {
        __logger = new Logger(Config.LOG, __logToConsole);
        return this;
    }

    public static Logger logger() {
        return __theOne.__logger;
    }

    private Logger __logger;
    private final boolean __logToConsole;
    private static Main __theOne = null;
}
