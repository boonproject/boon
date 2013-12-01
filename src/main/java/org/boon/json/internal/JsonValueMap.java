package org.boon.json.internal;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.boon.Exceptions.die;

public class JsonValueMap extends AbstractMap<String, Value> implements Map<String, Value> {

    Map<String, Value> map = null;

    public List<Entry<String, Value>> items = new ArrayList<> ( 20 );


    static class FakeSet extends AbstractSet <Entry<String, Value>> {



        public List<Entry<String, Value>> items;

        FakeSet (List<Entry<String, Value>> items ) {

            this.items = items;
        }
        @Override
        public Iterator<Entry<String, Value>> iterator() {
            return  items.iterator ();
        }

        @Override
        public int size() {
            return items.size ();
        }
    }

    private FakeSet set = new FakeSet ( items );

    @Override
    public Value get( Object key ) {
        if ( map == null && items.size () < 20 ) {
            for ( Object item : items ) {
                MapItemValue miv = ( MapItemValue) item;
                if ( key.equals ( miv.name().toValue () ) ) {
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
        if ( map == null )  {
            return set;
        } else {
            return map.entrySet ();
        }
    }

    private final void buildIfNeededMap() {

        map = new HashMap<> ( items.size () );

        for ( Entry<String, Value> miv : items ) {
            map.put ( miv.getKey (), miv.getValue () );
        }
    }


    public Collection<Value> values() {
        return map.values ();
    }


    public int size() {
        return this.items.size ();
    }
}
