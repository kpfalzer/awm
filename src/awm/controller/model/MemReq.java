package awm.controller.model;

import jmvc.model.Table;

public class MemReq {
    enum EMemReq implements Table.ColSpec {
        ID("? INT NOT NULL GENERATED ALWAYS AS IDENTITY; PRIMARY KEY (?)"),
        JOB_ID("? INT REFERENCES JOBS (ID)"),
        MEM_KB("? INT NOT NULL");

        EMemReq(String spec) {
            _spec = spec;
        }

        final String _spec;

        @Override
        public String getSpec() {
            return _spec;
        }
    }
}
