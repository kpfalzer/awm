package awm.controller;

import awm.controller.resource.Node;

import java.time.LocalDateTime;

import static gblibx.Util.invariant;
import static gblibx.Util.now;
import static gblibx.Util.secToNow;

public class RunningJob extends Job {
    public RunningJob(Job job, Node node) {
        super(job);
        __node = node;
    }

    public long getPID() {
        return __pid;
    }

    public Node getNode() {
        return __node;
    }

    public LocalDateTime getStartedAt() {
        return __startedAt;
    }

    public double elapsedSecs() {
        return secToNow(getStartedAt());
    }

    public RunningJob setPID(long pid) {
        invariant(0 > getPID());
        __pid = pid;
        return this;
    }

    private final LocalDateTime __startedAt = now();
    private final Node __node;
    private long __pid = -1;
}
