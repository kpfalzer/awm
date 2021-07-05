package awm.controller;

import awm.AwmProps;
import awm.Util;

import java.io.File;

public class Config {
    public static final String HOST = AwmProps.getProperty(
            "awm.controller.server.HOST", Util.hostname());
    public static final int PORT = Integer.parseInt(
            AwmProps.getProperty("awm.controller.server.PORT", "6817")
    );
    public static final File LOG = new File(
            AwmProps.getProperty("awm.controller.LOG", "/var/log/awm.controller.log"));
}
