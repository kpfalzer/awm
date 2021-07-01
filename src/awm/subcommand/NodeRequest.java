package awm.subcommand;

import awm.Requestor;
import awm.node.Config;
import gblibx.HttpConnection;
import gblibx.RunCmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static awm.Util.throwException;
import static awm.Util.toBufferedReader;
import static gblibx.Util.invariant;
import static gblibx.Util.toMap;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Node request.
 */
public class NodeRequest extends Requestor {
    private static final int __PORT = Config.PORT;

    public static Map<String, Object> request(String subcmd, String toHost, Object... kvs) {
        final Map<String, Object> params = toMap(kvs);
        final NodeRequest nr = new NodeRequest(subcmd, toHost);
        return nr.request(params);
    }

    public static void request(String subcmd, String toHost,
                               Map<String, Object> params, Consumer<String>[] readers) {
        final NodeRequest nr = new NodeRequest(subcmd, toHost);
        nr.request(params, readers);
    }

    private NodeRequest(String subcmd, String toHost) {
        super(subcmd);
        __toHost = toHost;
    }

    private final String __toHost;

    protected void request(Map<String, Object> params, Consumer<String>[] readers) {
        invariant(2 == readers.length);
        try {
            HttpConnection.postJSON(getHost(), getPort(), _route, params, (http) -> {
                try {
                    invariant(HTTP_OK == http.getResponseCode());
                    if (true) {
                        //null: InputStream cerr = http.getErrorStream();
                        InputStream cout = http.getInputStream();
                        BufferedReader rdr = toBufferedReader(cout);
                        //while (! rdr.ready()) ;
                        rdr.lines().forEach((line)->{
                            readers[0].accept(line);
                        });
                    } else {
                        //todo: really need threads here?  since this is just client command:
                        // awm run my-command
                        List<Thread> threads = Arrays.asList(
                                new Thread(new RunCmd.StreamGobbler(
                                        http.getInputStream(), (line) -> {
                                    readers[0].accept(line);
                                })),
                                new Thread(new RunCmd.StreamGobbler(
                                        http.getErrorStream(), (line) -> {
                                    readers[1].accept(line);
                                }))
                        );
                        threads.forEach(Thread::start);
                        for (Thread thread : threads) thread.join();
                    }
                } catch (IOException | InterruptedException e) {
                    throwException(e);
                }
            });
        } catch (HttpConnection.Exception e) {
            throwException(e);
        }
    }

    @Override
    protected String getHost() {
        return __toHost;
    }

    @Override
    protected int getPort() {
        return __PORT;
    }
}
