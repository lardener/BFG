package bfg.replicator;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bfg.mutator.Mutator;

public class ReplicatorFactory {
    private static final Logger log = LoggerFactory.getLogger(ReplicatorFactory.class);

    private static final String PROP_CHILDREN = "replicator.children";

    public static Replicator getInstance(Properties props, Mutator mutator) {
	return new Replicator(Integer.parseInt(props.getProperty(PROP_CHILDREN), 10), mutator);
    }
}
