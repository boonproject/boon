package org.boon.slumberdb.serialization;

import org.boon.core.Function;
import org.boon.json.serializers.impl.JsonSimpleSerializerImpl;

import java.nio.charset.StandardCharsets;

/**
 * Created by Richard on 4/9/14.
 */
public class JsonSerializerBytes<T> implements Function<T, byte[]> {


    /**
     * JSON valueObjectConverter we are using.
     */
    protected JsonSimpleSerializerImpl serializer = new JsonSimpleSerializerImpl();

    public JsonSerializerBytes(Class<T> type) {
    }

    @Override
    public byte[] apply(T t) {
        return serializer.serialize(t).toString().getBytes(StandardCharsets.UTF_8);
    }
}
