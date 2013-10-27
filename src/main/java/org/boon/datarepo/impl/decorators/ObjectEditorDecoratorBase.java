package org.boon.datarepo.impl.decorators;

import org.boon.datarepo.ObjectEditor;
import org.boon.criteria.Update;

import java.util.Collection;
import java.util.List;

public class ObjectEditorDecoratorBase<KEY, ITEM> implements ObjectEditor<KEY, ITEM> {
    private final ObjectEditor<KEY, ITEM> objectEditorDefault;

    public ObjectEditorDecoratorBase() {
        this.objectEditorDefault = null;
    }

    public ObjectEditorDecoratorBase(ObjectEditor oe) {
        this.objectEditorDefault = oe;

    }

    public ObjectEditor<KEY, ITEM> delegate() {
        return objectEditorDefault;
    }

    public void put(ITEM item) {
        objectEditorDefault.put(item);
    }

    public void removeByKey(KEY key) {
        objectEditorDefault.removeByKey(key);
    }

    public void removeAll(ITEM... items) {
        objectEditorDefault.removeAll(items);
    }

    public void removeAllAsync(Collection<ITEM> items) {
        objectEditorDefault.removeAllAsync(items);
    }

    public void addAll(ITEM... items) {
        objectEditorDefault.addAll(items);
    }

    public void addAllAsync(Collection<ITEM> items) {
        objectEditorDefault.addAllAsync(items);
    }

    public void modifyAll(ITEM... items) {
        objectEditorDefault.modifyAll(items);
    }

    public void modifyAll(Collection<ITEM> items) {
        objectEditorDefault.modifyAll(items);
    }

    public void modify(ITEM item) {
        objectEditorDefault.modify(item);
    }

    public void modify(ITEM item, String property, Object value) {
        objectEditorDefault.modify(item, property, value);
    }

    public void modifyByValue(ITEM item, String property, String value) {
        objectEditorDefault.modifyByValue(item, property, value);
    }

    public void modify(ITEM item, String property, int value) {
        objectEditorDefault.modify(item, property, value);
    }

    public void modify(ITEM item, String property, long value) {
        objectEditorDefault.modify(item, property, value);
    }

    public void modify(ITEM item, String property, char value) {
        objectEditorDefault.modify(item, property, value);
    }

    public void modify(ITEM item, String property, short value) {
        objectEditorDefault.modify(item, property, value);
    }

    public void modify(ITEM item, String property, byte value) {
        objectEditorDefault.modify(item, property, value);
    }

    public void modify(ITEM item, String property, float value) {
        objectEditorDefault.modify(item, property, value);
    }

    public void modify(ITEM item, String property, double value) {
        objectEditorDefault.modify(item, property, value);
    }

    public void modify(ITEM item, Update... values) {
        objectEditorDefault.modify(item, values);
    }

    public void update(KEY key, String property, Object value) {
        objectEditorDefault.update(key, property, value);
    }

    public void updateByValue(KEY key, String property, String value) {
        objectEditorDefault.updateByValue(key, property, value);
    }

    public void update(KEY key, String property, int value) {
        objectEditorDefault.update(key, property, value);
    }

    public void update(KEY key, String property, long value) {
        objectEditorDefault.update(key, property, value);
    }

    public void update(KEY key, String property, char value) {
        objectEditorDefault.update(key, property, value);
    }

    public void update(KEY key, String property, short value) {
        objectEditorDefault.update(key, property, value);
    }

    public void update(KEY key, String property, byte value) {
        objectEditorDefault.update(key, property, value);
    }

    public void update(KEY key, String property, float value) {
        objectEditorDefault.update(key, property, value);
    }

    public void update(KEY key, String property, double value) {
        objectEditorDefault.update(key, property, value);
    }

    public void update(KEY key, Update... values) {
        objectEditorDefault.update(key, values);
    }

    public boolean compareAndUpdate(KEY key, String property, Object compare, Object value) {
        return objectEditorDefault.compareAndUpdate(key, property, compare, value);
    }

    public boolean compareAndUpdate(KEY key, String property, int compare, int value) {
        return objectEditorDefault.compareAndUpdate(key, property, compare, value);
    }

    public boolean compareAndUpdate(KEY key, String property, long compare, long value) {
        return objectEditorDefault.compareAndUpdate(key, property, compare, value);
    }

    public boolean compareAndUpdate(KEY key, String property, char compare, char value) {
        return objectEditorDefault.compareAndUpdate(key, property, compare, value);
    }

    public boolean compareAndUpdate(KEY key, String property, short compare, short value) {
        return objectEditorDefault.compareAndUpdate(key, property, compare, value);
    }

    public boolean compareAndUpdate(KEY key, String property, byte compare, byte value) {
        return objectEditorDefault.compareAndUpdate(key, property, compare, value);
    }

    public boolean compareAndUpdate(KEY key, String property, float compare, float value) {
        return objectEditorDefault.compareAndUpdate(key, property, compare, value);
    }

    public boolean compareAndUpdate(KEY key, String property, double compare, double value) {
        return objectEditorDefault.compareAndUpdate(key, property, compare, value);
    }

    public boolean compareAndIncrement(KEY key, String property, int compare) {
        return objectEditorDefault.compareAndIncrement(key, property, compare);
    }

    public boolean compareAndIncrement(KEY key, String property, long compare) {
        return objectEditorDefault.compareAndIncrement(key, property, compare);
    }

    public boolean compareAndIncrement(KEY key, String property, short compare) {
        return objectEditorDefault.compareAndIncrement(key, property, compare);
    }

    public boolean compareAndIncrement(KEY key, String property, byte compare) {
        return objectEditorDefault.compareAndIncrement(key, property, compare);
    }

    public void addAll(List<ITEM> items) {
        objectEditorDefault.addAll(items);
    }

    @Override
    public Object readNestedValue(KEY key, String... properties) {
        return objectEditorDefault.readNestedValue(key, properties);
    }

    @Override
    public int readNestedInt(KEY key, String... properties) {
        return objectEditorDefault.readNestedInt(key, properties);
    }

    @Override
    public short readNestedShort(KEY key, String... properties) {
        return objectEditorDefault.readNestedShort(key, properties);
    }

    @Override
    public char readNestedChar(KEY key, String... properties) {
        return objectEditorDefault.readNestedChar(key, properties);
    }

    @Override
    public byte readNestedByte(KEY key, String... properties) {
        return objectEditorDefault.readNestedByte(key, properties);
    }

    @Override
    public double readNestedDouble(KEY key, String... properties) {
        return objectEditorDefault.readNestedDouble(key, properties);
    }

    @Override
    public float readNestedFloat(KEY key, String... properties) {
        return objectEditorDefault.readNestedFloat(key, properties);
    }

    @Override
    public long readNestedLong(KEY key, String... properties) {
        return objectEditorDefault.readNestedLong(key, properties);
    }

    @Override
    public Object readObject(KEY key, String property) {
        return objectEditorDefault.readObject(key, property);
    }

    @Override
    public <T> T readValue(KEY key, String property, Class<T> type) {
        return objectEditorDefault.readValue(key, property, type);
    }

    @Override
    public int readInt(KEY key, String property) {
        return objectEditorDefault.readInt(key, property);
    }

    @Override
    public long readLong(KEY key, String property) {
        return objectEditorDefault.readLong(key, property);

    }

    @Override
    public char readChar(KEY key, String property) {
        return objectEditorDefault.readChar(key, property);

    }

    @Override
    public short readShort(KEY key, String property) {
        return objectEditorDefault.readShort(key, property);

    }

    @Override
    public byte readByte(KEY key, String property) {
        return objectEditorDefault.readByte(key, property);

    }

    @Override
    public float readFloat(KEY key, String property) {
        return objectEditorDefault.readFloat(key, property);
    }

    @Override
    public double readDouble(KEY key, String property) {
        return objectEditorDefault.readDouble(key, property);

    }

    @Override
    public Object getObject(ITEM item, String property) {
        return objectEditorDefault.getObject(item, property);
    }

    @Override
    public <T> T getValue(ITEM item, String property, Class<T> type) {
        return objectEditorDefault.getValue(item, property, type);
    }

    @Override
    public int getInt(ITEM item, String property) {
        return objectEditorDefault.getInt(item, property);
    }

    @Override
    public long getLong(ITEM item, String property) {
        return objectEditorDefault.getLong(item, property);
    }

    @Override
    public char getChar(ITEM item, String property) {
        return objectEditorDefault.getChar(item, property);
    }

    @Override
    public short getShort(ITEM item, String property) {
        return objectEditorDefault.getShort(item, property);
    }

    @Override
    public byte getByte(ITEM item, String property) {
        return objectEditorDefault.getByte(item, property);
    }

    @Override
    public float getFloat(ITEM item, String property) {
        return objectEditorDefault.getFloat(item, property);
    }

    @Override
    public double getDouble(ITEM item, String property) {
        return objectEditorDefault.getDouble(item, property);
    }

    public boolean add(ITEM item) {
        return objectEditorDefault.add(item);
    }


    public ITEM get(KEY key) {
        return objectEditorDefault.get(key);
    }

    public KEY getKey(ITEM item) {
        return objectEditorDefault.getKey(item);
    }

    public void clear() {
        objectEditorDefault.clear();
    }

    public boolean delete(ITEM item) {
        return objectEditorDefault.delete(item);
    }

    public List<ITEM> all() {
        return objectEditorDefault.all();
    }

    public int size() {
        return objectEditorDefault.size();
    }

    public Collection<ITEM> toCollection() {
        return objectEditorDefault.toCollection();
    }

}
