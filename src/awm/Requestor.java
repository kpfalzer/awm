package awm;

import gblibx.HttpConnection;

import java.util.Map;

import static awm.Util.throwException;
import static gblibx.Util.invariant;
import static gblibx.Util.toMap;

public abstract class Requestor {
    protected Requestor(String subcmd) {
        __route = "/subcmd/" + subcmd;
    }

    protected Map<String, Object> request(Object... kvs) {
        return request(toMap(kvs));
    }

    protected Map<String, Object> request(Map<String, Object> params) {
        try {
            //TODO: enhance post() to specify timeouts...
            return HttpConnection.postJSON(getHost(), getPort(), __route, params);
        } catch (HttpConnection.Exception e) {
            throwException(e);
        }
        invariant(false);
        return null;
    }

    protected abstract String getHost();

    protected abstract int getPort();

    private final String __route;
}
