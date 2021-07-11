package awm;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static gblibx.RunCmd.runCommandStdout;
import static gblibx.Util.invariant;
import static gblibx.Util.isNonNull;

public class Util {
    public static String hostname() {
        return runCommandStdout("/bin/hostname -s");
    }

    /**
     * Check if directory exists or create if not.
     *
     * @param dir   directory to check or create.
     * @param perms permissions: one or more of "rwx".
     */
    public static void createDirectory(File dir, String perms) {
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throwException("DIR-1", dir.getAbsolutePath());
            }
            //is directory
        } else {
            if (!dir.mkdirs()) {
                throwException("DIR-2", dir.getAbsolutePath());
            }
        }
        checkOrSetPermission(dir, perms);
    }

    public static void createDirectory(String dirname, String perms) {
        createDirectory(new File(dirname), perms);
    }

    public static void checkOrSetPermission(File f, String perms) {
        for (int i = 0; i < perms.length(); ++i) {
            final char perm = perms.charAt(i);
            if (!checkOrSetPermission(f, perm)) {
                throwException("PERM-1", f.getAbsolutePath(), perm);
            }
        }
    }

    public static boolean checkOrSetPermission(File f, char perm) {
        switch (perm) {
            case 'r':
                return (f.canRead() || f.setReadable(true));
            case 'w':
                return (f.canWrite() || f.setWritable(true));
            case 'x':
                return (f.canExecute() || f.setExecutable(true));
            default:
                invariant(false, String.format("%c: expected one of r|w|x", perm));
        }
        return false;
    }

    public static void throwException(Exception ex) {
        throw new AwmException(ex);
    }

    public static void throwException(String key, Object... args) {
        throw new AwmException(message(key, args));
    }

    public static String message(String key, Object... args) {
        return Message.get(key, args);
    }

    public static String spacify(String s) {
        return s.replace("&nbsp;", " ");
    }

    public static final String CMD_JOIN = "&nbsp;";

    public static String joinArgs(String[] args, int start) {
        return Stream.of(Arrays.copyOfRange(args, start, args.length))
                .collect(Collectors.joining(CMD_JOIN));
    }

    public static String[] splitArgs(String s) {
        return s.split(CMD_JOIN);
    }

    public static void fatal(Exception e) {
        e.printStackTrace(System.err);
        System.exit(1);
    }

    public static void usageError(Class clz, Exception ex) {
        System.err.printf("FATAL: %s", clz.getName());
        if (isNonNull(ex))
            System.err.printf(": %s\n", Message.get("USAGE-1", ex.getMessage()));
        System.exit(1);
    }

    public static String getUserName() {
        return System.getProperty("user.name");
    }

    public static Map<String, Object> addUserName(Map<String, Object> kvs) {
        kvs.put("user", getUserName());
        return kvs;
    }

    public static Map<String, Object> addHostName(Map<String, Object> kvs) {
        kvs.put("host", hostname());
        return kvs;
    }

    public static BufferedReader toBufferedReader(InputStream ins) {
        return new BufferedReader(new InputStreamReader(ins));
    }

    public static PrintStream toPrintStream(OutputStream os) {
        return new PrintStream(os);
    }

    /**
     * Check that has set includes at least reqd set.
     *
     * @param has  has set.
     * @param reqd required set.
     * @return set of missing reqd keys (or empty set if none missing).
     */
    public static <T> Set<T> hasRequired(Set<T> has, Set<T> reqd) {
        return reqd.stream().filter(r -> {
            return !has.contains(r);
        }).collect(Collectors.toSet());
    }

    /**
     * Check that has covered by expected.
     *
     * @param has      has set.
     * @param expected superset of expected keys.
     * @return has keys not in any of expected.
     */
    public static <T> Set<T> notInAny(Set<T> has, Set<T>... expected) {
        Set<T> merged = new HashSet<>();
        for (Set<T> s : expected) merged.addAll(s);
        return has.stream().filter((h -> {
            return !merged.contains(h);
        })).collect(Collectors.toSet());
    }

    public static String getStackTrace(Exception e, String sep) {
        return Arrays.asList(e.getStackTrace()).stream()
                .map(s -> s.toString())
                .collect(Collectors.joining(sep));
    }

    public static <T> T convert(Object s, Function<Object, T> conv) {
        try {
            return conv.apply(s);
        } catch (Exception e) {
            return null;
        }
    }
}
