package org.boon.primitive;

import java.util.Objects;

import static org.boon.Exceptions.die;

public class ByteScanner {


    public static byte[][] splitExact( final byte[] inputArray,
                                       final int split, final int resultsArrayLength ) {
        /** Holds the results. */
        byte[][] results = new byte[ resultsArrayLength ][];

        int resultIndex = 0;
        int startCurrentLineIndex = 0;
        int currentLineLength = 1;


        byte c = 0;
        int index = 0;

        for (; index < inputArray.length; index++, currentLineLength++ ) {
            c = inputArray[ index ];
            if ( c == split ) {

                results[ resultIndex ] = Byt.copy(
                        inputArray, startCurrentLineIndex, currentLineLength - 1 );
                startCurrentLineIndex = index + 1; //skip the byte

                currentLineLength = 0;
                resultIndex++;
            }
        }

        if ( c != split ) {

            results[ resultIndex ] = Byt.copy(
                    inputArray, startCurrentLineIndex, currentLineLength - 1 );
            resultIndex++;
        }

        int actualLength = resultIndex;
        if ( actualLength < resultsArrayLength ) {
            final int newSize = resultsArrayLength - actualLength;
            results = __shrink( results, newSize );
        }
        return results;
    }

    public static byte[][] splitExact( final byte[] inputArray,
                                       final int resultsArrayLength, int... delims ) {
        /** Holds the results. */
        byte[][] results = new byte[ resultsArrayLength ][];

        int resultIndex = 0;
        int startCurrentLineIndex = 0;
        int currentLineLength = 1;


        byte c = '\u0000';
        int index = 0;
        int j;
        int split;


        for (; index < inputArray.length; index++, currentLineLength++ ) {
            c = inputArray[ index ];

            inner:
            for ( j = 0; j < delims.length; j++ ) {
                split = delims[ j ];
                if ( c == split ) {

                    results[ resultIndex ] = Byt.copy(
                            inputArray, startCurrentLineIndex, currentLineLength - 1 );
                    startCurrentLineIndex = index + 1; //skip the byte

                    currentLineLength = 0;
                    resultIndex++;
                    break inner;
                }
            }
        }

        if ( !Byt.inIntArray( c, delims ) ) {

            results[ resultIndex ] = Byt.copy(
                    inputArray, startCurrentLineIndex, currentLineLength - 1 );
            resultIndex++;
        }


        int actualLength = resultIndex;
        if ( actualLength < resultsArrayLength ) {
            final int newSize = resultsArrayLength - actualLength;
            results = __shrink( results, newSize );
        }
        return results;
    }

    public static byte[][] split( final byte[] inputArray,
                                  final int split ) {
        /** Holds the results. */
        byte[][] results = new byte[ 16 ][];

        int resultIndex = 0;
        int startCurrentLineIndex = 0;
        int currentLineLength = 1;


        byte c = '\u0000';
        int index = 0;

        for (; index < inputArray.length; index++, currentLineLength++ ) {
            c = inputArray[ index ];
            if ( c == split ) {

                if ( resultIndex == results.length ) {

                    results = _grow( results );
                }


                results[ resultIndex ] = Byt.copy(
                        inputArray, startCurrentLineIndex, currentLineLength - 1 );
                startCurrentLineIndex = index + 1; //skip the byte

                currentLineLength = 0;
                resultIndex++;
            }
        }

        if ( c != split ) {

            results[ resultIndex ] = Byt.copy(
                    inputArray, startCurrentLineIndex, currentLineLength - 1 );
            resultIndex++;
        }

        int actualLength = resultIndex;
        if ( actualLength < results.length ) {
            final int newSize = results.length - actualLength;
            results = __shrink( results, newSize );
        }
        return results;
    }

    public static byte[][] splitByChars( final byte[] inputArray,
                                         int... delims ) {
        /** Holds the results. */
        byte[][] results = new byte[ 16 ][];

        int resultIndex = 0;
        int startCurrentLineIndex = 0;
        int currentLineLength = 1;


        byte c = '\u0000';
        int index = 0;
        int j;
        int split;


        for (; index < inputArray.length; index++, currentLineLength++ ) {

            c = inputArray[ index ];

            inner:
            for ( j = 0; j < delims.length; j++ ) {
                split = delims[ j ];
                if ( c == split ) {

                    if ( resultIndex == results.length ) {

                        results = _grow( results );
                    }


                    results[ resultIndex ] = Byt.copy(
                            inputArray, startCurrentLineIndex, currentLineLength - 1 );
                    startCurrentLineIndex = index + 1; //skip the byte

                    currentLineLength = 0;
                    resultIndex++;
                    break inner;
                }
            }
        }

        if ( !Byt.inIntArray( c, delims ) ) {

            results[ resultIndex ] = Byt.copy(
                    inputArray, startCurrentLineIndex, currentLineLength - 1 );
            resultIndex++;
        }


        int actualLength = resultIndex;
        if ( actualLength < results.length ) {
            final int newSize = results.length - actualLength;
            results = __shrink( results, newSize );
        }
        return results;
    }

    private static byte[][] _grow( byte[][] array ) {
        Objects.requireNonNull( array );

        byte[][] newArray = new byte[ array.length * 2 ][];
        System.arraycopy( array, 0, newArray, 0, array.length );
        return newArray;
    }

    private static byte[][] __shrink( byte[][] array, int size ) {
        Objects.requireNonNull( array );
        byte[][] newArray = new byte[ array.length - size ][];

        System.arraycopy( array, 0, newArray, 0, array.length - size );
        return newArray;
    }


    final static String MIN_INT_STR_NO_SIGN = String.valueOf( Integer.MIN_VALUE ).substring( 1 );
    final static String MAX_INT_STR = String.valueOf( Integer.MAX_VALUE );


    final static String MIN_LONG_STR_NO_SIGN = String.valueOf( Long.MIN_VALUE ).substring( 1 );
    final static String MAX_LONG_STR = String.valueOf( Long.MAX_VALUE );


    public static boolean isInteger( byte[] digitChars, int offset, int len
                                      ) {
        String cmpStr = digitChars[offset] == '-' ? MIN_INT_STR_NO_SIGN : MAX_INT_STR;
        int cmpLen = cmpStr.length();
        if ( len < cmpLen ) return true;
        if ( len > cmpLen ) return false;

        for ( int i = 0; i < cmpLen; ++i ) {
            int diff = digitChars[ offset + i ] - cmpStr.charAt( i );
            if ( diff != 0 ) {
                return ( diff < 0 );
            }
        }
        return true;
    }

    public static boolean isLong( byte[] digitChars, int offset, int len
                                   ) {
        String cmpStr = digitChars[offset] == '-' ? MIN_INT_STR_NO_SIGN : MAX_INT_STR;
        int cmpLen = cmpStr.length();
        if ( len < cmpLen ) return true;
        if ( len > cmpLen ) return false;

        for ( int i = 0; i < cmpLen; ++i ) {
            int diff = digitChars[ offset + i ] - cmpStr.charAt( i );
            if ( diff != 0 ) {
                return ( diff < 0 );
            }
        }
        return true;
    }


    public static int parseInt( byte[] digitChars, int offset, int len ) {
        int num = digitChars[ offset ] - '0';
        int to = len + offset;
        // This looks ugly, but appears the fastest way (as per measurements)
        if ( ++offset < to ) {
            num = ( num * 10 ) + ( digitChars[ offset ] - '0' );
            if ( ++offset < to ) {
                num = ( num * 10 ) + ( digitChars[ offset ] - '0' );
                if ( ++offset < to ) {
                    num = ( num * 10 ) + ( digitChars[ offset ] - '0' );
                    if ( ++offset < to ) {
                        num = ( num * 10 ) + ( digitChars[ offset ] - '0' );
                        if ( ++offset < to ) {
                            num = ( num * 10 ) + ( digitChars[ offset ] - '0' );
                            if ( ++offset < to ) {
                                num = ( num * 10 ) + ( digitChars[ offset ] - '0' );
                                if ( ++offset < to ) {
                                    num = ( num * 10 ) + ( digitChars[ offset ] - '0' );
                                    if ( ++offset < to ) {
                                        num = ( num * 10 ) + ( digitChars[ offset ] - '0' );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return num;
    }


    public static int parseIntIgnoreDot( byte[] digitChars, int offset, int len ) {
        int num = digitChars[ offset ] - '0';
        int to = len + offset;
        // This looks ugly, but appears the fastest way (as per measurements)
        if ( ++offset < to ) {
            num = digitChars[ offset ] != '.' ? ( num * 10 ) + ( digitChars[ offset ] - '0' ) : num;
            if ( ++offset < to ) {
                num = digitChars[ offset ] != '.' ? ( num * 10 ) + ( digitChars[ offset ] - '0' ) : num;
                if ( ++offset < to ) {
                    num = digitChars[ offset ] != '.' ? ( num * 10 ) + ( digitChars[ offset ] - '0' ) : num;
                    if ( ++offset < to ) {
                        num = digitChars[ offset ] != '.' ? ( num * 10 ) + ( digitChars[ offset ] - '0' ) : num;
                        if ( ++offset < to ) {
                            num = digitChars[ offset ] != '.' ? ( num * 10 ) + ( digitChars[ offset ] - '0' ) : num;
                            if ( ++offset < to ) {
                                num = digitChars[ offset ] != '.' ? ( num * 10 ) + ( digitChars[ offset ] - '0' ) : num;
                                if ( ++offset < to ) {
                                    num = digitChars[ offset ] != '.' ? ( num * 10 ) + ( digitChars[ offset ] - '0' ) : num;
                                    if ( ++offset < to ) {
                                        num = digitChars[ offset ] != '.' ? ( num * 10 ) + ( digitChars[ offset ] - '0' ) : num;
                                        if ( ++offset < to ) {
                                            num = digitChars[ offset ] != '.' ? ( num * 10 ) + ( digitChars[ offset ] - '0' ) : num;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return num;
    }

    public static long parseLong( byte[] digitChars, int offset, int len ) {
        int len1 = len - 9;
        long val = parseInt( digitChars, offset, len1 ) * L_BILLION;
        return val + ( long ) parseInt( digitChars, offset + len1, 9 );
    }

    public static long parseLongIgnoreDot( byte[] digitChars, int offset, int len ) {
        int len1 = len - 9;
        long val = parseIntIgnoreDot( digitChars, offset, len1 ) * L_BILLION;
        return val + ( long ) parseIntIgnoreDot( digitChars, offset + len1, 9 );
    }

    private final static long L_BILLION = 1000000000;


    public static double doubleValue( byte[] buffer, int startIndex, int endIndex ) {

        boolean simple = true;
        int digitsPastPoint = 0;
        boolean foundPoint = false;


        if ( buffer[ startIndex ] == '-' ) {
            startIndex++;
        }

        loop:
        for ( int index = startIndex; index < endIndex; index++ ) {
            byte ch = buffer[ index ];
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
                if ( digitsPastPoint >= powersOf10.length ) {
                    simple = true;
                    break;
                }
            }
        }

        if ( simple ) {
            long value;
            final int length = endIndex - startIndex;

            if ( isInteger( buffer, startIndex, length ) ) {
                value = parseIntIgnoreDot( buffer, startIndex, length );
            } else {
                value = parseLongIgnoreDot( buffer, startIndex, length );
            }
            if ( digitsPastPoint < powersOf10.length ) {
                double power = powersOf10[ digitsPastPoint ];
                return value / power;

            }


        }

        return Double.parseDouble( new String( buffer, startIndex, ( endIndex - startIndex ) ) );
    }


    public static double simpleDouble( byte[] buffer, boolean simple,  int digitsPastPoint, int startIndex, int endIndex ) {

        double sign;



        if ( simple ) {
            long value;
            final int length = endIndex - startIndex;

            if ( isInteger( buffer, startIndex, length ) ) {
                value = parseIntIgnoreDot( buffer, startIndex, length );
            } else {
                value = parseLongIgnoreDot( buffer, startIndex, length );
            }
            if ( digitsPastPoint < powersOf10.length ) {
                double power = powersOf10[ digitsPastPoint ];
                return value / power;

            }


        }

        return Double.parseDouble( new String( buffer, startIndex, ( endIndex - startIndex ) ) );
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


    /**
     * Turns a single nibble into an ascii HEX digit.
     *
     * @param nibble the nibble to serializeObject.
     * @return the encoded nibble (1/2 byte).
     */
    protected static int encodeNibbleToHexAsciiCharByte( final int nibble ) {

        switch ( nibble ) {
            case 0x00:
            case 0x01:
            case 0x02:
            case 0x03:
            case 0x04:
            case 0x05:
            case 0x06:
            case 0x07:
            case 0x08:
            case 0x09:
                return nibble + 0x30; // 0x30('0') - 0x39('9')
            case 0x0A:
            case 0x0B:
            case 0x0C:
            case 0x0D:
            case 0x0E:
            case 0x0F:
                return nibble + 0x57; // 0x41('a') - 0x46('f')
            default:
                die( "illegal nibble: " + nibble );
                return -1;
        }
    }

    /**
     * Turn a single bytes into two hex character representation.
     *
     * @param decoded the byte to serializeObject.
     * @param encoded the array to which each encoded nibbles are now ascii hex representations.
     */
    public static void encodeByteIntoTwoAsciiCharBytes( final int decoded, final byte[] encoded ) {


        encoded[ 0 ] = ( byte ) encodeNibbleToHexAsciiCharByte( ( decoded >> 4 ) & 0x0F );
        encoded[ 1 ] = ( byte ) encodeNibbleToHexAsciiCharByte(  decoded & 0x0F  );
    }

}
