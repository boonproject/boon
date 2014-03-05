package org.boon.json.serializers.impl;

import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.json.serializers.StringSerializer;
import org.boon.primitive.CharBuf;

/**
 * Created by rick on 1/1/14.
 */
public class StringSerializerImpl implements StringSerializer {
    @Override
    public final void serializeString ( JsonSerializerInternal serializer, String string, CharBuf builder )  {
        builder.asJsonString(string);

    }
}
