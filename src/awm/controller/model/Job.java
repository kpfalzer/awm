package awm.controller.model;

import jmvc.model.Table;

public class Job {
    enum EJob implements Table.ColSpec {
        ID("? INT NOT NULL GENERATED ALWAYS AS IDENTITY; PRIMARY KEY (?)"),
        USER_SID("? INT REFERENCES SYMBOLS (ID)"),
        HOST_SID("? INT REFERENCES SYMBOLS (ID)"),
        COMMAND(Database.getMaxVarCharColSpec()),
        MEM_KB("? INT NOT NULL"),
        NCORE("? INT NOT NULL"),
        CREATED_AT("? TIMESTAMP NOT NULL");

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
