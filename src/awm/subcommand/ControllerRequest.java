package awm.subcommand;

import awm.Requestor;
import awm.controller.Config;
import gblibx.HttpConnection;

import java.util.Map;

import static awm.Util.throwException;
import static gblibx.Util.invariant;
import static gblibx.Util.toMap;

/**
 * Controller request.
 */
public class ControllerRequest extends Requestor {
    private static final String __HOST = Config.HOST;
    private static final int __PORT = Config.PORT;

    public static Map<String,Object> request(String subcmd, Object... kvs) {
        final ControllerRequest cr = new ControllerRequest(subcmd);
        final Map<String,Object> resp = cr.request(kvs);
        //todo: add back any params?
        return resp;
    }

    private ControllerRequest(String subcmd) {
        super(subcmd);
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
