package awm.controller.subcommand;

import awm.Message;
import awm.RequestHandler;
import awm.controller.Main;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static awm.Util.throwException;
import static gblibx.Util.toSet;

/**
 * Run request.
 * Required parameters: user, command, host.
 * Optional parameters: mem, ncore, license.
 *
 * Example: curl --header "Content-Type: application/json" --request POST --data '{"user":"kpfalzer","host":"vm01","command":"hostname"}' http://localhost:6817/subcmd/run
 */
public class Run extends RequestHandler {
    public Run() {
        //nothing
    }

    private Map<String, Object> __request = null;

    private static final Set<String> __REQUIRED_PARMS = toSet("user", "command", "host");
    private static final Set<String> __OPTIONAL_PARMS = toSet("mem", "ncore", "license");

    /**
     * Return JSON packet with parameters: status, jobid, node, command.
     * status == "0" on success; else error details (as status value).
     */
    void xrespond() {
        JSONObject json = new JSONObject();
        json.put("jobid", 123)
                .put("node", "vm02")
                .put("command", __request.get("command"))
        ;
        System.err.println("DEBUG: cntl-server-response");
        try {
            sendJsonResponse(json.toString());
        } catch (IOException e) {
            throwException(e);
        }
    }

    @Override
    protected RequestHandler respond() throws IOException {
        sendResponse(
                "status","0",
                "jobid", 123,
                "node", "vm02",
                "command", _request.get("command")
        );
        return this;
    }

    @Override
    protected Set<String> getRequiredParams() {
        return __REQUIRED_PARMS;
    }

    @Override
    protected Set<String> getOptionalParams() {
        return __OPTIONAL_PARMS;
    }

    @Override
    protected Message.Logger logger() {
        return Main.logger();
    }
}
