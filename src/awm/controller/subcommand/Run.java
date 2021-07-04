package awm.controller.subcommand;

import com.sun.net.httpserver.HttpExchange;
import jmvc.server.RequestHandler;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import static awm.Util.throwException;
import static gblibx.Util.invariant;

public class Run extends RequestHandler {
    public Run() {
        //nothing
    }

    private Map<String,Object> __request;

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        initialize(httpExchange);
        invariant(isPOST());//todo
        __request = bodyAsObj();
        boolean todo = true;
        //todo: do something with request
        respond();
    }

    private void respond() {
        JSONObject json = new JSONObject();
        json.put("jobid", 123)
                .put("node", "vm02")
                .put("command", __request.get("command"))
        ;
        System.err.println("DEBUG: cntl-server-response");
        try {
            sendJsonResponse(json.toString());
        } catch (IOException e) {
            throwException(e);
        }
    }
}
