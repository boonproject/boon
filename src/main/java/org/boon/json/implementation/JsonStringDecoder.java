package org.boon.json.implementation;

import org.boon.core.reflection.FastStringUtils;
import org.boon.json.JsonException;
import org.boon.primitive.CharBuf;
import org.boon.primitive.Chr;

public class JsonStringDecoder {





    public static String decode( String string ) {
        if ( !string.contains( "\\" ) ) {
            return string;
        }
        char[] cs = FastStringUtils.toCharArray( string );
        return decode( cs, 0, cs.length );
    }

    public static String decode( char[] chars ) {

        return decode( chars, 0, chars.length );
    }


    public static String decode( char[] chars, int start, int to ) {

        if ( !Chr.contains( chars, '\\', start, to - start ) ) {
            return new String( chars, start, to - start );
        }
        return decodeForSure( chars, start, to );
    }


    public static String decodeForSure( CharSequence cs, int start, int to ) {
        if ( cs instanceof String ) {
            return decodeForSure( ( String ) cs, start, to );
        } else {
            return decode( cs.subSequence( start, to ).toString() );
        }

    }


    public static String decodeForSure( String string, int start, int to ) {
        char[] cs = FastStringUtils.toCharArray( string );
        return decodeForSure( cs, start, to );
    }

    public static String decodeForSure( char[] chars ) {
        return decodeForSure( chars, 0, chars.length );
    }

    public static String decodeForSure( char[] chars, int start, int to ) {

        CharBuf builder = CharBuf.create( to - start );
        builder.decodeJsonString(chars, start, to);
        return builder.toString();

    }

    public static String decodeForSure( byte[] bytes, int start, int to ) {
        CharBuf builder = CharBuf.create( to - start );
        builder.decodeJsonString(bytes, start, to);
        return builder.toString();
    }
}