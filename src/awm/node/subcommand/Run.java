package awm.node.subcommand;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import gblibx.RunCmd;
import jmvc.server.RequestHandler;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import static awm.Util.splitArgs;
import static awm.Util.toPrintStream;
import static gblibx.Util.*;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Handle run by sending back stdout/err onto body (line-by-line).
 * stderr will prefix line with ERROR.
 */
public class Run extends RequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.err.println("DEBUG: Run.handle starts");
        initialize(httpExchange);
        invariant(isPOST());//todo
        __params = castobj(bodyAsObj());
        responseHeader().execute();
        //todo: upload result to server
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
        System.err.println("DEBUG: Run.execute run...");
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
