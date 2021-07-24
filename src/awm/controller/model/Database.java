package awm.controller.model;

import awm.AwmProps;
import jmvc.Config;
import jmvc.model.Table;
import jmvc.model.sql.SqlDatabase;
import jmvc.model.sql.SqlTable;

import static awm.controller.model.Symbol.ESymbol;
import static awm.controller.model.Job.EJob;
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
    }

    public static Table<EJob> Jobs() {
        return __theOne.__jobs;
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
                EJob.class,
                //additional indexes:
                EJob.USER_SID, EJob.JOB_NAME
        );
        //refences to Jobs.id
        __licenseReqs = SqlTable.create(
                __dbase,
                "licenseReqs",
                ELicenseReq.class,
                //additional indexes:
                ELicenseReq.LICENSE_SID
        );
        return this;
    }

    private SqlDatabase __dbase;
    private Table<ESymbol> __symbols;
    private Table<EJob> __jobs;
    private Table<ELicenseReq> __licenseReqs;
}
