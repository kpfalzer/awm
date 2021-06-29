package awm;

import java.io.File;

import static gblibx.RunCmd.runCommandStdout;
import static gblibx.Util.invariant;

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

    public static void fatal(Exception e) {
        e.printStackTrace(System.err);
        System.exit(1);
    }
}
