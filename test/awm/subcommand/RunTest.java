package awm.subcommand;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class RunTest {

    @Test
    void checkArguments() {
        String json = null;
        {
            final Run.Options opts = Run.checkArguments("-l", "lic1/45", "hostname");
            assertNotNull(opts);
            json = opts.toString();
        }
        {
            final Run.Options opts = Run.checkArguments("-x", "hostname");
            assertNull(opts);
        }
        {
            final Run.Options opts = Run.checkArguments(
                    "-m", "999M", "-l", "lic2/3", "-l", "lic3/8", "cmd", "arg1");
            assertNotNull(opts);
            json = opts.toString();
        }
        {
            final Run.Options opts = Run.checkArguments("-p", "100");
            assertNull(opts);
            //json = opts.toString();
        }
        {
            final Run.Options opts = Run.checkArguments("-p", "-666");
            assertNull(opts);
            //json = opts.toString();
        }
        boolean stop = true;
    }

}
