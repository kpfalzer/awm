package awm.controller;

import awm.controller.model.LicenseReq;

import java.util.Arrays;

import static gblibx.Util.castobj;
import static gblibx.Util.isNonNull;
import static jmvc.Util.nowTimestamp;

public class PendingJob implements Comparable {
    public PendingJob(
            String user,
            String host,
            String command,
            int memKB, int ncore, int priority,
            LicenseReq.Spec[] license
    ) {
        this.user = user;
        this.host = host;
        this.command = command;
        this.memKB = memKB;
        this.ncore = ncore;
        this.priority = priority;
        this.license = license;
        this.createdAt = nowTimestamp();
    }

    public final String user, host, command, createdAt;
    public final int memKB, ncore, priority;
    public final LicenseReq.Spec[] license;
    public int jobId = -1;

    @Override
    public String toString() {
        return "PendingJob{" +
                "user='" + user + '\'' +
                ", host='" + host + '\'' +
                ", command='" + command + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", memKB=" + memKB +
                ", ncore=" + ncore +
                ", priority=" + priority +
                ", license=" + (isNonNull(license)?Arrays.toString(license):"-") +
                ", jobId=" + jobId +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        final PendingJob x = castobj(o);
        return (priority == x.priority) ? 0 : ((priority < x.priority) ? -1 : 1);
    }
}
