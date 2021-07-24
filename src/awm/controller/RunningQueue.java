package awm.controller;

import java.util.HashMap;

public class RunningQueue extends HashMap<Long, RunningJob> {
    public RunningQueue addJob(RunningJob job) {
        super.put(job.getJobId(), job);
        return this;
    }
}
