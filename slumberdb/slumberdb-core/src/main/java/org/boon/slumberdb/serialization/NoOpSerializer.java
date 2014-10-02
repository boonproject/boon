package org.boon.slumberdb.serialization;

import org.boon.core.Function;

/**
 * Created by Richard on 4/9/14.
 */
public class NoOpSerializer<IN, OUT> implements Function<IN, OUT> {
    @Override
    public OUT apply(IN o) {
        return (OUT) o;
    }
}
