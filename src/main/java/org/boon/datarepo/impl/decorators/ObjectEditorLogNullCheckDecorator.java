package org.boon.datarepo.impl.decorators;


import org.boon.datarepo.ObjectEditor;
import org.boon.criteria.Update;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ObjectEditorLogNullCheckDecorator<KEY, ITEM> extends ObjectEditorDecoratorBase<KEY, ITEM> {

    Logger logger = Logger.getLogger(ObjectEditorLogNullCheckDecorator.class.getName());
    Level level = Level.FINER;


    private boolean debug = false;

    void log(final String msg, final Object... items) {

        if (debug) {
            System.out.printf(msg, items);
        }
        String message = String.format(msg, items);
        logger.log(level, message);

    }


    public void setLevel(Level level) {
        this.level = level;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }


    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public ObjectEditorLogNullCheckDecorator() {


    }

    public ObjectEditorLogNullCheckDecorator(ObjectEditor oe) {
        super(oe);

    }


    @Override
    public void put(ITEM item) {
        Objects.requireNonNull(item, "item cannot be null");
        log("put (item=%s)", item);
        super.put(item);
    }


    @Override
    public boolean add(ITEM item) {
        Objects.requireNonNull(item, "item cannot be null");
        log("add (item=%s)", item);
        return super.add(item);
    }

    @Override
    public ITEM get(KEY key) {
        Objects.requireNonNull(key, "key cannot be null");
        log("get (key=%s)", key);

        return super.get(key);
    }


    @Override
    public void modify(ITEM item) {
        Objects.requireNonNull(item, "item cannot be null");
        log("modify (item=%s)", item);

        super.modify(item);
    }

    @Override
    public void modify(ITEM item, String property, Object value) {
        Objects.requireNonNull(item, "item cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");

        log("modify (item=%s, property=%s, set=%s)", item, property, value);

        super.modify(item, property, value);
    }

    @Override
    public void modifyByValue(ITEM item, String property, String value) {
        Objects.requireNonNull(item,     "item cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");

        log("modify (item=%s, property=%s, set=%s)", item, property, value);

        super.modifyByValue(item, property, value);
    }

    @Override
    public void modify(ITEM item, String property, int value) {
        Objects.requireNonNull(item,     "item cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value,    "value cannot be null");

        log("modify (item=%s, property=%s, set=%s)", item, property, value);

        super.modify(item, property, value);
    }

    @Override
    public void modify(ITEM item, String property, long value) {

        Objects.requireNonNull(item, "item cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");

        log("modify (item=%s, property=%s, set=%s)", item, property, value);

        super.modify(item, property, value);
    }

    @Override
    public void modify(ITEM item, String property, char value) {

        Objects.requireNonNull(item, "item cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");

        log("modify (item=%s, property=%s, set=%s)", item, property, value);

        super.modify(item, property, value);
    }

    @Override
    public void modify(ITEM item, String property, short value) {

        Objects.requireNonNull(item, "item cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");

        log("modify (item=%s, property=%s, set=%s)", item, property, value);

        super.modify(item, property, value);
    }

    @Override
    public void modify(ITEM item, String property, byte value) {
        Objects.requireNonNull(item, "item cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");

        log("modify (item=%s, property=%s, set=%s)", item, property, value);

        super.modify(item, property, value);
    }

    @Override
    public void modify(ITEM item, String property, float value) {
        Objects.requireNonNull(item, "item cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");

        log("modify (item=%s, property=%s, set=%s)", item, property, value);

        super.modify(item, property, value);
    }

    @Override
    public void modify(ITEM item, String property, double value) {
        Objects.requireNonNull(item, "item cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");

        log("modify (item=%s, property=%s, set=%s)", item, property, value);

        super.modify(item, property, value);
    }

    @Override
    public void modify(ITEM item, Update... values) {
        Objects.requireNonNull(item, "item cannot be null");
        Objects.requireNonNull(values, "value cannot be null");

        log("modify (item=%s, property=%s, update=%s)", item, values);

        super.modify(item, values);
    }

    @Override
    public void update(KEY key, String property, Object value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("update (key=%s, property=%s, set=%s)", key, property, value);

        super.update(key, property, value);
    }

    @Override
    public void updateByValue(KEY key, String property, String value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("updateByValue (key=%s, property=%s, set=%s)", key, property, value);

        super.updateByValue(key, property, value);
    }

    @Override
    public void update(KEY key, String property, int value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("update (key=%s, property=%s, set=%s)", key, property, value);
        super.update(key, property, value);
    }

    @Override
    public void update(KEY key, String property, long value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("update (key=%s, property=%s, set=%s)", key, property, value);

        super.update(key, property, value);
    }

    @Override
    public void update(KEY key, String property, char value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("update (key=%s, property=%s, set=%s)", key, property, value);

        super.update(key, property, value);
    }

    @Override
    public void update(KEY key, String property, short value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("update (key=%s, property=%s, set=%s)", key, property, value);

        super.update(key, property, value);
    }

    @Override
    public void update(KEY key, String property, byte value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("update (key=%s, property=%s, set=%s)", key, property, value);

        super.update(key, property, value);
    }

    @Override
    public void update(KEY key, String property, float value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("update (key=%s, property=%s, set=%s)", key, property, value);

        super.update(key, property, value);
    }

    @Override
    public void update(KEY key, String property, double value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("update (key=%s, property=%s, set=%s)", key, property, value);

        super.update(key, property, value);
    }

    @Override
    public void update(KEY key, Update... values) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(values, "values cannot be null");

        log("update (key=%s, update=%s)", key, values);

        super.update(key, values);
    }

    @Override
    public boolean compareAndUpdate(KEY key, String property, Object compare, Object value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(compare, "compare cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value);

        return super.compareAndUpdate(key, property, compare, value);
    }

    @Override
    public boolean compareAndUpdate(KEY key, String property, int compare, int value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(compare, "compare cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value);

        return super.compareAndUpdate(key, property, compare, value);
    }

    @Override
    public boolean compareAndUpdate(KEY key, String property, long compare, long value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(compare, "compare cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value);

        return super.compareAndUpdate(key, property, compare, value);
    }

    @Override
    public boolean compareAndUpdate(KEY key, String property, char compare, char value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(compare, "compare cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value);

        return super.compareAndUpdate(key, property, compare, value);
    }

    @Override
    public boolean compareAndUpdate(KEY key, String property, short compare, short value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(compare, "compare cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value);

        return super.compareAndUpdate(key, property, compare, value);
    }

    @Override
    public boolean compareAndUpdate(KEY key, String property, byte compare, byte value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(compare, "compare cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value);

        return super.compareAndUpdate(key, property, compare, value);
    }

    @Override
    public boolean compareAndUpdate(KEY key, String property, float compare, float value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(compare, "compare cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value);

        return super.compareAndUpdate(key, property, compare, value);
    }

    @Override
    public boolean compareAndUpdate(KEY key, String property, double compare, double value) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(compare, "compare cannot be null");
        Objects.requireNonNull(value, "value cannot be null");
        log("compareAndUpdate (key=%s, property=%s, compare=%s, set=%s)", key, property, compare, value);

        return super.compareAndUpdate(key, property, compare, value);
    }

    @Override
    public boolean compareAndIncrement(KEY key, String property, int compare) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(compare, "compare cannot be null");
        log("compareAndIncrement (key=%s, property=%s, compare=%s)", key, property, compare);

        return super.compareAndIncrement(key, property, compare);
    }

    @Override
    public boolean compareAndIncrement(KEY key, String property, long compare) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(compare, "compare cannot be null");
        log("compareAndIncrement (key=%s, property=%s, compare=%s)", key, property, compare);

        return super.compareAndIncrement(key, property, compare);
    }

    @Override
    public boolean compareAndIncrement(KEY key, String property, short compare) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(compare, "compare cannot be null");
        log("compareAndIncrement (key=%s, property=%s, set=%s)", key, property, compare);

        return super.compareAndIncrement(key, property, compare);
    }

    @Override
    public boolean compareAndIncrement(KEY key, String property, byte compare) {
        Objects.requireNonNull(key, "key cannot be null");
        Objects.requireNonNull(property, "property cannot be null");
        Objects.requireNonNull(compare, "compare cannot be null");
        log("compareAndIncrement (key=%s, property=%s, set=%s)", key, property, compare);

        return super.compareAndIncrement(key, property, compare);
    }
}
