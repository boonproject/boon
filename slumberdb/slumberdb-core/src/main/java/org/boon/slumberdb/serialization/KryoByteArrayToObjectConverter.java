package org.boon.slumberdb.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import org.boon.Exceptions;
import org.boon.core.Function;

import java.io.ByteArrayInputStream;

/**
 * Created by Richard on 4/9/14.
 */
public class KryoByteArrayToObjectConverter<T> implements Function<byte[], T> {
    /**
     * Kryo valueObjectConverter/valueSerializer
     */
    private final Kryo kryo;

    /**
     * Type of class you are reading/writing.
     */
    private final Class<T> type;

    public KryoByteArrayToObjectConverter(Kryo kryo, Class<T> type) {
        this.kryo = kryo;
        this.type = type;
    }

    @Override
    public T apply(byte[] value) {
        if (value == null || value.length == 0) {
            return null;
        }
        T v = null;
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(value);
        try {
            Input input = new Input(inputStream);
            v = kryo.readObject(input, type);
            input.close();
        } catch (Exception e) {
            Exceptions.handle(e);
        }
        return v;

    }
}
