package org.boon.core.reflection.fields;

import org.boon.core.Type;
import org.boon.core.Value;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Map;


public interface FieldAccess {
    boolean injectable();
    boolean requiresInjection();
    boolean isNamed();
    boolean hasAlias();

    String getAlias();

    String named();

    String getName();
    Object getValue( Object obj );

    void setValue( Object obj, Object value );
    public void setFromValue( Object obj, Value value );

    boolean getBoolean( Object obj );
    void setBoolean( Object obj, boolean value );


    int getInt( Object obj );
    void setInt( Object obj, int value );


    short getShort( Object obj );
    void setShort( Object obj, short value );

    char getChar( Object obj );
    void setChar( Object obj, char value );


    long getLong( Object obj );
    void setLong( Object obj, long value );


    double getDouble( Object obj );
    void setDouble( Object obj, double value );


    float getFloat( Object obj );
    void setFloat( Object obj, float value );


    byte getByte( Object obj );
    void setByte( Object obj, byte value );

    Object getObject( Object obj );
    void setObject( Object obj, Object value );


    Type typeEnum();


    boolean isPrimitive();

    boolean isFinal();
    boolean isStatic();
    boolean isVolatile();
    boolean isQualified();
    boolean isReadOnly();
    boolean isWriteOnly();

    Class<?> type();


    Class<?> declaringParent();

    Object parent();
    Field getField();



    boolean include();
    boolean ignore();

    ParameterizedType getParameterizedType();
    Class<?> getComponentClass();
    boolean hasAnnotation(String annotationName) ;
    Map<String, Object> getAnnotationData(String annotationName) ;
    boolean isViewActive (String activeView);


}