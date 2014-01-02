package org.boon.json.serializers.impl;

import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.json.serializers.UnknownSerializer;
import org.boon.primitive.CharBuf;

/**
 * Created by rick on 1/1/14.
 */
public class UnknownSerializerImpl implements UnknownSerializer{
    @Override
    public final void serializeUnknown ( JsonSerializerInternal serializer, Object unknown, CharBuf builder ) {
        builder.addQuoted ( unknown.toString () );
    }
}
