package org.boon.json;

import org.boon.primitive.CharBuf;

public class FieldSerializationData {

    public final String fieldName;
    public final Class type;
    public final Class parentType;
    public final Object value;
    public final CharBuf output;
    private final Object parent;

    public FieldSerializationData ( String fieldName, Class type, Class parentType, Object value, CharBuf buffer,
                                    Object parent ) {
        this.fieldName = fieldName;
        this.type = type;
        this.parentType = parentType;
        this.value = value;
        this.output = buffer;
        this.parent = parent;

    }
}
