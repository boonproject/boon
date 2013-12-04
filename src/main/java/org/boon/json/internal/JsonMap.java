package org.boon.json.internal;

import java.util.*;

import static org.boon.Exceptions.die;

public class JsonMap extends AbstractMap<String, Object> implements Map<String, Object> {

    Map<String, Object> map = null;

    public Entry<String, Value>[] items = new  Entry[ 20 ];

    int len = 0;

    public void add ( MapItemValue miv) {
        if (len == items.length) {
            items =  org.boon.Arrays.grow ( items );
        }
        items [len] = miv;
        len++;

    }


    static class FakeSet extends AbstractSet <Entry<String, Object>> {
        @Override
        public <T> T[] toArray( T[] a ) {
            return (T[]) items;
        }

        Entry<String, Value>[] items;

        FakeSet (Entry<String, Value>[] items  ) {

            this.items = items;
        }

        @Override
        public Iterator<Entry<String, Object>> iterator() {
            return  new Iterator<Entry<String, Object>> () {
                int location = 0;
                @Override
                public boolean hasNext() {
                    return location < items.length;
                }

                @Override
                public Entry<String, Object> next() {
                    Object o =  items[location++];
                    return (Entry<String, Object>)o;
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

    private FakeSet set = new FakeSet ( items );

    @Override
    public Object get( Object key ) {
            if ( map == null ) buildMap ();
            return map.get ( key );
    }


    @Override
    public Value put( String key, Object value ) {
        die ( "Not that kind of map" );
        return null;
    }


    @Override
    public Set<Entry<String, Object>> entrySet() {
        if ( map == null )  {
            return set;
        } else {
            return map.entrySet ();
        }
    }

    private final void buildMap () {

        map = new HashMap<> ( items.length );

        for ( Entry<String, Value> miv : items ) {
            if (miv == null) {
                break;
            }
            map.put ( miv.getKey (), miv.getValue ().toValue () );
        }
    }


    public Collection<Object> values() {
        return map.values ();
    }


    public int size() {
        return len;
    }

    public String toString() {
        if ( map == null ) buildMap ();
        return map.toString ();

    }
}
