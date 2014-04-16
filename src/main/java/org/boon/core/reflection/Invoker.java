/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package org.boon.core.reflection;


import org.boon.Boon;
import org.boon.Exceptions;
import org.boon.Lists;
import org.boon.core.Typ;
import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.core.reflection.fields.FieldsAccessor;
import org.boon.primitive.CharBuf;

import java.lang.invoke.ConstantCallSite;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.boon.Boon.className;
import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Exceptions.handle;
import static org.boon.core.Type.gatherTypes;
import static org.boon.core.reflection.MapObjectConversion.matchAndConvertArgs;

/**
 *
 * @author Rick Hightower
 *         Created by Richard on 2/17/14.
 */
public class Invoker {


    public static Object invokeOverloadedFromObject(Object object, String name, Object args) {
        return invokeOverloadedFromObject(false, null, null, object, name, args);
    }

    public static Object invokeOverloadedFromObject(boolean respectIgnore, String view,
                                                    Set<String> ignoreProperties,
                                                    Object object, String name,
                                                    Object args) {

        try {
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
        catch(Exception ex) {
            return Exceptions.handle(Object.class, ex, "Unable to invoke method object", object, "name", name, "args", args);
        }
    }


    public static Object invokeFromObject(Object object, String name, Object args) {
        return invokeFromObject(false, null, null, object, name, args);

    }


    public static Object invokeFromObject(Class<?> cls, String name, Object args) {
        return invokeFromObject(false, null, null, cls, null, name, args);

    }


    /**
     * Invokes method from list or map depending on what the Object arg is.
     * @param object
     * @param method
     * @param args
     * @return
     */
    public static Object invokeMethodFromObjectArg(Object object, MethodAccess method, Object args) {
        return invokeMethodFromObjectArg(false, null, null, object, method, args);

    }


    public static Object invokeMethodFromObjectArg(boolean respectIgnore, String view, Set<String> ignoreProperties,
                                          Object object, MethodAccess method, Object args) {

        try {
            if (args instanceof Map) {
                return invokeMethodFromList(respectIgnore, view, ignoreProperties, object, method, Lists.list(args));
            } else if (args instanceof List) {
                List list = (List) args;

                Class<?>[] paramTypes = method.parameterTypes();

                if (paramTypes.length == 1 && list.size() > 0) {

                    Class<?> firstParamType = paramTypes[0];
                    Object firstArg = list.get(0);


                    if ( firstArg instanceof Map ) {
                        return invokeMethodFromList(respectIgnore, view, ignoreProperties, object, method, list);

                    }

                    else if (firstArg instanceof List &&
                            !Typ.isCollection(firstParamType)
                            && !firstParamType.isArray()) {
                        return invokeMethodFromList(respectIgnore, view, ignoreProperties, object, method, list);
                    }
                    else {
                        return invokeMethodFromList(respectIgnore, view, ignoreProperties, object, method,
                                Lists.list(args));
                    }
                } else {

                    return invokeMethodFromList(respectIgnore, view, ignoreProperties, object, method, list);

                }
            } else if (args == null) {
                return method.invoke(object);
            } else {
                return invokeMethodFromList(respectIgnore, view, ignoreProperties, object, method, Lists.list(args));
            }
        }catch (Exception ex) {
            return Exceptions.handle(Object.class, ex, "Unable to invoke method object", object, "method", method, "args", args);

        }

    }



    public static Object invokeFromObject(boolean respectIgnore, String view, Set<String> ignoreProperties,
                                          Object object, String name, Object args) {
        return invokeFromObject(respectIgnore, view, ignoreProperties, object.getClass(), object, name, args);

    }

    public static Object invokeFromObject(boolean respectIgnore, String view, Set<String> ignoreProperties,
                                          Class<?> cls, Object object, String name, Object args) {

        try {
            if (args instanceof Map) {
                return invokeFromList(respectIgnore, view, ignoreProperties, cls, object, name, Lists.list(args));
            } else if (args instanceof List) {
                List list = (List) args;
                ClassMeta classMeta = ClassMeta.classMeta(cls);
                MethodAccess m = classMeta.method(name);
                if (m.parameterTypes().length == 1 && list.size() > 0) {

                    Object firstArg = list.get(0);
                    if (firstArg instanceof Map || firstArg instanceof List) {
                        return invokeFromList(respectIgnore, view, ignoreProperties, cls, object, name, list);

                    } else {
                        return invokeFromList(respectIgnore, view, ignoreProperties, cls, object, name, Lists.list(args));
                    }
                } else {

                    return invokeFromList(respectIgnore, view, ignoreProperties, cls, object, name, list);

                }
            } else if (args == null) {
                return invoke(object, name);
            } else {
                return invokeFromList(respectIgnore, view, ignoreProperties, cls, object, name, Lists.list(args));
            }
        } catch (Exception ex) {
            return Exceptions.handle(Object.class, ex, "Unable to invoke method object", object, "name", name, "args", args);
        }

    }

    public static Object invokeFromList(Object object, String name, List<?> args) {
        return invokeFromList(true, null, null, object, name, args);
    }


    public static Object invokeFromList(Class<?> cls, String name, List<?> args) {
        return invokeFromList(true, null, null, cls, null, name, args);
    }

    public static Object invokeFromList(boolean respectIgnore, String view, Set<String> ignoreProperties, Object object, String name, List<?> args) {


        return invokeFromList(respectIgnore, view, ignoreProperties, object.getClass(), object, name, args);

    }



    public static Object invokeFromList(boolean respectIgnore, String view, Set<String> ignoreProperties, Class<?> cls, Object object, String name, List<?> argsList) {
        List<Object> convertedArguments = new ArrayList(argsList);

        ClassMeta classMeta = null;
        MethodAccess methodAccess = null;
        Class<?>[] parameterTypes = null;

        boolean[] flag = new boolean[1];

        /* The final arguments. */
        Object[] finalArgs = null;

        try {

            classMeta = ClassMeta.classMeta(cls);
            methodAccess = classMeta.method(name);
            parameterTypes = methodAccess.parameterTypes();

            if (convertedArguments.size() != parameterTypes.length) {
                return die(Object.class, "The list size does not match the parameter" +
                        " length of the method. Unable to invoke method", name, "on object", object, "with arguments", convertedArguments);
            }

            FieldsAccessor fieldsAccessor = FieldAccessMode.FIELD.create(true);

            for (int index = 0; index < parameterTypes.length; index++) {

                if (!matchAndConvertArgs(respectIgnore, view, fieldsAccessor, convertedArguments, methodAccess, parameterTypes, index, ignoreProperties, flag)) {
                    return die(Object.class, index, "Unable to invoke method as argument types did not match",
                            name, "on object", object, "with arguments", convertedArguments,
                            "\nValue at index = ", convertedArguments.get(index));
                }

            }

            if (argsList == null && methodAccess.parameterTypes().length == 0) {
                return methodAccess.invoke(object);
            } else {
                finalArgs = convertedArguments.toArray(new Object[convertedArguments.size()]);
                return methodAccess.invoke(object, finalArgs);
            }
        } catch (Exception ex) {



            if (methodAccess != null)  {


                CharBuf buf = CharBuf.create(200);
                buf.addLine();
                buf.multiply('-', 10).add("FINAL ARGUMENTS").multiply('-', 10).addLine();
                if (finalArgs!=null) {
                    for (Object o : finalArgs) {
                        buf.puts("argument type    ", className(o));
                    }
                }


                buf.multiply('-', 10).add("INVOKE METHOD").add(methodAccess).multiply('-', 10).addLine();
                buf.multiply('-', 10).add("INVOKE METHOD PARAMS").multiply('-', 10).addLine();
                for (Class<?> c : methodAccess.parameterTypes()) {
                    buf.puts("constructor type ", c);
                }

                buf.multiply('-', 35).addLine();

                if (Boon.debugOn()) {
                    puts(buf);
                }

                Boon.error( ex, "unable to create invoke method", buf );


                return  handle(Object.class, ex, buf.toString(),
                        "\nconstructor parameter types", methodAccess.parameterTypes(),
                        "\noriginal args\n", argsList,
                        "\nlist args after conversion", convertedArguments, "\nconverted types\n",
                        gatherTypes(convertedArguments),
                        "original types\n", gatherTypes(argsList), "\n");
            } else {
                return  handle(Object.class, ex,
                        "\nlist args after conversion", convertedArguments, "types",
                        gatherTypes(convertedArguments),
                        "\noriginal args", argsList,
                        "original types\n", gatherTypes(argsList), "\n");

            }


        }


    }


    public static Object invokeMethodFromList(boolean respectIgnore, String view, Set<String> ignoreProperties,
                                              Object object, MethodAccess method, List<?> args) {

        try {

            List<Object> list = new ArrayList(args);
            Class<?>[] parameterTypes = method.parameterTypes();

            boolean[] flag = new boolean[1];

            if (list.size() != parameterTypes.length) {
               return die(Object.class, "Unable to invoke method", method.name(), "on object", object, "with arguments", list);
            }

            FieldsAccessor fieldsAccessor = FieldAccessMode.FIELD.create(true);

            for (int index = 0; index < parameterTypes.length; index++) {

                if (!matchAndConvertArgs(respectIgnore, view, fieldsAccessor, list, method, parameterTypes, index, ignoreProperties, flag)) {
                    return die(Object.class, "Unable to invoke method as argument types did not match",
                            method.name(), "on object", object, "with arguments", list);
                }

            }

            if (args == null && method.parameterTypes().length == 0) {
                return method.invoke(object);
            } else {
                return method.invoke(object, list.toArray(new Object[list.size()]));
            }
        }catch (Exception ex) {
            return Exceptions.handle(Object.class, ex, "Unable to invoke method object", object, "method", method, "args", args);
        }

    }


    public static Object invokeEither(Object object, String name, Object... args) {
        if (object instanceof Class) {
            return invoke((Class<?>)object, name, args);
        } else {
            return invoke(object, name, args);
        }
    }

    public static Object invoke(Object object, String name, Object... args) {
        return ClassMeta.classMetaUnTyped(object.getClass()).invokeUntyped(object, name, args);
    }


    public static MethodAccess invokeMethodAccess( Object object, String name ) {
        return ClassMeta.classMeta(object.getClass()).invokeMethodAccess(name);
    }


    public static MethodAccess invokeMethodAccess(Class<?> cls, String name) {
        return ClassMeta.classMeta(cls).invokeMethodAccess(name);
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

    public static Object invokeOverloadedFromList(boolean respectIgnore,
                                                  String view,
                                                  Set<String> ignoreProperties,
                                                  Object object,
                                                  String name,
                                                  List<?> args) {
        ClassMeta classMeta = ClassMeta.classMeta(object.getClass());
        Iterable<MethodAccess> invokers = classMeta.methods(name);

        List<Object> list = new ArrayList(args);
        FieldsAccessor fieldsAccessor = FieldAccessMode.FIELD.create(true);

        boolean[] flag = new boolean[1];

        loop:
        for (MethodAccess m : invokers) {
            Class<?>[] parameterTypes = m.parameterTypes();
            if (!(parameterTypes.length == list.size())) {
                continue;
            }
            for (int index = 0; index < parameterTypes.length; index++) {
                if (!matchAndConvertArgs(respectIgnore, view, fieldsAccessor, list, m, parameterTypes, index, ignoreProperties, flag)) {
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


    public static <T> boolean invokeBooleanReturn(Object object, T v) {
        Class cls;
        Object instance = null;
        if (object instanceof  Class) {
            cls = (Class) object;
        } else {
            cls = object.getClass();
            instance = object;
        }

        ClassMeta meta = ClassMeta.classMeta(cls);
        return meta.invokePredicate(instance, v);

    }

    public static Object invokeReducer(Object object, Object sum, Object value) {
        if (object instanceof  Class) {
            ClassMeta meta = ClassMeta.classMeta((Class<?>)object);
            return meta.invokeReducer(null, sum, value);
        } else {
            ClassMeta meta = ClassMeta.classMeta(object.getClass());

            return meta.invokeReducer(object, sum, value);

        }
    }

    public static Object invokeFunction(Object object, Object arg) {

        if (object instanceof  Class) {
            ClassMeta meta = ClassMeta.classMeta((Class<?>)object);
            return meta.invokeFunction(null, arg);
        } else {
            ClassMeta meta = ClassMeta.classMeta(object.getClass());

            return meta.invokeFunction(object, arg);

        }
    }



    public static MethodAccess invokeFunctionMethodAccess(Object object) {

        if (object instanceof  Class) {
            ClassMeta meta = ClassMeta.classMeta((Class<?>)object);
            return meta.invokeFunctionMethodAccess();
        } else {
            ClassMeta meta = ClassMeta.classMeta(object.getClass());

            return meta.invokeFunctionMethodAccess();

        }
    }

    public static ConstantCallSite invokeReducerLongIntReturnLongMethodHandle(Object object ) {

            ClassMeta meta = ClassMeta.classMeta(object.getClass());
            return meta.invokeReducerLongIntReturnLongMethodHandle(object);
    }


    public  static <T> ConstantCallSite invokeReducerLongIntReturnLongMethodHandle(T object, String methodName ) {

        ClassMeta meta = ClassMeta.classMeta(object.getClass());
        return meta.invokeReducerLongIntReturnLongMethodHandle(object, methodName);
    }

    public static Method invokeReducerLongIntReturnLongMethod(Object object ) {

        ClassMeta meta = ClassMeta.classMeta(object.getClass());
        return meta.invokeReducerLongIntReturnLongMethod(object);
    }


    public  static <T> Method invokeReducerLongIntReturnLongMethod(T object, String methodName ) {

        ClassMeta meta = ClassMeta.classMeta(object.getClass());
        return meta.invokeReducerLongIntReturnLongMethod(object, methodName);
    }


}
