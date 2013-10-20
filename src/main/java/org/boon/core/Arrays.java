package org.boon.core;


import java.lang.reflect.Array;
import java.util.Objects;

@SuppressWarnings({"unchecked", "SuspiciousSystemArraycopy"})
public class Arrays {


    public static <V> V[] array(Class<V> clasz, int size) {
        Object newArray = Array.newInstance(clasz, size);
        return (V[]) newArray;
    }


    public static <V> V[] grow(V[] array, int size) {
        Objects.requireNonNull(array);
        Object newArray = Array.newInstance(array.getClass().getComponentType(),
                array.length + size);
        System.arraycopy(array, 0, newArray, 0, array.length);
        return (V[]) newArray;
    }

    public static <V> V[] shrink(V[] array, int size) {
        Objects.requireNonNull(array);
        Object newArray = Array.newInstance(array.getClass().getComponentType(),
                array.length - size);
        System.arraycopy(array, 0, newArray, 0, array.length-size);
        return (V[]) newArray;
    }

    public static <V> V[] compact(V[] array) {
        Objects.requireNonNull(array);

        int nullCount = 0;
        for (V anArray1 : array) {

            if (anArray1 == null) {
                nullCount++;
            }
        }
        Object newArray = Array.newInstance(array.getClass().getComponentType(),
                array.length - nullCount);

        int j = 0;
        for (V anArray : array) {

            if (anArray == null) {
                continue;
            }

            Array.set(newArray, j, anArray);
            j++;
        }
        return (V[]) newArray;
    }

    @SafeVarargs
    public static <V> V[] array(final V... array) {
        return array;

    }

    /* Universal methods */

    public static <V> int len(V[] array) {
         return array.length;
    }

    public static <V> V idx (final V[] array, int index) {
        final int i = calculateIndex(array, index);

        return array[i];
    }


    public static <V> void idx (final V[] array, int index, V value) {
        final int i = calculateIndex(array, index);

        array[i] = value;
    }


    public static <V> V[] slc(V[] array, int startIndex, int endIndex) {
        Objects.requireNonNull(array);

        final int start = calculateIndex(array, startIndex);
        final int end = calculateIndex(array, endIndex);
        final int newLength = end - start + (endIndex < 0 ? 1 : 0);
        if (newLength <0 ) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format( "start index %d, end index %d, length %d",
                            startIndex, endIndex, array.length )
            );
        }

        Object newArray = Array.newInstance(array.getClass().getComponentType(), newLength);
        System.arraycopy(array, start, newArray, 0, newLength);
        return (V[]) newArray;
    }

    public static <V> boolean in(V value, V[] array) {
        for (V currentValue : array) {
            if (currentValue.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public static <V> V[] slc(V[] array, int startIndex) {
        Objects.requireNonNull(array);


        final int start = calculateIndex(array, startIndex);
        final int newLength = array.length - start;

        if (newLength <0 ) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format( "start index %d, length %d",
                            startIndex, array.length )
            );
        }

        Object newArray = Array.newInstance(array.getClass().getComponentType(), newLength);
        System.arraycopy(array, start, newArray, 0, newLength);
        return (V[]) newArray;
    }



    public static <V> V[] copy(V[] array) {
        Objects.requireNonNull(array);
        Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length);
        System.arraycopy(array, 0, newArray, 0, array.length);
        return (V[]) newArray;
    }



    public static <V> V[] add(V[] array, V v) {
        Objects.requireNonNull(array);
        Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length+1);
        System.arraycopy(array, 0, newArray, 0, array.length);
        Array.set(newArray, array.length, v);
        return (V[]) newArray;
    }


    public static <V> V[] add(V[] array, V[] array2) {
        Objects.requireNonNull(array);
        Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length + array2.length);
        System.arraycopy(array, 0, newArray, 0, array.length);
        System.arraycopy(array2, 0, newArray, array.length, array2.length);

        return (V[]) newArray;
    }

    public static <V> V[] insert(V[] array, int index, V v) {
        Objects.requireNonNull(array);
        Object newArray = Array.newInstance(array.getClass().getComponentType(), array.length+1);
        if (index != 0) {
            System.arraycopy(array, 0, newArray, 0, index );
        }

        if (index == array.length -1 ) {
            System.arraycopy(array, index, newArray, index + 1, array.length - index );

        } else {
            System.arraycopy(array, index, newArray, index + 1, array.length - index -1 );

        }

        Array.set(newArray, index, v);
        return (V[]) newArray;
    }

    public static <V> V[] slcEnd(V[] array, int endIndex) {
        Objects.requireNonNull(array);


        final int end = calculateIndex(array, endIndex);
        final int newLength = end +    (endIndex < 0 ? 1 : 0);

        if (newLength <0 ) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format( "end index %d, length %d",
                            endIndex, array.length )
            );
        }

        Object newArray = Array.newInstance(array.getClass().getComponentType(), newLength);
        System.arraycopy(array, 0, newArray, 0, newLength);
        return (V[]) newArray;
    }



    /* End universal methods. */
    private static <T> int calculateIndex(T[] array, int originalIndex) {
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
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(
                    String.format("Out Of Bounds: length was %s index is %s",
                            length, originalIndex
                    ));
        }
        if (index > length) {

            throw new ArrayIndexOutOfBoundsException(
                    String.format("Out Of Bounds: length was %s index is %s",
                            length, originalIndex
                    ));
        }
        return index;
    }

}
