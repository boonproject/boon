package org.boon.json.implementation.serializers.impl;

import org.boon.core.reflection.FastStringUtils;
import org.boon.json.JsonSerializer;
import org.boon.json.implementation.serializers.MapSerializer;
import org.boon.primitive.CharBuf;

import java.util.Map;
import java.util.Set;

/**
 * Created by rick on 1/1/14.
 */
public class MapSerializerImpl implements MapSerializer {
    private static final char [] EMPTY_MAP_CHARS = {'{', '}'};


    private void serializeFieldName ( String name, CharBuf builder ) {
        builder.addJsonFieldName ( FastStringUtils.toCharArray ( name ) );
    }

    @Override
    public final void serializeMap ( JsonSerializer serializer, Map<String, Object> map, CharBuf builder ) {

        if ( map.size () == 0 ) {
            builder.addChars ( EMPTY_MAP_CHARS );
            return;
        }


        builder.addChar( '{' );

        final Set<Map.Entry<String, Object>> entrySet = map.entrySet();
        for ( Map.Entry<String, Object> entry : entrySet ) {
            serializeFieldName ( entry.getKey (), builder );
            serializer.serializeObject ( entry.getValue (), builder );
            builder.addChar ( ',' );
        }
        builder.removeLastChar ();
        builder.addChar( '}' );

    }
}
