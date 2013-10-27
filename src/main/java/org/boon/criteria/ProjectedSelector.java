package org.boon.criteria;


import java.util.List;
import java.util.Map;


import org.boon.core.Typ;
import org.boon.core.reflection.fields.FieldAccess;

import static org.boon.core.reflection.Conversions.*;
import static org.boon.Str.join;
import static org.boon.Lists.list;

public abstract class ProjectedSelector extends Selector {

    public static List<ProjectedSelector> projections(ProjectedSelector... projections) {
        return list(projections);
    }


    public static Selector max(final String fieldName) {
        return new Selector(join('.', "max", fieldName)) {
            Comparable max;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                Comparable value = (Comparable) fields.get(fieldName).getValue(item);

                if (max == null) {
                    max = value;
                }

                if (value.compareTo(max) > 0) {
                    max = value;
                }
            }

            @Override
            public void handleStart(List<? extends Object> results) {
                max = null;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(this.name, max);
                }
            }
        };
    }


    public static Selector min(final String name) {
        return new Selector(name) {
            Comparable min;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                Comparable value = (Comparable) fields.get(this.name).getValue(item);

                if (min == null) {
                    min = value;
                }

                if (value.compareTo(min) < 0) {
                    min = value;
                }
            }

            @Override
            public void handleStart(List<? extends Object> results) {
                min = null;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(join('.', "min", name), min);
                }
            }
        };
    }


    public static Selector sum(final String name) {
        return new Selector(name) {
            long sum = 0;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {

                FieldAccess field = fields.get(this.name);
                if (field.getType() == Typ.intgr) {
                    int value = field.getInt(item);
                    sum += value;
                } else {
                    Integer ovalue = toInt(field.getValue(item));
                    sum += ovalue;

                }

            }

            @Override
            public void handleStart(List<? extends Object> results) {
                sum = Integer.MIN_VALUE;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(join('.', "sum", name), sum);
                }
            }
        };
    }

    public static Selector sumFloat(final String name) {
        return new Selector(name) {
            double sum = 0;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {

                FieldAccess field = fields.get(this.name);
                if (field.getType() == Typ.flt) {
                    float value = field.getFloat(item);
                    sum += value;
                } else {
                    Float ovalue = toFloat(field.getValue(item));
                    sum += ovalue;

                }

            }

            @Override
            public void handleStart(List<? extends Object> results) {
                sum = Integer.MIN_VALUE;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(join('.', "sum", name), sum);
                }
            }
        };
    }


    public static Selector maxInt(final String name) {
        return new Selector(name) {
            int max = Integer.MIN_VALUE;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                FieldAccess field = fields.get(this.name);
                if (field.getType() == Typ.intgr) {
                    int value = field.getInt(item);
                    if (value > max) {
                        max = value;
                    }
                } else {
                    Integer ovalue = toInt(field.getValue(item));
                    if (ovalue > max) {
                        max = ovalue;
                    }

                }


            }

            @Override
            public void handleStart(List<? extends Object> results) {
                max = Integer.MIN_VALUE;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(join('.', "max", name), max);
                }
            }
        };
    }

    public static Selector maxLong(final String name) {
        return new Selector(name) {
            long max = Long.MIN_VALUE;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                FieldAccess field = fields.get(this.name);
                if (field.getType() == Typ.lng) {
                    long value = field.getLong(item);
                    if (value > max) {
                        max = value;
                    }
                } else {
                    Long ovalue = toLong(field.getValue(item));
                    if (ovalue > max) {
                        max = ovalue;
                    }

                }


            }

            @Override
            public void handleStart(List<? extends Object> results) {
                max = Long.MIN_VALUE;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(join('.', "max", name), max);
                }
            }
        };
    }

    public static Selector minInt(final String name) {
        return new Selector(name) {
            int min = Integer.MAX_VALUE;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                FieldAccess field = fields.get(this.name);
                if (field.getType() == Typ.intgr) {
                    int value = field.getInt(item);
                    if (value < min) {
                        min = value;
                    }
                } else {
                    Integer ovalue = toInt(field.getValue(item));
                    if (ovalue < min) {
                        min = ovalue;
                    }

                }
            }

            @Override
            public void handleStart(List<? extends Object> results) {
                min = Integer.MAX_VALUE;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(join('.', "min", name), min);
                }
            }
        };
    }

    public static Selector minLong(final String name) {
        return new Selector(name) {
            long min = Long.MAX_VALUE;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                FieldAccess field = fields.get(this.name);
                if (field.getType() == Typ.lng) {
                    long value = field.getLong(item);
                    if (value < min) {
                        min = value;
                    }
                } else {
                    Long ovalue = toLong(field.getValue(item));
                    if (ovalue < min) {
                        min = ovalue;
                    }

                }
            }

            @Override
            public void handleStart(List<? extends Object> results) {
                min = Long.MAX_VALUE;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(join('.', "min", name), min);
                }
            }
        };
    }

    public static Selector maxFloat(final String name) {
        return new Selector(name) {
            float max = Float.MIN_VALUE;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                FieldAccess field = fields.get(this.name);
                if (field.getType() == Typ.flt) {
                    float value = field.getFloat(item);
                    if (value > max) {
                        max = value;
                    }
                } else {
                    Float ovalue = toFloat(field.getValue(item));
                    if (ovalue > max) {
                        max = ovalue;
                    }

                }
            }

            @Override
            public void handleStart(List<? extends Object> results) {
                max = Float.MIN_VALUE;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(join('.', "max", name), max);
                }
            }
        };
    }

    public static Selector minFloat(final String name) {
        return new Selector(name) {

            float min = Float.MAX_VALUE;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                FieldAccess field = fields.get(this.name);

                if (field.getType() == Typ.flt) {
                    float value = field.getFloat(item);
                    if (value > min) {
                        min = value;
                    }
                } else {
                    Float ovalue = toFloat(field.getValue(item));
                    if (ovalue > min) {
                        min = ovalue;
                    }

                }
            }

            @Override
            public void handleStart(List<? extends Object> results) {
                min = Float.MAX_VALUE;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(join('.', "min", name), min);
                }
            }
        };
    }

    public static Selector maxDouble(final String name) {
        return new Selector(name) {
            double max = Double.MIN_VALUE;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                double value = fields.get(this.name).getDouble(item);
                if (value > max) {
                    max = value;
                }
            }

            @Override
            public void handleStart(List<? extends Object> results) {
                max = Double.MIN_VALUE;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(join('.', "max", name), max);
                }
            }
        };
    }

    public static Selector minDouble(final String name) {
        return new Selector(name) {
            double min = Double.MAX_VALUE;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                double value = fields.get(this.name).getDouble(item);
                if (value < min) {
                    min = value;
                }
            }

            @Override
            public void handleStart(List<? extends Object> results) {
                min = Double.MIN_VALUE;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(join('.', "max", name), min);
                }
            }
        };
    }

    public static Selector minShort(final String name) {
        return new Selector(name) {
            short min = Short.MAX_VALUE;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                short value = fields.get(this.name).getShort(item);
                if (value < min) {
                    min = value;
                }
            }

            @Override
            public void handleStart(List<? extends Object> results) {
                min = Short.MAX_VALUE;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(join('.', "min", name), min);
                }
            }
        };
    }

    public static Selector maxShort(final String name) {
        return new Selector(name) {
            short max = Short.MIN_VALUE;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                short value = fields.get(this.name).getShort(item);
                if (value > max) {
                    max = value;
                }
            }

            @Override
            public void handleStart(List<? extends Object> results) {
                max = Short.MIN_VALUE;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(join('.', "max", name), max);
                }
            }
        };
    }

    public static Selector maxByte(final String name) {
        return new Selector(name) {
            byte max = Byte.MIN_VALUE;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                byte value = fields.get(this.name).getByte(item);
                if (value > max) {
                    max = value;
                }
            }

            @Override
            public void handleStart(List<? extends Object> results) {
                max = Byte.MIN_VALUE;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(join('.', "max", name), max);
                }
            }
        };
    }


    public static Selector minByte(final String name) {
        return new Selector(name) {
            byte min = Byte.MAX_VALUE;

            @Override
            public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields) {
                byte value = fields.get(this.name).getByte(item);
                if (value < min) {
                    min = value;
                }
            }

            @Override
            public void handleStart(List<? extends Object> results) {
                min = Byte.MIN_VALUE;
            }

            @Override
            public void handleComplete(List<Map<String, Object>> rows) {
                if (rows.size() > 0) {
                    rows.get(0).put(join('.', "min", name), min);
                }
            }
        };
    }

}
