package org.boon.collections;

import java.util.ArrayList;

/**
 * Created by Richard on 9/28/14.
 */
public class MultiMaps {

    public static MultiMap multiMap() {
        return new MultiMapImpl(ArrayList.class);
    }

    public static MultiMap safeMultiMap() {
        return new MultiMapImpl(ConcurrentLinkedHashSet.class);
    }
}
