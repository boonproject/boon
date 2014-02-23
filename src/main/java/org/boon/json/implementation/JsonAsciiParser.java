package org.boon.json.implementation;

import org.boon.primitive.ByteScanner;
import org.boon.primitive.CharScanner;

import java.nio.charset.StandardCharsets;

/**
 * Created by rick on 12/15/13.
 */
public class JsonAsciiParser extends JsonBaseByteArrayParser {

    public JsonAsciiParser() {
        this.charset = StandardCharsets.US_ASCII;
    }

    protected final String decodeString() {

        byte[] array = charArray;
        int index = __index;
        int currentChar = array[index];

        if ( index < array.length && currentChar == '"' ) {
            index++;
        }

        final int startIndex = index;


        boolean encoded = ByteScanner.hasEscapeChar(array, index, indexHolder);
        index = indexHolder[0];



        String value = null;
        if ( encoded ) {
            index = ByteScanner.findEndQuote ( array,  index);
            value = builder.decodeJsonString ( array, startIndex, index ).toString ();
            builder.recycle ();
        } else {
            value = new String( array, startIndex, ( index - startIndex ) );
        }

        if ( index < charArray.length ) {
            index++;
        }
        __index = index;
        return value;
    }
}
