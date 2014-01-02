package org.boon.json.serializers.impl;

import org.boon.json.serializers.DateSerializer;
import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.primitive.CharBuf;

import java.util.Date;

/**
 * Created by rick on 1/1/14.
 */
public class DateSerializerImpl implements DateSerializer {
    @Override
    public final void serializeDate ( JsonSerializerInternal jsonSerializer, Date date, CharBuf builder ) {
            builder.addLong(date.getTime ());
    }
}
