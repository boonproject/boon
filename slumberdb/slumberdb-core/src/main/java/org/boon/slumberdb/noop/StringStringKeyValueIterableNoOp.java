package org.boon.slumberdb.noop;

import org.boon.slumberdb.entries.Entry;
import org.boon.slumberdb.KeyValueIterable;

import java.util.Collections;
import java.util.Iterator;

/**
 * Created by Richard on 9/3/14.
 */
public class StringStringKeyValueIterableNoOp implements KeyValueIterable<String, String> {


    public static final StringStringKeyValueIterableNoOp SINGLETON = new StringStringKeyValueIterableNoOp();

    @Override
    public void close() {

    }

    @Override
    public Iterator<Entry<String, String>> iterator() {
        return Collections.emptyIterator();
    }


}
