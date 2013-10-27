package org.boon.core.reflection.fields;

import org.boon.Exceptions;
import org.boon.core.Typ;
import org.boon.core.reflection.Conversions;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

import static org.boon.core.reflection.Conversions.*;

public class PropertyField implements FieldAccess {
    protected final boolean isFinal;
    protected final boolean isStatic;
    protected final boolean isVolatile = false;
    protected final boolean qualified = false;
    protected final boolean readOnly;
    private final Class<?> type;
    private final String name;
    private final Method getter;
    private final Method setter;
    private final Logger log =  Logger.getLogger(PropertyField.class.getName());


    public PropertyField(String name, Method setter, Method getter) {
        this.setter = setter;
        this.getter = getter;

        isStatic = Modifier.isStatic(getter.getModifiers());
        isFinal = Modifier.isFinal(getter.getModifiers());


        readOnly = setter == null;
        type = getter.getReturnType();
        this.name = name;
    }

    @Override
    public Object getValue(Object obj) {
        try {
            return getter.invoke(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean getBoolean(Object obj) {
        try {
            return (Boolean) this.getValue(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public int getInt(Object obj) {
        try {
            return (Integer) this.getValue(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public short getShort(Object obj) {
        try {
            return (Short) this.getValue(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public char getChar(Object obj) {
        try {
            return (Character) this.getValue(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getLong(Object obj) {
        try {
            return (Long) this.getValue(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public double getDouble(Object obj) {
        try {
            return (Double) this.getValue(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public float getFloat(Object obj) {
        try {
            return (Float) this.getValue(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte getByte(Object obj) {
        try {
            return (Byte) this.getValue(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getObject(Object obj) {
        return getValue(obj);
    }

    @Override
    public Field getField() {
        return null;
    }


    @Override
    public boolean isFinal() {
        return isFinal;
    }


    @Override
    public boolean isStatic() {
        return isStatic;
    }

    @Override
    public boolean isVolatile() {
        return isVolatile;
    }


    @Override
    public boolean isQualified() {
        return qualified;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }


    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setValue(Object obj, Object value) {
        if (obj.getClass() == this.type) {
            this.setObject(obj, value);
            return;
        }

        if (type == Typ.intgr) {
            setInt(obj, toInt(value));
        } else if (type == Typ.lng) {
            setLong(obj, toLong(value));
        } else if (type == Typ.bt) {
            setByte(obj, toByte(value));

        } else if (type == Typ.shrt) {
            setShort(obj, toShort(value));

        } else if (type == Typ.chr) {
            setChar(obj, toChar(value));

        } else if (type == Typ.dbl) {
            setDouble(obj, toDouble(value));

        } else if (type == Typ.flt) {
            setFloat(obj, toFloat(value));

        } else {
            setObject(obj, Conversions.coerce(type, value));
        }
    }

    @Override
    public void setBoolean(Object obj, boolean value) {
        try {
            this.setObject(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setInt(Object obj, int value) {
        try {
            this.setObject(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setShort(Object obj, short value) {
        try {
            this.setObject(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setChar(Object obj, char value) {
        try {
            this.setObject(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setLong(Object obj, long value) {
        try {
            this.setObject(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setDouble(Object obj, double value) {
        try {
            this.setObject(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setFloat(Object obj, float value) {
        try {
            this.setObject(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setByte(Object obj, byte value) {
        try {
            this.setObject(obj, value);
        } catch (Exception e) {
            throw new RuntimeException(e);

        }

    }

    @Override
    public void setObject(Object obj, Object value) {
        if (readOnly) {
            log.warning(String.format("You tried to modify property %s of %s for instance %s with set %s",
                    name, obj.getClass().getSimpleName(), obj, value));
            return;
        }
        try {
            setter.invoke(obj, value);
        } catch (Exception e) {
            Exceptions.handle(String.format("You tried to modify property %s of %s for instance %s with set %s using %s",
                    name, obj.getClass().getSimpleName(), obj, value, setter.getName()), e);

        }

    }

}
