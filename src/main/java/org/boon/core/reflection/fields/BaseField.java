package org.boon.core.reflection.fields;

import org.boon.Exceptions;
import org.boon.Str;
import org.boon.core.Conversions;
import org.boon.core.Typ;
import org.boon.core.Value;
import org.boon.core.reflection.AnnotationData;
import org.boon.core.reflection.Annotations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.core.Conversions.*;
import static org.boon.core.Conversions.toFloat;

public abstract class BaseField implements FieldAccess {

    protected final boolean isPrimitive;
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



        initAnnotationData ( field.getDeclaringClass () );



    }


    @Override
    public Object getValue ( Object obj ) {
        if ( type == Typ.intgr ) {
            int i = this.getInt ( obj );
            return Integer.valueOf ( i );
        } else if ( type == Typ.lng ) {
            long l = this.getLong ( obj );
            return Long.valueOf ( l );
        } else if ( type == Typ.bln ) {
            boolean bool = this.getBoolean ( obj );
            return Boolean.valueOf ( bool );
        } else if ( type == Typ.bt ) {
            byte b = this.getByte ( obj );
            return Byte.valueOf ( b );
        } else if ( type == Typ.shrt ) {
            short s = this.getShort ( obj );
            return Short.valueOf ( s );
        } else if ( type == Typ.chr ) {
            char c = this.getChar ( obj );
            return Character.valueOf ( c );
        } else if ( type == Typ.dbl ) {
            double d = this.getDouble ( obj );
            return Double.valueOf ( d );
        } else if ( type == Typ.flt ) {
            float f = this.getFloat ( obj );
            return Float.valueOf ( f );
        } else {
            return this.getObject ( obj );
        }
    }


    @Override
    public void setValue ( Object obj, Object value ) {
        if ( value != null && value.getClass () == this.type ) {
            this.setObject ( obj, value );
            return;
        }

        if ( value == null ) {
            this.setObject ( obj, null );
            return;
        }

        if (type.isInterface ()) {
            if (Typ.implementsInterface ( value.getClass (), type )) {
               if (Typ.isCollection ( type )) {
                   this.setObject ( obj, Conversions.toCollection ( type, value ) );
                   return;
               }
            }
        }

        if ( type == Typ.string ) {
            setObject ( obj, Conversions.coerce ( type, value ) );
        } else if ( type == Typ.intgr ) {
            setInt ( obj, toInt ( value ) );
        } else if ( type == Typ.bln ) {
            setBoolean ( obj, toBoolean ( value ) );
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


    public void setFromValue ( Object obj, Value value ) {

        if ( type == Typ.string ) {
            setObject ( obj, value.stringValue () );
        } else if ( type == Typ.intgr ) {
            setInt ( obj, value.intValue () );
        } else if ( type == Typ.flt ) {
            setFloat ( obj, value.floatValue () );
        } else if ( type == Typ.dbl ) {
            setDouble ( obj, value.doubleValue () );
        } else if ( type == Typ.lng ) {
            setLong ( obj, value.longValue () );
        } else if ( type == Typ.bt ) {
            setByte ( obj, value.byteValue () );
        } else if ( type == Typ.bln ) {
            setBoolean ( obj, value.booleanValue () );
        } else if ( type == Typ.shrt ) {
            setShort ( obj, value.shortValue () );
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
        } else if ( type.getSuperclass () == Enum.class ) {
            setObject ( obj, value.toEnum ( ( Class<? extends Enum> ) type ) );
        } else {
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
    public final String typeName () {
        return typeName;
    }


}
