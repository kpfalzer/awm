package awm.controller.model;

import gblibx.Util;
import jmvc.model.Table;

import java.util.Map;

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

    public static class Spec extends Util.Pair<String, Integer> {
        public Spec(String lic, Integer cnt) {
            super(lic, cnt);
        }

        public Map<String,Object> toMap() {
            return Util.toMap("license", v1, "count", v2);
        }
    }
}
