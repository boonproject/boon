package org.boon.core.reflection;


import org.boon.Lists;
import org.boon.core.Conversions;
import org.boon.core.Typ;
import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.core.reflection.fields.FieldsAccessor;
import org.boon.core.value.ValueContainer;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.boon.Exceptions.die;
import static org.boon.Exceptions.handle;
import static org.boon.core.Conversions.toEnum;
import static org.boon.core.reflection.MapObjectConversion.fromList;
import static org.boon.core.reflection.MapObjectConversion.fromMap;

/**
 * This version is not optimized like fields.
 * I plan on having an optimized version.
 * @author Rick Hightower
 * Created by Richard on 2/17/14.
 */
public class Invoker {




        public static Object invokeOverloadedFromObject (Object object, String name, Object args){
            if (object instanceof Map) {
                return invokeOverloadedFromList( object, name, Lists.list( args ) );
            } else if (object instanceof List) {
                List list = ( List ) args;
                ClassMeta classMeta = ClassMeta.classMeta( object.getClass() );
                MethodAccess m = classMeta.method( name );
                if (m.parameterTypes().length == 1 && list.size() > 0 && !(list.get( 0 ) instanceof List)) {

                    return invokeOverloadedFromList( object, name, Lists.list( args ) );
                } else {
                    return invokeOverloadedFromList( object, name, list );

                }
            } else {
                return invokeOverloadedFromList( object, name, Lists.list( args ) );
            }
        }

        public static Object invokeFromObject (Object object, String name, Object args){
            if (object instanceof Map) {
                return invokeFromList( object, name, Lists.list( args ) );
            } else if (object instanceof List) {
                List list = ( List ) args;
                ClassMeta classMeta = ClassMeta.classMeta( object.getClass() );
                MethodAccess m = classMeta.method( name );
                if (m.parameterTypes().length == 1 && list.size() > 0 && !(list.get( 0 ) instanceof List)) {

                    return invokeFromList( object, name, Lists.list( args ) );
                } else {
                    return invokeFromList( object, name, list );

                }
            } else {
                return invokeFromList( object, name, Lists.list( args ) );
            }
        }

        public static Object invokeFromList (Object object, String name, List<?> args){
            List<Object> list = new ArrayList( args );
            ClassMeta classMeta = ClassMeta.classMeta( object.getClass() );
            MethodAccess m = classMeta.method( name );
            Class<?>[] parameterTypes = m.parameterTypes();
            if (list.size()!=parameterTypes.length) {
                return die(Object.class, "Unable to invoke method", name, "on object", object, "with arguments", list);
            }

            FieldsAccessor fieldsAccessor = FieldAccessMode.FIELD.create( true );

            for (int index = 0; index < parameterTypes.length; index++) {

                if ( !matchAndConvertArgs( fieldsAccessor, list, m, parameterTypes, index ) ) {
                    return die(Object.class,"Unable to invoke method as argument types did not match",
                            name, "on object", object, "with arguments", list );
                }

            }

            return m.invoke( object, list.toArray(new Object[list.size()]) );

    }

    public static Object invoke (Object object, String name, Object... args){
         ClassMeta classMeta = ClassMeta.classMeta( object.getClass() );
         MethodAccess invoker = classMeta.method( name );
         return invoker.invoke( object, args );
    }


    public static Object invokeOverloaded (Object object, String name, Object... args){
        ClassMeta classMeta = ClassMeta.classMeta( object.getClass() );
        Iterable<MethodAccess> invokers =   classMeta.methods( name );

        loop:
        for (MethodAccess m : invokers) {
            Class<?>[] parameterTypes = m.parameterTypes();
            if ( !(parameterTypes.length == args.length) ) {
                continue;
            }
            for (int index = 0; index < parameterTypes.length; index++) {
                Class<?> type = parameterTypes[index];
                Object value = args[index];
                if (type.isPrimitive()) {

                    if (!(type == int.class &&  value instanceof Integer ||
                            type == boolean.class &&  value instanceof Boolean ||
                            type == long.class &&  value instanceof Long   ||
                            type == float.class &&  value instanceof Float   ||
                            type == double.class &&  value instanceof Double   ||
                            type == short.class &&  value instanceof Short   ||
                            type == byte.class &&  value instanceof Byte   ||
                            type == char.class &&  value instanceof Character
                    ))
                    {
                        continue loop;
                    }


                } else if (!type.isInstance( args[index] )) {
                    continue loop;
                }
            }
            return m.invoke( object, args );
        }
        return die(Object.class, "Unable to invoke method", name, "on object", object, "with arguments", args);
    }


    public static Object invokeOverloadedFromList (Object object, String name, List<?> args){
        ClassMeta classMeta = ClassMeta.classMeta( object.getClass() );
        Iterable<MethodAccess> invokers =   classMeta.methods( name );

        List<Object> list = new ArrayList( args );
        FieldsAccessor fieldsAccessor = FieldAccessMode.FIELD.create( true );


        loop:
        for (MethodAccess m : invokers) {
            Class<?>[] parameterTypes = m.parameterTypes();
            if ( !(parameterTypes.length == list.size()) ) {
                continue;
            }
            for (int index = 0; index < parameterTypes.length; index++) {
                if ( !matchAndConvertArgs( fieldsAccessor, list, m, parameterTypes, index ) ) {
                        continue loop;
                }
            }
            return m.invoke( object, list.toArray(new Object[list.size()]) );
        }
        return die(Object.class, "Unable to invoke method", name, "on object", object, "with arguments", args);
    }

    public static void invokeMethodWithAnnotationNoReturn( Object object, String annotation ) {
            invokeMethodWithAnnotationWithReturnType( object, annotation, void.class );
    }

    public static void invokeMethodWithAnnotationWithReturnType( Object object, String annotation, Class<?> returnType ) {
          invokeMethodWithAnnotationWithReturnType( object.getClass(), object, annotation, returnType );
    }

    public static void invokeMethodWithAnnotationWithReturnType( Class<?> type, Object object, String annotation, Class<?> returnType ) {
        ClassMeta classMeta = ClassMeta.classMeta( type );
        Iterator<MethodAccess> iterator =  classMeta.methods();
        while (iterator.hasNext()  ) {
            MethodAccess m = iterator.next();
            if (m.hasAnnotation( annotation )) {
                if (m instanceof MethodAccessImpl) {
                    Method method = ( ( MethodAccessImpl ) m ).method;

                    if (method.getParameterTypes().length==0 && method.getReturnType() == void.class) {
                        m.invoke( object );
                        break;
                    }
                }
            }
        }
    }

    private static void invokeMethod( Object object, Method method ) {
        try {
            method.setAccessible( true );
            method.invoke( object );
        } catch ( Exception ex ) {
            handle( ex );
        }
    }


    public static boolean matchAndConvertArgs( FieldsAccessor fieldsAccessor, List<Object> list, MethodAccess method, Class[] parameterTypes, int index ) {
        try {

            Class paramType;
            Object item;
            paramType = parameterTypes[index];
            item = list.get( index );
            if ( item instanceof ValueContainer ) {
                item = ( ( ValueContainer ) item ).toValue();
            }

            if ( Typ.isPrimitiveOrWrapper( paramType ) &&
                    ( item instanceof Number || item instanceof Boolean || item instanceof CharSequence ) ) {

                Object o = Conversions.coerceOrDie( paramType, item );
                list.set( index, o );
            }
            else if ( item instanceof Map && !Typ.isMap( paramType ) ) {
                list.set( index, fromMap(fieldsAccessor,  ( Map<String, Object> ) item, paramType ) );
            } else if ( item instanceof List && !Typ.isList( paramType ) ) {
                list.set( index, fromList(fieldsAccessor,  ( List<Object> ) item, paramType ) );
            } else if ( Typ.isList( paramType ) && item instanceof List ) {
                List<Object> itemList = ( List<Object> ) item;
                if ( itemList.size() > 0 && (itemList.get( 0 ) instanceof List || itemList.get(0) instanceof ValueContainer)  ) {
                    Type type = method.getGenericParameterTypes()[index];
                    if ( type instanceof ParameterizedType ) {
                        ParameterizedType pType = ( ParameterizedType ) type;
                        Class<?> componentType = ( Class<?> ) pType.getActualTypeArguments()[0];
                        List newList = new ArrayList( itemList.size() );

                        for ( Object o : itemList ) {
                            if ( o instanceof ValueContainer ) {
                                o = ( ( ValueContainer ) o ).toValue();
                            }

                            List fromList = ( List ) o;
                            o = fromList( fieldsAccessor, fromList, componentType );
                            newList.add( o );
                        }
                        list.set( index, newList );

                    }
                }
            } else if ( paramType == Typ.string  && item instanceof CharSequence ) {
                list.set( index, item.toString() );
            } else if ( paramType.isEnum()  && (item instanceof CharSequence| item instanceof Number)  ) {
                list.set( index, toEnum(paramType, item));
            } else if ( !paramType.isInstance( item ) ) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

}
