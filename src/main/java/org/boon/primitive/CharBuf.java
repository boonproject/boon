package org.boon.primitive;


import static org.boon.primitive.CharScanner.*;
import static org.boon.primitive.CharScanner.parseLong;

public class CharBuf {
    protected int capacity = 16;
    protected int location = 0;

    protected char[] buffer;


    public static CharBuf createExact( final int capacity ) {
        return new CharBuf ( capacity ) {
            public CharBuf add( char[] chars ) {
                Chr._idx ( buffer, location, chars );
                location += chars.length;
                return this;
            }
        };
    }

    public static CharBuf create( int capacity ) {
        return new CharBuf ( capacity );
    }

    protected CharBuf( int capacity ) {
        this.capacity = capacity;
        init ( );
    }


    protected CharBuf( ) {
        init ( );
    }

    public void init( ) {
        buffer = new char[capacity];
    }

    public CharBuf add( String str ) {
        add ( str.toCharArray ( ) );
        return this;
    }


    public CharBuf add( int i ) {
        add ( "" + i );
        return this;
    }



    public CharBuf addChar( byte i ) {
        add ((char) i );
        return this;
    }



    public CharBuf addChar( int i ) {
        add ((char) i );
        return this;
    }


    public CharBuf addChar( short i ) {
        add ((char) i );
        return this;
    }


    public CharBuf addChar( char c ) {
        add (c );
        return this;
    }

    public CharBuf addLine( String str ) {
        add ( str.toCharArray ( ) );
        add ( '\n' );
        return this;
    }


    public CharBuf add( char[] chars ) {
        if ( chars.length + location < capacity ) {
            Chr._idx ( buffer, location, chars );
        } else {
            buffer = Chr.grow ( buffer, buffer.length * 2 + chars.length );
            Chr._idx ( buffer, location, chars );
            capacity = buffer.length;
        }
        location += chars.length;
        return this;
    }


    public CharBuf add( char[] chars, final int length ) {
        if ( length + location < capacity ) {
            Chr._idx ( buffer, location, chars, length );
        } else {
            buffer = Chr.grow ( buffer, buffer.length * 2 + length );
            Chr._idx ( buffer, location, chars );
            capacity = buffer.length;
        }
        location += length;
        return this;
    }

    public CharBuf add( byte[] chars ) {
        if ( chars.length + location < capacity ) {
            Chr._idx ( buffer, location, chars );
        } else {
            buffer = Chr.grow ( buffer, buffer.length * 2 + chars.length );
            Chr._idx ( buffer, location, chars );
            capacity = buffer.length;
        }
        location += chars.length;
        return this;
    }


    public CharBuf add( byte[] chars, int start, int end ) {

        int charsLength = start - end;
        if ( charsLength + location < capacity ) {
            Chr._idx ( buffer, location, chars, start, end );
        } else {
            buffer = Chr.grow ( buffer, buffer.length * 2 + charsLength );
            Chr._idx ( buffer, location, chars, start, end );
            capacity = buffer.length;
        }
        location += (end - start);
        return this;
    }


    public void add( char ch ) {
        if ( 1 + location < capacity ) {
            Chr.idx ( buffer, location, ch );
        } else {
            buffer = Chr.grow ( buffer );
            Chr.idx ( buffer, location, ch );
            capacity = buffer.length;
        }
        location += 1;
    }

    public String toString( ) {
        return new String ( buffer, 0, location );
    }

    public int len( ) {
        return location;
    }

    public char [] toCharArray() {
        return this.buffer;
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

    public double doubleValue() {

        boolean simple = true;
        int digitsPastPoint = 0;
        boolean foundPoint = false;
        boolean negative = false;
        int startIndex = 0;

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
        for ( int index = startIndex; index < location; index++ ) {
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
            final int length = location - startIndex;

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


    public float floatValue() {

        boolean simple = true;
        int digitsPastPoint = 0;
        boolean foundPoint = false;

        float sign;
        int startIndex = 0;

        if ( buffer[startIndex] == '-' ) {
            startIndex++;
            sign = -1.0f;
        } else {
            sign = 1.0f;
        }


        int length = location - startIndex;
        if (length > 10) {
            return Float.parseFloat ( toString () ) * sign;
        }
        loop:
        for ( int index = startIndex; index < location; index++ ) {
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

    public int intValue() {
        int sign = 1;
        int startIndex = 0;
        if (buffer[startIndex]== '-') {
            startIndex++;
            sign=-1;

        }
        return parseInt ( buffer, startIndex, location - startIndex ) * sign;
    }

    public long longValue() {
        long sign = 1;
        int startIndex = 0;

        if (buffer[startIndex]== '-') {
            startIndex++;
            sign=-1;

        }
        return parseLong ( buffer, startIndex, location - startIndex ) * sign;
    }


    public byte byteValue() {
        return ( byte ) intValue ();
    }

    public short shortValue() {
        return ( short ) intValue ();
    }


    public Number toIntegerWrapper() {

        int sign = 1;
        boolean negative = false;
        int startIndex = 0;
        if (buffer[startIndex]== '-') {
            startIndex++;
            sign=-1;
            negative = true;

        }


        if (isInteger(buffer, startIndex, location - startIndex, negative)) {
            return intValue () * sign;
        } else {
            return longValue () * sign;
        }

    }


}
