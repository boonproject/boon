package org.boon.json.serializers.impl;

import org.boon.json.serializers.ArraySerializer;
import org.boon.json.serializers.CollectionSerializer;
import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.primitive.CharBuf;

import java.lang.reflect.Array;
import java.util.Collection;

public class CollectionSerializerImpl implements CollectionSerializer, ArraySerializer {

    private static final char [] EMPTY_LIST_CHARS = {'[', ']'};


    @Override
    public final void serializeCollection ( JsonSerializerInternal serializer, Collection<?> collection, CharBuf builder ) {
        if ( collection.size () == 0 ) {
            builder.addChars ( EMPTY_LIST_CHARS );
            return;
        }

        builder.addChar( '[' );
        for ( Object o : collection ) {
            serializer.serializeObject ( o, builder );
            builder.addChar ( ',' );
        }
        builder.removeLastChar ();
        builder.addChar( ']' );

    }

    @Override
    public void serializeArray ( JsonSerializerInternal serializer, Object array, CharBuf builder )  {
        if ( Array.getLength ( array ) == 0 ) {
            builder.addChars ( EMPTY_LIST_CHARS );
            return;
        }

        builder.addChar( '[' );
        final int length = Array.getLength ( array );
        for ( int index = 0; index < length; index++ ) {
            serializer.serializeObject ( Array.get ( array, index ), builder );
            builder.addChar ( ',' );
        }
        builder.removeLastChar ();
        builder.addChar( ']' );

    }
}
