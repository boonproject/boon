package org.boon.json.internal;

import org.boon.json.JsonStringDecoder;

import java.math.BigDecimal;
import java.util.*;

import static org.boon.Exceptions.die;

public class ValueInCharBuf extends ValueBase {

    public char [] buffer;


    public ValueInCharBuf( Type type ) {
        this.type = type;
    }

    public ValueInCharBuf() {

    }


    public String toString() {

      return new String(buffer, startIndex, (endIndex - startIndex)  );
    }



    @Override
    public  Object toValue() {

        if (value != null) {
             return value;
        } else {
            value = doToValue ();
            return value;
        }
    }

    private  Object doToValue() {

        switch ( type ) {
            case DOUBLE:
                return doubleValue ();
            case INTEGER:
                try {
                    return intValue ();
                } catch (Exception ex) {
                    return longValue ();
                }
            case STRING:
                return stringValue ();
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

    public String stringValue() {
        if (this.decodeStrings) {
            return JsonStringDecoder.decode ( buffer, startIndex, endIndex );
        } else {
            return toString ();
        }
    }

    @Override
    public String stringValueEncoded() {
        return JsonStringDecoder.decode ( buffer, startIndex, endIndex );
    }




}
