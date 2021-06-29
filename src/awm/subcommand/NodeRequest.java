package awm.subcommand;

import awm.Requestor;
import awm.node.Config;

import java.util.Map;

/**
 * Node request.
 */
public class NodeRequest extends Requestor {
    private static final int __PORT = Config.PORT;

    public static Map<String,Object> request(String subcmd, Object... kvs) {
        final NodeRequest nr = new NodeRequest(subcmd);
        return nr.request(kvs);
    }

    private NodeRequest(String subcmd) {
        super(subcmd);
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
