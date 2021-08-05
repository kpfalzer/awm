package awm.controller.subcommand;

import awm.Logger;
import awm.RequestHandler;
import awm.controller.Main;
import awm.controller.PendingJob;
import awm.controller.model.Job;
import awm.controller.model.LicenseReq;

import java.io.IOException;
import java.util.Set;

import static gblibx.Util.invariant;
import static gblibx.Util.supplyIfNull;
import static gblibx.Util.toSet;

/**
 * Run request.
 * Example: curl --header "Content-Type: application/json" --request POST --data '{"user":"kpfalzer","host":"vm01","command":"hostname"}' http://localhost:6817/subcmd/run
 */
public class Run {
    private Run(PendingJob job) {
        __job = job;
    }

    private Run submit() {
        //todo: add to dbase, queue...
        return addToDbase()
                .addToQueue();
    }

    private Run addToDbase() {
        int jobId = Job.create(__job);
        invariant(0 < jobId);//todo
        __job.setJobId(jobId);
        return this;
    }

    private Run addToQueue() {
        final int n = Main.addJob(__job);
        Main.logger().debug("QUEUE-1", __job.toString(), n);
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
    private static final Set<String> __OPTIONAL_PARMS = toSet("license", "jobName");

    private static class Handler extends RequestHandler {
        public PendingJob toJob() {
            return new PendingJob(
                    get("user"),
                    get("host"),
                    get("command"),
                    get("jobName"),
                    get("mem"),
                    get("ncore"),
                    get("priority"),
                    LicenseReq.Spec.create(get("license"))
            );
        }

        @Override
        protected RequestHandler respond() throws IOException {
            //TODO: add job to dbase, queue, ...
            final Run run = new Run(toJob()).submit();
            {
                //fake latency to respond
                if (true) try {
                    Thread.sleep(1000/*ms/s*/ * 60/*s/min*/ * 1/*min*/);
                } catch (InterruptedException e) {
                    invariant(false);
                }
            }
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
        protected Logger logger() {
            return Main.logger();
        }
    }
}
