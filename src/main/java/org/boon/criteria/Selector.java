package org.boon.criteria;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.boon.core.reflection.fields.FieldAccess;
import static org.boon.core.reflection.Reflection.getPropByPath;

import static org.boon.Lists.list;

import static org.boon.core.reflection.Reflection.joinBy;

public abstract class Selector {
    protected String name;

    public Selector() {
    }

    public Selector(String n) {
        name = n;
    }

    public String getName() {
        return name;
    }

    public static List<Selector> selects(Selector... selects) {
        return list(selects);
    }

    public static Selector select(final String name) {
        return new Selector(name) {
            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                row.put(this.name, fields.get(this.name).getValue(item));
            }

            @Override
            public void handleStart(List<? extends Object> results) {
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
            }
        };
    }

    public static Selector toStr(final String name) {
        return new Selector(name + ".toString()") {
            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                Object selected = fields.get(this.name).getValue(item);
                row.put(this.name, selected == null ? "" : selected.toString());
            }

            @Override
            public void handleStart(List<? extends Object> results) {
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
            }
        };
    }

    public static Selector toStr() {
        return new Selector("toString()") {
            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                row.put(this.name, item.toString());
            }

            @Override
            public void handleStart(List<? extends Object> results) {
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
            }
        };
    }

    public static Selector select(final String... path) {
        return new Selector(joinBy('.', path)) {
            int index = 0;

            @Override
            public void handleRow(int rowNum, Map<String, Object> row,
                                  Object item, Map<String, FieldAccess> fields) {

                Object o = getPropByPath(item, path);


                row.put(this.name, o);
            }


            @Override
            public void handleStart(List<? extends Object> results) {
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
            }
        };
    }

    public static Selector toStr(final String... path) {
        return new Selector(joinBy('.', path) + ".toString()") {
            int index = 0;

            @Override
            public void handleRow(int rowNum, Map<String, Object> row,
                                  Object item, Map<String, FieldAccess> fields) {

                Object o = getPropByPath(item, path);


                row.put(this.name, o == null ? "" : o.toString());
            }


            @Override
            public void handleStart(List<? extends Object> results) {
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
            }
        };
    }

    public static Selector selectPropPath(final String... path) {
        return new Selector(joinBy('.', path)) {
            @Override
            public void handleRow(int rowNum, Map<String, Object> row,
                                  Object item, Map<String, FieldAccess> fields) {

                Object o = getPropByPath(item, path);

                row.put(this.name, o);
            }

            @Override
            public void handleStart(List<? extends Object> results) {
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
            }
        };
    }

    public static Selector rowId() {

        return new Selector("rowId") {
            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                row.put(name, index);
            }

            @Override
            public void handleStart(List<? extends Object> results) {
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
            }
        };
    }


    public static <ITEM> List<Map<String, Object>> performSelection(List<Selector> selectors, List<ITEM> results, Map<String, FieldAccess> fields) {
        List<Map<String, Object>> rows = new ArrayList<>(results.size());


        for (Selector s : selectors) {
            s.handleStart(results);
        }


        int index = 0;
        for (ITEM item : results) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (Selector s : selectors) {
                s.handleRow(index, row, item, fields);
            }
            index++;
            rows.add(row);
        }

        for (Selector s : selectors) {
            s.handleComplete(rows);
        }

        return rows;
    }


    public abstract void handleRow(int index, Map<String, Object> row,
                                   Object item,
                                   Map<String, FieldAccess> fields);

    public abstract void handleStart(List<? extends Object> results);

    public abstract void handleComplete(List<Map<String, Object>> rows);
}
