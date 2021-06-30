package awm.node.subcommand;

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

public class Run extends RequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        initialize(httpExchange);
        invariant(isPOST());//todo
        __params = bodyAsObj();
        __ostream = toPrintStream(httpExchange.getResponseBody());
        execute();
        //todo: upload result to server
    }

    private Map<String, Object> __params;
    private PrintStream __ostream;
    private int __exitValue = -1;

    private Run execute() {
        new NodeRun();
        return this;
    }

    private class NodeRun {
        public NodeRun() {
            final String user = castobj(__params.get("user"));
            final String command = castobj(__params.get("command"));
            __builder = new ProcessBuilder(__RUNUSER, "-u", user, command);
            __builder.environment().put("AWM_SERVER", "value-for-server");
            __builder.redirectErrorStream(true);
            try {
                final Process proc = __builder.start();
                List<Thread> threads = Arrays.asList(
                        new Thread(new RunCmd.StreamGobbler(proc.getInputStream(), (line) -> {
                            __ostream.println(line);
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
        private static final String __RUNUSER = "/usr/sbin/runuser";
    }

}
