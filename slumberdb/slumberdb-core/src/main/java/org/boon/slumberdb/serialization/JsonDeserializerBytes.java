package org.boon.slumberdb.serialization;

import org.boon.core.Function;
import org.boon.json.JsonParserAndMapper;
import org.boon.json.JsonParserFactory;

/**
 * Created by Richard on 4/9/14.
 */
public class JsonDeserializerBytes<T> implements Function<byte[], T> {


    /**
     * The type of object that we are serializing.
     */
    protected final Class<T> type;


    /**
     * JSON parser/valueSerializer that we are using.
     */
    protected JsonParserAndMapper deserializer = new JsonParserFactory().create();


    public JsonDeserializerBytes(Class<T> type) {
        this.type = type;
    }

    @Override
    public T apply(byte[] json) {
        return deserializer.parse(type, json);
    }
}
