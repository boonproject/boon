package org.boon.collections;

import org.boon.Str;
import org.junit.Test;

/**
 * Created by Richard on 9/24/14.
 */
public class ConcurrentWeakHashMapTest {

    @Test
    public void test() {
        ConcurrentWeakHashMap<String, String> map = new ConcurrentWeakHashMap<>();

        map.put("HI", "MOM");

        final String hi = map.get("HI");

        Str.equalsOrDie("MOM", hi);
    }
}
