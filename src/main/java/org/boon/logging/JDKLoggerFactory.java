package org.boon.logging;

public class JDKLoggerFactory implements LoggerFactory {

    public LoggerDelegate logger(final String name) {
        return new JDKLogger(name);
    }

    @Override
    public LoggerDelegate apply(String logName) {
        return logger(logName);
    }
}
