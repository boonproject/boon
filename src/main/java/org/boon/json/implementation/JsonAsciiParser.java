package org.boon.json.implementation;

import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.core.reflection.fields.FieldsAccessor;
import org.boon.json.JsonParser;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Created by rick on 12/15/13.
 */
public class JsonAsciiParser extends JsonBaseByteArrayParser implements JsonParser {


    public JsonAsciiParser() {
        super( FieldAccessMode.create( FieldAccessMode.FIELD ) );
        this.charset = StandardCharsets.US_ASCII;

    }

    public JsonAsciiParser( FieldAccessMode mode ) {
        super( FieldAccessMode.create( mode ) );
        this.charset = StandardCharsets.US_ASCII;

    }

    public JsonAsciiParser( FieldsAccessor fieldsAccessor ) {
        super( fieldsAccessor );
        this.charset = StandardCharsets.US_ASCII;

    }



    protected final String decodeString() {

        byte[] array = charArray;
        int index = __index;
        int currentChar = charArray[ index ];

        if ( index < array.length && currentChar == DOUBLE_QUOTE ) {
            index++;
        }

        final int startIndex = index;


        boolean escape = false;

        boolean hasEscaped = false;


        while ( true ) {
            currentChar = array[ index ];
            if ( isDoubleQuote( currentChar ) ) {
                if ( !escape ) {
                    break;
                }
            }
            if ( isEscape( currentChar ) ) {
                hasEscaped = true;
                escape = true;
            } else {
                escape = false;
            }
            index++;
            if ( index >= array.length ) break;
        }


        String value;
        if ( hasEscaped ) {
            value = JsonStringDecoder.decodeForSure( array, startIndex, index );
        } else {
            value = new String( array, startIndex, ( index - startIndex ), StandardCharsets.US_ASCII );
        }

        if ( index < charArray.length ) {
            index++;
        }
        __index = index;
        return value;
    }


}
