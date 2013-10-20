package org.boon.core.primitive;


import java.util.Objects;

public class Byt {



    /**
     * Creates an array of bytes
     * @param size size of the array you want to make
     * @return
     */
    public static  byte[] arrayOfByte(final int size) {
        return new byte[size];
    }

    /**
     *
     * @param array
     * @return
     */
    public static byte[] array(final byte... array) {
        Objects.requireNonNull(array);
        return array;
    }

    public static int len(byte[] array) {
        return array.length;
    }



    public static byte idx (final byte[] array, final int index) {
        final int i = calculateIndex(array, index);

        return array[i];
    }


    public static void idx (final byte[] array, int index, byte value) {
        final int i = calculateIndex(array, index);

        array[i] = value;
    }



    public static byte[] slc(byte[] array, int startIndex, int endIndex) {
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

        byte [] newArray = new byte[newLength];
        System.arraycopy(array, start, newArray, 0, newLength);
        return newArray;
    }

    public static byte[] slc(byte[] array, int startIndex) {
        Objects.requireNonNull(array);

        final int start = calculateIndex(array, startIndex);
        final int newLength = array.length - start;

        if (newLength <0 ) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format( "start index %d, length %d",
                            startIndex, array.length )
            );
        }

        byte [] newArray = new byte[newLength];
        System.arraycopy(array, start, newArray, 0, newLength);
        return newArray;
    }

    public static byte[] slcEnd(byte[] array, int endIndex) {
        Objects.requireNonNull(array);

        final int end = calculateIndex(array, endIndex);
        final int newLength = end; // +    (endIndex < 0 ? 1 : 0);

        if (newLength <0 ) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format( "start index %d, length %d",
                            endIndex, array.length )
            );
        }

        byte [] newArray = new byte[newLength];
        System.arraycopy(array, 0, newArray, 0, newLength);
        return newArray;
    }

    public static boolean in(byte value, byte[] array) {
        for (byte currentValue : array) {
            if ( currentValue == value ) {
                return true;
            }
        }
        return false;
    }


    public static byte[] grow(byte [] array, final int size) {
        Objects.requireNonNull(array);

        byte [] newArray  = new byte[array.length + size];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }



    public static byte[] grow(byte [] array) {
        Objects.requireNonNull(array);

        byte [] newArray  = new byte[array.length *2];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }


    public static byte[] shrink(byte[] array, int size) {
        Objects.requireNonNull(array);

        byte[] newArray = new byte[array.length - size];

        System.arraycopy(array, 0, newArray, 0, array.length-size);
        return newArray;
    }


    public static byte[] compact(byte[] array) {
        Objects.requireNonNull(array);

        int nullCount = 0;
        for (byte ch : array) {

            if (ch == '\0') {
                nullCount++;
            }
        }
        byte [] newArray = new byte [array.length - nullCount];

        int j = 0;
        for (byte ch : array) {

            if (ch == '\0') {
                continue;
            }

            newArray[j] = ch;
            j++;
        }
        return newArray;
    }


    public static byte[] copy(byte[] array) {
        Objects.requireNonNull(array);
        byte[] newArray = new byte[array.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }


    public static byte[] add(byte[] array, byte v) {
        Objects.requireNonNull(array);
        byte[] newArray = new byte[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[array.length] = v;
        return newArray;
    }

    public static byte[] add(byte[] array, byte[] array2) {
        Objects.requireNonNull(array);
        byte[] newArray = new byte[array.length + array2.length];
        System.arraycopy(array, 0, newArray, 0, array.length);
        System.arraycopy(array2, 0, newArray, array.length, array2.length);
        return newArray;
    }



    public static byte[] insert(final byte[] array, final int idx, final byte v) {
        Objects.requireNonNull(array);

        if (idx >= array.length) {
            return add(array, v);
        }

        final int index = calculateIndex(array, idx);

        //Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length+1);
        byte [] newArray = new byte[array.length+1];

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


    public static byte[] insert(final byte[] array, final int fromIndex, final byte[] values) {
        Objects.requireNonNull(array);

        if (fromIndex >= array.length) {
            return add(array, values);
        }

        final int index = calculateIndex(array, fromIndex);

        //Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length+1);
        byte [] newArray = new byte[array.length +  values.length];

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
    private static int calculateIndex(byte[] array, int originalIndex) {
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
