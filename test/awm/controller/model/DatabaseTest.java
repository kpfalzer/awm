package awm.controller.model;

import jmvc.model.Table;
import jmvc.model.sql.SqlQueryResult;
import org.junit.jupiter.api.Test;

import static awm.controller.model.Database.Jobs;
import static awm.controller.model.Database.LicenseReqs;
import static awm.controller.model.Symbol.getHost;
import static awm.controller.model.Symbol.getLicense;
import static awm.controller.model.Symbol.getUser;
import static awm.controller.model.Job.EJob;
import static awm.controller.model.LicenseReq.ELicenseReq;
import static gblibx.Util.castobj;
import static jmvc.Util.nowTimestamp;
import static jmvc.model.Table.valueOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseTest {

    @Test
    void runs() {
        int jobId = 0;
        if (true) {
            Table jobs = Jobs();
            jobId = jobs.insertRow(
                    EJob.USER_SID, getUser("kpfalzer"),
                    EJob.HOST_SID, getHost("vm01"),
                    EJob.COMMAND, "command-to-execute arg1,arg2",
                    EJob.MEM_KB, 123,
                    EJob.NCORE, 4,
                    EJob.CREATED_AT, nowTimestamp(),
                    EJob.PRIORITY, 0
            );
            assertTrue(0 < jobId);
            SqlQueryResult row = castobj(jobs.findById(jobId));
            assertTrue(1 == row.nrows());
            assertTrue(Integer.valueOf(jobId).equals(valueOf(row.rows()[0], EJob.ID)));
        }
        {
            Table lics = LicenseReqs();
            int id = lics.insertRow(
                    ELicenseReq.JOB_ID, jobId,
                    ELicenseReq.LICENSE_SID, getLicense("lic1"),
                    ELicenseReq.COUNT, 2
            );
        }
    }
}