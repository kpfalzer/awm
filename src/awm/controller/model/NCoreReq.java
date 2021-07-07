package awm.controller.model;

import jmvc.model.Table;

public class NCoreReq {
    enum ENCoreReq implements Table.ColSpec {
        ID("? INT NOT NULL GENERATED ALWAYS AS IDENTITY; PRIMARY KEY (?)"),
        JOB_ID("? INT REFERENCES JOBS (ID)"),
        NCORE("? INT NOT NULL");

        ENCoreReq(String spec) {
            _spec = spec;
        }

        final String _spec;

        @Override
        public String getSpec() {
            return _spec;
        }
    }
}
