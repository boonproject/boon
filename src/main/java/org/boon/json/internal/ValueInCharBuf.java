package org.boon.json.internal;

import org.boon.json.JsonStringDecoder;

import java.math.BigDecimal;
import java.util.*;

import static org.boon.Exceptions.die;
import static org.boon.primitive.CharScanner.*;

public class ValueInCharBuf extends ValueBase {

    public char[] buffer;


    public ValueInCharBuf( Type type ) {
        this.type = type;
    }

    public ValueInCharBuf() {

    }


    public String toString() {

        return new String ( buffer, startIndex, ( endIndex - startIndex ) );
    }


    @Override
    public Object toValue() {

        if ( value != null ) {
            return value;
        } else {
            value = doToValue ();
            return value;
        }
    }

    private Object doToValue() {

        switch ( type ) {
            case DOUBLE:
                return doubleValue ();
            case INTEGER:

                int sign = 1;
                boolean negative = false;
                if (buffer[startIndex]== '-') {
                    startIndex++;
                    sign=-1;
                    negative = true;

                }


                if (isInteger(buffer, startIndex, endIndex - startIndex, negative)) {
                    return intValue () * sign;
                } else {
                    return longValue () * sign;
                }
            case STRING:
                return stringValue ();
        }
        die ();
        return null;
    }

    @Override
    public boolean equals( Object o ) {
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
    public int hashCode() {
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

        ValueInCharBuf b = new ValueInCharBuf ();
        b.startIndex = start;
        b.endIndex = end;
        return b;
    }


    public BigDecimal bigDecimalValue() {
        return new BigDecimal ( buffer, startIndex, endIndex - startIndex );
    }

    public String stringValue() {
        if ( this.decodeStrings ) {
            return JsonStringDecoder.decode ( buffer, startIndex, endIndex );
        } else {
            return toString ();
        }
    }

    @Override
    public String stringValueEncoded() {
        return JsonStringDecoder.decode ( buffer, startIndex, endIndex );
    }


    @Override
    public int intValue() {
        int sign = 1;
        if (buffer[startIndex]== '-') {
            startIndex++;
            sign=-1;

        }
        return parseInt ( buffer, startIndex, endIndex - startIndex ) * sign;
    }

    @Override
    public long longValue() {
        long sign = 1;
        if (buffer[startIndex]== '-') {
            startIndex++;
            sign=-1;

        }
        return parseLong ( buffer, startIndex, endIndex - startIndex ) * sign;
    }


    public byte byteValue() {
        return ( byte ) intValue ();
    }

    public short shortValue() {
        return ( short ) intValue ();
    }


    private static double powersOf10[] = {
            1.0,
            10.0,
            100.0,
            1_000.0,
            10_000.0,
            100_000.0,
            1_000_000.0,
            10_000_000.0,
            100_000_000.0,
            1_000_000_000.0,
            10_000_000_000.0,
            100_000_000_000.0,
            1_000_000_000_000.0,
            10_000_000_000_000.0,
            100_000_000_000_000.0,
    };


    private static  float fpowersOf10[] = {
            1.0f,
            10.0f,
            100.0f,
            1_000.0f,
            10_000.0f,
            100_000.0f,
            1_000_000.0f,
            10_000_000.0f,
            100_000_000.0f,
            1_000_000_000.0f,
    };

    @Override
    public double doubleValue() {

        boolean simple = true;
        int digitsPastPoint = 0;
        boolean foundPoint = false;
        boolean negative = false;

        double sign ;

        if ( buffer[startIndex] == '-' ) {
            startIndex++;
            negative = true;
            sign = -1.0;
        } else {
            negative = false;
            sign = 1.0;
        }

        loop:
        for ( int index = startIndex; index < endIndex; index++ ) {
            char ch = buffer[index];
            switch ( ch ) {
                case 'e':
                    simple = false;
                    break loop;
                case 'E':
                    simple = false;
                    break loop;
                case 'F':
                    simple = false;
                    break loop;
                case 'f':
                    simple = false;
                    break loop;
                case '.':
                    foundPoint = true;
                    continue loop;
            }
            if ( foundPoint ) {
                digitsPastPoint++;
                if (digitsPastPoint >= powersOf10.length) {
                    simple = true;
                    break;
                }
            }
        }

        if ( simple ) {
            long value;
            final int length = endIndex - startIndex;

            if ( isInteger ( buffer, startIndex, length, negative ) ) {
                value = parseIntIgnoreDot ( buffer, startIndex, length );
            } else {
                value = parseLongIgnoreDot ( buffer, startIndex, length );
            }
            if ( digitsPastPoint < powersOf10.length ) {
                double power = powersOf10[digitsPastPoint] * sign;
                return value / power;

            }


        }

        return Double.parseDouble ( toString () ) * sign;
    }


    @Override
    public float floatValue() {

        boolean simple = true;
        int digitsPastPoint = 0;
        boolean foundPoint = false;

        float sign;

        if ( buffer[startIndex] == '-' ) {
            startIndex++;
            sign = -1.0f;
        } else {
            sign = 1.0f;
        }


        int length = endIndex - startIndex;
        if (length > 10) {
            return Float.parseFloat ( toString () ) * sign;
        }
        loop:
        for ( int index = startIndex; index < endIndex; index++ ) {
            char ch = buffer[index];
            switch ( ch ) {
                case 'e':
                    simple = false;
                    break loop;
                case 'E':
                    simple = false;
                    break loop;
                case 'F':
                    simple = false;
                    break loop;
                case 'f':
                    simple = false;
                    break loop;
                case '.':
                    foundPoint = true;
                    continue loop;
            }
            if ( foundPoint ) {
                digitsPastPoint++;
                if (digitsPastPoint >= powersOf10.length) {
                    simple = true;
                    break;
                }
            }
        }

        if ( simple ) {
            int value;

            value = parseIntIgnoreDot ( buffer, startIndex, length );
            if ( digitsPastPoint < powersOf10.length ) {
                float power = fpowersOf10[digitsPastPoint] * sign;
                return value / power;

            }


        }

        return Float.parseFloat ( toString () ) * sign;

    }


}
