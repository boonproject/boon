package org.boon.core.reflection.fields;

import java.lang.reflect.Field;


public interface FieldAccess {
    String getName();

    Object getValue(Object obj);

    void setValue(Object obj, Object value);


    boolean getBoolean(Object obj);

    void setBoolean(Object obj, boolean value);


    int getInt(Object obj);

    void setInt(Object obj, int value);


    short getShort(Object obj);

    void setShort(Object obj, short value);

    char getChar(Object obj);

    void setChar(Object obj, char value);


    long getLong(Object obj);

    void setLong(Object obj, long value);


    double getDouble(Object obj);

    void setDouble(Object obj, double value);


    float getFloat(Object obj);

    void setFloat(Object obj, float value);


    byte getByte(Object obj);

    void setByte(Object obj, byte value);

    Object getObject(Object obj);

    void setObject(Object obj, Object value);


    boolean isFinal();

    boolean isStatic();

    boolean isVolatile();

    boolean isQualified();

    boolean isReadOnly();

    Class<?> getType();

    Field getField();
}