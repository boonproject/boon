package org.boon.json.serializers;

import org.boon.primitive.CharBuf;

/**
 * Created by rick on 1/1/14.
 */
public interface UnknownSerializer {

     void serializeUnknown ( JsonSerializerInternal serializer, Object unknown, CharBuf builder );
}