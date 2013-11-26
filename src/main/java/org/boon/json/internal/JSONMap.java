package org.boon.json.internal;

import java.util.*;

public class JSONMap extends AbstractMap<String, Object> implements Map<String, Object> {
  
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
        buildIfNeededMap();
        return map.put ( key, value );
    }

    @Override
    public Set<Entry<String, Object>> entrySet () {
        buildIfNeededMap();
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
}
