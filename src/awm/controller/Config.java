package awm.controller;

import awm.AwmProps;
import awm.Util;

public class Config {
    public static final String HOST = AwmProps.getProperty(
            "awm.controller.server.HOST", Util.hostname());
    public static final int PORT = Integer.parseInt(
            AwmProps.getProperty("awm.controller.server.PORT", "3010")
    );

}
