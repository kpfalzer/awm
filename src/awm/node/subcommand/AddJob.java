package awm.node.subcommand;

import awm.Message;
import awm.node.Main;
import com.sun.net.httpserver.HttpExchange;
import awm.RequestHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class AddJob extends RequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
    }

    @Override
    protected Message.Logger logger() {
        return Main.logger();
    }

    @Override
    protected RequestHandler respond() throws IOException {
        return null;
    }

    @Override
    protected Set<String> getRequiredParams() {
        return null;
    }

    private Map<String, String> __params;
}
