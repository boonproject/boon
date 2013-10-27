package org.boon.datarepo.impl.indexes;

import org.boon.datarepo.LookupIndex;
import org.boon.datarepo.spi.SPIFactory;
import org.boon.predicates.Function;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.boon.Lists.list;

public class UniqueLookupIndex<KEY, ITEM> implements LookupIndex<KEY, ITEM> {

    protected Function<ITEM, KEY> keyGetter;
    protected Map<KEY, ITEM> map = null;
    protected List<ITEM> items = new LinkedList();


    private Logger log = Logger.getLogger(UniqueLookupIndex.class.getName());

    private Function<Object, KEY> keyTransformer;

    public UniqueLookupIndex(Class<?> keyType) {
        if (keyType == null) {
            return;
        }
        map = SPIFactory.getMapCreatorFactory().get().createMap(keyType);

    }

    @Override
    public ITEM get(KEY key) {
        key = getKey(key);
        return map.get(key);
    }

    @Override
    public void setKeyGetter(Function<ITEM, KEY> keyGetter) {
        this.keyGetter = keyGetter;
    }

    @Override
    public boolean add(ITEM item) {


        if (log.isLoggable(Level.FINE)) {
            log.fine(String.format("add item = %s", item));
        }

        KEY key = keyGetter.apply(item);

        key = getKey(key);


        if (key == null) {
            return false;
        }

        map.put(key, item);
        items.add(item);
        return true;
    }

    @Override
    public boolean delete(ITEM item) {

        if (log.isLoggable(Level.FINE)) {
            log.fine(String.format("delete item = %s", item));
        }

        KEY key = keyGetter.apply(item);
        key = getKey(key);
        map.remove(key);
        return items.remove(item);
    }

    @Override
    public List<ITEM> all() {

        if (log.isLoggable(Level.FINE)) {
            log.fine("all called ");
        }

        return new ArrayList<>(items);
    }

    @Override
    public List<ITEM> getAll(KEY key) {

        if (log.isLoggable(Level.FINE)) {
            log.fine("getAll called ");
        }

        return list(this.get(key));
    }

    @Override
    public int size() {
        return this.items.size();
    }

    @Override
    public Collection<ITEM> toCollection() {
        return this.items;
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public boolean deleteByKey(KEY key) {
        key = getKey(key);
        this.map.remove(key);
        return true;
    }

    @Override
    public boolean isPrimaryKeyOnly() {
        return false;
    }

    @Override
    public void init() {

    }


    @Override
    public void setInputKeyTransformer(Function<Object, KEY> func) {
        this.keyTransformer = func;
    }

    @Override
    public void setBucketSize(int size) {

    }

    protected KEY getKey(KEY key) {
        if (keyTransformer != null) {
            key = this.keyTransformer.apply(key);
        }
        return key;
    }


}
