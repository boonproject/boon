package org.boon.json.serializers.impl;

import org.boon.core.reflection.fields.FieldAccess;
import org.boon.json.serializers.InstanceSerializer;
import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.primitive.CharBuf;

import java.util.Collection;
import java.util.Map;

/**
 * Created by rick on 1/1/14.
 */
public class InstanceSerializerImpl implements InstanceSerializer{
    @Override
    public final void serializeInstance ( JsonSerializerInternal serializer, Object instance, CharBuf builder ) {
        final Map<String, FieldAccess> fieldAccessors =   serializer.getFields(instance.getClass ());
        final Collection<FieldAccess> values = fieldAccessors.values ();



        builder.addChar( '{' );

        int index = 0;
        for ( FieldAccess fieldAccess : values ) {
            if (serializer.serializeField ( instance, fieldAccess, builder ) ) {
                builder.addChar ( ',' );
                index++;
            }
        }
        if ( index > 0 ) {
            builder.removeLastChar();
        }
        builder.addChar( '}' );

    }

    @Override
    public void serializeSubtypeInstance( JsonSerializerInternal serializer, Object instance, CharBuf builder ) {
        builder.addString( "{\"class\":" );
        builder.addQuoted ( instance.getClass ().getName () );
        final Map<String, FieldAccess> fieldAccessors = serializer.getFields ( instance.getClass () );

        int index = 0;
        Collection<FieldAccess> values = fieldAccessors.values();
        int length = values.size();

        if ( length > 0 ) {
            builder.addChar( ',' );


            for ( FieldAccess fieldAccess : values ) {
                boolean sent = serializer.serializeField ( instance, fieldAccess, builder );
                if (sent) {
                    index++;
                    builder.addChar( ',' );
                }
            }


            if ( index > 0 ) {
                builder.removeLastChar();
            }

            builder.addChar( '}' );


        }

    }
}
