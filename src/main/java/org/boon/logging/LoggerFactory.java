package org.boon.logging;

import org.boon.core.Function;

public interface LoggerFactory extends Function<String, LoggerDelegate>{
    public LoggerDelegate logger(final String name);
}
