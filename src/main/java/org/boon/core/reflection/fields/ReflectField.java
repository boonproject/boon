package org.boon.core.reflection.fields;


import org.boon.Exceptions;
import org.boon.Str;

import java.lang.reflect.Field;
import java.util.Objects;


public class ReflectField extends BaseField {
    private final Field field;

    public ReflectField ( Field field ) {
        super ( field );
        this.field = field;
    }

    @Override
    public Object getValue( Object obj ) {
        try {
            Objects.requireNonNull( field );
            Objects.requireNonNull( obj );

            return field.get( obj );
        } catch ( Exception e ) {
            e.printStackTrace();
            analyzeError( e, obj );
            return null;
        }
    }


    public boolean getBoolean( Object obj ) {
        try {
            return field.getBoolean( obj );
        } catch ( Exception e ) {
            analyzeError( e, obj );
            return false;
        }

    }

    @Override
    public int getInt( Object obj ) {
        try {
            return field.getInt( obj );
        } catch ( Exception e ) {
            analyzeError( e, obj );
            return 0;
        }
    }

    @Override
    public short getShort( Object obj ) {
        try {
            return field.getShort( obj );
        } catch ( Exception e ) {
            analyzeError( e, obj );
            return 0;
        }
    }

    @Override
    public char getChar( Object obj ) {
        try {
            return field.getChar( obj );
        } catch ( Exception e ) {
            analyzeError( e, obj );
            return 0;
        }
    }

    @Override
    public long getLong( Object obj ) {
        try {
            return field.getLong( obj );
        } catch ( Exception e ) {
            analyzeError( e, obj );
            return 0;
        }
    }

    @Override
    public double getDouble( Object obj ) {
        try {
            return field.getDouble( obj );
        } catch ( Exception e ) {
            analyzeError( e, obj );
            return 0;
        }

    }

    @Override
    public float getFloat( Object obj ) {
        try {
            return field.getFloat( obj );
        } catch ( Exception e ) {
            analyzeError( e, obj );
            return 0;
        }
    }

    @Override
    public byte getByte( Object obj ) {
        try {
            return field.getByte( obj );
        } catch ( Exception e ) {
            analyzeError( e, obj );
            return 0;
        }
    }

    @Override
    public Object getObject( Object obj ) {
        return getValue( obj );
    }

    public boolean getStaticBoolean() {
        return getBoolean( null );
    }

    public int getStaticInt() {
        return getInt( null );

    }

    public short getStaticShort() {
        return getShort( null );
    }


    public long getStaticLong() {
        return getLong( null );
    }


    public double getStaticDouble() {
        return getDouble( null );
    }

    public float getStaticFloat() {
        return getFloat( null );
    }

    public byte getStaticByte() {
        return getByte( null );
    }

    public Object getObject() {
        return getObject( null );
    }

    @Override
    public Field getField() {
        return field;
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
    public void setBoolean( Object obj, boolean value ) {
        try {
            field.setBoolean( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError( e, obj );
        }

    }

    @Override
    public void setInt( Object obj, int value ) {
        try {
            field.setInt( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError( e, obj );
        }

    }

    @Override
    public void setShort( Object obj, short value ) {
        try {
            field.setShort( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError( e, obj );
        }

    }

    @Override
    public void setChar( Object obj, char value ) {
        try {
            field.setChar( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError( e, obj );
        }

    }

    @Override
    public void setLong( Object obj, long value ) {
        try {
            field.setLong( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError( e, obj );
        }

    }

    @Override
    public void setDouble( Object obj, double value ) {
        try {
            field.setDouble( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError( e, obj );
        }

    }

    @Override
    public void setFloat( Object obj, float value ) {
        try {
            field.setFloat( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError( e, obj );
        }

    }

    @Override
    public void setByte( Object obj, byte value ) {
        try {
            field.setByte( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError( e, obj );
        }

    }

    @Override
    public void setObject( Object obj, Object value ) {
        try {
            field.set( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError( e, obj );
        }

    }

}
