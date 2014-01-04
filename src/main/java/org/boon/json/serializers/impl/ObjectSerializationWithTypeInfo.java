package org.boon.json.serializers.impl;

import org.boon.core.reflection.fields.FieldAccess;
import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.json.serializers.ObjectSerializer;
import org.boon.primitive.CharBuf;

import java.util.Collection;
import java.util.Map;

/**
 * Created by rick on 1/4/14.
 */
public class ObjectSerializationWithTypeInfo implements ObjectSerializer {


    @Override
    public void serializeObject ( JsonSerializerInternal serializer, Object instance, CharBuf builder ) {
        builder.addString( "{\"class\":" );
        builder.addQuoted ( instance.getClass ().getName () );
        final Map<String, FieldAccess> fieldAccessors = serializer.getFields ( instance.getClass () );

        int index = 0;
        Collection<FieldAccess> values = fieldAccessors.values();
        int length = values.size();

        if ( length > 0 ) {
            builder.addChar( ',' );
           

        for ( FieldAccess fieldAccess : values ) {
            serializer.serializeField ( instance, fieldAccess, builder );
            index++;
            builder.addChar( ',' );
        }


        if ( index > 0 ) {
            builder.removeLastChar();
        }

        builder.addChar( '}' );

    }
}

}
