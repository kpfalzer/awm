package awm;

import gblibx.GbLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.LogRecord;

import static awm.Util.getStackTrace;
import static gblibx.Util.invariant;
import static gblibx.Util.isEven;

public class Message {
    public static String get(String key, Object... args) {
        invariant(__FORMATS.containsKey(key));
        return String.format(__FMT, String.format(__FORMATS.get(key), args), key);
    }

    public static Function<LogRecord, String> getLogRecordMessage() {
        return new Function<LogRecord, String>() {
            @Override
            public String apply(LogRecord r) {
                return get("LOGGER-1",
                        r.getLevel(),
                        r.getSourceClassName(), r.getSourceMethodName(),
                        r.getMessage());
            }
        };
    }

    private static final String __FMT = "%s  (%s)";
    private static final Map<String, String> __FORMATS = new HashMap<>();

    static Map<String, String> add(String... kvs) {
        invariant(isEven(kvs.length));
        for (int i = 0; i < kvs.length; i += 2) {
            __FORMATS.put(kvs[i], kvs[i + 1]);
        }
        return __FORMATS;
    }

    static {
        add("DIR-1", "%s: exists, but is not a directory",
                "DIR-2", "%s: could not create directory",
                "EXCEPTION-1", "%s%s",
                "LOG-1", "%s: could not create log (details: %s)",
                "LOGGER-1", "%s:%s#%s: %s",
                "REQ-1", "Request error: %s",
                "REQ-2", "Request from: %s: %s",
                "SERVER-1", "start server: %s:%d"
        );
    }

    public static class Logger {
        public Logger(GbLogger logger) {
            __logger = logger;
        }

        public Logger debug(String message) {
            __logger.debug(message);
            return this;
        }

        public Logger debug(String key, Object... args) {
            return debug(get(key, args));
        }

        public Logger info(String message) {
            __logger.info(message);
            return this;
        }

        public Logger info(String key, Object... args) {
            return info(get(key, args));
        }

        public Logger warning(String message) {
            __logger.warning(message);
            return this;
        }

        public Logger warning(String key, Object... args) {
            return warning(get(key, args));
        }

        public Logger error(String message) {
            __logger.error(message);
            return this;
        }

        public Logger error(String key, Object... args) {
            return error(get(key, args));
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
            return fatal(get(key, args));
        }

        public Logger message(String message) {
            __logger.message(message);
            return this;
        }

        public Logger message(String key, Object... args) {
            return message(get(key, args));
        }

        private final GbLogger __logger;
    }
}
