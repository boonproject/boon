package org.boon.json.internal;


import java.util.*;

import static org.boon.Exceptions.die;

public class JsonValueMap extends AbstractMap<String, Value> implements Map<String, Value> {

    Map<String, Value> map = null;

    public List<MapItemValue> items = new ArrayList<> ( 10 );

    @Override
    public Value get( Object key ) {
            buildIfNeededMap ();
            return  map.get ( key );
    }



    @Override
    public Value put( String key, Value value ) {
        die ("Not that kind of map");
        return null;
    }


    @Override
    public Set<Entry<String, Value>> entrySet() {
        buildIfNeededMap ();
        return map.entrySet ();
    }

    private final void buildIfNeededMap() {
        if ( map == null ) {
            map = new HashMap<> ( this.items.size () );
            for ( MapItemValue miv : items ) {
                map.put ( miv.name.stringValue (), miv.value );
            }
        }
    }


    public Collection<Value> values() {
        return map.values ();
    }


    public int size() {
        return this.items.size ();
    }
}
