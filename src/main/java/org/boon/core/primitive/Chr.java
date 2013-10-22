package org.boon.core.primitive;


import org.boon.core.Universal;

import java.lang.reflect.Array;
import java.util.Objects;

public class Chr {


    /**
     * Creates an array of chars
     * @param size size of the array you want to make
     * @return
     */
    public static  char[] arrayOfChar(final int size) {
        return new char[size];
    }


    /**
     *
     * @param array
     * @return
     */
    @Universal
    public static char[] array( final char... array ) {
        Objects.requireNonNull(array);
        return array;
    }

    @Universal
    public static char[] chars( final String array ) {
        return array.toCharArray();
    }

    @Universal
    public static int len( char[] array ) {
        return array.length;
    }


    @Universal
    public static char idx ( final char[] array, final int index ) {
        final int i = calculateIndex(array, index);

        return array[i];
    }


    @Universal
    public static void idx ( final char[] array, int index, char value ) {
        final int i = calculateIndex(array, index);

        array[i] = value;
    }



    @Universal
    public static char[] slc( char[] array, int startIndex, int endIndex ) {
        Objects.requireNonNull(array);

        final int start = calculateIndex(array, startIndex);
        final int end = calculateIndex(array, endIndex);
        final int newLength = end - start;

        if (newLength <0 ) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format( "start index %d, end index %d, length %d",
                            startIndex, endIndex, array.length )
            );
        }

        char [] newArray = new char[newLength];
        System.arraycopy(array, start, newArray, 0, newLength);
        return newArray;
    }

    @Universal
    public static char[] slc( char[] array, int startIndex ) {
        Objects.requireNonNull(array);

        final int start = calculateIndex(array, startIndex);
        final int newLength = array.length - start;

        if (newLength <0 ) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format( "start index %d, length %d",
                            startIndex, array.length )
            );
        }

        char [] newArray = new char[newLength];
        System.arraycopy(array, start, newArray, 0, newLength);
        return newArray;
    }

    @Universal
    public static char[] slcEnd( char[] array, int endIndex ) {
        Objects.requireNonNull(array);

        final int end = calculateIndex(array, endIndex);
        final int newLength = end; // +    (endIndex < 0 ? 1 : 0);

        if (newLength <0 ) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format( "start index %d, length %d",
                            endIndex, array.length )
            );
        }

        char [] newArray = new char[newLength];
        System.arraycopy(array, 0, newArray, 0, newLength);
        return newArray;
    }

    @Universal
    public static boolean in( char value, char[] array ) {
        for (char currentValue : array) {
            if ( currentValue == value ) {
                return true;
            }
        }
        return false;
    }

    @Universal
    public static boolean in( char value, int offset, char[] array ) {
        for ( int index = offset; index < array.length; index++ ) {
            char currentValue = array[index];
            if ( currentValue == value ) {
                return true;
            }
        }
        return false;
    }

    @Universal
    public static boolean in( char value, int offset, int end, char[] array ) {
        for ( int index = offset; index < end; index++ ) {
            char currentValue = array[index];
            if ( currentValue == value ) {
                return true;
            }
        }
        return false;
    }


    public static char[][] splitExact( final char[] inputArray,
                                 final char split, final int resultsArrayLength ) {
        /** Holds the results. */
        char[][] results = new char[resultsArrayLength][];

        int resultIndex = 0;
        int startCurrentLineIndex=0;
        int currentLineLength=1;


        char c='\u0000';
        int index = 0;

        for ( ; index < inputArray.length; index++, currentLineLength++ ) {
            c = inputArray[index];
            if (c == split) {

                results[ resultIndex ] = copy(
                        inputArray, startCurrentLineIndex, currentLineLength -1 );
                startCurrentLineIndex = index + 1; //skip the char

                currentLineLength=0;
                resultIndex++;
            }
        }

        if ( c !=  split ) {

            results[ resultIndex ] = copy(
                    inputArray, startCurrentLineIndex, currentLineLength -1 );
            resultIndex++;
        }

        int actualLength = resultIndex;
        if ( actualLength < resultsArrayLength) {
            final int newSize = resultsArrayLength - actualLength;
            results = __shrink(results, newSize);
        }
        return results;
    }

    public static char[][] splitExact(final char[] inputArray,
                                      final int resultsArrayLength, char... delims) {
        /** Holds the results. */
        char[][] results = new char[resultsArrayLength][];

        int resultIndex = 0;
        int startCurrentLineIndex=0;
        int currentLineLength=1;


        char c='\u0000';
        int index = 0;
        int j;
        char split;


        for ( ; index < inputArray.length; index++, currentLineLength++ ) {
            c = inputArray[index];

            inner:
            for ( j = 0; j < delims.length; j++) {
                split = delims[j];
                if (c == split) {

                    results[ resultIndex ] = copy(
                        inputArray, startCurrentLineIndex, currentLineLength -1 );
                    startCurrentLineIndex = index + 1; //skip the char

                    currentLineLength=0;
                    resultIndex++;
                    break inner;
                }
            }
        }

        if ( !in(c, delims) ) {

                results[ resultIndex ] = copy(
                        inputArray, startCurrentLineIndex, currentLineLength -1 );
                resultIndex++;
        }


        int actualLength = resultIndex;
        if ( actualLength < resultsArrayLength) {
            final int newSize = resultsArrayLength - actualLength;
            results = __shrink(results, newSize);
        }
        return results;
    }


    public static char[][] split( final char[] inputArray,
                                  final char split) {
        /** Holds the results. */
        char[][] results = new char[16][];

        int resultIndex = 0;
        int startCurrentLineIndex=0;
        int currentLineLength=1;


        char c='\u0000';
        int index = 0;

        for ( ; index < inputArray.length; index++, currentLineLength++ ) {
            c = inputArray[index];
            if (c == split) {

                if (resultIndex == results.length ) {

                    results = _grow(results);
                }


                results[ resultIndex ] = copy(
                        inputArray, startCurrentLineIndex, currentLineLength -1 );
                startCurrentLineIndex = index + 1; //skip the char

                currentLineLength=0;
                resultIndex++;
            }
        }

        if ( c !=  split ) {

            results[ resultIndex ] = copy(
                    inputArray, startCurrentLineIndex, currentLineLength -1 );
            resultIndex++;
        }

        int actualLength = resultIndex;
        if ( actualLength < results.length) {
            final int newSize = results.length - actualLength;
            results = __shrink(results, newSize);
        }
        return results;
    }

    public static char[][] splitByChars( final char[] inputArray,
                                  char... delims ) {
        /** Holds the results. */
        char[][] results = new char[16][];

        int resultIndex = 0;
        int startCurrentLineIndex=0;
        int currentLineLength=1;


        char c='\u0000';
        int index = 0;
        int j;
        char split;


        for ( ; index < inputArray.length; index++, currentLineLength++ ) {

            c = inputArray[index];

            inner:
            for ( j = 0; j < delims.length; j++) {
                split = delims[j];
                if (c == split) {

                    if (resultIndex == results.length ) {

                        results = _grow(results);
                    }


                    results[ resultIndex ] = copy(
                            inputArray, startCurrentLineIndex, currentLineLength -1 );
                    startCurrentLineIndex = index + 1; //skip the char

                    currentLineLength=0;
                    resultIndex++;
                    break inner;
                }
            }
        }

        if ( !in(c, delims) ) {

            results[ resultIndex ] = copy(
                    inputArray, startCurrentLineIndex, currentLineLength -1 );
            resultIndex++;
        }


        int actualLength = resultIndex;
        if ( actualLength < results.length) {
            final int newSize = results.length - actualLength;
            results = __shrink(results, newSize);
        }
        return results;
    }

    public static char[] grow(char [] array, final int size) {
        Objects.requireNonNull(array);

        char [] newArray  = new char[array.length + size];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }



    public static char[] grow(char [] array) {
        Objects.requireNonNull(array);

        char [] newArray  = new char[array.length *2];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    private static char[][] _grow(char [][] array) {
        Objects.requireNonNull(array);

        char [][] newArray  = new char[array.length *2][];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    public static char[] shrink(char[] array, int size) {
        Objects.requireNonNull(array);

        char[] newArray = new char[array.length - size];

        System.arraycopy(array, 0, newArray, 0, array.length-size);
        return newArray;
    }


    public static char[] compact(char[] array) {
        Objects.requireNonNull(array);

        int nullCount = 0;
        for (char ch : array) {

            if (ch == '\0') {
                nullCount++;
            }
        }
        char [] newArray = new char [array.length - nullCount];

        int j = 0;
        for (char ch : array) {

            if (ch == '\0') {
                continue;
            }

            newArray[j] = ch;
            j++;
        }
        return newArray;
    }


    @Universal
    public static char[] copy(char[] array) {
        Objects.requireNonNull(array);
        char[] newArray = new char[array.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    @Universal
    public static char[] copy(char[] array, int offset, int length) {
        Objects.requireNonNull(array);
        char[] newArray = new char[length];
        System.arraycopy(array, offset, newArray, 0, length);
        return newArray;
    }


    @Universal
    public static char[] add(char[] array, char v) {
        Objects.requireNonNull(array);
        char[] newArray = new char[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = v;
        return newArray;
    }


    @Universal
    public static char[] add(char[] array, String str) {
          return add(array, str.toCharArray());
    }

    @Universal
    public static char[] add(char[] array, StringBuilder stringBuilder) {
        return add(array, getCharsFromStringBuilder(stringBuilder));
    }


    @Universal
    public static char[] add(char[] array, char[] array2) {
        Objects.requireNonNull(array);
        char[] newArray = new char[array.length + array2.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        System.arraycopy(array2, 0, newArray, array.length, array2.length);
        return newArray;
    }



    @Universal
    public static char[] insert(final char[] array, final int idx, final char v) {
        Objects.requireNonNull(array);

        if (idx >= array.length) {
           return add(array, v);
        }

        final int index = calculateIndex(array, idx);

        //Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length+1);
        char [] newArray = new char[array.length+1];

        if (index != 0) {
            /* Copy up to the location in the array before the index. */
            /*                 src     sbegin  dst       dbegin   length of copy */
            System.arraycopy( array,   0,      newArray, 0,       index );
        }


        boolean lastIndex = index == array.length -1;
        int remainingIndex = array.length - index;

        if (lastIndex ) {
            /* Copy the area after the insert. Make sure we don't write over the end. */
            /*                 src  sbegin   dst       dbegin     length of copy */
            System.arraycopy(array, index,   newArray, index + 1, remainingIndex );

        } else {
            /* Copy the area after the insert.  */
            /*                 src  sbegin   dst       dbegin     length of copy */
            System.arraycopy(array, index,   newArray, index + 1, remainingIndex );

        }

        newArray[index] = v;
        return  newArray;
    }


    @Universal
    public static char[] insert(final char[] array, final int fromIndex, String values) {
        return insert(array, fromIndex, values.toCharArray());
    }


    @Universal
    public static char[] insert(final char[] array, final int fromIndex, StringBuilder values) {
        return insert(array, fromIndex, getCharsFromStringBuilder(values));
    }


    @Universal
    public static char[] insert(final char[] array, final int fromIndex, final char[] values) {
        Objects.requireNonNull(array);

        if (fromIndex >= array.length) {
            return add(array, values);
        }

        final int index = calculateIndex(array, fromIndex);

        //Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length+1);
        char [] newArray = new char[array.length +  values.length];

        if (index != 0) {
            /* Copy up to the location in the array before the index. */
            /*                 src     sbegin  dst       dbegin   length of copy */
            System.arraycopy( array,   0,      newArray, 0,       index );
        }


        boolean lastIndex = index == array.length -1;

        int toIndex = index + values.length;
        int remainingIndex = newArray.length - toIndex;

        if (lastIndex ) {
            /* Copy the area after the insert. Make sure we don't write over the end. */
            /*                 src  sbegin   dst       dbegin     length of copy */
            System.arraycopy(array, index,   newArray, index + values.length, remainingIndex );

        } else {
            /* Copy the area after the insert.  */
            /*                 src  sbegin   dst       dbegin     length of copy */
            System.arraycopy(array, index,   newArray, index + values.length, remainingIndex );

        }

        for (int i = index, j=0; i < toIndex; i++, j++) {
            newArray[ i ] = values[ j ];
        }
        return  newArray;
    }



    /* End universal methods. */


    private static char[] getCharsFromStringBuilder(StringBuilder sbuf) {
        int length = sbuf.length();
        char [] array2 = new char [sbuf.length()];
        sbuf.getChars(0, sbuf.length(), array2, 0);
        return  array2;
    }

    private static int calculateIndex(char[] array, int originalIndex) {
        final int length = array.length;

        Objects.requireNonNull(array, "array cannot be null");


        int index = originalIndex;

        /* Adjust for reading from the right as in
        -1 reads the 4th element if the length is 5
         */
        if (index < 0) {
            index = length + index;
        }

        /* Bounds check
            if it is still less than 0, then they
            have an negative index that is greater than length
         */
         /* Bounds check
            if it is still less than 0, then they
            have an negative index that is greater than length
         */
        if (index < 0) {
            index = 0;
        }
        if (index >= length) {
            index = length -1;
        }
        return index;
    }



    private static char[][] __shrink(char[][] array, int size) {
        Objects.requireNonNull(array);
        char[][] newArray = new char[ array.length - size ][];

        System.arraycopy(array, 0, (char[][])newArray, 0, array.length-size);
        return newArray;
    }


}
