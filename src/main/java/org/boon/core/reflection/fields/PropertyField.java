package org.boon.core.reflection.fields;

import org.boon.Exceptions;
import org.boon.core.Typ;
import org.boon.core.Value;
import org.boon.core.reflection.Conversions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.logging.Logger;

import static org.boon.Boon.sputs;
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
    private final Logger log = Logger.getLogger ( PropertyField.class.getName () );


    public PropertyField ( String name, Method setter, Method getter ) {


        try {
            this.setter = setter;
            this.getter = getter;

            if ( getter != null ) {
                isStatic = Modifier.isStatic ( getter.getModifiers () );
                isFinal = Modifier.isFinal ( getter.getModifiers () );
                type = getter.getReturnType ();
            } else {
                isStatic = Modifier.isStatic ( setter.getModifiers () );
                isFinal = Modifier.isFinal ( setter.getModifiers () );
<<<<<<< HEAD
                type = setter.getParameterTypes ()[ 0 ];
=======
                type = setter.getParameterTypes ()[0];
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            }


            readOnly = setter == null;
            this.name = name;
        } catch ( Exception ex ) {
            Exceptions.handle ( "name " + name + " setter " + setter + " getter " + getter, ex );
            throw new RuntimeException ( "die" );
        }
    }

    @Override
    public Object getValue ( Object obj ) {
        try {
            return getter.invoke ( obj );
        } catch ( Exception e ) {
            return Exceptions.handle ( Object.class, sputs ( "unable to call getValue for property ", this.name,
                    "for class ", this.type ), e );
        }
    }

    public boolean getBoolean ( Object obj ) {
        try {
            return ( Boolean ) this.getValue ( obj );
        } catch ( Exception e ) {
            return Exceptions.handle ( boolean.class, sputs ( "unable to call getValue for property", this.name ), e );
        }

    }

    @Override
    public int getInt ( Object obj ) {
        try {
            return ( Integer ) this.getValue ( obj );
        } catch ( Exception e ) {
            return Exceptions.handle ( int.class, sputs ( "unable to call getValue for property", this.name ), e );
        }
    }

    @Override
    public short getShort ( Object obj ) {
        try {
            return ( Short ) this.getValue ( obj );
        } catch ( Exception e ) {
            return Exceptions.handle ( short.class, sputs ( "unable to call getValue for property", this.name ), e );
        }
    }

    @Override
    public char getChar ( Object obj ) {
        try {
            return ( Character ) this.getValue ( obj );
        } catch ( Exception e ) {
            return Exceptions.handle ( char.class, sputs ( "unable to call getValue for property", this.name ), e );
        }
    }

    @Override
    public long getLong ( Object obj ) {
        try {
            return ( Long ) this.getValue ( obj );
        } catch ( Exception e ) {
            throw new RuntimeException ( e );
        }
    }

    @Override
    public double getDouble ( Object obj ) {
        try {
            return ( Double ) this.getValue ( obj );
        } catch ( Exception e ) {
            throw new RuntimeException ( e );
        }

    }

    @Override
    public float getFloat ( Object obj ) {
        try {
            return ( Float ) this.getValue ( obj );
        } catch ( Exception e ) {
            throw new RuntimeException ( e );
        }
    }

    @Override
    public byte getByte ( Object obj ) {
        try {
            return ( Byte ) this.getValue ( obj );
        } catch ( Exception e ) {
            throw new RuntimeException ( e );
        }
    }

    @Override
    public Object getObject ( Object obj ) {
        return getValue ( obj );
    }

    @Override
<<<<<<< HEAD
    public Field getField () {
=======
    public Field getField() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return null;
    }


<<<<<<< HEAD
    public ParameterizedType getParameterizedType () {
=======
    public ParameterizedType getParameterizedType() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc


        return null;

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


<<<<<<< HEAD
    public final void setFromValue ( Object obj, Value value ) {
=======
    public final void setFromValue( Object obj, Value value ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

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
            this.setObject ( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException ( e );
        }

    }

    @Override
    public void setInt ( Object obj, int value ) {
        try {
            this.setObject ( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException ( e );
        }

    }

    @Override
    public void setShort ( Object obj, short value ) {
        try {
            this.setObject ( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException ( e );
        }

    }

    @Override
    public void setChar ( Object obj, char value ) {
        try {
            this.setObject ( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException ( e );
        }

    }

    @Override
    public void setLong ( Object obj, long value ) {
        try {
            this.setObject ( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException ( e );
        }

    }

    @Override
    public void setDouble ( Object obj, double value ) {
        try {
            this.setObject ( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException ( e );
        }

    }

    @Override
    public void setFloat ( Object obj, float value ) {
        try {
            this.setObject ( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException ( e );
        }

    }

    @Override
    public void setByte ( Object obj, byte value ) {
        try {
            this.setObject ( obj, value );
        } catch ( Exception e ) {
            throw new RuntimeException ( e );

        }

    }

    @Override
    public void setObject ( Object obj, Object value ) {
        if ( readOnly ) {
            log.warning ( String.format ( "You tried to modify property %s of %s for instance %s with set %s",
                    name, obj.getClass ().getSimpleName (), obj, value ) );
            return;
        }
        try {
            setter.invoke ( obj, value );
        } catch ( Exception e ) {
            Exceptions.handle ( String.format ( "You tried to modify property %s of %s for instance %s with set %s using %s",
                    name, obj.getClass ().getSimpleName (), obj, value, setter.getName () ), e );

        }

    }

}
