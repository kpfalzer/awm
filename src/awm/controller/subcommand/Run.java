package awm.controller.subcommand;

import awm.Message;
import awm.RequestHandler;
import awm.controller.Main;
import awm.controller.PendingJob;
import awm.controller.model.LicenseReq;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

import static awm.Util.throwException;
import static gblibx.Util.supplyIfNull;
import static gblibx.Util.toSet;

/**
 * Run request.
 * Required parameters: user, command, host.
 * Optional parameters: mem, ncore, license.
 * <p>
 * Example: curl --header "Content-Type: application/json" --request POST --data '{"user":"kpfalzer","host":"vm01","command":"hostname"}' http://localhost:6817/subcmd/run
 */
public class Run {
    private Run(PendingJob job) {
        __job = job;
    }

    private Run submit() {
        //todo: add to dbase, queue...
        return this;
    }

    private final PendingJob __job;

    public static class Delegate extends jmvc.server.RequestHandler.Delegate {

        @Override
        public jmvc.server.RequestHandler create() {
            return new Run.Handler();
        }
    }

    public static Delegate handlerFactory() {
        return __delegator = supplyIfNull(__delegator, () -> new Delegate());
    }

    private static Delegate __delegator = null;

    private static final Set<String> __REQUIRED_PARMS =
            toSet("user", "command", "host", "mem", "ncore", "priority");
    private static final Set<String> __OPTIONAL_PARMS = toSet("license");

    private static class Handler extends RequestHandler {
        public PendingJob toJob() {
            return new PendingJob(
                    get("user"),
                    get("host"),
                    get("command"),
                    get("mem"),
                    get("ncore"),
                    get("priority"),
                    LicenseReq.Spec.create(get("license"))
            );
        }

        /**
         * Return JSON packet with parameters: status, jobid, node, command.
         * status == "0" on success; else error details (as status value).
         */
        void xrespond() {
            JSONObject json = new JSONObject();
            json.put("jobid", 123)
                    .put("node", "vm02")
                    .put("command", _request.get("command"))
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
            //TODO: add job to dbase, queue, ...
            final Run run = new Run(toJob()).submit();
            //todo...
            sendResponse(
                    "status", "0",
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
}
