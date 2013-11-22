package org.boon.primitive;

import java.util.Objects;

public class ByteScanner {


    public static byte[][] splitExact( final byte[] inputArray,
                                       final int split, final int resultsArrayLength ) {
        /** Holds the results. */
        byte[][] results = new byte[resultsArrayLength][];

        int resultIndex = 0;
        int startCurrentLineIndex = 0;
        int currentLineLength = 1;


        byte c = 0;
        int index = 0;

        for (; index < inputArray.length; index++, currentLineLength++ ) {
            c = inputArray[index];
            if ( c == split ) {

                results[resultIndex] = Byt.copy (
                        inputArray, startCurrentLineIndex, currentLineLength - 1 );
                startCurrentLineIndex = index + 1; //skip the byte

                currentLineLength = 0;
                resultIndex++;
            }
        }

        if ( c != split ) {

            results[resultIndex] = Byt.copy (
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

    public static byte[][] splitExact( final byte[] inputArray,
                                       final int resultsArrayLength, int... delims ) {
        /** Holds the results. */
        byte[][] results = new byte[resultsArrayLength][];

        int resultIndex = 0;
        int startCurrentLineIndex = 0;
        int currentLineLength = 1;


        byte c = '\u0000';
        int index = 0;
        int j;
        int split;


        for (; index < inputArray.length; index++, currentLineLength++ ) {
            c = inputArray[index];

            inner:
            for ( j = 0; j < delims.length; j++ ) {
                split = delims[j];
                if ( c == split ) {

                    results[resultIndex] = Byt.copy (
                            inputArray, startCurrentLineIndex, currentLineLength - 1 );
                    startCurrentLineIndex = index + 1; //skip the byte

                    currentLineLength = 0;
                    resultIndex++;
                    break inner;
                }
            }
        }

        if ( !Byt.inIntArray ( c, delims ) ) {

            results[resultIndex] = Byt.copy (
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

    public static byte[][] split( final byte[] inputArray,
                                  final int split ) {
        /** Holds the results. */
        byte[][] results = new byte[16][];

        int resultIndex = 0;
        int startCurrentLineIndex = 0;
        int currentLineLength = 1;


        byte c = '\u0000';
        int index = 0;

        for (; index < inputArray.length; index++, currentLineLength++ ) {
            c = inputArray[index];
            if ( c == split ) {

                if ( resultIndex == results.length ) {

                    results = _grow ( results );
                }


                results[resultIndex] = Byt.copy (
                        inputArray, startCurrentLineIndex, currentLineLength - 1 );
                startCurrentLineIndex = index + 1; //skip the byte

                currentLineLength = 0;
                resultIndex++;
            }
        }

        if ( c != split ) {

            results[resultIndex] = Byt.copy (
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

    public static byte[][] splitByChars( final byte[] inputArray,
                                         int... delims ) {
        /** Holds the results. */
        byte[][] results = new byte[16][];

        int resultIndex = 0;
        int startCurrentLineIndex = 0;
        int currentLineLength = 1;


        byte c = '\u0000';
        int index = 0;
        int j;
        int split;


        for (; index < inputArray.length; index++, currentLineLength++ ) {

            c = inputArray[index];

            inner:
            for ( j = 0; j < delims.length; j++ ) {
                split = delims[j];
                if ( c == split ) {

                    if ( resultIndex == results.length ) {

                        results = _grow ( results );
                    }


                    results[resultIndex] = Byt.copy (
                            inputArray, startCurrentLineIndex, currentLineLength - 1 );
                    startCurrentLineIndex = index + 1; //skip the byte

                    currentLineLength = 0;
                    resultIndex++;
                    break inner;
                }
            }
        }

        if ( !Byt.inIntArray ( c, delims ) ) {

            results[resultIndex] = Byt.copy (
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

    private static byte[][] _grow( byte[][] array ) {
        Objects.requireNonNull ( array );

        byte[][] newArray = new byte[array.length * 2][];
        System.arraycopy ( array, 0, newArray, 0, array.length );
        return newArray;
    }

    private static byte[][] __shrink( byte[][] array, int size ) {
        Objects.requireNonNull ( array );
        byte[][] newArray = new byte[array.length - size][];

        System.arraycopy ( array, 0, newArray, 0, array.length - size );
        return newArray;
    }

}
