package awm;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static awm.Util.fatal;
import static gblibx.Util.isNonNull;

/**
 * Properties used throughout AWM.
 * System/cmdline prop value take precedence over any in optional file (awm.properties).
 */
public class AwmProps {
    public static String getProperty(String key, String dflt) {
        return (System.getProperties().containsKey(key))
                ? System.getProperty(key)
                : props().getProperty(key, dflt);
    }

    private static Properties props() {
        return __theOne.__props;
    }

    private Properties __props = new Properties();
    private static final AwmProps __theOne = new AwmProps();

    private AwmProps() {
        final String cfgFname = System.getProperty("awm.properties");
        if (isNonNull(cfgFname)) {
            try {
                __props.load(new FileInputStream(cfgFname));
            } catch (IOException e) {
                fatal(e);
            }
        }
    }
}
