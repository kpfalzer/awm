package awm.subcommand;

import java.util.Map;
import java.util.function.Consumer;

import static awm.Util.*;
import static gblibx.Util.*;

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
        System.err.printf("DEBUG: checkControllerResponse: node=%s\n",__cntlResponse.get("node"));
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

    private Map<String, Object> __reqParams;
    private Map<String, Object> __cntlResponse;

    private Run(String... kvs) {
        __reqParams = toMap((Object[])kvs);
        addUserName(__reqParams);
    }

    public static void execute(String[] argv) {
        invariant(2 <= argv.length);
        final Run run = new Run("command", joinArgs(argv, 1)).execute();
        boolean todo = true;
    }

    public static final String CMD = "run";
    private static String __USER = getUserName();
}
