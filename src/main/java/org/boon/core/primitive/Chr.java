package org.boon.core.primitive;


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
    public static char[] array(final char... array) {
        Objects.requireNonNull(array);
        return array;
    }

    public static int len(char[] array) {
        return array.length;
    }



    public static char idx (final char[] array, final int index) {
        final int i = calculateIndex(array, index);

        return array[i];
    }


    public static void idx (final char[] array, int index, char value) {
        final int i = calculateIndex(array, index);

        array[i] = value;
    }



    public static char[] slc(char[] array, int startIndex, int endIndex) {
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

    public static char[] slc(char[] array, int startIndex) {
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

    public static char[] slcEnd(char[] array, int endIndex) {
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

    public static boolean in(char value, char[] array) {
        for (char currentValue : array) {
            if ( currentValue == value ) {
                return true;
            }
        }
        return false;
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


    public static char[] copy(char[] array) {
        Objects.requireNonNull(array);
        char[] newArray = new char[array.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }


    public static char[] add(char[] array, char v) {
        Objects.requireNonNull(array);
        char[] newArray = new char[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = v;
        return newArray;
    }

    public static char[] add(char[] array, char[] array2) {
        Objects.requireNonNull(array);
        char[] newArray = new char[array.length + array2.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        System.arraycopy(array2, 0, newArray, array.length, array2.length);
        return newArray;
    }



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


}
