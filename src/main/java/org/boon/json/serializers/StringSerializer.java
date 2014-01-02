package org.boon.json.serializers;

import org.boon.json.JsonSerializer;
import org.boon.primitive.CharBuf;

/**
 * Created by rick on 1/1/14.
 */
public interface StringSerializer {

    void serializeString(JsonSerializerInternal serializer,  String string, CharBuf builder );

}
