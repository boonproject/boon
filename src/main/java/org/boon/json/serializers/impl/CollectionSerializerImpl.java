package org.boon.json.serializers.impl;

import org.boon.json.serializers.ArraySerializer;
import org.boon.json.serializers.CollectionSerializer;
import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.primitive.CharBuf;

import java.util.Collection;

/**
 * Created by rick on 1/1/14.
 */
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
    public void serializeArray ( JsonSerializerInternal serializer, Object objArray, CharBuf builder )  {
        Object [] array = (Object[])objArray;
        if ( array.length == 0 ) {
            builder.addChars ( EMPTY_LIST_CHARS );
            return;
        }

        builder.addChar( '[' );
        for ( int index = 0; index < array.length; index++ ) {
            serializer.serializeObject ( array[ index ], builder );
            builder.addChar ( ',' );
        }
        builder.removeLastChar ();
        builder.addChar( ']' );

    }
}
