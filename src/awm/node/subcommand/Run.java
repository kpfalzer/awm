package awm.node.subcommand;

import awm.Logger;
import awm.RequestHandler;
import awm.node.Main;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import gblibx.RunCmd;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static awm.Util.splitArgs;
import static awm.Util.toPrintStream;
import static gblibx.Util.*;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Handle run by sending back stdout/err onto body (line-by-line).
 * stderr will prefix line with ERROR.
 */
public class Run extends RequestHandler {
    //todo: added awm.RequestHandler: need to check method overrides.
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        initialize(httpExchange);
        invariant(isPOST());//todo
        __params = castobj(bodyAsObj());
        responseHeader().execute();
        //todo: upload result to server
    }

    @Override
    protected Logger logger() {
        return Main.logger();
    }

    @Override
    protected RequestHandler respond() throws IOException {
        throw new NotImplementedException();
        //todo: return null;
    }

    @Override
    protected Set<String> getRequiredParams() {
        throw new NotImplementedException();
        //todo: return null;
    }

    private Run responseHeader() throws IOException {
        final Headers headers = _exchange.getResponseHeaders();
        headers.add("Transfer-Encoding", "chunked");  //continuous response
        _exchange.sendResponseHeaders(HTTP_OK, 0);
        return this;
    }

    private Map<String, String> __params;
    private PrintStream __ostream;
    private Pair<Boolean,Integer> __exitVal;

    private Run execute() {
        final NodeRun nrun = new NodeRun();
        nrun.run();
        _exchange.close();
        __exitVal = new Pair<>(nrun.isNormalExit(), nrun.getExitValue());
        return this;
    }

    private List<String> getCmdArgs() {
        List<String> args = toList(__RUNUSER, "-u", __params.get("user"));
        List<String> cmd = toList(splitArgs(__params.get("command")));
        args.addAll(cmd);
        return args;
    }

    private class NodeRun extends RunCmd {

        private NodeRun() {
            super(getCmdArgs(), null, null);
            initialize();
        }

        private NodeRun initialize() {
            _builder.environment().put("AWM_SERVER", "value-for-server");
            __ostream = toPrintStream(_exchange.getResponseBody());
            setCout((line) -> {
                __ostream.println(line);
                __ostream.flush();
            });
            setCerr((line) -> {
                __ostream.println("ERROR: " + line);
                __ostream.flush();
            });
            return this;
        }
    }

    private static final String __RUNUSER;

    static {
        if (System.getProperty("os.name").toUpperCase().startsWith("MAC")) {
            __RUNUSER = "/usr/bin/sudo";
        } else {
            __RUNUSER = "/usr/sbin/runuser";
        }
    }

}
