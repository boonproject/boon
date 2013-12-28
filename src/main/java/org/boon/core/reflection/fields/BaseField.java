package org.boon.core.reflection.fields;

import org.boon.Exceptions;
import org.boon.Str;
import org.boon.core.Conversions;
import org.boon.core.Typ;
import org.boon.core.Type;
import org.boon.core.Value;
import org.boon.core.reflection.AnnotationData;
import org.boon.core.reflection.Annotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.core.Conversions.*;
import static org.boon.core.Conversions.toFloat;

public abstract class BaseField implements FieldAccess {

    public final boolean isPrimitive;
    protected final boolean isFinal;
    protected final boolean isStatic;
    protected final boolean isVolatile;
    protected final boolean qualified;
    protected final boolean readOnly;
    protected final Class<?> type;
    protected final String name;
    protected final ParameterizedType parameterizedType;
    protected final Class<?> componentClass;
    protected final String typeName;
    public final Type typeEnum;
    private  Map<String,  Map<String, Object>> annotationMap = new ConcurrentHashMap<> (  );



    private void initAnnotationData(Class clazz ) {

        final Collection<AnnotationData> annotationDataForFieldAndProperty =
                Annotations.getAnnotationDataForFieldAndProperty ( clazz, name, Collections.EMPTY_SET );

        for (AnnotationData data : annotationDataForFieldAndProperty) {
            annotationMap.put ( data.getSimpleClassName (), data.getValues () );
            annotationMap.put ( data.getFullClassName (), data.getValues () );
        }

    }

    protected BaseField ( String name, Method getter, Method setter ) {

        try {

            readOnly = setter == null;
            this.name = name.intern();
            isVolatile = false;
            qualified = false;


            if ( getter != null ) {
                isStatic = Modifier.isStatic ( getter.getModifiers () );
                isFinal = Modifier.isFinal ( getter.getModifiers () );
                type = getter.getReturnType ();
                isPrimitive = type.isPrimitive ();
                typeName = type.getName().intern ();
                Object obj = getter.getGenericReturnType ();

                if ( obj instanceof ParameterizedType ) {

                    parameterizedType = ( ParameterizedType ) obj;
                } else {
                    parameterizedType = null;
                }


                if ( parameterizedType == null ) {
                    componentClass = null;
                } else {
                    Object obj2 = parameterizedType.getActualTypeArguments ()[ 0 ];
                    if (obj2 instanceof Class) {
                        componentClass = ( Class<?> ) parameterizedType.getActualTypeArguments ()[ 0 ];
                    }else {
                        componentClass=null;
                    }
                }


                initAnnotationData ( getter.getDeclaringClass () );

            } else {
                isStatic = Modifier.isStatic ( setter.getModifiers () );
                isFinal = Modifier.isFinal ( setter.getModifiers () );
                type = setter.getParameterTypes ()[ 0 ];
                isPrimitive = type.isPrimitive ();
                typeName = type.getName ().intern ();
                parameterizedType = null;
                componentClass = null;

                initAnnotationData ( setter.getDeclaringClass () );
            }

            if (name.startsWith ( "$" )) {
               this.typeEnum = Type.SYSTEM;
            } else {
                this.typeEnum = Type.getType(type);
            }
        } catch ( Exception ex ) {
            Exceptions.handle ( "name " + name + " setter " + setter + " getter " + getter, ex );
            throw new RuntimeException ( "die" );
        }

    }


    protected BaseField ( Field field ) {
        name = field.getName().intern ();

        isFinal = Modifier.isFinal ( field.getModifiers () );
        isStatic = Modifier.isStatic ( field.getModifiers () );

        isVolatile = Modifier.isVolatile ( field.getModifiers () );
        qualified = isFinal || isVolatile;
        readOnly = isFinal || isStatic;
        type = field.getType ();
        typeName = type.getName().intern ();
        isPrimitive = type.isPrimitive ();

        if ( field != null ) {
            Object obj = field.getGenericType ();

            if ( obj instanceof ParameterizedType ) {

                parameterizedType = ( ParameterizedType ) obj;
            } else {
                parameterizedType = null;
            }

        } else {
            parameterizedType = null;
        }


        if ( parameterizedType == null ) {
            componentClass = null;
        } else {
            Object obj = parameterizedType.getActualTypeArguments ()[ 0 ];
            if (obj instanceof Class) {
                componentClass = ( Class<?> ) parameterizedType.getActualTypeArguments ()[ 0 ];
            }else {
                componentClass=null;
            }
        }




        if (name.startsWith ( "$" )) {
            this.typeEnum = Type.SYSTEM;
        } else {
            this.typeEnum = Type.getType(type);
        }
        initAnnotationData ( field.getDeclaringClass () );



    }


    @Override
    public Object getValue ( Object obj ) {

        switch ( typeEnum ) {
            case INT:
                return this.getInt ( obj );
            case LONG:
                return this.getLong ( obj );
            case BOOLEAN:
                return this.getBoolean ( obj );
            case BYTE:
                return this.getByte ( obj );
            case SHORT:
                return this.getShort ( obj );
            case CHAR:
                return this.getChar ( obj );
            case DOUBLE:
                return this.getDouble ( obj );
            case FLOAT:
                return this.getFloat ( obj );
            default:
                return this.getObject ( obj );

        }
    }


    @Override
    public void setValue ( Object obj, Object value ) {

        switch ( typeEnum ) {
            case INT:
                 this.setInt ( obj, toInt ( value ) );
                 return;
            case LONG:
                 this.setLong ( obj, toLong ( value ) );
                 return;
            case BOOLEAN:
                 this.setBoolean ( obj, toBoolean ( value ) );
                 return;
            case BYTE:
                 this.setByte ( obj, toByte ( value ) );
                 return;
            case SHORT:
                 this.setShort ( obj, toShort ( value ) );
                 return;
            case CHAR:
                 this.setChar ( obj, toChar ( value ) );
                 return;
            case DOUBLE:
                 this.setDouble ( obj, toDouble ( value ) );
                 return;
            case FLOAT:
                 this.setFloat ( obj, toFloat ( value ) );
                 return;
            case DATE:
                this.setObject ( obj, toDate ( value ) );
                return;
            case STRING:
                if (value instanceof String)  {
                    this.setObject ( obj, value );
                } else {
                    this.setObject ( obj, Conversions.toString (value) );
                }
                return;
            case ENUM:
                if ( value.getClass () == this.type ) {
                    this.setObject ( obj, value );
                } else {
                    this.setObject ( obj, toEnum ( (Class<? extends Enum>) type, value ) );
                }
                return;
            case BIG_DECIMAL:
                if ( value instanceof BigDecimal )  {
                    this.setObject ( obj, value );
                } else {
                    this.setObject ( obj, toBigDecimal ( value ) );
                }
                return;
            case BIG_INT:
                if ( value instanceof BigInteger )  {
                    this.setObject ( obj, value );
                } else {
                    this.setObject ( obj, toBigInteger ( value ) );
                }
                return;
            case COLLECTION:
            case LIST:
            case SET:
                 this.setObject ( obj, Conversions.toCollection ( type, value ) );
                 return;
            default:
                if ( value != null ) {
                    if ( value.getClass () == this.type ) {
                        this.setObject ( obj, value );
                    } else if ( Typ.implementsInterface ( value.getClass (), type ) ) {
                        this.setObject ( obj, value );
                    } else {
                        setObject ( obj, Conversions.coerce ( type, value ) );
                    }
                } else {
                    this.setObject ( obj, null );
                }

        }

    }


    public void setFromValue ( Object obj, Value value ) {

        switch ( typeEnum ) {
            case INT:
                this.setInt ( obj, value.intValue()  );
                return;
            case LONG:
                this.setLong ( obj, value.longValue () );
                return;
            case BOOLEAN:
                this.setBoolean ( obj, value.booleanValue () );
                return;
            case BYTE:
                this.setByte ( obj, value.byteValue () );
                return;
            case SHORT:
                this.setShort ( obj, value.shortValue () );
                return;
            case CHAR:
                this.setChar ( obj, value.charValue () );
                return;
            case DOUBLE:
                this.setDouble ( obj, value.doubleValue () );
                return;
            case FLOAT:
                this.setFloat ( obj, value.floatValue () );
                return;
            case INTEGER_WRAPPER:
                this.setObject ( obj, value.intValue () );
                return;
            case LONG_WRAPPER:
                this.setObject ( obj, value.longValue () );
                return;
            case BOOLEAN_WRAPPER:
                this.setObject ( obj, value.booleanValue () );
                return;
            case BYTE_WRAPPER:
                this.setObject ( obj, value.byteValue () );
                return;
            case SHORT_WRAPPER:
                this.setObject ( obj, value.shortValue () );
                return;
            case CHAR_WRAPPER:
                this.setObject ( obj, value.charValue () );
                return;
            case DOUBLE_WRAPPER:
                this.setObject ( obj, value.doubleValue () );
                return;
            case FLOAT_WRAPPER:
                this.setObject ( obj, value.floatValue () );
                return;
            case STRING:
            case CHAR_SEQUENCE:
                this.setObject ( obj, value.stringValue() );
                return;
            case BIG_DECIMAL:
                this.setObject ( obj, value.bigDecimalValue () );
                return;
            case BIG_INT:
                this.setObject ( obj, value.bigIntegerValue () );
                return;
            case DATE:
                this.setObject ( obj, value.dateValue () );
                return;
            case ENUM:
                this.setObject ( obj, value.toEnum (  ( Class<? extends Enum> )type ) );
                return;
            default:
                setObject ( obj, coerce ( type, value ) );
        }

    }


    public ParameterizedType getParameterizedType () {

        return parameterizedType;

    }


    public Class<?> getComponentClass () {
        return componentClass;
    }



    protected void analyzeError( Exception e, Object obj ) {
        Exceptions.handle( Str.lines (
                e.getClass ().getName (),
                String.format ( "cause %s", e.getCause () ),
                String.format ( "Field info name %s, type %s, class that declared field %s", this.getName (), this.getType (), this.getField ().getDeclaringClass () ),
                String.format ( "Type of object passed %s", obj.getClass ().getName () )
        ), e );

    }



    @Override
    public boolean hasAnnotation ( String annotationName ) {
        return this.annotationMap.containsKey ( annotationName );
    }

    @Override
    public Map<String, Object> getAnnotationData ( String annotationName ) {

        return this.annotationMap.get ( annotationName );
    }


    public boolean isPrimitive() {
        return  isPrimitive;
    }






    @Override
    public final Type typeEnum () {
        return this.typeEnum;
    }


}
