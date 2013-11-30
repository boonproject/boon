package org.boon.json.internal;

import org.boon.json.JsonStringDecoder;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

import static org.boon.Exceptions.die;

public class ValueInCharBuf extends ValueBase {

    public char [] buffer;

    private static final int LETTER_L = 'l';

    private static final int LETTER_T = 't';

    private static final int LETTER_R = 'r';

    private static final int LETTER_E = 'e';

    private static final int LETTER_U = 'u';

    private static final int LETTER_F = 'f';

    private static final int LETTER_A = 'a';


    private static final int LETTER_S = 's';


    public ValueInCharBuf( Type type ) {
        this.type = type;
    }

    public ValueInCharBuf() {

    }


    public String toString() {

      return new String(buffer, startIndex, (endIndex - startIndex)  );
    }



    @Override
    public final Object toValue() {

        if (value != null) {
             return value;
        } else {
            value = doToValue ();
            return value;
        }
    }

    private final Object doToValue() {

        switch ( type ) {
            case FALSE:
                return false;
            case TRUE:
                return true;
            case NULL:
                return null;
            case DOUBLE:
                return Double.parseDouble ( toString ()  );
            case INTEGER:
                String str = toString ();
                try {
                    return Integer.parseInt ( str );
                } catch (Exception ex) {
                    return Long.parseLong ( str  );
                }
            case STRING:
                if (this.decodeStrings) {
                    return JsonStringDecoder.decode ( buffer, startIndex, endIndex );
                } else {
                    return toString ();
                }
        }
        die();
        return null;
    }

    @Override
    public boolean equals ( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof ValueBase ) ) return false;

        ValueInCharBuf value1 = ( ValueInCharBuf ) o;

        if ( endIndex != value1.endIndex ) return false;
        if ( startIndex != value1.startIndex ) return false;
        if ( !Arrays.equals ( buffer, value1.buffer ) ) return false;
        if ( type != value1.type ) return false;
        if ( value != null ? !value.equals ( value1.value ) : value1.value != null ) return false;

        return true;
    }

    @Override
    public int hashCode () {
        int result = type != null ? type.hashCode () : 0;
        result = 31 * result + ( buffer != null ? Arrays.hashCode ( buffer ) : 0 );
        result = 31 * result + startIndex;
        result = 31 * result + endIndex;
        result = 31 * result + ( value != null ? value.hashCode () : 0 );
        return result;
    }



    @Override
    public final int length() {
        return buffer.length;
    }

    @Override
    public final char charAt( int index ) {
        return buffer[index];
    }

    @Override
    public final CharSequence subSequence( int start, int end ) {

        ValueInCharBuf b = new ValueInCharBuf (  );
        b.startIndex = start;
        b.endIndex = end;
        return b;
    }




    public BigDecimal bigDecimalValue() {
        return new BigDecimal ( buffer, startIndex, endIndex - startIndex );
    }

    public BigInteger bigIntegerValue() {
        return new BigInteger ( toString () );
    }

    public boolean booleanValue() {
        final int length = this.endIndex - this.startIndex;
        if (length  == 4
                && buffer[ startIndex ] == LETTER_T
                && buffer[ startIndex + 1 ] == LETTER_R
                && buffer[ startIndex + 2 ] == LETTER_U
                && buffer[ startIndex + 3 ] == LETTER_E
                ) {
            return true;
        } else if (length  == 5
                && buffer[ startIndex ] == LETTER_F
                && buffer[ startIndex + 1 ] == LETTER_A
                && buffer[ startIndex + 2 ] == LETTER_L
                && buffer[ startIndex + 3 ] == LETTER_S
                && buffer[ startIndex + 4 ] == LETTER_E
                ){
            return false;
        } else {
            int i = intValue ();
            if (i==0) {
                return false;
            } else {
                return true;
            }
        }
    }

    public String stringValue() {
        return toString ();
    }



    @Override
    public String stringValueEncoded() {
        return JsonStringDecoder.decode ( buffer, startIndex, endIndex );
    }




}
