package awm.node;

import java.util.HashMap;

/**
 * RunningJob by jobid.
 */
public class RunningJobs extends HashMap<Long, RunningJob> {
    public RunningJobs addJob(long jobId, long pid) {
        super.put(jobId, new RunningJob(jobId, pid));
        return this;
    }
}
