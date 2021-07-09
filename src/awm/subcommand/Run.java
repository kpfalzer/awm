package awm.subcommand;

import awm.AwmProps;
import awm.controller.model.LicenseReq;
import gblibx.Util;
import gblibx.yaap.Group;
import gblibx.yaap.Option;
import gblibx.yaap.Parser;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static awm.Util.addHostName;
import static awm.Util.addUserName;
import static awm.Util.convert;
import static awm.Util.getUserName;
import static gblibx.Util.castobj;
import static gblibx.Util.isNonNull;
import static gblibx.Util.join;
import static gblibx.Util.supplyIfNull;
import static java.util.Objects.isNull;

public class Run {
    /**
     * Execute run.
     * 1) send request to controller.
     * 2) Could take LONG time for controller to respond.
     * 3) controller responds with run parameters (and node).
     * 3.1) or details why cannot be run.
     * 4) collect response and send request to (designated) node.
     */
    private Run execute() {
        return controllerRequest().checkControllerResponse().nodeRequest();
    }

    private Run controllerRequest() {
        __cntlResponse = ControllerRequest.request(CMD, __reqParams);
        return this;
    }

    private Run checkControllerResponse() {
        //todo: check __runParams;
        System.err.printf("DEBUG: checkControllerResponse: node=%s\n", __cntlResponse.get("node"));
        addUserName(__cntlResponse);
        return this;
    }

    private static final Consumer<String>[] __READERS = new Consumer[]{
            System.out::println, System.err::println
    };

    private Run nodeRequest() {
        //todo: perhaps add in mem, timeout, ...
        //NOTE: mem could be ulimit?
        final String toHost = castobj(__cntlResponse.get("node"));
        NodeRequest.request(CMD, toHost, __cntlResponse, __READERS);
        return this;
    }

    private final Map<String, Object> __reqParams;
    private Map<String, Object> __cntlResponse;

    private Run(Options opts) {
        __reqParams = opts.toMap();
    }

    public static void execute(String[] argv) {
        final Options opts = __checkArguments(false, argv);
        final Run run = new Run(opts).execute();
        boolean todo = true;
    }

    public static final String CMD = "run";
    private static String __USER = getUserName();

    /**
     * Setup Run (sub)command options.
     *
     * @return option parser ({@link Parser})
     */
    private static Parser setupOptions() {
        __MEM = supplyIfNull(__MEM,
                () -> Integer.valueOf(AwmProps.getProperty("awm.subcommand.run.MEM", "1024")));
        __NCORE = supplyIfNull(__NCORE,
                () -> Integer.valueOf(AwmProps.getProperty("awm.subcommand.run.NCORE", "1")));
        final Parser parser = new Parser("awm-run", "Run command using AWM");
        {
            Group reqd = new Group("Requirements", new Option[]{
                    new Option('m', "mem", null, __MEM,
                            "(min) required memory: K|M|G (e.g. '10M')", '?',
                            (arg) -> {
                                final Pattern p = Pattern.compile("(\\d+)([K|M|G])");
                                final Matcher m = p.matcher(castobj(arg));
                                if (!m.matches()) {
                                    return new Util.Pair(null, arg);
                                }
                                int mult = 1;
                                switch (m.group(2).charAt(0)) {
                                    case 'K':
                                        mult = 1;
                                        break;
                                    case 'M':
                                        mult = 1024;
                                        break;
                                    case 'G':
                                        mult = 1024 ^ 2;
                                        break;
                                    default:
                                }
                                final int memKB = Integer.parseInt(m.group(1)) * mult;
                                return new Util.Pair(memKB, null);
                            }),
                    new Option('n', "ncore", null, __NCORE,
                            "(min) ncore(s)", '?',
                            (arg) -> {
                                Object ncore = null;
                                Integer v = convert(arg, (s) -> Integer.parseInt(castobj(s)));
                                if (isNonNull(v) && (0 <= v)) ncore = 0;
                                return new Util.Pair(ncore, (isNull(ncore)) ? arg : null);
                            }),
                    new Option('l', "license", null, null,
                            "license requirement (e.g. 'lic1/n')", '*',
                            (arg) -> {
                                LicenseReq.Spec licReq = null;
                                final String[] lv = (Util.<String>castobj(arg)).split("\\s*/\\s*");
                                if (2 == lv.length) {
                                    Integer n = convert(lv[1], (s) -> Integer.parseInt(castobj(s)));
                                    if (isNonNull(n) && (0 < n)) licReq = new LicenseReq.Spec(lv[0], n);
                                }
                                return new Util.Pair(licReq, isNull(licReq) ? arg : null);
                            })
            });
            parser.add(reqd);
        }
        return parser.addPosArgUsage("command [arg]...", "Command (and optional arg) to run");
    }

    private static void error(boolean exitOnErr, String e) {
        if (isNonNull(e)) System.err.printf("awm-run: %s\n", e);
        if (exitOnErr) System.exit(1);
    }

    private static void error(boolean exitOnErr) {
        error(exitOnErr, null);
    }

    private static Options __checkArguments(String[] args) {
        return __checkArguments(true, args);
    }

    private static Options __checkArguments(boolean exitOnErr, String[] args) {
        try {
            final Parser parser = setupOptions();
            boolean ok = parser.parse(args);
            if (!ok) error(exitOnErr);
            if (!parser.hasPosArgs()) {
                error(exitOnErr, "missing 'command [arg]...'");
            }
            if (ok) return new Options(parser);
        } catch (Exception e) {
            error(exitOnErr, e.getMessage());
        }
        return null;
    }

    public static Options checkArguments(String... args) {
        return __checkArguments(false, args);
    }

    public static void main(String[] argv) {
        execute(argv);
    }

    public static class Options {
        private Options(Parser xopts) {
            this.opts = xopts;
            memKB = opts.getValue("mem").getOptAsInteger();
            ncore = opts.getValue("ncore").getOptAsInteger();
            licenses = opts.ifhasOption("license", (opt) -> opt.getOptsAsT());
        }

        private Map<String, Object> toMap() {
            Map<String, Object> map = Util.toMap(
                    "mem", memKB, "ncore", ncore, "command", join(opts.getPosArgs()));
            map = addHostName(addUserName(map));
            if (isNonNull(licenses))
                map.put("license", Arrays.stream(licenses)
                        .map(l -> l.toMap()).toArray());
            return map;
        }

        public String toString() {
            return (new JSONObject(toMap())).toString();
        }

        final Parser opts;
        final int memKB;
        final int ncore;
        final LicenseReq.Spec[] licenses;
    }

    private static Integer __MEM = null;
    private static Integer __NCORE = null;
}
