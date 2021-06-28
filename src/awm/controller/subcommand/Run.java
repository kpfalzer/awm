package awm.controller.subcommand;

import com.sun.net.httpserver.HttpExchange;
import jmvc.server.RequestHandler;

import java.io.IOException;
import java.util.Map;

import static gblibx.Util.invariant;

public class Run extends RequestHandler {
    public Run() {
        //nothing
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        initialize(httpExchange);
        invariant(isPOST());//todo
        Map<String, Object> params = bodyAsObj();
        boolean todo = true;
    }
}
