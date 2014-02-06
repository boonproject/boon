package org.boon.di.modules;

import org.boon.Exceptions;
import org.boon.Sets;
import org.boon.collections.MultiMap;
import org.boon.core.Supplier;
import org.boon.core.reflection.BeanUtils;
import org.boon.core.reflection.MapObjectConversion;
import org.boon.core.reflection.Reflection;
import org.boon.di.Module;
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
public class SupplierModule implements Module {

    private Map<Class, Supplier<Object>> supplierTypeMap = new ConcurrentHashMap<>();

    private MultiMap<String, ProviderInfo> supplierNameMap = new MultiMap<>();

    public SupplierModule( ProviderInfo... suppliers ) {
        supplierExtraction( suppliers );
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
        return ( T ) supplierTypeMap.get( type ).get();
    }

    @Override
    public Object get( String name ) {
        return supplierNameMap.get( name ).supplier().get();

    }

    @Override
    public <T> T get( Class<T> type, String name ) {

        return getSupplier( type, name ).get();
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


    private void extractClassIntoMaps( Class type, boolean foundName, Supplier supplier ) {

        if ( type == null ) {
            return;
        }
        String named = null;


        Class superClass = type.getSuperclass();


        Class[] superTypes = type.getInterfaces();

        for ( Class superType : superTypes ) {
            this.supplierTypeMap.put( superType, supplier );
        }

        while ( superClass != Object.class ) {
            this.supplierTypeMap.put( superClass, supplier );

            if ( !foundName ) {
                named = NamedUtils.namedValueForClass( superClass );
                if ( named != null ) {
                    supplierNameMap.put( named, new ProviderInfo( named, type, supplier, null ) );
                    foundName = true;
                }
            }

            superTypes = type.getInterfaces();
            for ( Class superType : superTypes ) {
                this.supplierTypeMap.put( superType, supplier );
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
                supplier = createSupplier( type, providerInfo.value() );
            }

            if ( type != null ) {

                supplierTypeMap.put( type, supplier );


                /* Named passed in overrides name in class annotation @Named. */
                if ( named == null ) {

                    named = namedValueForClass( type );
                }
            }

            extractClassIntoMaps( type, named != null, supplier );
            if ( named != null ) {
                supplierNameMap.put( named, new ProviderInfo( named, type, supplier, null ) );
            }
        }
    }


    private Supplier createSupplier( final Class<?> type, final Object value ) {
        if ( value != null ) {
            return new Supplier() {
                @Override
                public Object get() {
                    return value;
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