package org.boon.datarepo.impl.indexes;


import java.util.ArrayList;
import java.util.List;

/**
 * This allows search based on type hierarchy.
 */
public class TypeHierarchyIndex extends BaseIndexWrapper {

    @Override
    public boolean add(Object o) {
        List keys = getKeys(o);
        index.addManyKeys(o, keys);
        return true;
    }

    @Override
    public boolean delete(Object o) {
        List keys = getKeys(o);
        index.removeManyKeys(o, keys);
        return true;

    }

    @Override
    protected List getKeys(Object o) {
        List<Object> list = new ArrayList();
        Class cls = o.getClass();

        while (cls != null && cls != Object.class) {
            list.add(cls.getSimpleName());
            list.add(cls.getName());

            for (Class<?> i : cls.getInterfaces()) {
                list.add(i.getSimpleName());
                list.add(i.getName());
            }

            cls = cls.getSuperclass();
        }
        return list;
    }


}
