package awm.node.subcommand;

import awm.Logger;
import awm.RequestHandler;
import awm.node.Main;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Set;

public class AddJob extends RequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
    }

    @Override
    protected Logger logger() {
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
}
