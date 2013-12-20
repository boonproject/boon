package org.boon.json.internal;


import java.util.*;

import static org.boon.Exceptions.die;

public class JsonValueMap extends AbstractMap<String, Value> implements Map<String, Value> {

    Map<String, Value> map = null;

<<<<<<< HEAD
    public Entry<String, Value>[] items = new Entry[ 20 ];

    int len = 0;

    public void add ( MapItemValue miv ) {
        if ( len == items.length ) {
            items = org.boon.Arrays.grow ( items );
        }
        items[ len ] = miv;
=======
    public Entry<String, Value>[] items = new Entry[20];

    int len = 0;

    public void add( MapItemValue miv ) {
        if ( len == items.length ) {
            items = org.boon.Arrays.grow ( items );
        }
        items[len] = miv;
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        len++;

    }


    static class FakeSet extends AbstractSet<Entry<String, Value>> {
        @Override
<<<<<<< HEAD
        public <T> T[] toArray ( T[] a ) {
=======
        public <T> T[] toArray( T[] a ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            return ( T[] ) items;
        }

        Entry<String, Value>[] items;

<<<<<<< HEAD
        FakeSet ( Entry<String, Value>[] items ) {
=======
        FakeSet( Entry<String, Value>[] items ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

            this.items = items;
        }

        @Override
<<<<<<< HEAD
        public Iterator<Entry<String, Value>> iterator () {
=======
        public Iterator<Entry<String, Value>> iterator() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            return new Iterator<Entry<String, Value>> () {
                int location = 0;

                @Override
<<<<<<< HEAD
                public boolean hasNext () {
=======
                public boolean hasNext() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                    return location < items.length;
                }

                @Override
<<<<<<< HEAD
                public Entry<String, Value> next () {
                    return items[ location++ ];
=======
                public Entry<String, Value> next() {
                    return items[location++];
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

                }

                @Override
                public void remove () {

                }
            };
        }

        @Override
        public int size () {
            return items.length;
        }


    }

    private FakeSet set = new FakeSet ( items );

    @Override
    public Value get ( Object key ) {
        if ( map == null && items.length < 20 ) {
            for ( Object item : items ) {
                MapItemValue miv = ( MapItemValue ) item;
                if ( key.equals ( miv.name ().toValue () ) ) {
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
    public Value put ( String key, Value value ) {
        die ( "Not that kind of map" );
        return null;
    }


    @Override
<<<<<<< HEAD
    public Set<Entry<String, Value>> entrySet () {
=======
    public Set<Entry<String, Value>> entrySet() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( map == null ) {
            return set;
        } else {
            return map.entrySet ();
        }
    }

    private final void buildIfNeededMap () {

        map = new HashMap<> ( items.length );

        for ( Entry<String, Value> miv : items ) {
            map.put ( miv.getKey (), miv.getValue () );
        }
    }


    public Collection<Value> values () {
        return map.values ();
    }


    public int size () {
        return len;
    }
}
