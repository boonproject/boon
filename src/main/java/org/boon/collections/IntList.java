package org.boon.collections;

import org.boon.Arrays;

import java.util.AbstractList;

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


    public int [] toValueArray() {

        return java.util.Arrays.copyOfRange(values, 0, end);
    }
}
