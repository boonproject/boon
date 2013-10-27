/**
 * Copyright 2013 Rick Hightower
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.boon.datarepo;

import org.boon.criteria.Update;

import java.util.Collection;
import java.util.List;

public interface ObjectEditor<KEY, ITEM> extends Bag<ITEM> {


    ITEM get(KEY key);

    KEY getKey(ITEM item);


    void put(ITEM item);

    void removeByKey(KEY key);

    void removeAll(ITEM... items);

    void removeAllAsync(Collection<ITEM> items);

    void addAll(ITEM... items);

    void addAllAsync(Collection<ITEM> items);

    void modifyAll(ITEM... items);

    void modifyAll(Collection<ITEM> items);


    void modify(ITEM item);

    /* Does basic conversion */
    void modifyByValue(ITEM item, String property, String value);

    void modify(ITEM item, String property, Object value);

    void modify(ITEM item, String property, int value);

    void modify(ITEM item, String property, long value);

    void modify(ITEM item, String property, char value);

    void modify(ITEM item, String property, short value);

    void modify(ITEM item, String property, byte value);

    void modify(ITEM item, String property, float value);

    void modify(ITEM item, String property, double value);

    void modify(ITEM item, Update... values);

    void updateByValue(KEY key, String property, String value);

    void update(KEY key, String property, Object value);

    void update(KEY key, String property, int value);

    void update(KEY key, String property, long value);

    void update(KEY key, String property, char value);

    void update(KEY key, String property, short value);

    void update(KEY key, String property, byte value);

    void update(KEY key, String property, float value);

    void update(KEY key, String property, double value);

    void update(KEY key, Update... values);


    boolean compareAndUpdate(KEY key, String property, Object compare, Object value);

    boolean compareAndUpdate(KEY key, String property, int compare, int value);

    boolean compareAndUpdate(KEY key, String property, long compare, long value);

    boolean compareAndUpdate(KEY key, String property, char compare, char value);

    boolean compareAndUpdate(KEY key, String property, short compare, short value);

    boolean compareAndUpdate(KEY key, String property, byte compare, byte value);

    boolean compareAndUpdate(KEY key, String property, float compare, float value);

    boolean compareAndUpdate(KEY key, String property, double compare, double value);


    boolean compareAndIncrement(KEY key, String property, int compare);

    boolean compareAndIncrement(KEY key, String property, long compare);

    boolean compareAndIncrement(KEY key, String property, short compare);

    boolean compareAndIncrement(KEY key, String property, byte compare);


    void addAll(List<ITEM> items);


    Object readNestedValue(KEY key, String... properties);

    int readNestedInt(KEY key, String... properties);

    short readNestedShort(KEY key, String... properties);

    char readNestedChar(KEY key, String... properties);

    byte readNestedByte(KEY key, String... properties);

    double readNestedDouble(KEY key, String... properties);

    float readNestedFloat(KEY key, String... properties);

    long readNestedLong(KEY key, String... properties);


    Object readObject(KEY key, String property);

    <T> T readValue(KEY key, String property, Class<T> type);

    int readInt(KEY key, String property);

    long readLong(KEY key, String property);

    char readChar(KEY key, String property);

    short readShort(KEY key, String property);

    byte readByte(KEY key, String property);

    float readFloat(KEY key, String property);

    double readDouble(KEY key, String property);


    Object getObject(ITEM item, String property);

    <T> T getValue(ITEM item, String property, Class<T> type);

    int getInt(ITEM item, String property);

    long getLong(ITEM item, String property);

    char getChar(ITEM item, String property);

    short getShort(ITEM item, String property);

    byte getByte(ITEM item, String property);

    float getFloat(ITEM item, String property);

    double getDouble(ITEM item, String property);


}
