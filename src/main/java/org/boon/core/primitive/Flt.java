package org.boon.core.primitive;

import org.boon.core.Universal;

import java.util.Objects;

public class Flt {




    public static float[] grow(float [] array, final int size) {
        Objects.requireNonNull(array);

        float [] newArray  = new float[array.length + size];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }



    public static float[] grow(float [] array) {
        Objects.requireNonNull(array);

        float [] newArray  = new float[array.length *2];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }


    public static float[] shrink(float[] array, int size) {
        Objects.requireNonNull(array);

        float[] newArray = new float[array.length - size];

        System.arraycopy(array, 0, newArray, 0, array.length-size);
        return newArray;
    }


    public static float[] compact(float[] array) {
        Objects.requireNonNull(array);

        int nullCount = 0;
        for (float ch : array) {

            if (ch == '\0') {
                nullCount++;
            }
        }
        float [] newArray = new float [array.length - nullCount];

        int j = 0;
        for (float ch : array) {

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
    public static  float[] arrayOfFloat(final int size) {
        return new float[size];
    }

    /**
     *
     * @param array
     * @return
     */
    @Universal
    public static float[] array(final float... array) {
        Objects.requireNonNull(array);
        return array;
    }


    @Universal
    public static int len(float[] array) {
        return array.length;
    }


    @Universal
    public static float idx (final float[] array, final int index) {
        final int i = calculateIndex(array, index);

        return array[i];
    }


    @Universal
    public static void idx (final float[] array, int index, float value) {
        final int i = calculateIndex(array, index);

        array[i] = value;
    }



    @Universal
    public static float[] slc(float[] array, int startIndex, int endIndex) {
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

        float [] newArray = new float[newLength];
        System.arraycopy(array, start, newArray, 0, newLength);
        return newArray;
    }

    @Universal
    public static float[] slc(float[] array, int startIndex) {
        Objects.requireNonNull(array);

        final int start = calculateIndex(array, startIndex);
        final int newLength = array.length - start;

        if (newLength <0 ) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format( "start index %d, length %d",
                            startIndex, array.length )
            );
        }

        float [] newArray = new float[newLength];
        System.arraycopy(array, start, newArray, 0, newLength);
        return newArray;
    }

    @Universal
    public static float[] slcEnd(float[] array, int endIndex) {
        Objects.requireNonNull(array);

        final int end = calculateIndex(array, endIndex);
        final int newLength = end; // +    (endIndex < 0 ? 1 : 0);

        if (newLength <0 ) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format( "start index %d, length %d",
                            endIndex, array.length )
            );
        }

        float [] newArray = new float[newLength];
        System.arraycopy(array, 0, newArray, 0, newLength);
        return newArray;
    }

    @Universal
    public static boolean in(float value, float[] array) {
        for (float currentValue : array) {
            if ( currentValue == value ) {
                return true;
            }
        }
        return false;
    }



    @Universal
    public static float[] copy(float[] array) {
        Objects.requireNonNull(array);
        float[] newArray = new float[array.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }


    @Universal
    public static float[] add(float[] array, float v) {
        Objects.requireNonNull(array);
        float[] newArray = new float[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = v;
        return newArray;
    }

    @Universal
    public static float[] add(float[] array, float[] array2) {
        Objects.requireNonNull(array);
        float[] newArray = new float[array.length + array2.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        System.arraycopy(array2, 0, newArray, array.length, array2.length);
        return newArray;
    }



    @Universal
    public static float[] insert(final float[] array, final int idx, final float v) {
        Objects.requireNonNull(array);

        if (idx >= array.length) {
            return add(array, v);
        }

        final int index = calculateIndex(array, idx);

        //Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length+1);
        float [] newArray = new float[array.length+1];

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
    public static float[] insert(final float[] array, final int fromIndex, final float[] values) {
        Objects.requireNonNull(array);

        if (fromIndex >= array.length) {
            return add(array, values);
        }

        final int index = calculateIndex(array, fromIndex);

        //Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length+1);
        float [] newArray = new float[array.length +  values.length];

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
    private static int calculateIndex(float[] array, int originalIndex) {
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
