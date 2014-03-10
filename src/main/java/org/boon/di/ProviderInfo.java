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

package org.boon.di;

import org.boon.core.Supplier;

/**
 * Created by Richard on 2/3/14.
 */
public class ProviderInfo<T> {

    private String name;
    private Class<T> type;
    private Supplier<T> supplier;
    private T object;


    private boolean postConstructCalled;


    boolean prototype;


    public ProviderInfo( Class<T> type) {
        this.type = type;
    }

//    public ProviderInfo( T object) {
//        this.object = object;
//    }

    public ProviderInfo( String name, Class<T> type, Supplier<T> supplier, T object ) {
        this.name = name;
        this.type = type;
        this.supplier = supplier;
        this.object = object;
    }


    public ProviderInfo( String name, Class<T> type, Supplier<T> supplier, T object, boolean prototype ) {
        this.name = name;
        this.type = type;
        this.supplier = supplier;
        this.object = object;
        this.prototype = prototype;
    }

    public static <T> ProviderInfo<T> providerOf( Class<T> type, Supplier<T> supplier ) {
        return new ProviderInfo<>( null, type, supplier, null );
    }


    public static <T> ProviderInfo<T> providerOf( String name, Class<T> type, Supplier<T> supplier ) {
        return new ProviderInfo<>( name, type, supplier, null );
    }


    public static <T> ProviderInfo<T> providerOf( String name, Supplier<T> supplier ) {
        return new ProviderInfo<>( name, null, supplier, null );
    }


    public static <T> ProviderInfo<T> providerOf( String name, Class<T> type ) {
        return new ProviderInfo<>( name, type, null, null );
    }


    public static <T> ProviderInfo<T> providerOf( Class<T> type ) {
        return new ProviderInfo<>( null, type, null, null );
    }


    public static <T> ProviderInfo<T> providerOf( String name, T object ) {
        return new ProviderInfo<>( name, null, null, object );
    }


    public static <T> ProviderInfo<T> providerOf( T object ) {
        return new ProviderInfo<>( null, null, null, object );
    }


    public static <T> ProviderInfo<T> objectProviderOf( T object ) {
        return new ProviderInfo<>( null, null, null, object );
    }


    public static <T> ProviderInfo<T> prototypeProviderOf( T object ) {
        return new ProviderInfo<>( null, null, null, object, true );
    }

    public static <T> ProviderInfo<T> provider( Object name, Object value ) {

        ProviderInfo info;

        if (value instanceof ProviderInfo ) {
            ProviderInfo valueInfo = ( ProviderInfo ) value;
            info  = new ProviderInfo( name.toString(), valueInfo.type(), valueInfo.supplier(), valueInfo.value()  );
        } else if (value instanceof Class) {
            info = new ProviderInfo( name.toString(), (Class) value, null, null  );
        } else if (value instanceof Supplier) {
            info = new ProviderInfo( name.toString(), null, (Supplier) value, null  );
        } else  {
            if (value == null) {
                info = new ProviderInfo( name.toString(), null, null, value  );
            }  else {
                info = new ProviderInfo( name.toString(), value.getClass(), null, value  );
            }
        }
        return info;
    }

    public Class<T> type() {
        return type;
    }

    public Supplier<T> supplier() {
        return supplier;
    }

    public String name() {
        return name;
    }

    public Object value() {
        return object;
    }


    public boolean isPostConstructCalled() {
        return postConstructCalled;
    }

    public void setPostConstructCalled(boolean postConstructCalled) {
        this.postConstructCalled = postConstructCalled;
    }


    public boolean prototype() {
        return prototype;
    }
}
