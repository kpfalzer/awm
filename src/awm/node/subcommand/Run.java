package awm.node.subcommand;

import gblibx.RunCmd;

import java.io.IOException;

public class Run {
    public static class NodeRun {
        public static void run(String user, String command) {
            new NodeRun(user, command);
        }

        public NodeRun(String user, String command) {
            __builder = new ProcessBuilder(__RUNUSER, "-u", user, command);
            __builder.environment().put("AWM_SERVER", "value-for-server");
            __builder.redirectErrorStream(true);
            __builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            try {
                final Process p = __builder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private ProcessBuilder __builder;
        private static final String __RUNUSER = "/usr/sbin/runuser";

        public static void main(String[] argv) {
            //NodeRun.run(argv[0], argv[1]);
            RunCmd.runCommand(String.format("%s -u %s %s", __RUNUSER, argv[0], argv[1]));
        }
    }

}
