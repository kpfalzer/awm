package awm;

import gblibx.HttpConnection;

import java.util.Map;

import static awm.Util.throwException;
import static gblibx.Util.*;

public abstract class Requestor {

    protected Requestor(String subcmd) {
        _route = "/subcmd/" + subcmd;
    }

    protected  Map<String,Object> request(Map<String,Object> params) {
        try {
            //TODO: enhance post() to specify timeouts...
            return HttpConnection.postJSON(getHost(), getPort(), _route, params);
        } catch (HttpConnection.Exception e) {
            throwException(e);
        }
        invariant(false);
        return null;
    }

    protected abstract String getHost();

    protected abstract int getPort();

    protected final String _route;
}
