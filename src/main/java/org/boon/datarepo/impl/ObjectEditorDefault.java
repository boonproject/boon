package org.boon.datarepo.impl;

import org.boon.datarepo.ObjectEditor;
import org.boon.datarepo.SearchableCollection;
import org.boon.criteria.Update;
import org.boon.datarepo.spi.ObjectEditorComposer;

import org.boon.core.reflection.fields.FieldAccess;
import org.boon.core.reflection.Reflection;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;



public class ObjectEditorDefault<KEY, ITEM> implements ObjectEditorComposer<KEY, ITEM>, ObjectEditor<KEY, ITEM> {

    private Logger log = Logger.getLogger(ObjectEditorDefault.class.getName());

    protected SearchableCollection<KEY, ITEM> query;
    protected Map<String, FieldAccess> fields = new LinkedHashMap<>();
    private boolean hashCodeOptimization;


    private boolean lookupAndExcept;


    public void put(ITEM item) {
        this.add(item);
    }

    public void removeByKey(KEY key) {
        query.removeByKey(key);
    }

    public void removeAll(ITEM... items) {
        for (ITEM item : items) {
            this.delete(item);
        }
    }

    public void removeAllAsync(Collection<ITEM> items) {
        for (ITEM item : items) {
            this.delete(item);
        }
    }

    public void addAll(ITEM... items) {
        for (ITEM item : items) {
            this.add(item);
        }
    }


    public void addAllAsync(Collection<ITEM> items) {
        query.addAll(items);
    }

    public void modifyAll(ITEM... items) {
        for (ITEM item : items) {
            this.modify(item);
        }
    }

    public void modifyAll(Collection<ITEM> items) {
        for (ITEM item : items) {
            this.modify(item);
        }
    }

    public void modify(ITEM item) {

        /** See if we have an original. */
        KEY key = query.getKey(item);
        ITEM oldItem = this.doGet(key);

        if (oldItem != null) {
            delete(oldItem);
        } else {
            log.warning(String.format("An original item was not in the repo %s", item));
        }

        this.add(item);

        if (log.isLoggable(Level.FINE)) {
            log.fine(String.format("This item %s was modified like this %s", oldItem, item));
        }


    }

    public void modify(ITEM item, String property, Object value) {
        item = lookupAndExpect(item);
        query.invalidateIndex(property, item);
        fields.get(property).setObject(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    private void optimizeHash(ITEM item) {
        if (!hashCodeOptimization) {
            return;
        }
        FieldAccess hashCode = fields.get("_hashCode");
        if (hashCode == null) {
            return;
        } else {
            hashCode.setInt(item, -1);
            hashCode.setInt(item, item.hashCode());
        }
    }

    public void modifyByValue(ITEM item, String property, String value) {
        item = lookupAndExpect(item);
        query.invalidateIndex(property, item);
        fields.get(property).setValue(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);

    }

    public void modify(ITEM item, String property, int value) {
        item = lookupAndExpect(item);
        query.invalidateIndex(property, item);
        fields.get(property).setInt(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void modify(ITEM item, String property, long value) {
        item = lookupAndExpect(item);
        query.invalidateIndex(property, item);
        fields.get(property).setLong(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void modify(ITEM item, String property, char value) {
        item = lookupAndExpect(item);
        query.invalidateIndex(property, item);
        fields.get(property).setChar(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void modify(ITEM item, String property, short value) {
        item = lookupAndExpect(item);
        query.invalidateIndex(property, item);
        fields.get(property).setShort(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void modify(ITEM item, String property, byte value) {
        item = lookupAndExpect(item);
        query.invalidateIndex(property, item);
        fields.get(property).setByte(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void modify(ITEM item, String property, float value) {
        item = lookupAndExpect(item);
        query.invalidateIndex(property, item);
        fields.get(property).setFloat(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void modify(ITEM item, String property, double value) {
        item = lookupAndExpect(item);
        query.invalidateIndex(property, item);
        fields.get(property).setDouble(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void modify(ITEM item, Update... values) {
        item = lookupAndExpect(item);
        for (Update value : values) {
            query.invalidateIndex(value.getName(), item);
            value.doSet(this, item);
            optimizeHash(item);
            query.validateIndex(value.getName(), item);
        }

    }

    public void update(KEY key, String property, Object value) {
        ITEM item = lookupAndExpectByKey(key);
        query.invalidateIndex(property, item);
        fields.get(property).setObject(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void updateByValue(KEY key, String property, String value) {
        ITEM item = lookupAndExpectByKey(key);
        query.invalidateIndex(property, item);
        fields.get(property).setValue(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void update(KEY key, String property, int value) {
        ITEM item = lookupAndExpectByKey(key);
        query.invalidateIndex(property, item);
        fields.get(property).setInt(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void update(KEY key, String property, long value) {
        ITEM item = lookupAndExpectByKey(key);
        query.invalidateIndex(property, item);
        fields.get(property).setLong(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void update(KEY key, String property, char value) {
        ITEM item = lookupAndExpectByKey(key);
        query.invalidateIndex(property, item);
        fields.get(property).setChar(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void update(KEY key, String property, short value) {
        ITEM item = lookupAndExpectByKey(key);
        query.invalidateIndex(property, item);
        fields.get(property).setShort(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void update(KEY key, String property, byte value) {
        ITEM item = lookupAndExpectByKey(key);
        query.invalidateIndex(property, item);
        fields.get(property).setByte(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void update(KEY key, String property, float value) {
        ITEM item = lookupAndExpectByKey(key);
        query.invalidateIndex(property, item);
        fields.get(property).setFloat(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void update(KEY key, String property, double value) {
        ITEM item = lookupAndExpectByKey(key);
        query.invalidateIndex(property, item);
        fields.get(property).setDouble(item, value);
        optimizeHash(item);
        query.validateIndex(property, item);
    }

    public void update(KEY key, Update... values) {
        ITEM item = lookupAndExpectByKey(key);

        for (Update value : values) {
            query.invalidateIndex(value.getName(), item);
            value.doSet(this, item);
            optimizeHash(item);
            query.validateIndex(value.getName(), item);
        }
    }

    public boolean compareAndUpdate(KEY key, String property, Object compare, Object value) {
        ITEM item = lookupAndExpectByKey(key);
        FieldAccess field = fields.get(property);
        boolean set = false;
        if (field.getObject(item).equals(compare)) {
            query.invalidateIndex(property, item);
            field.setObject(item, value);
            set = true;
            optimizeHash(item);
            query.validateIndex(property, item);
        }
        return set;
    }

    public boolean compareAndUpdate(KEY key, String property, int compare, int value) {
        ITEM item = lookupAndExpectByKey(key);
        FieldAccess field = fields.get(property);
        boolean set = false;
        if (field.getInt(item) == (compare)) {
            query.invalidateIndex(property, item);
            field.setInt(item, value);
            set = true;
            optimizeHash(item);
            query.validateIndex(property, item);
        }
        return set;
    }

    public boolean compareAndUpdate(KEY key, String property, long compare, long value) {
        ITEM item = lookupAndExpectByKey(key);
        FieldAccess field = fields.get(property);
        boolean set = false;
        if (field.getLong(item) == (compare)) {
            query.invalidateIndex(property, item);
            field.setLong(item, value);
            set = true;
            optimizeHash(item);
            query.validateIndex(property, item);
        }
        return set;
    }

    public boolean compareAndUpdate(KEY key, String property, char compare, char value) {
        ITEM item = lookupAndExpectByKey(key);
        FieldAccess field = fields.get(property);
        boolean set = false;
        if (field.getChar(item) == (compare)) {
            query.invalidateIndex(property, item);
            field.setChar(item, value);
            set = true;
            optimizeHash(item);
            query.validateIndex(property, item);
        }
        return set;
    }

    public boolean compareAndUpdate(KEY key, String property, short compare, short value) {
        ITEM item = lookupAndExpectByKey(key);
        FieldAccess field = fields.get(property);
        boolean set = false;
        if (field.getShort(item) == (compare)) {
            query.invalidateIndex(property, item);
            field.setShort(item, value);
            set = true;
            optimizeHash(item);
            query.validateIndex(property, item);
        }
        return set;

    }

    public boolean compareAndUpdate(KEY key, String property, byte compare, byte value) {
        ITEM item = lookupAndExpectByKey(key);
        FieldAccess field = fields.get(property);
        boolean set = false;
        if (field.getByte(item) == (compare)) {
            query.invalidateIndex(property, item);
            field.setByte(item, value);
            set = true;
            optimizeHash(item);
            query.validateIndex(property, item);
        }
        return set;
    }

    public boolean compareAndUpdate(KEY key, String property, float compare, float value) {
        ITEM item = lookupAndExpectByKey(key);
        FieldAccess field = fields.get(property);
        boolean set = false;
        if (field.getFloat(item) == (compare)) {
            query.invalidateIndex(property, item);
            field.setFloat(item, value);
            set = true;
            optimizeHash(item);
            query.validateIndex(property, item);
        }
        return set;
    }

    public boolean compareAndUpdate(KEY key, String property, double compare, double value) {
        ITEM item = lookupAndExpectByKey(key);
        FieldAccess field = fields.get(property);
        boolean set = false;
        if (field.getDouble(item) == (compare)) {
            query.invalidateIndex(property, item);
            field.setDouble(item, value);
            set = true;
            optimizeHash(item);
            query.validateIndex(property, item);
        }
        return set;
    }

    public boolean compareAndIncrement(KEY key, String property, int compare) {
        ITEM item = lookupAndExpectByKey(key);
        FieldAccess field = fields.get(property);
        boolean set = false;
        if (field.getInt(item) == (compare)) {
            query.invalidateIndex(property, item);
            field.setInt(item, (compare + 1));
            set = true;
            optimizeHash(item);
            query.validateIndex(property, item);
        }
        return set;

    }

    public boolean compareAndIncrement(KEY key, String property, long compare) {
        ITEM item = lookupAndExpectByKey(key);
        FieldAccess field = fields.get(property);
        boolean set = false;
        if (field.getLong(item) == (compare)) {
            query.invalidateIndex(property, item);
            field.setLong(item, (compare + 1));
            set = true;
            optimizeHash(item);
            query.validateIndex(property, item);
        }
        return set;
    }

    public boolean compareAndIncrement(KEY key, String property, short compare) {
        ITEM item = lookupAndExpectByKey(key);
        FieldAccess field = fields.get(property);
        boolean set = false;
        if (field.getShort(item) == (compare)) {
            query.invalidateIndex(property, item);
            field.setShort(item, (short) (compare + 1));
            set = true;
            optimizeHash(item);
            query.validateIndex(property, item);
        }
        return set;
    }

    public boolean compareAndIncrement(KEY key, String property, byte compare) {
        ITEM item = lookupAndExpectByKey(key);
        FieldAccess field = fields.get(property);
        boolean set = false;
        if (field.getByte(item) == (compare)) {
            query.invalidateIndex(property, item);
            field.setByte(item, (byte) (compare + 1));
            set = true;
            optimizeHash(item);
            query.validateIndex(property, item);
        }
        return set;
    }

    @Override
    public void addAll(List<ITEM> items) {
        query.addAll(items);
    }


    public boolean add(ITEM item) {
        return query.add(item);
    }


    public void setFields(Map<String, FieldAccess> fields) {
        this.fields = fields;
    }


    @Override
    public void setSearchableCollection(SearchableCollection searchableCollection) {
        this.query = searchableCollection;
    }

    @Override
    public void init() {
    }

    @Override
    public void hashCodeOptimizationOn() {
        this.hashCodeOptimization = true;
    }


    public ITEM get(KEY key) {
        return (ITEM) query.get(key);
    }

    private ITEM doGet(KEY key) {
        return (ITEM) query.get(key);
    }

    @Override
    public KEY getKey(ITEM item) {
        return (KEY) query.getKey(item);
    }

    private ITEM lookupAndExpect(ITEM item) {
        if (!lookupAndExcept) {
            return item;
        }

        KEY key = getKey(item);
        ITEM oldItem = this.doGet(key);


        if (oldItem == null) {
            throw new IllegalStateException(
                    String.format("An original item was not in the repo %s", item));

        }
        return oldItem;
    }

    private ITEM lookupAndExpectByKey(KEY key) {
        ITEM oldItem = this.doGet(key);

        if (oldItem == null) {

            throw new IllegalStateException(
                    String.format( "An original item was not" +
                            " in the repo at this key %s", key));


        }
        return oldItem;
    }


    @Override
    public void clear() {
        query.clear();
    }

    @Override
    public boolean delete(ITEM item) {
        return query.delete(item);

    }

    @Override
    public List<ITEM> all() {
        return query.all();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int size() {
        return query.size();

    }

    @Override
    public Collection<ITEM> toCollection() {
        return query;
    }


    public SearchableCollection<KEY, ITEM> query() {
        return query;
    }


    @Override
    public Object readNestedValue(KEY key, String... properties) {
        ITEM item = this.get(key);
        return Reflection.getPropertyValue(item, properties);
    }


    @Override
    public int readNestedInt(KEY key, String... properties) {
        ITEM item = this.get(key);
        return Reflection.getPropertyInt(item, properties);
    }

    @Override
    public short readNestedShort(KEY key, String... properties) {
        ITEM item = this.get(key);
        return Reflection.getPropertyShort(item, properties);
    }

    @Override
    public char readNestedChar(KEY key, String... properties) {
        ITEM item = this.get(key);
        return Reflection.getPropertyChar(item, properties);
    }

    @Override
    public byte readNestedByte(KEY key, String... properties) {
        ITEM item = this.get(key);
        return Reflection.getPropertyByte(item, properties);
    }

    @Override
    public double readNestedDouble(KEY key, String... properties) {
        ITEM item = this.get(key);
        return Reflection.getPropertyDouble(item, properties);
    }

    @Override
    public float readNestedFloat(KEY key, String... properties) {
        ITEM item = this.get(key);
        return Reflection.getPropertyFloat(item, properties);
    }

    @Override
    public long readNestedLong(KEY key, String... properties) {
        ITEM item = this.get(key);
        return Reflection.getPropertyLong(item, properties);
    }


    @Override
    public Object readObject(KEY key, String property) {
        ITEM item = this.get(key);
        return this.fields.get(property).getObject(item);
    }


    @Override
    public <T> T readValue(KEY key, String property, Class<T> type) {
        ITEM item = this.get(key);
        return (T) this.fields.get(property).getValue(item);
    }

    @Override
    public int readInt(KEY key, String property) {
        ITEM item = this.get(key);
        return this.fields.get(property).getInt(item);
    }

    @Override
    public long readLong(KEY key, String property) {
        ITEM item = this.get(key);
        return this.fields.get(property).getLong(item);
    }

    @Override
    public char readChar(KEY key, String property) {
        ITEM item = this.get(key);
        return this.fields.get(property).getChar(item);
    }

    @Override
    public short readShort(KEY key, String property) {
        ITEM item = this.get(key);
        return this.fields.get(property).getShort(item);
    }

    @Override
    public byte readByte(KEY key, String property) {
        ITEM item = this.get(key);
        return this.fields.get(property).getByte(item);
    }

    @Override
    public float readFloat(KEY key, String property) {
        ITEM item = this.get(key);
        return this.fields.get(property).getFloat(item);

    }

    @Override
    public double readDouble(KEY key, String property) {
        ITEM item = this.get(key);
        return this.fields.get(property).getDouble(item);

    }

    @Override
    public Object getObject(ITEM item, String property) {
        return this.fields.get(property).getObject(item);
    }

    @Override
    public <T> T getValue(ITEM item, String property, Class<T> type) {
        return (T) this.fields.get(property).getValue(item);
    }

    @Override
    public int getInt(ITEM item, String property) {
        return this.fields.get(property).getInt(item);
    }

    @Override
    public long getLong(ITEM item, String property) {
        return this.fields.get(property).getLong(item);
    }

    @Override
    public char getChar(ITEM item, String property) {
        return this.fields.get(property).getChar(item);
    }

    @Override
    public short getShort(ITEM item, String property) {
        return this.fields.get(property).getShort(item);
    }

    @Override
    public byte getByte(ITEM item, String property) {
        return this.fields.get(property).getByte(item);
    }

    @Override
    public float getFloat(ITEM item, String property) {
        return this.fields.get(property).getFloat(item);
    }

    @Override
    public double getDouble(ITEM item, String property) {
        return this.fields.get(property).getDouble(item);
    }


    public void setLookupAndExcept(boolean lookupAndExcept) {
        this.lookupAndExcept = lookupAndExcept;
    }

}
