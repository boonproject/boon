package org.boon.slumberdb.serialization;

import org.boon.core.Function;

import java.nio.charset.StandardCharsets;

/**
 * Created by Richard on 4/9/14.
 */
public class StringToByteArrayConverter implements Function<String, byte[]> {
    @Override
    public byte[] apply(String s) {

        return s.getBytes(StandardCharsets.UTF_8);
    }
}
