package awm.node;

import java.io.File;

/**
 * Node configuration settings.
 */
public class Config {
    public static final File JOB_SPOOL_DIR = new File(
            System.getProperty("awm.node.JOB_SPOOL_DIR", "/var/spool/awm"));
    public static final String JOB_SCRIPT_NAME_FMT =
            System.getProperty("awm.node.JOB_SCRIPT_NAME_FMT", "awm.job.%d");
    public static final File LOG = new File(
            System.getProperty("awm.node.LOG", "/var/log/awm.node.log"));
}
