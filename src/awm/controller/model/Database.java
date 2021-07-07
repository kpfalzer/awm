package awm.controller.model;

import awm.AwmProps;
import jmvc.Config;
import jmvc.model.Table;
import jmvc.model.sql.SqlDatabase;
import jmvc.model.sql.SqlTable;

import static awm.controller.model.Symbol.ESymbol;
import static awm.controller.model.Job.EJob;
import static awm.controller.model.MemReq.EMemReq;
import static awm.controller.model.NCoreReq.ENCoreReq;
import static awm.controller.model.LicenseReq.ELicenseReq;
import static gblibx.Util.downcast;
import static gblibx.Util.invariant;
import static java.util.Objects.isNull;

public class Database {
    static final int VARCHAR_MAX = 32650;

    public static String getVarCharColSpec(int size) {
        return String.format("? VARCHAR(%d) NOT NULL", size);
    }

    public static String getMaxVarCharColSpec() {
        return getVarCharColSpec(VARCHAR_MAX);
    }

    public static Table<ESymbol> Symbols() {
        return __theOne.__symbols;
    }

    /**
     * Helper for dealing with symbols.
     */
    public static class Symbols {
        public static int getId(Object value, Symbol.EType type) {
            return Symbol.findOrInsert(Symbols(), value, type);
        }
        public static int getUser(String user) {
            return getId(user, Symbol.EType.USER);
        }
        public static int getHost(String host) {
            return getId(host, Symbol.EType.HOST);
        }
        public static int getLicense(String license) {
            return getId(license, Symbol.EType.LICENSE);
        }
    }

    public static Table<EJob> Jobs() {
        return __theOne.__jobs;
    }

    public static Table<EMemReq> MemReqs() {
        return __theOne.__memReqs;
    }

    public static Table<ENCoreReq> NCoreReqs() {
        return __theOne.__ncoreReqs;
    }

    public static Table<ELicenseReq> LicenseReqs() {
        return __theOne.__licenseReqs;
    }

    private static Database __theOne = new Database();

    private Database() {
        invariant(isNull(__theOne));
        initialize();
        __theOne = this;
    }

    private Database initialize() {
        return connect().setupTables();
    }

    private Database connect() {
        final Config config = jmvc.model.Database.getConfig(
                AwmProps.getProperty(
                        "awm.controller.model.URL", "jdbc:derby://localhost:3308/awmDb"),
                AwmProps.getProperty(
                        "awm.controller.model.NAME", "awmDb"),
                AwmProps.getProperty(
                        "awm.controller.model.USER", "awmDbUser"),
                AwmProps.getProperty(
                        "awm.controller.model.PASSWD", "awmDbPasswd")
        );
        __dbase = downcast(jmvc.model.Database.connect(config));
        return this;
    }

    /**
     * Create tables in order: referent before referred to.
     *
     * @return
     */
    private Database setupTables() {
        __symbols = SqlTable.create(
                __dbase,
                "symbols",
                ESymbol.class,
                //additional indexes:
                "XTYPE", "XVALUE"
        );
        __jobs = SqlTable.create(
                __dbase,
                "jobs",
                EJob.class
        );
        //refences to Jobs.id
        __memReqs = SqlTable.create(
                __dbase,
                "memReqs",
                EMemReq.class
        );
        //refences to Jobs.id
        __ncoreReqs = SqlTable.create(
                __dbase,
                "ncoreReqs",
                ENCoreReq.class
        );
        //refences to Jobs.id
        __licenseReqs = SqlTable.create(
                __dbase,
                "licenseReqs",
                ELicenseReq.class
        );
        return this;
    }

    private SqlDatabase __dbase;
    private Table<ESymbol> __symbols;
    private Table<EJob> __jobs;
    private Table<EMemReq> __memReqs;
    private Table<ENCoreReq> __ncoreReqs;
    private Table<ELicenseReq> __licenseReqs;
}
