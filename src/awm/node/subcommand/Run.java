package awm.node.subcommand;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import gblibx.RunCmd;
import jmvc.server.RequestHandler;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static awm.Util.*;
import static gblibx.Util.*;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Handle run by sending back stdout/err onto body (line-by-line).
 * stderr will prefix line with ERROR.
 */
public class Run extends RequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        initialize(httpExchange);
        invariant(isPOST());//todo
        __params = castobj(bodyAsObj());
        //got what we need, so lets start response
        responseHeader();
        //__ostream.println("DEBUG1");
        //__ostream.flush(); //need flush()
        execute();
        //todo: upload result to server
    }

    private void responseHeader() throws IOException {
        final Headers headers = _exchange.getResponseHeaders();
        headers.add("Transfer-Encoding", "chunked");
        _exchange.sendResponseHeaders(HTTP_OK, 0);
    }

    private Map<String, String> __params;
    private PrintStream __ostream;
    private int __exitValue = -1;

    private Run execute() {
        new NodeRun();
        return this;
    }

    //todo: extend RunCmd!

    private class NodeRun {
        private List<String> getCmdArgs() {
            List<String> args = toList(__RUNUSER, "-u", __params.get("user"));
            List<String> cmd = toList(splitArgs(__params.get("command")));
            args.addAll(cmd);
            return args;
        }

        public NodeRun() {
            __builder = new ProcessBuilder(getCmdArgs());
            __builder.environment().put("AWM_SERVER", "value-for-server");
            try {
                final Process proc = __builder.start();
                __ostream = toPrintStream(_exchange.getResponseBody());
                List<Thread> threads = Arrays.asList(
                        new Thread(new RunCmd.StreamGobbler(proc.getInputStream(), (line) -> {
                            __ostream.println(line);
                            __ostream.flush(); //need flush()
                        })),
                        new Thread(new RunCmd.StreamGobbler(proc.getErrorStream(), (line) -> {
                            __ostream.println("ERROR: " + line);
                            __ostream.flush(); //!!
                        }))
                );
                threads.forEach(Thread::start);
                proc.waitFor();
                for (Thread thread : threads) thread.join();
                _exchange.close();
                __exitValue = proc.exitValue();
            } catch (IOException | InterruptedException e) {
                throwException(e);
            }
        }

        private ProcessBuilder __builder;
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
