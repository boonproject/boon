/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

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
