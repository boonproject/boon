package org.boon.json.serializers;

import org.boon.json.JsonSerializer;
import org.boon.primitive.CharBuf;

import java.util.Map;

/**
 * Created by rick on 1/1/14.
 */
public interface MapSerializer {
    void serializeMap ( JsonSerializerInternal serializer, Map<String, Object> map, CharBuf builder );

}
