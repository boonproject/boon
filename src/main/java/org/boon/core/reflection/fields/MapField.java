package org.boon.core.reflection.fields;

import org.boon.core.reflection.Conversions;

import java.lang.reflect.Field;
import java.util.Map;

import static org.boon.Exceptions.die;

//import static org.boon.utils.ComplainAndDie.complain;


public class MapField implements FieldAccess {

    private String name;

    public MapField() {

    }

    public MapField(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getValue(Object obj) {
        if (obj instanceof Map) {
            Map map = (Map) obj;
            return map.get(name);
        }
        return die(Object.class, "Object must be a map but was a " + obj.getClass().getName());
    }

    @Override
    public void setValue(Object obj, Object value) {
        if (obj instanceof Map) {
            Map map = (Map) obj;
            map.put(name, value);
        }
        die("Object must be a map");
    }

    @Override
    public boolean getBoolean(Object obj) {
        if (obj instanceof Map) {
            Map map = (Map) obj;
            return Conversions.toBoolean(map.get(name));
        }
        return die(Boolean.class, "Object must be a map");
    }

    @Override
    public void setBoolean(Object obj, boolean value) {
        if (obj instanceof Map) {
            Map map = (Map) obj;
            map.put(name, value);
        }
        die("Object must be a map");
    }

    @Override
    public int getInt(Object obj) {
        if (obj instanceof Map) {
            Map map = (Map) obj;
            return Conversions.toInt(map.get(name));
        }
        die("Object must be a map");
        return -1;
    }

    @Override
    public void setInt(Object obj, int value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public short getShort(Object obj) {
        if (obj instanceof Map) {
            Map map = (Map) obj;
            return Conversions.toShort(map.get(name));
        }
        die("Object must be a map");
        return -1;
    }

    @Override
    public void setShort(Object obj, short value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public char getChar(Object obj) {
        if (obj instanceof Map) {
            Map map = (Map) obj;
            return Conversions.toChar(map.get(name));
        }
        die("Object must be a map");
        return 0;
    }

    @Override
    public void setChar(Object obj, char value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getLong(Object obj) {
        if (obj instanceof Map) {
            Map map = (Map) obj;
            return Conversions.toLong(map.get(name));
        }
        die("Object must be a map");
        return -1;
    }

    @Override
    public void setLong(Object obj, long value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getDouble(Object obj) {
        if (obj instanceof Map) {
            Map map = (Map) obj;
            return Conversions.toDouble(map.get(name));
        }
        die("Object must be a map");
        return Double.NaN;
    }

    @Override
    public void setDouble(Object obj, double value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public float getFloat(Object obj) {
        if (obj instanceof Map) {
            Map map = (Map) obj;
            return Conversions.toFloat(map.get(name));
        }
        die("Object must be a map");
        return Float.NaN;
    }

    @Override
    public void setFloat(Object obj, float value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public byte getByte(Object obj) {
        if (obj instanceof Map) {
            Map map = (Map) obj;
            return Conversions.toByte(map.get(name));
        }
        die("Object must be a map");
        return Byte.MAX_VALUE;
    }

    @Override
    public void setByte(Object obj, byte vaue) {

    }

    @Override
    public Object getObject(Object obj) {
        if (obj instanceof Map) {
            Map map = (Map) obj;
            return map.get(name);
        }
        die("Object must be a map");
        return -1;
    }

    @Override
    public void setObject(Object obj, Object value) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public Field getField() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isFinal() {
        return false;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isVolatile() {
        return false;
    }

    @Override
    public boolean isQualified() {
        return false;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public Class<?> getType() {
        return null;
    }
}
