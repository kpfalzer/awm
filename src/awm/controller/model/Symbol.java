package awm.controller.model;

import jmvc.model.QueryResult;
import jmvc.model.Table;

import static gblibx.Util.castobj;
import static gblibx.Util.invariant;
import static gblibx.Util.squotify;

public class Symbol {
    public static int getId(Object value, EType type) {
        return findOrInsert(Database.Symbols(), value, type);
    }

    public static int getUser(String user) {
        return getId(user, EType.USER);
    }

    public static int getHost(String host) {
        return getId(host, EType.HOST);
    }

    public static int getLicense(String license) {
        return getId(license, EType.LICENSE);
    }

    public enum EType {
        USER("U"),
        LICENSE("L"),
        HOST("H");

        EType(String code) {
            invariant(1 == code.length());
            this.code = code;
        }

        public final String code;
    }

    enum ESymbol implements Table.ColSpec {
        ID("? INT NOT NULL GENERATED ALWAYS AS IDENTITY; PRIMARY KEY (?)"),
        //TYPE and VALUE are SQL reserved words
        XTYPE("? VARCHAR(1) NOT NULL"),
        XVALUE("? VARCHAR(1024) NOT NULL");

        ESymbol(String spec) {
            _spec = spec;
        }

        final String _spec;

        @Override
        public String getSpec() {
            return _spec;
        }
    }

    public static int findOrInsert(Table<ESymbol> table, Object val, EType xtype) {
        int valId = -1;
        final String sval = squotify(castobj(val));
        final String type = squotify(xtype.code);
        synchronized (table) {
            final QueryResult qr = table
                    .select("ID")
                    .whereEQ("XVALUE", sval, "XTYPE", type)
                    .execute();
            if (0 < qr.nrows()) {
                valId = castobj(table.getVal(qr.rows()[0], ESymbol.ID));
            } else {
                valId = table.insertRow(ESymbol.XVALUE, val, ESymbol.XTYPE, xtype.code);
            }
        }
        return valId;
    }

}
