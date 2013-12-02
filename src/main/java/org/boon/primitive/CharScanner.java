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



    final static String MIN_LONG_STR_NO_SIGN = String.valueOf(Long.MIN_VALUE).substring(1);
    final static String MAX_LONG_STR = String.valueOf(Long.MAX_VALUE);


    final static String MIN_INT_STR_NO_SIGN = String.valueOf(Integer.MIN_VALUE).substring(1);
    final static String MAX_INT_STR = String.valueOf(Integer.MAX_VALUE);

    public static boolean isLong(char[] digitChars, int offset, int len,
                                      boolean negative) {
        String cmpStr = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
        int cmpLen = cmpStr.length();
        if (len < cmpLen) return true;
        if (len > cmpLen) return false;

        for (int i = 0; i < cmpLen; ++i) {
            int diff = digitChars[offset+i] - cmpStr.charAt(i);
            if (diff != 0) {
                return (diff < 0);
            }
        }
        return true;
    }

    public static boolean isInteger(char[] digitChars, int offset, int len,
                                 boolean negative) {
        String cmpStr = negative ? MIN_INT_STR_NO_SIGN : MAX_INT_STR;
        int cmpLen = cmpStr.length();
        if (len < cmpLen) return true;
        if (len > cmpLen) return false;

        for (int i = 0; i < cmpLen; ++i) {
            int diff = digitChars[offset+i] - cmpStr.charAt(i);
            if (diff != 0) {
                return (diff < 0);
            }
        }
        return true;
    }

    public static int parseInt(char[] digitChars, int offset, int len) {
        int num = digitChars[offset] - '0';
        int to =   len + offset;
        // This looks ugly, but appears the fastest way (as per measurements)
        if (++offset < to) {
            num = (num * 10) + (digitChars[offset] - '0');
            if (++offset < to) {
                num = (num * 10) + (digitChars[offset] - '0');
                if (++offset < to) {
                    num = (num * 10) + (digitChars[offset] - '0');
                    if (++offset < to) {
                        num = (num * 10) + (digitChars[offset] - '0');
                        if (++offset < to) {
                            num = (num * 10) + (digitChars[offset] - '0');
                            if (++offset < to) {
                                num = (num * 10) + (digitChars[offset] - '0');
                                if (++offset < to) {
                                    num = (num * 10) + (digitChars[offset] - '0');
                                    if (++offset < to) {
                                        num = (num * 10) + (digitChars[offset] - '0');
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



    public static int parseIntIgnoreDot(char[] digitChars, int offset, int len) {
        int num = digitChars[offset] - '0';
        int to =   len + offset;
        // This looks ugly, but appears the fastest way (as per measurements)
        if (++offset < to) {
            num = digitChars[offset] != '.' ? (num * 10) + (digitChars[offset] - '0') : num;
            if (++offset < to) {
                num = digitChars[offset] != '.' ? (num * 10) + (digitChars[offset] - '0') : num;
                if (++offset < to) {
                    num = digitChars[offset] != '.' ? (num * 10) + (digitChars[offset] - '0') : num;
                    if (++offset < to) {
                        num = digitChars[offset] != '.' ? (num * 10) + (digitChars[offset] - '0') : num;
                        if (++offset < to) {
                            num = digitChars[offset] != '.' ? (num * 10) + (digitChars[offset] - '0') : num;
                            if (++offset < to) {
                                num = digitChars[offset] != '.' ? (num * 10) + (digitChars[offset] - '0') : num;
                                if (++offset < to) {
                                    num = digitChars[offset] != '.' ? (num * 10) + (digitChars[offset] - '0') : num;
                                    if (++offset < to) {
                                        num = digitChars[offset] != '.' ? (num * 10) + (digitChars[offset] - '0') : num;
                                        if (++offset < to) {
                                            num = digitChars[offset] != '.' ? (num * 10) + (digitChars[offset] - '0') : num;
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

    public static long parseLong(char[] digitChars, int offset, int len) {
        int len1 = len-9;
        long val = parseInt(digitChars, offset, len1) * L_BILLION;
        return val + (long) parseInt(digitChars, offset+len1, 9);
    }

    public static long parseLongIgnoreDot(char[] digitChars, int offset, int len) {
        int len1 = len-9;
        long val = parseIntIgnoreDot (digitChars, offset, len1) * L_BILLION;
        return val + (long) parseIntIgnoreDot(digitChars, offset+len1, 9);
    }

    private final static long L_BILLION = 1000000000;

}
