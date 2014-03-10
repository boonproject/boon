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
