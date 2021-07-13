package awm.controller.model;

import awm.controller.PendingJob;
import jmvc.model.Table;

import static awm.controller.model.Database.Jobs;
import static awm.controller.model.Database.LicenseReqs;
import static awm.controller.model.Job.EJob.*;
import static awm.controller.model.LicenseReq.ELicenseReq.*;
import static awm.controller.model.Symbol.getHost;
import static awm.controller.model.Symbol.getLicense;
import static awm.controller.model.Symbol.getUser;
import static gblibx.Util.invariant;
import static gblibx.Util.isNonNull;

public class Job {
    public static int create(PendingJob pj) {
        int jobId = -1;
        jobId = Jobs().insertRow(
                USER_SID, getUser(pj.user),
                HOST_SID,getHost(pj.host),
                COMMAND,pj.command,
                MEM_KB,pj.memKB,
                NCORE,pj.ncore,
                PRIORITY,pj.priority,
                CREATED_AT,pj.createdAt
        );
        if (0 < jobId && isNonNull(pj.license)) {
            for (LicenseReq.Spec lic : pj.license) {
                int id = LicenseReqs().insertRow(
                        JOB_ID, jobId,
                        LICENSE_SID, getLicense(lic.name()),
                        COUNT, lic.count()
                );
                    invariant(0 <= id);
            }
        }
        return jobId;
    }

    enum EJob implements Table.ColSpec {
        ID("? INT NOT NULL GENERATED ALWAYS AS IDENTITY; PRIMARY KEY (?)"),
        USER_SID("? INT REFERENCES SYMBOLS (ID)"),
        HOST_SID("? INT REFERENCES SYMBOLS (ID)"),
        COMMAND(Database.getMaxVarCharColSpec()),
        MEM_KB("? INT NOT NULL"),
        NCORE("? INT NOT NULL"),
        PRIORITY("? INT NOT NULL"),
        CREATED_AT("? TIMESTAMP NOT NULL"),
        STARTED_AT("? TIMESTAMP DEFAULT NULL"),
        STATE("? CHAR(1) DEFAULT 'P'"),
        RUN_HOST_SID("? INT REFERENCES SYMBOLS (ID) DEFAULT NULL"),
        RUN_HOST_PID("? INT DEFAULT -1"),
        RUN_HOST_MEM_KB("? INT DEFAULT -1"),
        RUN_HOST_CPU_TIME("? INT DEFAULT -1");

        EJob(String spec) {
            _spec = spec;
        }

        final String _spec;

        @Override
        public String getSpec() {
            return _spec;
        }
    }
}
