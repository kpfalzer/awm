package awm.controller;

import java.util.HashMap;

public class RunningJobs extends HashMap<Long, RunningJob> {
    public RunningJobs addJob(RunningJob job) {
        super.put(job.getJobId(), job);
        return this;
    }
}
