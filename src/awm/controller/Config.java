package awm.controller;

public class Config {
    public static final String HOST = System.getProperty("awm.controller.server.HOST", "localhost");
    public static final int PORT = Integer.parseInt(
            System.getProperty("awm.controller.server.PORT", "3010")
    );

}
