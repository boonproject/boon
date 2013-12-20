package org.boon.core.reflection.fields;


import org.boon.Exceptions;
import org.boon.Str;
import org.boon.core.Typ;
import org.boon.core.Value;
import org.boon.core.reflection.Conversions;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Objects;

import static org.boon.core.reflection.Conversions.*;

public class ReflectField implements FieldAccess {
    protected final Field field;
    protected final boolean isFinal;
    protected final boolean isStatic;
    protected final boolean isVolatile;
    protected final boolean qualified;
    protected final boolean readOnly;
    private final Class<?> type;
    private final String name;

    public ReflectField ( Field f ) {
        field = f;
        isFinal = Modifier.isFinal ( field.getModifiers () );
        isStatic = Modifier.isStatic ( field.getModifiers () );
        isVolatile = Modifier.isVolatile ( field.getModifiers () );
        qualified = isFinal || isVolatile;
        readOnly = isFinal || isStatic;
        type = f.getType ();
        name = f.getName ();
    }

    @Override
    public Object getValue ( Object obj ) {
        try {
            Objects.requireNonNull ( field );
            Objects.requireNonNull ( obj );

            return field.get ( obj );
        } catch ( Exception e ) {
            e.printStackTrace ();
            analyzeError ( e, obj );
            return null;
        }
    }

    private void analyzeError ( Exception e, Object obj ) {
        Exceptions.handle ( Str.lines (
                e.getClass ().getName (),
                String.format ( "cause %s", e.getCause () ),
                String.format ( "Field info name %s, type %s, class that declared field %s", this.getName (), this.getType (), this.getField ().getDeclaringClass () ),
                String.format ( "Type of object passed %s", obj.getClass ().getName () )
        ), e );

    }

<<<<<<< HEAD
    public ParameterizedType getParameterizedType () {
=======
    public ParameterizedType getParameterizedType() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc


        ParameterizedType type = null;

        if ( field != null ) {
            Object obj = field.getGenericType ();

            if ( obj instanceof ParameterizedType ) {

                type = ( ParameterizedType ) obj;
            }

        }

        return type;

    }

    private Class<?> componentClass;

<<<<<<< HEAD
    public Class<?> getComponentClass () {
=======
    public Class<?> getComponentClass() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( componentClass == null ) {
            componentClass = doGetComponentClass ();
        }
        return componentClass;
    }


<<<<<<< HEAD
    private Class<?> doGetComponentClass () {
=======
    private Class<?> doGetComponentClass() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        final ParameterizedType parameterizedType = this.getParameterizedType ();
        if ( parameterizedType == null ) {
            return null;
        } else {
<<<<<<< HEAD
            return ( Class<?> ) ( parameterizedType.getActualTypeArguments ()[ 0 ] );
=======
            return ( Class<?> ) ( parameterizedType.getActualTypeArguments ()[0] );
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        }
    }


    public boolean getBoolean ( Object obj ) {
        try {
            return field.getBoolean ( obj );
        } catch ( Exception e ) {
            analyzeError ( e, obj );
            return false;
        }

    }

    @Override
    public int getInt ( Object obj ) {
        try {
            return field.getInt ( obj );
        } catch ( Exception e ) {
            analyzeError ( e, obj );
            return 0;
        }
    }

    @Override
    public short getShort ( Object obj ) {
        try {
            return field.getShort ( obj );
        } catch ( Exception e ) {
            analyzeError ( e, obj );
            return 0;
        }
    }

    @Override
    public char getChar ( Object obj ) {
        try {
            return field.getChar ( obj );
        } catch ( Exception e ) {
            analyzeError ( e, obj );
            return 0;
        }
    }

    @Override
    public long getLong ( Object obj ) {
        try {
            return field.getLong ( obj );
        } catch ( Exception e ) {
            analyzeError ( e, obj );
            return 0;
        }
    }

    @Override
    public double getDouble ( Object obj ) {
        try {
            return field.getDouble ( obj );
        } catch ( Exception e ) {
            analyzeError ( e, obj );
            return 0;
        }

    }

    @Override
    public float getFloat ( Object obj ) {
        try {
            return field.getFloat ( obj );
        } catch ( Exception e ) {
            analyzeError ( e, obj );
            return 0;
        }
    }

    @Override
    public byte getByte ( Object obj ) {
        try {
            return field.getByte ( obj );
        } catch ( Exception e ) {
            analyzeError ( e, obj );
            return 0;
        }
    }

    @Override
    public Object getObject ( Object obj ) {
        return getValue ( obj );
    }

<<<<<<< HEAD
    public boolean getStaticBoolean () {
        return getBoolean ( null );
    }

    public int getStaticInt () {
=======
    public boolean getStaticBoolean() {
        return getBoolean ( null );
    }

    public int getStaticInt() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return getInt ( null );

    }

<<<<<<< HEAD
    public short getStaticShort () {
=======
    public short getStaticShort() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return getShort ( null );
    }


<<<<<<< HEAD
    public long getStaticLong () {
=======
    public long getStaticLong() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return getLong ( null );
    }


<<<<<<< HEAD
    public double getStaticDouble () {
        return getDouble ( null );
    }

    public float getStaticFloat () {
        return getFloat ( null );
    }

    public byte getStaticByte () {
        return getByte ( null );
    }

    public Object getObject () {
=======
    public double getStaticDouble() {
        return getDouble ( null );
    }

    public float getStaticFloat() {
        return getFloat ( null );
    }

    public byte getStaticByte() {
        return getByte ( null );
    }

    public Object getObject() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return getObject ( null );
    }

    @Override
<<<<<<< HEAD
    public Field getField () {
=======
    public Field getField() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return field;
    }


    @Override
<<<<<<< HEAD
    public boolean isFinal () {
=======
    public boolean isFinal() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return isFinal;
    }


    @Override
<<<<<<< HEAD
    public boolean isStatic () {
=======
    public boolean isStatic() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return isStatic;
    }

    @Override
<<<<<<< HEAD
    public boolean isVolatile () {
=======
    public boolean isVolatile() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return isVolatile;
    }


    @Override
<<<<<<< HEAD
    public boolean isQualified () {
=======
    public boolean isQualified() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return qualified;
    }

    @Override
<<<<<<< HEAD
    public boolean isReadOnly () {
=======
    public boolean isReadOnly() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return readOnly;
    }


    @Override
<<<<<<< HEAD
    public Class<?> getType () {
=======
    public Class<?> getType() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return type;
    }

    @Override
<<<<<<< HEAD
    public String getName () {
=======
    public String getName() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return name;
    }

    @Override
<<<<<<< HEAD
    public void setValue ( Object obj, Object value ) {
=======
    public void setValue( Object obj, Object value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( value != null && value.getClass () == this.type ) {
            this.setObject ( obj, value );
            return;
        }

        if ( value instanceof Value ) {
            setFromValue ( obj, ( Value ) value );
        } else if ( type == Typ.string ) {
            setObject ( obj, coerce ( type, value ) );
        } else if ( type == Typ.intgr ) {
            setInt ( obj, toInt ( value ) );
        } else if ( type == Typ.lng ) {
            setLong ( obj, toLong ( value ) );
        } else if ( type == Typ.bt ) {
            setByte ( obj, toByte ( value ) );

        } else if ( type == Typ.shrt ) {
            setShort ( obj, toShort ( value ) );

        } else if ( type == Typ.chr ) {
            setChar ( obj, toChar ( value ) );

        } else if ( type == Typ.dbl ) {
            setDouble ( obj, toDouble ( value ) );

        } else if ( type == Typ.flt ) {
            setFloat ( obj, toFloat ( value ) );

        } else {
            setObject ( obj, Conversions.coerce ( type, value ) );
        }
    }


    public final void setFromValue ( Object obj, Value value ) {

        if ( type == Typ.string ) {
            setObject ( obj, value.stringValue () );
        } else if ( type == Typ.intgr ) {
            setInt ( obj, value.intValue () );
        } else if ( type == Typ.flt ) {
            setFloat ( obj, value.floatValue () );
        } else if ( type == Typ.dbl ) {
            setDouble ( obj, value.doubleValue () );
        } else if ( type == Typ.lng ) {
            setDouble ( obj, value.longValue () );
        } else if ( type == Typ.bt ) {
            setByte ( obj, value.byteValue () );
        } else if ( type == Typ.bln ) {
            setBoolean ( obj, value.booleanValue () );
        } else if ( type == Typ.shrt ) {
            setObject ( obj, value.shortValue () );
        } else if ( type == Typ.integer ) {
            setObject ( obj, value.intValue () );
        } else if ( type == Typ.floatWrapper ) {
            setObject ( obj, value.floatValue () );
        } else if ( type == Typ.doubleWrapper ) {
            setObject ( obj, value.doubleValue () );
        } else if ( type == Typ.longWrapper ) {
            setObject ( obj, value.longValue () );
        } else if ( type == Typ.byteWrapper ) {
            setObject ( obj, value.byteValue () );
        } else if ( type == Typ.bool ) {
            setObject ( obj, value.booleanValue () );
        } else if ( type == Typ.shortWrapper ) {
            setObject ( obj, value.shortValue () );
        } else if ( type == Typ.bigDecimal ) {
            setObject ( obj, value.bigDecimalValue () );
        } else if ( type == Typ.bigInteger ) {
            setObject ( obj, value.bigIntegerValue () );
        } else if ( type == Typ.date ) {
            setObject ( obj, value.dateValue () );
        } else {
            setValue ( obj, coerce ( type, value ) );
        }

    }


    @Override
    public void setBoolean ( Object obj, boolean value ) {
        try {
            field.setBoolean ( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError ( e, obj );
        }

    }

    @Override
    public void setInt ( Object obj, int value ) {
        try {
            field.setInt ( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError ( e, obj );
        }

    }

    @Override
    public void setShort ( Object obj, short value ) {
        try {
            field.setShort ( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError ( e, obj );
        }

    }

    @Override
    public void setChar ( Object obj, char value ) {
        try {
            field.setChar ( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError ( e, obj );
        }

    }

    @Override
    public void setLong ( Object obj, long value ) {
        try {
            field.setLong ( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError ( e, obj );
        }

    }

    @Override
    public void setDouble ( Object obj, double value ) {
        try {
            field.setDouble ( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError ( e, obj );
        }

    }

    @Override
    public void setFloat ( Object obj, float value ) {
        try {
            field.setFloat ( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError ( e, obj );
        }

    }

    @Override
    public void setByte ( Object obj, byte value ) {
        try {
            field.setByte ( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError ( e, obj );
        }

    }

    @Override
    public void setObject ( Object obj, Object value ) {
        try {
            field.set ( obj, value );
        } catch ( IllegalAccessException e ) {
            analyzeError ( e, obj );
        }

    }

}
