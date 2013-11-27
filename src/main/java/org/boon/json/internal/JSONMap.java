package org.boon.json.internal;

import java.util.*;

public class JsonMap extends AbstractMap<String, Object> implements Map<String, Object> {
  
    Map<String, Object> map = null;

    public List<MapItemValue> items = new ArrayList<> ( 10 );

    @Override
    public Object get ( Object key ) {
        buildIfNeededMap ();
        Object obj = map.get (key);

        if (obj instanceof MapItemValue) {
            obj = convert((MapItemValue) obj);
            map.put(key.toString (), obj);
        }
        return obj;
    }

    private Object convert ( MapItemValue miv ) {
        return miv.value.toValue ();
    }


    @Override
    public Object put ( String key, Object value ) {
        return map.put ( key, value );
    }


    boolean converted = false;
    @Override
    public Set<Entry<String, Object>> entrySet () {
        buildIfNeededMap();
        if ( !converted ) {
            Set<Entry<String, Object>> entries = map.entrySet ();

            for (Entry<String, Object> entry : entries) {
                if (entry.getValue () instanceof  MapItemValue) {
                    get(entry.getKey ());
                }
            }
            converted = true;
        }

        return map.entrySet ();
    }

    private void buildIfNeededMap () {
        if (map==null) {
            map = new LinkedHashMap<> ( this.items.size () );
            for (MapItemValue miv : items ) {
                map.put ( miv.name, miv );
            }
        }
    }


    public Collection<Object> values() {
        buildIfNeededMap ();
        if ( !converted ) {
            this.entrySet ();
        }
        return map.values ();
    }


    public int size() {
        return this.items.size ();
    }
}
