package org.boon;

import org.boon.core.Function;
import org.boon.core.reflection.Invoker;
import org.boon.core.reflection.MethodAccess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Richard on 2/28/14.
 */
public class Functional {


    public static void each(Iterable<?> objects, Class<?> cls, String methodName) {

        for (Object o : objects) {
            Invoker.invoke(cls, methodName, o);
        }
    }

    public static void each( Object[] objects, Object instance, String methodName) {

        for (Object o : objects) {
            Invoker.invoke(instance, methodName, o);
        }
    }


    public static void each (Object[] objects, Class<?> cls, String methodName) {
        for (Object o : objects) {
            Invoker.invoke(cls, methodName, o);
        }
    }





    public static void each(Iterable<?> objects, Object instance, String methodName) {

        for (Object o : objects) {
            Invoker.invoke(instance, methodName, o);
        }
    }


    public static void each(Collection<?> objects, Class<?> cls, String methodName) {

         MethodAccess methodAccess = Invoker.invokeMethodAccess(cls, methodName);

        for (Object o : objects) {
            methodAccess.invokeStatic(o);
        }
    }

    public static void each(Collection<?> objects, Object function) {


        MethodAccess methodAccess = Invoker.invokeFunctionMethodAccess(function);

        for (Object o : objects) {
            methodAccess.invoke(function, o);
        }

    }


    public static void each(Map<?,?> map, Object object) {


        MethodAccess methodAccess = Invoker.invokeFunctionMethodAccess(object);

        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;

            methodAccess.invoke(object, ((Map.Entry) o).getKey(), ((Map.Entry) o).getValue());
        }
    }


    public static void each(Iterable<?> objects, Object function) {


        MethodAccess methodAccess = Invoker.invokeFunctionMethodAccess(function);

        for (Object o : objects) {
            methodAccess.invoke(function, o);
        }
    }


    public static void each (Object[] objects, Object function) {

        MethodAccess methodAccess = Invoker.invokeFunctionMethodAccess(function);

        for (Object o : objects) {
            methodAccess.invoke(function, o);
        }
    }



    public static void each(Collection<?> objects, Object object, String methodName) {


        MethodAccess methodAccess = Invoker.invokeMethodAccess(object.getClass(), methodName);

        for (Object o : objects) {
            methodAccess.invoke(object, o);
        }
    }


    public static void each(Map<?,?> map, Object object, String methodName) {


        MethodAccess methodAccess = Invoker.invokeMethodAccess(object.getClass(), methodName);

        for (Object o : map.entrySet()) {
            Map.Entry entry = (Map.Entry) o;

            methodAccess.invoke(object, ((Map.Entry) o).getKey(), ((Map.Entry) o).getValue());
        }
    }


    public static <V, N> void each(  final V[] array, Function<V, N> function ) {

        for ( V v : array ) {
            function.apply(v);
        }
    }

    public static <V, N> void each( final Collection<V> array, Function<V, N> function ) {

        for ( V v : array ) {
            function.apply(v);
        }
    }

    public static <V, N> void each( final Iterable<V> array, Function<V, N> function ) {

        for ( V v : array ) {
            function.apply(v);
        }
    }

}
