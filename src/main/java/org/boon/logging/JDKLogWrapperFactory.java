package org.boon.logging;

public class JDKLogWrapperFactory implements LogWrapperFactory {

    public Logger logger(final String name) {
        return new JDKLogWrapper(name);
    }
}
