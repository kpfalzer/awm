package awm.node;

/**
 * Main for node.
 */
public class Main {
    public Main(boolean logToConsoleToo) {
        __logToConsole = logToConsoleToo;
    }

    private Main initialize() {
        //todo: add more
        return setLogger();
    }

    private Main setLogger() {
        __logger = new Logger(Config.LOG, __logToConsole);
        return this;
    }

    private Logger __logger;
    private final boolean __logToConsole;
}
