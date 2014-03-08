package org.boon.collections;

import java.util.AbstractList;

import static org.boon.Exceptions.die;
import static org.boon.primitive.Int.grow;

/**
 * Created by Richard on 2/18/14.
 */
public class IntList extends AbstractList<Integer> {

    private int [] values;
    int end;


    public IntList(int capacity) {
        this.values = new int[capacity];
    }


    public IntList() {
        this.values = new int[10];
    }

    @Override
    public Integer get(int index) {
        return values[index];
    }


    public int getInt(int index) {
        return values[index];
    }

    @Override
    public boolean add(Integer integer) {
        if (end + 1 >= values.length) {
            values = grow(values);
        }
        values [end] = integer;
        end++;
        return true;
    }

    public boolean addInt(int integer) {
        if (end + 1 >= values.length) {
            values = grow(values);
        }
        values [end] = integer;
        end++;
        return true;
    }


    public boolean addArray(int... integers) {
        if (end + integers.length >= values.length) {
            values = grow(values, (values.length + integers.length) * 2);
        }

        System.arraycopy(integers, 0, values, end, integers.length);
        end+=integers.length;
        return true;
    }

    @Override
    public Integer set(int index, Integer element) {
        int oldValue = values[index];
        values [index] = element;
        return oldValue;
    }


    public int setInt(int index, int element) {
        int oldValue = values[index];
        values [index] = element;
        return oldValue;
    }

    @Override
    public int size() {
        return end;
    }



    public int sum() {
        long sum = 0;
        for (int index = 0; index < end; index++ ) {
            sum+= values[index];
        }

        if (sum < Integer.MIN_VALUE) {
            die ("overflow the sum is too small", sum);
        }


        if (sum > Integer.MAX_VALUE) {
            die ("overflow the sum is too big", sum);
        }

        return (int) sum;


    }


    public int [] toValueArray() {

        return java.util.Arrays.copyOfRange(values, 0, end);
    }
}
