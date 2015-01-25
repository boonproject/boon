package org.boon.slumberdb;

import org.vertx.java.core.MultiMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiMapUtil extends HashMap<String, String> {

    public static Map<String, String> toMap(MultiMap multiMap) {
        Map<String, String> map = new HashMap<String, String>();
        for (String name : multiMap.names()) {
            List<String> all = multiMap.getAll(name);
            if (all.size() > 0) {
                map.put(name, all.get(0));
            }
        }
        return map;
    }
}