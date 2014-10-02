package org.boon.slumberdb.serialization;

import org.boon.core.Function;
import org.boon.json.serializers.impl.JsonSimpleSerializerImpl;

/**
 * Created by Richard on 4/9/14.
 */
public class JsonSerializer<T> implements Function<T, String> {


    /**
     * JSON valueObjectConverter we are using.
     */
    protected JsonSimpleSerializerImpl serializer = new JsonSimpleSerializerImpl();

    public JsonSerializer(Class<T> type) {
    }

    @Override
    public String apply(T t) {
        return serializer.serialize(t).toString();
    }
}
