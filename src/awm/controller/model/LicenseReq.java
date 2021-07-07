package awm.controller.model;

import jmvc.model.Table;

public class LicenseReq {
    enum ELicenseReq implements Table.ColSpec {
        ID("? INT NOT NULL GENERATED ALWAYS AS IDENTITY; PRIMARY KEY (?)"),
        JOB_ID("? INT REFERENCES JOBS (ID)"),
        LICENSE_SID("? INT REFERENCES SYMBOLS (ID)"),
        COUNT("? INT NOT NULL");

        ELicenseReq(String spec) {
            _spec = spec;
        }

        final String _spec;

        @Override
        public String getSpec() {
            return _spec;
        }
    }
}
