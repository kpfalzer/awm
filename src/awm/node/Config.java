package awm.node;

import awm.AwmProps;
import awm.Util;

import java.io.File;

/**
 * Node configuration settings.
 */
public class Config {
    public static final File JOB_SPOOL_DIR = new File(
            AwmProps.getProperty("awm.node.JOB_SPOOL_DIR", "/var/spool/awm"));
    public static final String JOB_SCRIPT_NAME_FMT =
            AwmProps.getProperty("awm.node.JOB_SCRIPT_NAME_FMT", "awm.job.%d");
    public static final File LOG = new File(
            AwmProps.getProperty("awm.node.LOG", "/var/log/awm.node.log"));

    public static final String HOST = AwmProps.getProperty(
            "awm.node.server.HOST", Util.hostname());
    public static final int PORT = Integer.parseInt(
            AwmProps.getProperty("awm.node.server.PORT", "3011")
    );

}
