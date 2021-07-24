package awm.controller;

import awm.controller.model.LicenseReq;

import static gblibx.Util.castobj;

public class PendingJob extends Job implements Comparable {
    public PendingJob(
            String user,
            String host,
            String command,
            String jobName,
            int memKB, int ncore, int priority,
            LicenseReq.Spec[] license
    ) {
        super(user, host, command, jobName, memKB, ncore, priority, license);
    }

    @Override
    public int compareTo(Object o) {
        final PendingJob x = castobj(o);
        return (priority == x.priority) ? 0 : ((priority < x.priority) ? -1 : 1);
    }
}
