package org.boon.core.reflection.fields;

import org.boon.core.Value;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;


public interface FieldAccess {
<<<<<<< HEAD
    String getName ();
=======
    String getName();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

    Object getValue ( Object obj );

    void setValue ( Object obj, Object value );

<<<<<<< HEAD
    public void setFromValue ( Object obj, Value value );
=======
    public void setFromValue( Object obj, Value value );
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

    boolean getBoolean ( Object obj );

    void setBoolean ( Object obj, boolean value );


    int getInt ( Object obj );

    void setInt ( Object obj, int value );


    short getShort ( Object obj );

    void setShort ( Object obj, short value );

    char getChar ( Object obj );

    void setChar ( Object obj, char value );


    long getLong ( Object obj );

    void setLong ( Object obj, long value );


    double getDouble ( Object obj );

    void setDouble ( Object obj, double value );


    float getFloat ( Object obj );

    void setFloat ( Object obj, float value );


    byte getByte ( Object obj );

    void setByte ( Object obj, byte value );

    Object getObject ( Object obj );

    void setObject ( Object obj, Object value );


<<<<<<< HEAD
    boolean isFinal ();

    boolean isStatic ();

    boolean isVolatile ();

    boolean isQualified ();

    boolean isReadOnly ();

    Class<?> getType ();

    Field getField ();


    public ParameterizedType getParameterizedType ();


    public Class<?> getComponentClass ();
=======
    boolean isFinal();

    boolean isStatic();

    boolean isVolatile();

    boolean isQualified();

    boolean isReadOnly();

    Class<?> getType();

    Field getField();


    public ParameterizedType getParameterizedType();


    public Class<?> getComponentClass();
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
}