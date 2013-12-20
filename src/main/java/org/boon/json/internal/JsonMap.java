package org.boon.json.internal;

import java.util.*;

import static org.boon.Exceptions.die;

public class JsonMap extends AbstractMap<String, Object> implements Map<String, Object> {

    Map<String, Object> map = null;

    public Entry<String, Value>[] items = new Entry[ 20 ];

    int len = 0;

<<<<<<< HEAD
    public void add ( MapItemValue miv ) {
=======
    private final boolean lazyChop;

    public JsonMap( boolean lazyChop ) {

        this.lazyChop = lazyChop;
    }

    public void add( MapItemValue miv ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( len == items.length ) {
            items = org.boon.Arrays.grow ( items );
        }
        items[ len ] = miv;
        len++;

    }


    static class FakeSet extends AbstractSet<Entry<String, Object>> {
        @Override
        public <T> T[] toArray ( T[] a ) {
            return ( T[] ) items;
        }

        Entry<String, Value>[] items;

        FakeSet ( Entry<String, Value>[] items ) {

            this.items = items;
        }

        @Override
        public Iterator<Entry<String, Object>> iterator () {
            return new Iterator<Entry<String, Object>> () {
                int location = 0;

                @Override
                public boolean hasNext () {
                    return location < items.length;
                }

                @Override
                public Entry<String, Object> next () {
                    Object o = items[ location++ ];
                    return ( Entry<String, Object> ) o;
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
    public Object get ( Object key ) {
        if ( map == null ) buildMap ();
        Object object = map.get ( key );
        chopIfNeeded ( object );
        return object;
    }

<<<<<<< HEAD
    private void chopIfNeeded ( Object object ) {
        if ( object instanceof JsonMap ) {
            JsonMap m = new JsonMap ();
            m.chopMap ();
        } else if ( object instanceof JsonList ) {
            JsonList list = new JsonList ();
            list.chopList ();
=======
    private void chopIfNeeded( Object object ) {
        if ( lazyChop ) {
            if ( object instanceof JsonMap ) {
                JsonMap m = ( JsonMap ) object;
                m.chopMap ();
            } else if ( object instanceof JsonList ) {
                JsonList list = ( JsonList ) object;
                list.chopList ();
            }
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        }

    }

    boolean mapChopped = false;

    void chopMap () {
        if ( mapChopped ) {
            return;
        }
        mapChopped = true;
        if ( this.map != null ) {
            return;
        } else {
            for ( Entry e : this.items ) {
                if ( e == null ) break;
                MapItemValue entry = ( MapItemValue ) e;

                Value value = entry.getValue ();
                if ( value == null ) continue;
                if ( value.isContainer () ) {
                    chopContainer ( value );
                } else {
                    value.chop ();
                }
            }
        }

    }

    void chopContainer ( Value value ) {
        Object obj = value.toValue ();
        if ( obj instanceof JsonMap ) {
            JsonMap map = ( JsonMap ) obj;
            map.chopMap ();
        } else if ( obj instanceof JsonList ) {
            JsonList list = ( JsonList ) obj;
            list.chopList ();
        }
    }


    @Override
    public Value put ( String key, Object value ) {
        die ( "Not that kind of map" );
        return null;
    }


    @Override
    public Set<Entry<String, Object>> entrySet () {
        if ( map == null ) {
            return set;
        } else {
            return map.entrySet ();
        }
    }

    private final void buildMap () {

        map = new HashMap<> ( items.length );

        for ( Entry<String, Value> miv : items ) {
            if ( miv == null ) {
                break;
            }
            map.put ( miv.getKey (), miv.getValue ().toValue () );
        }
    }


    public Collection<Object> values () {
        return map.values ();
    }


    public int size () {
        return len;
    }

    public String toString () {
        if ( map == null ) buildMap ();
        return map.toString ();

    }
}
