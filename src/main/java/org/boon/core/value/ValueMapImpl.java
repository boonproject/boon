package org.boon.core.value;


import org.boon.primitive.Arry;
import org.boon.core.Value;

import java.util.*;

import static org.boon.Exceptions.die;

/**
 * This map is for object serialization mainly.
 * The idea is the final conversion of
 * the Value objects are delayed until the last possible moment, i.e., just before injected into a bean.
 */
public class ValueMapImpl extends AbstractMap<String, Value> implements ValueMap <String, Value> {

    /** The internal map to hold the Value map. */
    private Map<String, Value> map = null;

    /** The items held in the map. */
    private Entry<String, Value>[] items = new Entry[ 20 ];

    /* The current length of the map. */
    private int len = 0;


    /**
     * Add a MapItemValue to the map.
     * @param miv map value item.
     */
    @Override
    public void add( MapItemValue miv ) {
        if ( len >= items.length ) {
            items = Arry.grow(items);
        }
        items[ len ] = miv;
        len++;
    }

    @Override
    public int len() {
        return len;
    }

    @Override
    public boolean hydrated() {
        return map!=null;
    }

    @Override
    public Entry<String, Value>[] items() {
        return  items;
    }


    /**
     * Get the items for the key.
     * @param key
     * @return
     */
    @Override
    public Value get( Object key ) {
        /* If the length is under and we are asking for the key, then just look for the key. Don't build the map. */
        if ( map == null && items.length < 20 ) {
            for ( Object item : items ) {
                MapItemValue miv = ( MapItemValue ) item;
                if ( key.equals( miv.name.toValue() ) ) {
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


    /** If the map has not been built yet, then we just return a fake entry set. */
    @Override
    public Set<Entry<String, Value>> entrySet() {
        buildIfNeededMap();
        return map.entrySet();
    }

    /** Build the map if requested to, it does this lazily. */
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


    /** Return a collection of values. */
    public Collection<Value> values() {
        this.buildIfNeededMap();
        return map.values();
    }


    /**
     * Return the size of the map. Use the map if it has already been created.
     * @return size
     */
    public int size() {
        this.buildIfNeededMap();
        return map.size();
    }

}
