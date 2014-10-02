package org.boon.slumberdb.serialization;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import org.boon.core.Function;

import java.io.ByteArrayOutputStream;

/**
 * Created by Richard on 4/9/14.
 */
public class KryoObjectToByteArrayConverter<T> implements Function<T, byte[]> {
    private final Kryo kryo;
    private final Class<T> type;

    public KryoObjectToByteArrayConverter(Kryo kryo, Class<T> type) {
        this.kryo = kryo;
        this.type = type;
    }

    @Override
    public byte[] apply(T value) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output streamOut = new Output(baos);
        this.kryo.writeObject(streamOut, value);
        streamOut.close();
        return baos.toByteArray();
    }
}
