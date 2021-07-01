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

import static awm.Util.toPrintStream;
import static gblibx.Util.castobj;
import static gblibx.Util.invariant;
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
        __params = bodyAsObj();
        //got what we need, so lets start response
        responseHeader();
        __ostream = toPrintStream(httpExchange.getResponseBody());
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

    private Map<String, Object> __params;
    private PrintStream __ostream;
    private int __exitValue = -1;

    private Run execute() {
        new NodeRun();
        return this;
    }

    //todo: extend RunCmd!

    private class NodeRun {
        public NodeRun() {
            final String user = castobj(__params.get("user"));
            final String command = castobj(__params.get("command"));
            __builder = new ProcessBuilder(__RUNUSER, "-u", user, command);
            __builder.environment().put("AWM_SERVER", "value-for-server");
            try {
                final Process proc = __builder.start();
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
                __exitValue = proc.exitValue();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
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
