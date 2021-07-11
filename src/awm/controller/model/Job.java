package awm.controller.model;

import jmvc.model.Table;

public class Job {
    private static final String __CREATE = "INSERT INTO JOBS"
            + " (USER_SID,HOST_SID,COMMAND,MEM_KB,NCORE,PRIORITY,CREATED_AT)"
            + " VALUES (%d,)";
    public static int create() {
        int jobId = 0;
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
