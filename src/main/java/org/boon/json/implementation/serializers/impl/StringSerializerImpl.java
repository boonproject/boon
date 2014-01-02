package org.boon.json.implementation.serializers.impl;

import org.boon.json.JsonSerializer;
import org.boon.json.implementation.serializers.StringSerializer;
import org.boon.primitive.CharBuf;

/**
 * Created by rick on 1/1/14.
 */
public class StringSerializerImpl implements StringSerializer {
    @Override
    public final void serializeString ( JsonSerializer serializer, String string, CharBuf builder )  {
        builder.addJsonEscapedString ( string );

    }
}
