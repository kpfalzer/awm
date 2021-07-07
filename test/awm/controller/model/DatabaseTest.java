package awm.controller.model;

import jmvc.model.Table;
import jmvc.model.sql.SqlQueryResult;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static awm.controller.model.Database.Jobs;
import static awm.controller.model.Database.MemReqs;
import static awm.controller.model.Database.NCoreReqs;
import static awm.controller.model.Database.Symbols.getHost;
import static awm.controller.model.Database.Symbols.getUser;
import static awm.controller.model.MemReq.EMemReq;
import static awm.controller.model.NCoreReq.ENCoreReq;
import static awm.controller.model.Job.EJob;
import static gblibx.Util.castobj;
import static jmvc.Util.nowTimestamp;
import static jmvc.model.Table.valueOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseTest {

    @Test
    void runs() {
        int jobId = 0;
        {
            Table jobs = Jobs();
            jobId = jobs.insertRow(
                    EJob.USER_SID, getUser("kpfalzer"),
                    EJob.HOST_SID, getHost("vm01"),
                    EJob.COMMAND, "command-to-execute arg1,arg2",
                    EJob.CREATED_AT, nowTimestamp()
            );
            assertTrue(0 < jobId);
            SqlQueryResult row = castobj(jobs.findById(jobId));
            assertTrue(1 == row.nrows());
            assertTrue(Integer.valueOf(jobId).equals(valueOf(row.rows()[0], EJob.ID)));
        }
        {
            Table memReqs = MemReqs();
            int memKb = new Random().nextInt(Integer.MAX_VALUE);
            int mid = memReqs.insertRow(
                    EMemReq.JOB_ID, jobId,
                    EMemReq.MEM_KB, memKb
            );
            assertTrue(0 < mid);
            SqlQueryResult row = castobj(memReqs.findById(mid));
            assertTrue(1 == row.nrows());
            assertTrue(Integer.valueOf(memKb).equals(valueOf(row.rows()[0], EMemReq.MEM_KB)));
        }
        {
            Table ncoreReqs = NCoreReqs();
            int ncore = new Random().nextInt(8);
            int mid = ncoreReqs.insertRow(
                    ENCoreReq.JOB_ID, jobId,
                    ENCoreReq.NCORE, ncore
            );
            assertTrue(0 < mid);
            SqlQueryResult row = castobj(ncoreReqs.findById(mid));
            assertTrue(1 == row.nrows());
            assertTrue(Integer.valueOf(ncore).equals(valueOf(row.rows()[0], ENCoreReq.NCORE)));
        }
    }
}