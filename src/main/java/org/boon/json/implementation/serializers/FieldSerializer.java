package org.boon.json.implementation.serializers;

import org.boon.core.reflection.fields.FieldAccess;
import org.boon.json.JsonSerializer;
import org.boon.primitive.CharBuf;

/**
 * Created by rick on 1/1/14.
 */
public interface FieldSerializer {

    boolean serializeField(JsonSerializer serializer,  Object parent, FieldAccess fieldAccess, CharBuf builder );

}
