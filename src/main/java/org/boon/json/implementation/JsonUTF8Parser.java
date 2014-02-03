package org.boon.json.implementation;


import java.nio.charset.StandardCharsets;


public class JsonUTF8Parser extends JsonBaseByteArrayParser {





    private static int skipChar( final int c, int index ) {


        if ( ( c >> 5 ) == -2 ) {
            index++;
        } else if ( ( c >> 4 ) == -2 ) {
            index+=2;
        } else if ( ( c >> 3 ) == -2 ) {
            index+=3;
        }

        return index;
    }





    protected final String decodeString() {

        byte[] array = charArray;
        int index = __index;
        int currentChar = charArray[index];

        if ( index < array.length && currentChar == DOUBLE_QUOTE ) {
            index++;
        }

        final int startIndex = index;


        boolean escape = false;

        boolean hasEscaped = false;


        while ( true ) {
                currentChar = array[index];
                if (currentChar>=0)  {
                    if ( isDoubleQuote ( currentChar )) {
                        if (!escape) {
                            break;
                        }
                    }  if ( isEscape (currentChar) ) {
                        hasEscaped = true;
                        escape = true;
                    } else {
                        escape = false;
                    }
                    index++;
                    if (index >= array.length) break;
                } else {
                    index = skipChar(currentChar, index);
                    index++;
                    if (index >= array.length) break;

                }
        }


        String value;
        if ( hasEscaped ) {

            value = builder.decodeJsonString(array, startIndex, index).toStringAndRecycle();

        } else {
            value = new String( array, startIndex, ( index - startIndex ), StandardCharsets.UTF_8 );
        }

        if ( index < charArray.length ) {
            index++;
        }
        __index = index;
        return value;
    }


}

