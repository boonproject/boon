package org.boon.json.implementation.serializers;

import org.boon.json.JsonSerializer;
import org.boon.primitive.CharBuf;

import java.util.Map;

/**
 * Created by rick on 1/1/14.
 */
public interface MapSerializer {
    void serializeMap ( JsonSerializer serializer, Map<String, Object> map, CharBuf builder );

}
