package org.boon.datarepo.impl.decorators;


import org.boon.datarepo.ObjectEditor;
import org.boon.datarepo.modification.ModificationEvent;
import org.boon.datarepo.modification.ModificationListener;
import org.boon.criteria.Update;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.boon.datarepo.modification.ModificationEvent.createModification;
import static org.boon.datarepo.modification.ModificationType.*;


import static org.boon.Lists.list;

public class ObjectEditorEventDecorator<KEY, ITEM> extends ObjectEditorDecoratorBase<KEY, ITEM> {

    List<ModificationListener<KEY, ITEM>> listeners = new CopyOnWriteArrayList<>();

    public void add(ModificationListener l) {
        listeners.add(l);


    }

    public void remove(ModificationListener l) {
        listeners.add(l);
    }

    public ObjectEditorEventDecorator() {

    }

    public ObjectEditorEventDecorator(ObjectEditor oe) {
        super(oe);

    }


    public void modify(ITEM item) {
        fire(createModification(BEFORE_MODIFY, super.getKey(item), item, null, null));
        super.modify(item);
        fire(createModification(AFTER_MODIFY, super.getKey(item), item, null, null));
    }

    private void fire(ModificationEvent<KEY, ITEM> event) {
        for (ModificationListener l : listeners) {
            l.modification(event);
        }
    }

    public void modify(ITEM item, String property, Object value) {
        fire(createModification(BEFORE_MODIFY, super.getKey(item), item, property, value));
        super.modify(item, property, value);
        fire(createModification(AFTER_MODIFY, super.getKey(item), item, property, value));

    }

    public void modifyByValue(ITEM item, String property, String value) {
        fire(createModification(BEFORE_MODIFY, super.getKey(item), item, property, value));
        super.modifyByValue(item, property, value);
        fire(createModification(AFTER_MODIFY, super.getKey(item), item, property, value));

    }

    public void modify(ITEM item, String property, int value) {
        fire(createModification(BEFORE_MODIFY, super.getKey(item), item, property, value));
        super.modify(item, property, value);
        fire(createModification(AFTER_MODIFY, super.getKey(item), item, property, value));

    }

    public void modify(ITEM item, String property, long value) {
        fire(createModification(BEFORE_MODIFY, super.getKey(item), item, property, value));
        super.modify(item, property, value);
        fire(createModification(AFTER_MODIFY, super.getKey(item), item, property, value));
    }

    public void modify(ITEM item, String property, char value) {
        fire(createModification(BEFORE_MODIFY, super.getKey(item), item, property, value));
        super.modify(item, property, value);
        fire(createModification(AFTER_MODIFY, super.getKey(item), item, property, value));
    }

    public void modify(ITEM item, String property, short value) {
        fire(createModification(BEFORE_MODIFY, super.getKey(item), item, property, value));
        super.modify(item, property, value);
        fire(createModification(AFTER_MODIFY, super.getKey(item), item, property, value));
    }

    public void modify(ITEM item, String property, byte value) {
        fire(createModification(BEFORE_MODIFY, super.getKey(item), item, property, value));
        super.modify(item, property, value);
        fire(createModification(AFTER_MODIFY, super.getKey(item), item, property, value));
    }

    public void modify(ITEM item, String property, float value) {
        fire(createModification(BEFORE_MODIFY, super.getKey(item), item, property, value));
        super.modify(item, property, value);
        fire(createModification(AFTER_MODIFY, super.getKey(item), item, property, value));
    }

    public void modify(ITEM item, String property, double value) {
        fire(createModification(BEFORE_MODIFY, super.getKey(item), item, property, value));
        super.modify(item, property, value);
        fire(createModification(AFTER_MODIFY, super.getKey(item), item, property, value));
    }

    public void modify(ITEM item, Update... values) {
        fire(createModification(BEFORE_MODIFY_BY_VALUE_SETTERS, super.getKey(item), item, null, values));
        super.modify(item, values);
        fire(createModification(AFTER_MODIFY_BY_VALUE_SETTERS, super.getKey(item), item, null, values));
    }

    public void update(KEY key, String property, Object value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, value));
        super.update(key, property, value);
        fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, value));
    }

    public void updateByValue(KEY key, String property, String value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, value));
        super.update(key, property, value);
        fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, value));
    }

    public void update(KEY key, String property, int value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, value));
        super.update(key, property, value);
        fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, value));
    }

    public void update(KEY key, String property, long value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, value));
        super.update(key, property, value);
        fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, value));
    }

    public void update(KEY key, String property, char value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, value));
        super.update(key, property, value);
        fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, value));
    }

    public void update(KEY key, String property, short value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, value));
        super.update(key, property, value);
        fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, value));
    }

    public void update(KEY key, String property, byte value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, value));
        super.update(key, property, value);
        fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, value));
    }

    public void update(KEY key, String property, float value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, value));
        super.update(key, property, value);
        fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, value));
    }

    public void update(KEY key, String property, double value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, value));
        super.update(key, property, value);
        fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, value));
    }

    public void update(KEY key, Update... values) {
        fire(createModification(BEFORE_UPDATE_BY_VALUE_SETTERS, key, (ITEM) null, null, values));
        super.update(key, values);
        fire(createModification(AFTER_UPDATE_BY_VALUE_SETTERS, key, (ITEM) null, null, values));
    }

    public boolean compareAndUpdate(KEY key, String property, Object compare, Object value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, list(compare, value)));
        boolean updated = super.compareAndUpdate(key, property, compare, value);
        if (updated) {
            fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, list(compare, value)));
        }
        return updated;
    }

    public boolean compareAndUpdate(KEY key, String property, int compare, int value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, list(compare, value)));
        boolean updated = super.compareAndUpdate(key, property, compare, value);
        if (updated) {
            fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, list(compare, value)));
        }
        return updated;
    }

    public boolean compareAndUpdate(KEY key, String property, long compare, long value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, list(compare, value)));
        boolean updated = super.compareAndUpdate(key, property, compare, value);
        if (updated) {
            fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, list(compare, value)));
        }
        return updated;
    }

    public boolean compareAndUpdate(KEY key, String property, char compare, char value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, list((char) compare, (char) value)));
        boolean updated = super.compareAndUpdate(key, property, compare, value);
        if (updated) {
            fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, list((char) compare, (char) value)));
        }
        return updated;
    }

    public boolean compareAndUpdate(KEY key, String property, short compare, short value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, list(compare, value)));
        boolean updated = super.compareAndUpdate(key, property, compare, value);
        if (updated) {
            fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, list(compare, value)));
        }
        return updated;
    }

    public boolean compareAndUpdate(KEY key, String property, byte compare, byte value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, list(compare, value)));
        boolean updated = super.compareAndUpdate(key, property, compare, value);
        if (updated) {
            fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, list(compare, value)));
        }
        return updated;
    }

    public boolean compareAndUpdate(KEY key, String property, float compare, float value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, list(compare, value)));
        boolean updated = super.compareAndUpdate(key, property, compare, value);
        if (updated) {
            fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, list(compare, value)));
        }
        return updated;
    }

    public boolean compareAndUpdate(KEY key, String property, double compare, double value) {
        fire(createModification(BEFORE_UPDATE, key, (ITEM) null, property, list(compare, value)));
        boolean updated = super.compareAndUpdate(key, property, compare, value);
        if (updated) {
            fire(createModification(AFTER_UPDATE, key, (ITEM) null, property, list(compare, value)));
        }
        return updated;
    }

    public boolean compareAndIncrement(KEY key, String property, int compare) {
        fire(createModification(BEFORE_INCREMENT, key, (ITEM) null, property, compare));
        boolean updated = super.compareAndIncrement(key, property, compare);
        fire(createModification(AFTER_INCREMENT, key, (ITEM) null, property, compare));
        return updated;
    }

    public boolean compareAndIncrement(KEY key, String property, long compare) {
        fire(createModification(BEFORE_INCREMENT, key, (ITEM) null, property, compare));
        boolean updated = super.compareAndIncrement(key, property, compare);
        fire(createModification(AFTER_INCREMENT, key, (ITEM) null, property, compare));
        return updated;
    }

    public boolean compareAndIncrement(KEY key, String property, short compare) {
        fire(createModification(BEFORE_INCREMENT, key, (ITEM) null, property, compare));
        boolean updated = super.compareAndIncrement(key, property, compare);
        fire(createModification(AFTER_INCREMENT, key, (ITEM) null, property, compare));
        return updated;
    }

    public boolean compareAndIncrement(KEY key, String property, byte compare) {
        fire(createModification(BEFORE_INCREMENT, key, (ITEM) null, property, compare));
        boolean updated = super.compareAndIncrement(key, property, compare);
        fire(createModification(AFTER_INCREMENT, key, (ITEM) null, property, compare));
        return updated;
    }


}
