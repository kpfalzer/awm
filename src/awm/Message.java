package awm;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.LogRecord;

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
                "LOG-2", "%s: starting new logfile",
                "LOGGER-1", "%s:%s#%s: %s",
                "QUEUE-1", "Added job: %s: %d on queue",
                "REQ-1", "Request error: %s",
                "REQ-2", "Request from: %s: %s",
                "SERVER-1", "start server: %s:%d",
                "USAGE-1", "%s"
        );
    }

}
