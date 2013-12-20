package org.boon.primitive;


import java.util.Objects;

public class CharScanner {


    public static final char[] DIGITS = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9'};


    public static boolean isDigit( char a ) {
        for ( int j = 0; j < DIGITS.length; j++ ) {
            if ( a == DIGITS[j] ) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDigits( final char[] inputArray ) {
        for ( int index = 0; index < inputArray.length; index++ ) {
            char a = inputArray[index];
            if ( !isDigit ( a ) ) {
                return false;
            }
        }
        return true;
    }

    public static char[][] splitExact( final char[] inputArray,
                                       final char split, final int resultsArrayLength ) {
        /** Holds the results. */
        char[][] results = new char[resultsArrayLength][];

        int resultIndex = 0;
        int startCurrentLineIndex = 0;
        int currentLineLength = 1;


        char c = '\u0000';
        int index = 0;

        for (; index < inputArray.length; index++, currentLineLength++ ) {
            c = inputArray[index];
            if ( c == split ) {

                results[resultIndex] = Chr.copy (
                        inputArray, startCurrentLineIndex, currentLineLength - 1 );
                startCurrentLineIndex = index + 1; //skip the char

                currentLineLength = 0;
                resultIndex++;
            }
        }

        if ( c != split ) {

            results[resultIndex] = Chr.copy (
                    inputArray, startCurrentLineIndex, currentLineLength - 1 );
            resultIndex++;
        }

        int actualLength = resultIndex;
        if ( actualLength < resultsArrayLength ) {
            final int newSize = resultsArrayLength - actualLength;
            results = __shrink ( results, newSize );
        }
        return results;
    }

    public static char[][] splitExact( final char[] inputArray,
                                       final int resultsArrayLength, char... delims ) {
        /** Holds the results. */
        char[][] results = new char[resultsArrayLength][];

        int resultIndex = 0;
        int startCurrentLineIndex = 0;
        int currentLineLength = 1;


        char c = '\u0000';
        int index = 0;
        int j;
        char split;


        for (; index < inputArray.length; index++, currentLineLength++ ) {
            c = inputArray[index];

            inner:
            for ( j = 0; j < delims.length; j++ ) {
                split = delims[j];
                if ( c == split ) {

                    results[resultIndex] = Chr.copy (
                            inputArray, startCurrentLineIndex, currentLineLength - 1 );
                    startCurrentLineIndex = index + 1; //skip the char

                    currentLineLength = 0;
                    resultIndex++;
                    break inner;
                }
            }
        }

        if ( !Chr.in ( c, delims ) ) {

            results[resultIndex] = Chr.copy (
                    inputArray, startCurrentLineIndex, currentLineLength - 1 );
            resultIndex++;
        }


        int actualLength = resultIndex;
        if ( actualLength < resultsArrayLength ) {
            final int newSize = resultsArrayLength - actualLength;
            results = __shrink ( results, newSize );
        }
        return results;
    }

    public static char[][] split( final char[] inputArray,
                                  final char split ) {
        /** Holds the results. */
        char[][] results = new char[16][];

        int resultIndex = 0;
        int startCurrentLineIndex = 0;
        int currentLineLength = 1;


        char c = '\u0000';
        int index = 0;

        for (; index < inputArray.length; index++, currentLineLength++ ) {
            c = inputArray[index];
            if ( c == split ) {

                if ( resultIndex == results.length ) {

                    results = _grow ( results );
                }


                results[resultIndex] = Chr.copy (
                        inputArray, startCurrentLineIndex, currentLineLength - 1 );
                startCurrentLineIndex = index + 1; //skip the char

                currentLineLength = 0;
                resultIndex++;
            }
        }

        if ( c != split ) {

            results[resultIndex] = Chr.copy (
                    inputArray, startCurrentLineIndex, currentLineLength - 1 );
            resultIndex++;
        }

        int actualLength = resultIndex;
        if ( actualLength < results.length ) {
            final int newSize = results.length - actualLength;
            results = __shrink ( results, newSize );
        }
        return results;
    }

    public static char[][] splitByChars( final char[] inputArray,
                                         final char... delims ) {
        /** Holds the results. */
        char[][] results = new char[16][];

        int resultIndex = 0;
        int startCurrentLineIndex = 0;
        int currentLineLength = 1;


        char c = '\u0000';
        int index = 0;
        int j;
        char split;


        for (; index < inputArray.length; index++, currentLineLength++ ) {

            c = inputArray[index];

            inner:
            for ( j = 0; j < delims.length; j++ ) {
                split = delims[j];
                if ( c == split ) {

                    if ( resultIndex == results.length ) {

                        results = _grow ( results );
                    }


                    results[resultIndex] = Chr.copy (
                            inputArray, startCurrentLineIndex, currentLineLength - 1 );
                    startCurrentLineIndex = index + 1; //skip the char

                    currentLineLength = 0;
                    resultIndex++;
                    break inner;
                }
            }
        }

        if ( !Chr.in ( c, delims ) ) {

            results[resultIndex] = Chr.copy (
                    inputArray, startCurrentLineIndex, currentLineLength - 1 );
            resultIndex++;
        }


        int actualLength = resultIndex;
        if ( actualLength < results.length ) {
            final int newSize = results.length - actualLength;
            results = __shrink ( results, newSize );
        }
        return results;
    }

    public static char[][] splitByCharsFromToDelims( final char[] inputArray, int from, int to,
                                                     final char... delims ) {
        /** Holds the results. */
        char[][] results = new char[16][];

        final int length = to - from;

        int resultIndex = 0;
        int startCurrentLineIndex = 0;
        int currentLineLength = 1;


        char c = '\u0000';
        int index = from;
        int j;
        char split;


        for (; index < length; index++, currentLineLength++ ) {

            c = inputArray[index];

            inner:
            for ( j = 0; j < delims.length; j++ ) {
                split = delims[j];
                if ( c == split ) {

                    if ( resultIndex == results.length ) {

                        results = _grow ( results );
                    }


                    results[resultIndex] = Chr.copy (
                            inputArray, startCurrentLineIndex, currentLineLength - 1 );
                    startCurrentLineIndex = index + 1; //skip the char

                    currentLineLength = 0;
                    resultIndex++;
                    break inner;
                }
            }
        }

        if ( !Chr.in ( c, delims ) ) {

            results[resultIndex] = Chr.copy (
                    inputArray, startCurrentLineIndex, currentLineLength - 1 );
            resultIndex++;
        }


        int actualLength = resultIndex;
        if ( actualLength < results.length ) {
            final int newSize = results.length - actualLength;
            results = __shrink ( results, newSize );
        }
        return results;
    }

    public static char[][] splitByCharsNoneEmpty( final char[] inputArray,
                                                  final char... delims ) {

        final char[][] results = splitByChars ( inputArray, delims );
        return compact ( results );
    }


    public static char[][] splitByCharsNoneEmpty( final char[] inputArray, int from, int to,
                                                  final char... delims ) {

        final char[][] results = splitByCharsFromToDelims ( inputArray, from, to, delims );
        return compact ( results );
    }

    public static char[][] compact( char[][] array ) {
        Objects.requireNonNull ( array );

        int nullCount = 0;
        for ( char[] ch : array ) {

            if ( ch == null || ch.length == 0 ) {
                nullCount++;
            }
        }
        char[][] newArray = new char[array.length - nullCount][];

        int j = 0;
        for ( char[] ch : array ) {

            if ( ch == null || ch.length == 0 ) {
                continue;
            }

            newArray[j] = ch;
            j++;
        }
        return newArray;
    }


    private static char[][] _grow( char[][] array ) {
        Objects.requireNonNull ( array );

        char[][] newArray = new char[array.length * 2][];
        System.arraycopy ( array, 0, newArray, 0, array.length );
        return newArray;
    }

    private static char[][] __shrink( char[][] array, int size ) {
        Objects.requireNonNull ( array );
        char[][] newArray = new char[array.length - size][];

        System.arraycopy ( array, 0, ( char[][] ) newArray, 0, array.length - size );
        return newArray;
    }


    final static String MIN_LONG_STR_NO_SIGN = String.valueOf ( Long.MIN_VALUE ).substring ( 1 );
    final static String MAX_LONG_STR = String.valueOf ( Long.MAX_VALUE );


    final static String MIN_INT_STR_NO_SIGN = String.valueOf ( Integer.MIN_VALUE ).substring ( 1 );
    final static String MAX_INT_STR = String.valueOf ( Integer.MAX_VALUE );

    public static boolean isLong( char[] digitChars, int offset, int len,
                                  boolean negative ) {
        String cmpStr = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
        int cmpLen = cmpStr.length ();
        if ( len < cmpLen ) return true;
        if ( len > cmpLen ) return false;

        for ( int i = 0; i < cmpLen; ++i ) {
            int diff = digitChars[offset + i] - cmpStr.charAt ( i );
            if ( diff != 0 ) {
                return ( diff < 0 );
            }
        }
        return true;
    }

    public static boolean isInteger( char[] digitChars, int offset, int len,
                                     boolean negative ) {
        String cmpStr = negative ? MIN_INT_STR_NO_SIGN : MAX_INT_STR;
        int cmpLen = cmpStr.length ();
        if ( len < cmpLen ) return true;
        if ( len > cmpLen ) return false;

        for ( int i = 0; i < cmpLen; ++i ) {
            int diff = digitChars[offset + i] - cmpStr.charAt ( i );
            if ( diff != 0 ) {
                return ( diff < 0 );
            }
        }
        return true;
    }

    public static int parseInt( char[] digitChars ) {
        return parseInt ( digitChars, 0, digitChars.length );
    }

    public static int parseInt( char[] digitChars, int offset, int len ) {
        int num = digitChars[offset] - '0';
        int to = len + offset;
        // This looks ugly, but appears the fastest way (as per measurements)
        if ( ++offset < to ) {
            num = ( num * 10 ) + ( digitChars[offset] - '0' );
            if ( ++offset < to ) {
                num = ( num * 10 ) + ( digitChars[offset] - '0' );
                if ( ++offset < to ) {
                    num = ( num * 10 ) + ( digitChars[offset] - '0' );
                    if ( ++offset < to ) {
                        num = ( num * 10 ) + ( digitChars[offset] - '0' );
                        if ( ++offset < to ) {
                            num = ( num * 10 ) + ( digitChars[offset] - '0' );
                            if ( ++offset < to ) {
                                num = ( num * 10 ) + ( digitChars[offset] - '0' );
                                if ( ++offset < to ) {
                                    num = ( num * 10 ) + ( digitChars[offset] - '0' );
                                    if ( ++offset < to ) {
                                        num = ( num * 10 ) + ( digitChars[offset] - '0' );
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


    public static int parseIntFromTo( char[] digitChars, int offset, int to ) {
        int num = digitChars[offset] - '0';
        // This looks ugly, but appears the fastest way (as per measurements)
        if ( ++offset < to ) {
            num = ( num * 10 ) + ( digitChars[offset] - '0' );
            if ( ++offset < to ) {
                num = ( num * 10 ) + ( digitChars[offset] - '0' );
                if ( ++offset < to ) {
                    num = ( num * 10 ) + ( digitChars[offset] - '0' );
                    if ( ++offset < to ) {
                        num = ( num * 10 ) + ( digitChars[offset] - '0' );
                        if ( ++offset < to ) {
                            num = ( num * 10 ) + ( digitChars[offset] - '0' );
                            if ( ++offset < to ) {
                                num = ( num * 10 ) + ( digitChars[offset] - '0' );
                                if ( ++offset < to ) {
                                    num = ( num * 10 ) + ( digitChars[offset] - '0' );
                                    if ( ++offset < to ) {
                                        num = ( num * 10 ) + ( digitChars[offset] - '0' );
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

    public static int parseIntIgnoreDot( char[] digitChars, int offset, int len ) {
        int num = digitChars[offset] - '0';
        int to = len + offset;
        // This looks ugly, but appears the fastest way (as per measurements)
        if ( ++offset < to ) {
            num = digitChars[offset] != '.' ? ( num * 10 ) + ( digitChars[offset] - '0' ) : num;
            if ( ++offset < to ) {
                num = digitChars[offset] != '.' ? ( num * 10 ) + ( digitChars[offset] - '0' ) : num;
                if ( ++offset < to ) {
                    num = digitChars[offset] != '.' ? ( num * 10 ) + ( digitChars[offset] - '0' ) : num;
                    if ( ++offset < to ) {
                        num = digitChars[offset] != '.' ? ( num * 10 ) + ( digitChars[offset] - '0' ) : num;
                        if ( ++offset < to ) {
                            num = digitChars[offset] != '.' ? ( num * 10 ) + ( digitChars[offset] - '0' ) : num;
                            if ( ++offset < to ) {
                                num = digitChars[offset] != '.' ? ( num * 10 ) + ( digitChars[offset] - '0' ) : num;
                                if ( ++offset < to ) {
                                    num = digitChars[offset] != '.' ? ( num * 10 ) + ( digitChars[offset] - '0' ) : num;
                                    if ( ++offset < to ) {
                                        num = digitChars[offset] != '.' ? ( num * 10 ) + ( digitChars[offset] - '0' ) : num;
                                        if ( ++offset < to ) {
                                            num = digitChars[offset] != '.' ? ( num * 10 ) + ( digitChars[offset] - '0' ) : num;
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

    public static long parseLong( char[] digitChars, int offset, int len ) {
        int len1 = len - 9;
        long val = parseInt ( digitChars, offset, len1 ) * L_BILLION;
        return val + ( long ) parseInt ( digitChars, offset + len1, 9 );
    }

    public static long parseLongIgnoreDot( char[] digitChars, int offset, int len ) {
        int len1 = len - 9;
        long val = parseIntIgnoreDot ( digitChars, offset, len1 ) * L_BILLION;
        return val + ( long ) parseIntIgnoreDot ( digitChars, offset + len1, 9 );
    }

    private final static long L_BILLION = 1000000000;


    public static double doubleValue( char[] buffer, int startIndex, int endIndex ) {

        boolean simple = true;
        int digitsPastPoint = 0;
        boolean foundPoint = false;
        boolean negative = false;

        double sign;

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
                if ( digitsPastPoint >= powersOf10.length ) {
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

        return Double.parseDouble ( new String ( buffer, startIndex, ( endIndex - startIndex ) ) ) * sign;
    }


    public static double simpleDouble( char[] buffer, boolean simple, boolean negative, int digitsPastPoint, int startIndex, int endIndex ) {

        double sign;

        if ( negative ) {
            sign = -1.0;
        } else {
            sign = 1.0;
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

        return Double.parseDouble ( new String ( buffer, startIndex, ( endIndex - startIndex ) ) ) * sign;
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


}
