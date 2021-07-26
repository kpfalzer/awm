package awm.node;

import java.time.LocalDateTime;

import static gblibx.Util.now;

public class RunningJob {
    public RunningJob(long jobID, long pid) {
        this.jobID = jobID;
        this.pid = pid;
    }

    public final long jobID, pid;
    public final LocalDateTime startedAt = now();

}
