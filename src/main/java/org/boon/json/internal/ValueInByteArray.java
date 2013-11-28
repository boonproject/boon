package org.boon.json.internal;

import org.boon.json.JsonStringDecoder;
import org.boon.primitive.CharBuf;

import java.util.Arrays;

import static org.boon.Exceptions.die;

public class ValueInByteArray extends ValueBase {

    public byte [] buffer;

    public ValueInByteArray( Type type ) {
        this.type = type;
    }

    public ValueInByteArray() {

    }


    public String toString() {
        CharBuf charBuf = CharBuf.create ( endIndex - startIndex );
        charBuf.add ( buffer, startIndex, endIndex) ;
        return charBuf.toString ();
    }


    @Override
    public String toKey() {
        CharBuf charBuf = CharBuf.create ( (endIndex - startIndex)  );
        charBuf.add ( buffer, startIndex+1, endIndex) ;
        return charBuf.toString ();

    }

    @Override
    public Object toValue() {

        if (value!=null) {
            return value;
        }
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
                return JsonStringDecoder.decode ( buffer, startIndex, endIndex );
        }
        die();
        return null;
    }

    @Override
    public boolean equals ( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof ValueBase ) ) return false;

        ValueInByteArray value1 = ( ValueInByteArray ) o;

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
    public int intValue() {
        return Integer.parseInt ( toString () );
    }

    @Override
    public long longValue() {
        return Long.parseLong ( toString ()  );
    }

    @Override
    public float floatValue() {
        return Float.parseFloat ( toString ()  );

    }

    @Override
    public double doubleValue() {
        return Double.parseDouble ( toString ()  );
    }




    @Override
    public int length() {
        return buffer.length;
    }

    @Override
    public char charAt( int index ) {
        return (char) buffer[index];
    }

    @Override
    public CharSequence subSequence( int start, int end ) {

        ValueInCharBuf b = new ValueInCharBuf (  );
        b.startIndex = start;
        b.endIndex = end;
        return b;
    }

}
