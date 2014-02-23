package org.boon.core.reflection;


import org.boon.Lists;
import org.boon.core.Conversions;
import org.boon.core.Typ;
import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.core.reflection.fields.FieldsAccessor;
import org.boon.core.reflection.impl.MethodAccessImpl;
import org.boon.core.value.ValueContainer;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

import static org.boon.Exceptions.die;
import static org.boon.core.Conversions.toEnum;
import static org.boon.core.reflection.MapObjectConversion.fromList;

import static org.boon.core.reflection.MapObjectConversion.fromMap;

/**
 *
 * @author Rick Hightower
 *         Created by Richard on 2/17/14.
 */
public class Invoker {


    public static Object invokeOverloadedFromObject(Object object, String name, Object args) {
        return invokeOverloadedFromObject(false, null, null, object, name, args);
    }

    public static Object invokeOverloadedFromObject(boolean respectIgnore, String view, Set<String> ignoreProperties,
                                                    Object object, String name, Object args) {
        if (args instanceof Map) {
            return invokeOverloadedFromList(respectIgnore, view, ignoreProperties, object, name, Lists.list(args));
        } else if (args instanceof List) {
            List list = (List) args;
            ClassMeta classMeta = ClassMeta.classMeta(object.getClass());
            MethodAccess m = classMeta.method(name);
            if (m.parameterTypes().length == 1 && list.size() > 0) {

                Object firstArg = list.get(0);
                if (firstArg instanceof Map || firstArg instanceof List) {
                    return invokeOverloadedFromList(respectIgnore, view, ignoreProperties, object, name, list);

                } else {
                    return invokeOverloadedFromList(respectIgnore, view, ignoreProperties, object, name, Lists.list(args));
                }
            } else {

                return invokeOverloadedFromList(respectIgnore, view, ignoreProperties, object, name, list);

            }
        } else if (args == null) {
            return invoke(object, name);
        } else {
            return invokeOverloadedFromList(respectIgnore, view, ignoreProperties, object, name, Lists.list(args));
        }
    }


    public static Object invokeFromObject(Object object, String name, Object args) {
        return invokeFromObject(false, null, null, object, name, args);

    }

    public static Object invokeFromObject(boolean respectIgnore, String view, Set<String> ignoreProperties,
                                          Object object, String name, Object args) {
        if (args instanceof Map) {
            return invokeFromList(respectIgnore, view, ignoreProperties, object, name, Lists.list(args));
        } else if (args instanceof List) {
            List list = (List) args;
            ClassMeta classMeta = ClassMeta.classMeta(object.getClass());
            MethodAccess m = classMeta.method(name);
            if (m.parameterTypes().length == 1 && list.size() > 0) {

                Object firstArg = list.get(0);
                if (firstArg instanceof Map || firstArg instanceof List) {
                    return invokeFromList(respectIgnore, view, ignoreProperties, object, name, list);

                } else {
                    return invokeFromList(respectIgnore, view, ignoreProperties, object, name, Lists.list(args));
                }
            } else {

                return invokeFromList(respectIgnore, view, ignoreProperties, object, name, list);

            }
        } else if (args == null) {
            return invoke(object, name);
        } else {
            return invokeFromList(respectIgnore, view, ignoreProperties, object, name, Lists.list(args));
        }

    }

    public static Object invokeFromList(Object object, String name, List<?> args) {
        return invokeFromList(true, null, null, object, name, args);
    }

    public static Object invokeFromList(boolean respectIgnore, String view, Set<String> ignoreProperties, Object object, String name, List<?> args) {
        List<Object> list = new ArrayList(args);
        ClassMeta classMeta = ClassMeta.classMeta(object.getClass());
        MethodAccess m = classMeta.method(name);
        Class<?>[] parameterTypes = m.parameterTypes();
        if (list.size() != parameterTypes.length) {
            return die(Object.class, "Unable to invoke method", name, "on object", object, "with arguments", list);
        }

        FieldsAccessor fieldsAccessor = FieldAccessMode.FIELD.create(true);

        for (int index = 0; index < parameterTypes.length; index++) {

            if (!matchAndConvertArgs(respectIgnore, view, ignoreProperties, fieldsAccessor, list, m, parameterTypes, index)) {
                return die(Object.class, "Unable to invoke method as argument types did not match",
                        name, "on object", object, "with arguments", list);
            }

        }

        if (args == null && m.parameterTypes().length == 0) {
            return m.invoke(object);
        } else {
            return m.invoke(object, list.toArray(new Object[list.size()]));
        }

    }

    public static Object invoke(Object object, String name, Object... args) {
        return ClassMeta.classMeta(object.getClass()).invoke(object, name, args);
    }


    public static Object invoke(Class cls, String name, Object... args) {
        return ClassMeta.classMeta(cls).invokeStatic(name, args);
    }


    public static Object invokeOverloaded(Object object, String name, Object... args) {
        ClassMeta classMeta = ClassMeta.classMeta(object.getClass());
        Iterable<MethodAccess> invokers = classMeta.methods(name);

        for (MethodAccess m : invokers) {
            if (m.respondsTo(args)) {
                return m.invoke(object, args);
            }
        }
        return die(Object.class, "Unable to invoke method", name, "on object", object, "with arguments", args);
    }


    public static Object invokeOverloadedFromList(Object object, String name, List<?> args) {

        return invokeOverloadedFromList(true, null, null, object, name, args);

    }

    public static Object invokeOverloadedFromList(boolean respectIgnore, String view, Set<String> ignoreProperties, Object object, String name, List<?> args) {
        ClassMeta classMeta = ClassMeta.classMeta(object.getClass());
        Iterable<MethodAccess> invokers = classMeta.methods(name);

        List<Object> list = new ArrayList(args);
        FieldsAccessor fieldsAccessor = FieldAccessMode.FIELD.create(true);


        loop:
        for (MethodAccess m : invokers) {
            Class<?>[] parameterTypes = m.parameterTypes();
            if (!(parameterTypes.length == list.size())) {
                continue;
            }
            for (int index = 0; index < parameterTypes.length; index++) {
                if (!matchAndConvertArgs(respectIgnore, view, ignoreProperties, fieldsAccessor, list, m, parameterTypes, index)) {
                    continue loop;
                }
            }
            return m.invoke(object, list.toArray(new Object[list.size()]));
        }
        return die(Object.class, "Unable to invoke method", name, "on object", object, "with arguments", args);
    }

    public static void invokeMethodWithAnnotationNoReturn(Object object, String annotation) {
        invokeMethodWithAnnotationWithReturnType(object, annotation, void.class);
    }

    public static void invokeMethodWithAnnotationWithReturnType(Object object, String annotation, Class<?> returnType) {
        invokeMethodWithAnnotationWithReturnType(object.getClass(), object, annotation, returnType);
    }

    public static void invokeMethodWithAnnotationWithReturnType(Class<?> type, Object object, String annotation, Class<?> returnType) {
        ClassMeta classMeta = ClassMeta.classMeta(type);
        Iterable<MethodAccess> iterate = classMeta.methods();
        for (MethodAccess m : iterate) {
            if (m.hasAnnotation(annotation)) {
                    if (m.parameterTypes().length == 0 && m.returnType() == void.class) {
                        m.invoke(object);
                        break;
                    }
            }
        }
    }

    public static boolean matchAndConvertArgs(boolean respectIgnore, String view, Set<String> ignoreSet, FieldsAccessor fieldsAccessor, List<Object> list, MethodAccess method, Class[] parameterTypes, int index) {
        try {

            Class paramType;
            Object item;
            paramType = parameterTypes[index];
            item = list.get(index);
            if (item instanceof ValueContainer) {
                item = ((ValueContainer) item).toValue();
            }

            if (Typ.isPrimitiveOrWrapper(paramType) &&
                    (item instanceof Number || item instanceof Boolean || item instanceof CharSequence)) {

                Object o = Conversions.coerceOrDie(paramType, item);
                list.set(index, o);
            } else if (item instanceof Map && !Typ.isMap(paramType)) {
                list.set(index, fromMap(respectIgnore, view, fieldsAccessor, (Map<String, Object>) item, paramType, ignoreSet));
            } else if (item instanceof List && !Typ.isList(paramType)) {
                list.set(index, fromList(fieldsAccessor, (List<Object>) item, paramType));
            } else if (Typ.isList(paramType) && item instanceof List) {
                List<Object> itemList = (List<Object>) item;
                if (itemList.size() > 0 && (itemList.get(0) instanceof List || itemList.get(0) instanceof ValueContainer)) {
                    Type type = method.getGenericParameterTypes()[index];
                    if (type instanceof ParameterizedType) {
                        ParameterizedType pType = (ParameterizedType) type;
                        Class<?> componentType = (Class<?>) pType.getActualTypeArguments()[0];
                        List newList = new ArrayList(itemList.size());

                        for (Object o : itemList) {
                            if (o instanceof ValueContainer) {
                                o = ((ValueContainer) o).toValue();
                            }

                            List fromList = (List) o;
                            o = fromList(fieldsAccessor, fromList, componentType);
                            newList.add(o);
                        }
                        list.set(index, newList);

                    }
                }
            } else if (paramType == Typ.string && item instanceof CharSequence) {
                list.set(index, item.toString());
            } else if (paramType.isEnum() && (item instanceof CharSequence | item instanceof Number)) {
                list.set(index, toEnum(paramType, item));
            } else if (!paramType.isInstance(item)) {
                return false;
            }
        } catch (Exception ex) {
            return false;
        }

        return true;
    }

}
