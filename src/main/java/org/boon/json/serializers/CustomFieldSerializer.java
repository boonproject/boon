package org.boon.json.serializers;

import org.boon.core.reflection.fields.FieldAccess;
import org.boon.primitive.CharBuf;

/**
 * Created by rick on 1/2/14.
 */
public interface CustomFieldSerializer {

    boolean serializeField(JsonSerializerInternal serializer, Object parent, FieldAccess fieldAccess, CharBuf builder  );

}
