package awm.subcommand;

import awm.Requestor;
import awm.node.Config;
import gblibx.HttpConnection;
import gblibx.RunCmd;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static awm.Util.throwException;
import static gblibx.Util.toMap;

/**
 * Node request.
 */
public class NodeRequest extends Requestor {
    private static final int __PORT = Config.PORT;

    public static Map<String, Object> request(String subcmd, Object... kvs) {
        final Map<String, Object> params = toMap(kvs);
        final NodeRequest nr = new NodeRequest(subcmd);
        return nr.request(params);
    }

    private NodeRequest(String subcmd) {
        super(subcmd);
    }

    protected void request(Map<String, Object> params, Consumer<String> reader) {
        try {
            HttpConnection.postJSON(getHost(), getPort(), _route, params, (http) -> {
                try {
                    List<Thread> threads = Arrays.asList(
                            new Thread(new RunCmd.StreamGobbler(http.getInputStream(), reader)),
                            new Thread(new ErrorStreamGobbler(http.getErrorStream(), reader))
                    );
                    threads.forEach(Thread::start);
                    for (Thread thread : threads) thread.join();
                } catch (IOException | InterruptedException e) {
                    throwException(e);
                }
            });
        } catch (HttpConnection.Exception e) {
            throwException(e);
        }
    }

    static class ErrorStreamGobbler extends RunCmd.StreamGobbler {

        public ErrorStreamGobbler(InputStream from, Consumer<String> to) {
            super(from, to);
        }

        @Override
        public void run() {
            _reader.lines().forEach((line) -> {
                _writer.accept("Error: " + line);
            });
        }
    }

    @Override
    protected String getHost() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int getPort() {
        return __PORT;
    }
}
