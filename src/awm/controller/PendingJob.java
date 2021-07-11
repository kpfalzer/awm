package awm.controller;

import awm.controller.model.LicenseReq;

import static gblibx.Util.castobj;

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
    }

    public final String user, host, command;
    public final int memKB, ncore, priority;
    public final LicenseReq.Spec[] license;

    @Override
    public int compareTo(Object o) {
        final PendingJob x = castobj(o);
        return (priority == x.priority) ? 0 : ((priority < x.priority) ? -1 : 1);
    }
}
