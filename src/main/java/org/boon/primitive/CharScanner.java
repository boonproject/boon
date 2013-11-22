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
}
