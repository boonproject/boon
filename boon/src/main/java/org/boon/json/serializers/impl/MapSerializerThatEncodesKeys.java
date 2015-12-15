package org.boon.json.serializers.impl;


import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.json.serializers.MapSerializer;
import org.boon.primitive.CharBuf;

import java.util.Map;
import java.util.Set;

/**
 * Created by rick on 12/14/15.
 */
public class MapSerializerThatEncodesKeys implements MapSerializer {
    private static final char[] EMPTY_MAP_CHARS = {'{', '}'};
    private final boolean includeNulls;

    public MapSerializerThatEncodesKeys(boolean includeNulls) {
        this.includeNulls = includeNulls;
    }


    private void serializeFieldName(JsonSerializerInternal serializer, Object key, CharBuf builder) {
        serializer.serializeObject(key, builder);
        builder.add(':');

    }

    @Override
    public final void serializeMap(JsonSerializerInternal serializer, Map<Object, Object> map, CharBuf builder) {

        if (map.size() == 0) {
            builder.addChars(EMPTY_MAP_CHARS);
            return;
        }


        builder.addChar('{');

        final Set<Map.Entry<Object, Object>> entrySet = map.entrySet();
        int index = 0;

        if (!includeNulls) {
            for (Map.Entry<Object, Object> entry : entrySet) {
                if (entry.getValue() != null) {
                    serializeFieldName(serializer, entry.getKey(), builder);
                    serializer.serializeObject(entry.getValue(), builder);
                    builder.addChar(',');
                    index++;
                }
            }
        } else {
            for (Map.Entry<Object, Object> entry : entrySet) {
                serializeFieldName(serializer, entry.getKey(), builder);
                serializer.serializeObject(entry.getValue(), builder);
                builder.addChar(',');
                index++;

            }
        }
        if (index > 0)
            builder.removeLastChar();
        builder.addChar('}');

    }
}