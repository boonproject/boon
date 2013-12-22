package org.boon.json.internal;


import java.util.*;

import static org.boon.Exceptions.die;

public class JsonValueMap extends AbstractMap<String, Value> implements Map<String, Value> {

    Map<String, Value> map = null;

    public Entry<String, Value>[] items = new Entry[ 20 ];

    int len = 0;

    public void add( MapItemValue miv ) {
        if ( len == items.length ) {
            items = org.boon.Arrays.grow( items );
        }
        items[ len ] = miv;
        len++;

    }


    static class FakeSet extends AbstractSet<Entry<String, Value>> {
        @Override
        public <T> T[] toArray( T[] a ) {
            return ( T[] ) items;
        }

        Entry<String, Value>[] items;

        FakeSet( Entry<String, Value>[] items ) {

            this.items = items;
        }

        @Override
        public Iterator<Entry<String, Value>> iterator() {
            return new Iterator<Entry<String, Value>>() {
                int location = 0;

                @Override
                public boolean hasNext() {
                    return location < items.length;
                }

                @Override
                public Entry<String, Value> next() {
                    return items[ location++ ];

                }

                @Override
                public void remove() {

                }
            };
        }

        @Override
        public int size() {
            return items.length;
        }


    }

    private FakeSet set = new FakeSet( items );

    @Override
    public Value get( Object key ) {
        if ( map == null && items.length < 20 ) {
            for ( Object item : items ) {
                MapItemValue miv = ( MapItemValue ) item;
                if ( key.equals( miv.name().toValue() ) ) {
                    return miv.value;
                }
            }
            return null;
        } else {
            if ( map == null ) buildIfNeededMap();
            return map.get( key );
        }
    }


    @Override
    public Value put( String key, Value value ) {
        die( "Not that kind of map" );
        return null;
    }


    @Override
    public Set<Entry<String, Value>> entrySet() {
        if ( map == null ) {
            return set;
        } else {
            return map.entrySet();
        }
    }

    private final void buildIfNeededMap() {
        if ( map == null ) {
            map = new HashMap<>( items.length );

            for ( Entry<String, Value> miv : items ) {
                if ( miv == null ) {
                    break;
                }
                map.put( miv.getKey(), miv.getValue() );
            }
        }
    }


    public Collection<Value> values() {
        this.buildIfNeededMap();
        return map.values();
    }


    public int size() {
        if ( map != null ) {
            return map.size();
        } else {
            return len;
        }
    }
}
