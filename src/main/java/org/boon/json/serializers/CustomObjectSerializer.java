package org.boon.json.serializers;

import org.boon.json.JsonSerializer;
import org.boon.primitive.CharBuf;

/**
 * Created by rick on 1/2/14.
 */
public interface CustomObjectSerializer <T> {

    Class<T> type();

    void serializeObject(JsonSerializerInternal serializer,  T instance, CharBuf builder );

}
