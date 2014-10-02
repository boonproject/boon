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

package org.boon.di.modules;

import org.boon.Exceptions;
import org.boon.Sets;
import org.boon.collections.MultiMap;
import org.boon.collections.MultiMapImpl;
import org.boon.core.Supplier;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.MapObjectConversion;
import org.boon.core.reflection.Reflection;
import org.boon.di.ProviderInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.di.modules.NamedUtils.namedValueForClass;

/**
 * Created by Richard on 2/3/14.
 */
public class SupplierModule extends BaseModule {

    private Map<Class, ProviderInfo> supplierTypeMap = new ConcurrentHashMap<>();

    private MultiMapImpl<String, ProviderInfo> supplierNameMap = new MultiMapImpl<>();

    public SupplierModule( ProviderInfo... suppliers ) {
        supplierExtraction( suppliers );
    }


    public SupplierModule( List<ProviderInfo> suppliers ) {
        supplierExtraction( suppliers.toArray(new ProviderInfo[suppliers.size()]) );
    }



    @Override
    public Iterable<Object> values() {
        return (Iterable<Object>) (Object)supplierNameMap.values();
    }

    @Override
    public Iterable<String> names() {
        return supplierNameMap.keySet();
    }

    @Override
    public Iterable types() {
        return supplierTypeMap.keySet();
    }

    public SupplierModule( Map<?, ?> map ) {
        List<ProviderInfo> list = new ArrayList<>(  );

        for (Map.Entry<?,?> entry : map.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                Map <String, Object> valueMap = (Map <String, Object>) value;
                ProviderInfo pi = addProviderFromMapToList(key, valueMap);
                list.add( pi );

            } else {
                list.add( ProviderInfo.provider( key, value ));
            }
        }
        supplierExtraction( list.toArray( new ProviderInfo[list.size()] ) );
    }

    private ProviderInfo addProviderFromMapToList( final Object key, final Map<String, Object> valueMap) {
        if (valueMap.containsKey( "class" )) {
            CharSequence className =  (String) valueMap.get( "class" );
            if (className!=null) {
                try {

                    final Class<Object> type = (Class<Object>) Class.forName( className.toString());

                    final Supplier<Object> supplier = new Supplier<Object>() {
                        @Override
                        public Object get() {
                            return MapObjectConversion.fromMap(valueMap);
                        }
                    };
                    return ProviderInfo.providerOf( key.toString(),  type, supplier );
                } catch ( ClassNotFoundException e ) {
                    return ProviderInfo.provider( key, valueMap ) ;
                }

            }

        }
        return ProviderInfo.provider( key, valueMap ) ;

    }


    @Override
    public <T> T get( Class<T> type ) {
        ProviderInfo pi = supplierTypeMap.get(type);
        if (pi!=null) {
            return (T) pi.supplier().get();
        }
        return null;

    }

    @Override
    public Object get( String name ) {

        ProviderInfo pi = supplierNameMap.get(name);
        if (pi!=null) {
            return pi.supplier().get();
        }
        return null;


    }

    @Override
    public <T> T get( Class<T> type, String name ) {
        ProviderInfo providerInfo = getProviderInfo(type, name);
        if (providerInfo!=null) {
            return (T)providerInfo.supplier().get();
        }
        return null;
    }

    @Override
    public ProviderInfo getProviderInfo(Class<?> type) {
        return supplierTypeMap.get(type);
    }

    @Override
    public ProviderInfo getProviderInfo(String name) {
        return supplierNameMap.get(name);
    }

    @Override
    public ProviderInfo getProviderInfo(Class<?> type, String name) {
        return doGetProvider(type, name);
    }


    @Override
    public <T> Supplier<T> getSupplier( final Class<T> type, final String name ) {


        ProviderInfo nullInfo = null;

        try {
            Set<ProviderInfo> set = Sets.set( supplierNameMap.getAll( name ) );

            for ( ProviderInfo info : set ) {

                if ( info.type() == null ) {
                    nullInfo = info;
                    continue;
                }
                if ( type.isAssignableFrom( info.type() ) ) {
                    return (  Supplier<T> ) info.supplier();
                }
            }

            return (  Supplier<T> ) ( nullInfo != null ? nullInfo.supplier() :   new Supplier<T>() {
                @Override
                public T get() {
                    return null;
                }
            });

        } catch ( Exception e ) {
            Exceptions.handle( e );
            return null;
        }
    }


    private  ProviderInfo doGetProvider( final Class<?> type, final String name ) {


        Set<ProviderInfo> set = Sets.set( supplierNameMap.getAll( name ) );


        ProviderInfo nullTypeInfo = null;

        for ( ProviderInfo info : set ) {

                if ( info.type() == null ) {
                    nullTypeInfo = info;

                    continue;
                }
                if ( type.isAssignableFrom( info.type() ) ) {
                    return info;
                }
         }
        return nullTypeInfo;
    }


    @Override
    public <T> Supplier<T> getSupplier( Class<T> type ) {
        Supplier<T> supplier = ( Supplier<T> ) supplierTypeMap.get( type );
        if (supplier == null ) {
            supplier =  new Supplier<T>() {
                @Override
                public T get() {
                    return null;
                }
            };
        }

        return supplier;
    }


    @Override
    public boolean has( Class type ) {
        return supplierTypeMap.containsKey( type );
    }

    @Override
    public boolean has( String name ) {
        return supplierNameMap.containsKey( name );
    }


    private void extractClassIntoMaps( ProviderInfo info, Class type, boolean foundName, Supplier supplier ) {

        if ( type == null ) {
            return;
        }
        String named = null;


        Class superClass = type.getSuperclass();


        Class[] superTypes = type.getInterfaces();

        for ( Class superType : superTypes ) {
            this.supplierTypeMap.put( superType, info );
        }

        while ( superClass != Object.class ) {
            this.supplierTypeMap.put( superClass, info );

            if ( !foundName ) {
                named = NamedUtils.namedValueForClass( superClass );
                if ( named != null ) {
                    supplierNameMap.put( named, new ProviderInfo( named, type, supplier, null ) );
                    foundName = true;
                }
            }

            superTypes = type.getInterfaces();
            for ( Class superType : superTypes ) {
                this.supplierTypeMap.put( superType, info );
            }
            superClass = superClass.getSuperclass();
        }


    }


    private void supplierExtraction( ProviderInfo[] suppliers ) {
        for ( ProviderInfo providerInfo : suppliers ) {

            Class<?> type = providerInfo.type();
            /* Get type from value. */
            if ( type == null && providerInfo.value() != null ) {
                type = providerInfo.value().getClass();

            }
            String named = providerInfo.name();
            Supplier supplier = providerInfo.supplier();

            if ( supplier == null ) {
                supplier = createSupplier( providerInfo.prototype(), type, providerInfo.value() );
                providerInfo = new ProviderInfo(named, type, supplier, providerInfo.value());
            }

            if ( type != null ) {

                supplierTypeMap.put( type, providerInfo );


                /* Named passed in overrides name in class annotation @Named. */
                if ( named == null ) {

                    named = namedValueForClass( type );
                }
            }

            extractClassIntoMaps( providerInfo, type, named != null, supplier );
            if ( named != null ) {
                supplierNameMap.put( named, new ProviderInfo( named, type, supplier, providerInfo.value() ) );
            }
        }
    }


    private Supplier createSupplier( final boolean prototype, final Class<?> type, final Object value ) {
        if ( value != null && !prototype) {
            return new Supplier() {
                @Override
                public Object get() {
                    return value;
                }
            };
        } else if (value!=null && prototype) {
            return new Supplier() {
                @Override
                public Object get() {
                    return BeanUtils.copy(value);
                }
            };
        } else if ( type != null ) {
            return new Supplier() {
                @Override
                public Object get() {
                    return Reflection.newInstance( type );
                }
            };
        } else {
            return new Supplier() {
                @Override
                public Object get() {
                    return null;
                }
            };
        }
    }



}