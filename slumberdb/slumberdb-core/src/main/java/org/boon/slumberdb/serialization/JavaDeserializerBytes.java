package org.boon.slumberdb.serialization;

import org.boon.Exceptions;
import org.boon.core.Function;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;

/**
 * Created by Richard on 4/9/14.
 */
public class JavaDeserializerBytes<V> implements Function<byte[], V> {
    @Override
    public V apply(byte[] value) {
        V v = null;
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(value);
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            v = (V) objectInputStream.readObject();
        } catch (Exception e) {
            Exceptions.handle(e);
        }
        return v;

    }
}
