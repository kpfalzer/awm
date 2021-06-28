package awm;

import gblibx.MultiLogger;

import java.io.File;
import java.io.IOException;

import static awm.Util.createDirectory;
import static awm.Util.throwException;

/**
 * Logger for node messages.
 */
public class Logger {
    public Logger(File fname, boolean useConsoleToo) {
        createDirectory(fname.getParent(), "rwx");
        try {
            __logger = new MultiLogger(fname, useConsoleToo);
        } catch (IOException e) {
            throwException("LOG-1", fname.getAbsolutePath(), e.getMessage());
        }
    }

    private MultiLogger __logger;
}
