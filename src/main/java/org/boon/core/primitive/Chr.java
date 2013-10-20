package org.boon.core.primitive;


import java.util.Objects;

public class Chr {



    public static  char[] arrayOfChar(final int size) {
        return new char[size];
    }

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


//
//    public static <V> V[] insert(V[] array, int index, V v) {
//        Objects.requireNonNull(array);
//        Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length+1);
//        if (index != 0) {
//            System.arraycopy(array, 0, newArray, 0, index );
//        }
//
//        if (index == array.length -1 ) {
//            System.arraycopy(array, index, newArray, index + 1, array.length - index );
//
//        } else {
//            System.arraycopy(array, index, newArray, index + 1, array.length - index -1 );
//
//        }
//
//        Array.set(newArray, index, v);
//        return (V[]) newArray;
//    }
//
//
//
//
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
