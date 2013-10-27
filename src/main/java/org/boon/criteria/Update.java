package org.boon.criteria;

import org.boon.Lists;
import org.boon.datarepo.ObjectEditor;

import java.io.Serializable;
import java.util.List;


public abstract class Update implements Serializable {

    private String name;

    public String getName() {
        return name;
    }


    public abstract void doSet(ObjectEditor repo, Object item);

    public static Update set(final String name, final int value) {
        return new Update() {
            @Override
            public void doSet(ObjectEditor repo, Object item) {
                repo.modify(item, name, value);
            }
        };
    }

    public static Update incInt(final String name) {
        return new Update() {
            @Override
            public void doSet(ObjectEditor repo, Object item) {
                int v = repo.getInt(item, name);
                v++;
                repo.modify(item, name, v);
            }
        };
    }

    public static Update incPercent(final String name, final int percent) {
        return new Update() {

            //Avoid the lookup, pass the fields.
            @Override
            public void doSet(ObjectEditor repo, Object item) {
                int value = repo.getInt(item, name);
                double dvalue = value;
                double dprecent = percent / 100.0;
                dvalue = dvalue + (dvalue * dprecent);
                value = (int) dvalue;
                repo.modify(item, name, value);
            }
        };
    }

    public static Update set(final String name, final long value) {
        return new Update() {
            @Override
            public void doSet(ObjectEditor repo, Object item) {
                repo.modify(item, name, value);
            }
        };
    }

    public static Update set(final String name, final Object value) {
        return new Update() {
            @Override
            public void doSet(ObjectEditor repo, Object item) {
                repo.modify(item, name, value);
            }
        };
    }

    public static Update set(final String name, final byte value) {
        return new Update() {
            @Override
            public void doSet(ObjectEditor repo, Object item) {
                repo.modify(item, name, value);
            }
        };
    }

    public static Update set(final String name, final float value) {
        return new Update() {
            @Override
            public void doSet(ObjectEditor repo, Object item) {
                repo.modify(item, name, value);
            }
        };
    }

    public static Update set(final String name, final char value) {
        return new Update() {
            @Override
            public void doSet(ObjectEditor repo, Object item) {
                repo.modify(item, name, value);
            }
        };
    }

    public static Update set(final String name, final String value) {
        return new Update() {
            @Override
            public void doSet(ObjectEditor repo, Object item) {
                repo.modify(item, name, value);
            }
        };
    }

    public static List<Update> update(Update... values) {
        return Lists.list(values);
    }


}
