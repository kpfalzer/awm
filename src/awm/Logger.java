package awm;

import gblibx.GbLogger;

import java.io.File;

import static awm.Util.getStackTrace;

public class Logger {
    public Logger(File log, boolean useConsole) throws gblibx.Util.FileException {
        this(new GbLogger(log, useConsole));
    }
    public Logger(GbLogger logger) {
        __logger = logger;
    }

    public Logger debug(String message) {
        __logger.debug(message);
        return this;
    }

    public Logger debug(String key, Object... args) {
        return debug(Message.get(key, args));
    }

    public Logger info(String message) {
        __logger.info(message);
        return this;
    }

    public Logger info(String key, Object... args) {
        return info(Message.get(key, args));
    }

    public Logger warning(String message) {
        __logger.warning(message);
        return this;
    }

    public Logger warning(String key, Object... args) {
        return warning(Message.get(key, args));
    }

    public Logger error(String message) {
        __logger.error(message);
        return this;
    }

    public Logger error(String key, Object... args) {
        return error(Message.get(key, args));
    }

    private static final String __X1 = "\n    ";

    public Logger error(Exception e) {
        return error("EXCEPTION-1", e.getMessage(), __X1 + getStackTrace(e, __X1));
    }

    public Logger fatal(String message) {
        __logger.fatal(message);
        return this;
    }

    public Logger fatal(String key, Object... args) {
        return fatal(Message.get(key, args));
    }

    public Logger message(String message) {
        __logger.message(message);
        return this;
    }

    public Logger message(String key, Object... args) {
        return message(Message.get(key, args));
    }

    private final GbLogger __logger;
}
