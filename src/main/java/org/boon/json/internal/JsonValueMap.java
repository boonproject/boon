package org.boon.json.internal;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.Exceptions.die;

public class JsonValueMap extends AbstractMap<String, Value> implements Map<String, Value> {

    Map<String, Value> map = null;

    public List<MapItemValue> items = new ArrayList<> ( 20 );

    @Override
    public Value get( Object key ) {
        if ( map == null && items.size () < 20 ) {
            for ( MapItemValue miv : items ) {
                if ( key.equals ( miv.name.toValue () ) ) {
                    return miv.value;
                }
            }
            return null;
        } else {
            if ( map == null ) buildIfNeededMap ();
            return map.get ( key );
        }
    }


    @Override
    public Value put( String key, Value value ) {
        die ( "Not that kind of map" );
        return null;
    }


    @Override
    public Set<Entry<String, Value>> entrySet() {
        if ( map == null ) buildIfNeededMap ();
        return map.entrySet ();
    }

    private final void buildIfNeededMap() {

        map = new HashMap<> ( items.size () );

        for ( MapItemValue miv : items ) {
            map.put ( miv.name.stringValue (), miv.value );
        }
    }


    public Collection<Value> values() {
        return map.values ();
    }


    public int size() {
        return this.items.size ();
    }
}
