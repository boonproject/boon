package org.boon.datarepo.impl.decorators;

import org.boon.datarepo.ObjectEditor;

import static org.boon.core.reflection.Reflection.copy;


public class ObjectEditorCloneDecorator<KEY, ITEM> extends ObjectEditorDecoratorBase<KEY, ITEM> {

    public ObjectEditorCloneDecorator() {

    }


    public void modify(ITEM item) {
        item = copy(item);
        super.modify(item);
    }

    @Override
    public void put(ITEM item) {
        item = copy(item);
        super.put(item);
    }


    @Override
    public boolean add(ITEM item) {
        item = copy(item);
        return super.add(item);
    }

    @Override
    public ITEM get(KEY key) {
        ITEM item = super.get(key);
        if (item != null) {
            return copy(item);
        }
        return item;
    }

    public ObjectEditorCloneDecorator(ObjectEditor oe) {
        super(oe);

    }


}
