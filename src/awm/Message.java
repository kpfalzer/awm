package awm;

import java.util.HashMap;
import java.util.Map;

import static gblibx.Util.invariant;

public class Message {
    public static String get(String key, Object... args) {
        invariant(__FORMATS.containsKey(key));
        return String.format(__FMT, String.format(__FORMATS.get(key), args), key);
    }

    private static final String __FMT = "%s  (%s)";
    private static final Map<String, String> __FORMATS = new HashMap<>();

    static {
        __FORMATS.put("DIR-1", "%s: exists, but is not a directory");
        __FORMATS.put("DIR-2", "%s: could not create directory");
        __FORMATS.put("LOG-1", "%s: could not create log (details: %s)");
        __FORMATS.put("PERM-2", "%s: could not set %c permission");
    }
}
