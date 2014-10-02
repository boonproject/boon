package org.boon.slumberdb.serialization;

import org.boon.Exceptions;
import org.boon.core.Function;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class JavaSerializerBytes<V> implements Function<V, byte[]> {
    @Override
    public byte[] apply(V value) {


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream streamOut = new ObjectOutputStream(baos);
            streamOut.writeObject(value);
        } catch (IOException e) {
            Exceptions.handle(e);
        }

        return baos.toByteArray();

    }
}
