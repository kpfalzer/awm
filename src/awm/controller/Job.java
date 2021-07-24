package awm.controller;

import awm.controller.model.LicenseReq;

import java.util.Arrays;

import static gblibx.Util.invariant;
import static gblibx.Util.isNonNull;
import static gblibx.Util.supplyIfNull;
import static jmvc.Util.nowTimestamp;

public class Job {
    public Job(
            String user,
            String host,
            String command,
            String jobName,
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
        this.jobName = jobName;
    }

    public Job(Job job) {
        this.user = job.user;
        this.host = job.host;
        this.command = job.command;
        this.memKB = job.memKB;
        this.ncore = job.ncore;
        this.priority = job.priority;
        this.license = job.license;
        this.createdAt = job.createdAt;
        this.jobName = job.jobName;
    }
    public Job setJobId(int id) {
        invariant(0 > getJobId());
        __jobId = id;
        return this;
    }

    public long getJobId() {
        return __jobId;
    }

    public final String user, host, command, createdAt, jobName;
    public final int memKB, ncore, priority;
    public final LicenseReq.Spec[] license;
    private long __jobId = -1;

    @Override
    public String toString() {
        return "PendingJob{" +
                "user='" + user + '\'' +
                ", host='" + host + '\'' +
                ", command='" + command + '\'' +
                ", jobName='" + (supplyIfNull(jobName, () -> "")) + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", memKB=" + memKB +
                ", ncore=" + ncore +
                ", priority=" + priority +
                ", license=" + (isNonNull(license) ? Arrays.toString(license) : "-") +
                ", jobId=" + getJobId() +
                '}';
    }
}
