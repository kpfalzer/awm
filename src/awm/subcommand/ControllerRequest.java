package awm.subcommand;

import awm.controller.Config;
import gblibx.HttpConnection;

import java.util.Map;

import static awm.Util.throwException;
import static gblibx.Util.invariant;
import static gblibx.Util.toMap;

/**
 * Controller request.
 */
public class ControllerRequest {
    protected ControllerRequest(String subcmd) {
        __route = "/subcmd/" + subcmd;
    }

    protected Map<String, Object> request(Object... kvs) {
        return request(toMap(kvs));
    }

    protected Map<String, Object> request(Map<String, Object> params) {
        try {
            //TODO: enhance post() to specify timeouts...
            return HttpConnection.postJSON(__HOST, __PORT, __route, params);
        } catch (HttpConnection.Exception e) {
            throwException(e);
        }
        invariant(false);
        return null;
    }

    private final String __route;
    private static final String __HOST = Config.HOST;
    private static final int __PORT = Config.PORT;
}
