package org.boon.core.value;

import org.boon.core.Value;

import java.util.Map;

public interface ValueMap <K, V> extends  Map<K, V> {

    /* add a map item value. */
    void add( MapItemValue miv );
    /** Return size w/o hydrating the map. */
    int len();
    /** Has the map been hydrated. */
    boolean hydrated();
    /** Give me the items in the map without hydrating the map.
     * Realize that the array is likely larger than the length so array items can be null.
     * */
    Entry<String, Value>[] items();

}
