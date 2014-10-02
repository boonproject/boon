package org.boon.slumberdb.serialization;

import org.boon.core.Function;

import java.nio.charset.StandardCharsets;

/**
 * Created by Richard on 4/9/14.
 */
public class ByteArrayToStringConverter implements Function<byte[], String> {
    @Override
    public String apply(byte[] bytes) {

        return new String(bytes, StandardCharsets.UTF_8);
    }
}
