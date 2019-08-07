package bfg.recorder;

import java.nio.file.Paths;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecorderFactory {
    private static final Logger log = LoggerFactory.getLogger(RecorderFactory.class);

    private static final String PROP_DIRECTORY = "run.directory";

    public static Recorder getInstance(Properties props) {
	return new Recorder(Paths.get(props.getProperty(PROP_DIRECTORY)));
    }
}
