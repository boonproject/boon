package org.boon.json.implementation;


import org.boon.primitive.ByteScanner;



public class JsonUTF8Parser extends JsonBaseByteArrayParser {





    protected final String decodeString() {

        byte[] array = charArray;
        int index = __index;
        int currentChar = array[index];

        if ( index < array.length && currentChar == DOUBLE_QUOTE ) {
            index++;
        }

        final int startIndex = index;


        boolean encoded = ByteScanner.hasEscapeCharUTF8(array, index, indexHolder);
        index = indexHolder[0];



        String value = null;
        if ( encoded ) {
            index = ByteScanner.findEndQuoteUTF8( array,  index);
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

