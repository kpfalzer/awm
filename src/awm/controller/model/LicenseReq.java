package awm.controller.model;

import gblibx.Util;
import jmvc.model.Table;

import java.util.Map;

import static gblibx.Util.castobj;
import static java.util.Objects.isNull;

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
        public String name() {
            return super.v1;
        }

        public int count() {
            return super.v2;
        }

        public Spec(String lic, Integer cnt) {
            super(lic, cnt);
        }

        public Map<String, Object> toMap() {
            return Util.toMap("license", v1, "count", v2);
        }

        public static Spec[] create(Object[] ary) {
            if (isNull(ary)) return null;
            Spec[] specs = new Spec[ary.length];
            for (int i = 0; i < ary.length; i++) {
                Map<String, Object> e = castobj(ary[i]);
                specs[i] = new Spec(castobj(e.get("license")), castobj(e.get("count")));
            }
            return specs;
        }

        public String toString() {
            return String.format("%s:%d", super.v1,super.v2);
        }
    }
}
