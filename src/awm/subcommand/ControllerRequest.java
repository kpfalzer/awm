package awm.subcommand;

import awm.Requestor;
import awm.controller.Config;

import java.util.Map;

/**
 * Controller request.
 */
public class ControllerRequest extends Requestor {
    private static final String __HOST = Config.HOST;
    private static final int __PORT = Config.PORT;

    public static Map<String,Object> request(String subcmd, Map<String,Object> params) {
        final ControllerRequest cr = new ControllerRequest(subcmd);
        final Map<String,Object> resp = cr.request(params);
        return resp;
    }

    private ControllerRequest(String subcmd) {
        super(subcmd);
        System.err.printf("DEBUG: ControllerRequest: %s:%d: %s\n",getHost(),getPort(),subcmd);
    }

    @Override
    protected String getHost() {
        return __HOST;
    }

    @Override
    protected int getPort() {
        return __PORT;
    }
}
