package org.boon.primitive;

import org.boon.Universal;

import java.util.Objects;

public class Int {




    public static int[] grow(int [] array, final int size) {
        Objects.requireNonNull(array);

        int [] newArray  = new int[array.length + size];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }



    public static int[] grow(int [] array) {
        Objects.requireNonNull(array);

        int [] newArray  = new int[array.length *2];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }


    public static int[] shrink(int[] array, int size) {
        Objects.requireNonNull(array);

        int[] newArray = new int[array.length - size];

        System.arraycopy(array, 0, newArray, 0, array.length-size);
        return newArray;
    }


    public static int[] compact(int[] array) {
        Objects.requireNonNull(array);

        int nullCount = 0;
        for (int ch : array) {

            if (ch == '\0') {
                nullCount++;
            }
        }
        int [] newArray = new int [array.length - nullCount];

        int j = 0;
        for (int ch : array) {

            if (ch == '\0') {
                continue;
            }

            newArray[j] = ch;
            j++;
        }
        return newArray;
    }



    /**
     * Creates an array of bytes
     * @param size size of the array you want to make
     * @return
     */
    public static  int[] arrayOfInt(final int size) {
        return new int[size];
    }

    /**
     *
     * @param array
     * @return
     */
    @Universal
    public static int[] array(final int... array) {
        Objects.requireNonNull(array);
        return array;
    }


    @Universal
    public static int len(int[] array) {
        return array.length;
    }


    @Universal
    public static int idx (final int[] array, final int index) {
        final int i = calculateIndex(array, index);

        return array[i];
    }


    @Universal
    public static void idx (final int[] array, int index, int value) {
        final int i = calculateIndex(array, index);

        array[i] = value;
    }



    @Universal
    public static int[] slc(int[] array, int startIndex, int endIndex) {
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

        int [] newArray = new int[newLength];
        System.arraycopy(array, start, newArray, 0, newLength);
        return newArray;
    }

    @Universal
    public static int[] slc(int[] array, int startIndex) {
        Objects.requireNonNull(array);

        final int start = calculateIndex(array, startIndex);
        final int newLength = array.length - start;

        if (newLength <0 ) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format( "start index %d, length %d",
                            startIndex, array.length )
            );
        }

        int [] newArray = new int[newLength];
        System.arraycopy(array, start, newArray, 0, newLength);
        return newArray;
    }

    @Universal
    public static int[] slcEnd(int[] array, int endIndex) {
        Objects.requireNonNull(array);

        final int end = calculateIndex(array, endIndex);
        final int newLength = end; // +    (endIndex < 0 ? 1 : 0);

        if (newLength <0 ) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format( "start index %d, length %d",
                            endIndex, array.length )
            );
        }

        int [] newArray = new int[newLength];
        System.arraycopy(array, 0, newArray, 0, newLength);
        return newArray;
    }

    @Universal
    public static boolean in(int value, int[] array) {
        for (int currentValue : array) {
            if ( currentValue == value ) {
                return true;
            }
        }
        return false;
    }



    @Universal
    public static int[] copy(int[] array) {
        Objects.requireNonNull(array);
        int[] newArray = new int[array.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }


    @Universal
    public static int[] add(int[] array, int v) {
        Objects.requireNonNull(array);
        int[] newArray = new int[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = v;
        return newArray;
    }

    @Universal
    public static int[] add(int[] array, int[] array2) {
        Objects.requireNonNull(array);
        int[] newArray = new int[array.length + array2.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        System.arraycopy(array2, 0, newArray, array.length, array2.length);
        return newArray;
    }



    @Universal
    public static int[] insert(final int[] array, final int idx, final int v) {
        Objects.requireNonNull(array);

        if (idx >= array.length) {
            return add(array, v);
        }

        final int index = calculateIndex(array, idx);

        //Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length+1);
        int [] newArray = new int[array.length+1];

        if (index != 0) {
            /* Copy up to the length in the array before the index. */
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
    public static int[] insert(final int[] array, final int fromIndex, final int[] values) {
        Objects.requireNonNull(array);

        if (fromIndex >= array.length) {
            return add(array, values);
        }

        final int index = calculateIndex(array, fromIndex);

        //Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length+1);
        int [] newArray = new int[array.length +  values.length];

        if (index != 0) {
            /* Copy up to the length in the array before the index. */
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
    private static int calculateIndex(int[] array, int originalIndex) {
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
