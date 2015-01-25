package org.boon.slumberdb;

import org.vertx.java.core.MultiMap;

import java.util.HashMap;
import java.util.List;

public class MultiMapUtil extends HashMap<String, String> {

    public MultiMapUtil(MultiMap multiMap) {
        for (String name : multiMap.names()) {
            List<String> all = multiMap.getAll(name);
            if (all.size() > 0) {
                put(name, all.get(0));
            }
        }
    }
}