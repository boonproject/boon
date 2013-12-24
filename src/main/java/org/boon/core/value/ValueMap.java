package org.boon.core.value;

import java.util.Map;

public interface ValueMap <K, V> extends  Map<K, V> {

    public void add( MapItemValue miv );

}
