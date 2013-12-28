package org.boon.json;

import org.boon.primitive.CharBuf;

/**
 * Created by rick on 12/27/13.
 */
public class ObjectSerializationData {

    public final Object instance;
    public final Class<?> type;
    public final CharBuf output;

    public ObjectSerializationData ( Object instance, Class<?> type, CharBuf output ) {
        this.instance = instance;
        this.type = type;
        this.output = output;
    }
}
