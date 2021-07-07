package awm;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static awm.Util.hasRequired;
import static awm.Util.notInAny;
import static gblibx.Util.castobj;
import static gblibx.Util.invariant;
import static gblibx.Util.isEven;
import static gblibx.Util.isNonNull;
import static gblibx.Util.join;
import static java.util.Objects.isNull;

public abstract class RequestHandler extends jmvc.server.RequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            initialize(httpExchange);
            invariant(isPOST());
            _request = bodyAsObj();
            if (isNull(_request)) _request = Collections.EMPTY_MAP;
            logger().debug("REQ-2", httpExchange.getRemoteAddress().toString(), _request);
            checkParams()
                    .respond();
        } catch (IOException e) {
            logger().error(e);
            throw e;
        }
    }

    protected abstract Message.Logger logger();

    protected Map<String, Object> _request = null;
    protected StringBuilder _errorMsg = null;
    protected JSONObject _response = new JSONObject();

    protected boolean hasError() {
        return isNonNull(_errorMsg);
    }

    protected StringBuilder getErrorMsg() {
        invariant(hasError());
        return _errorMsg;
    }

    private StringBuilder error(String msg) {
        if (isNull(_errorMsg)) {
            _errorMsg = new StringBuilder();
        }
        return _errorMsg.append(msg);
    }

    private StringBuilder error(String fmt, Object... args) {
        return error(String.format(fmt, args));
    }

    private StringBuilder invalidParams(String type, Set<String> bad) {
        return error("%s request params: %s", type, join(bad, ","));
    }

    protected abstract RequestHandler respond() throws IOException;

    protected RequestHandler checkParams() throws IOException {
        final Set<String> has = _request.keySet();
        Set<String> bad = hasRequired(has, getRequiredParams());
        if (!bad.isEmpty()) {
            invalidParams("Missing", bad);
        }
        if (!hasError()) {
            bad = notInAny(has, getRequiredParams(), getOptionalParams());
            if (!bad.isEmpty()) {
                invalidParams("Invalid", bad);
            }
        }
        if (hasError()) {
            requestErrorResponse();
        }
        return this;
    }

    protected void requestErrorResponse() throws IOException {
        invariant(hasError());
        logger().debug("REQ-1", getErrorMsg());
        sendResponse("status", getErrorMsg());
    }

    protected boolean canSendResponse() {
        return isNonNull(_response);
    }

    protected void sendResponse(Object... kvs) throws IOException {
        if (!canSendResponse()) return;
        invariant(isEven(kvs.length));
        for (int i = 0; i < kvs.length; i += 2) {
            _response.put(castobj(kvs[i]), kvs[i + 1]);
        }
        final String jsons = _response.toString();
        _response = null;   //no more
        sendJsonResponse(jsons);
    }

    abstract protected Set<String> getRequiredParams();

    protected Set<String> getOptionalParams() {
        return Collections.EMPTY_SET;
    }
}
