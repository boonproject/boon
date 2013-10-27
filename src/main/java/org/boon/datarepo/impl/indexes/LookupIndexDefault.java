package org.boon.datarepo.impl.indexes;

import org.boon.datarepo.LookupIndex;
import org.boon.datarepo.spi.SPIFactory;
import org.boon.predicates.Function;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A really simple lookup index that uses a standard java.util.HashMap.
 *
 * @param <KEY>  key to lookup
 * @param <ITEM> value
 */
public class LookupIndexDefault<KEY, ITEM> implements LookupIndex<KEY, ITEM> {


    protected Function<ITEM, KEY> keyGetter;
    protected Function<ITEM, KEY> primaryKeyGetter;

    protected Map<KEY, MultiValue> map;
    private Logger log = Logger.getLogger(LookupIndexDefault.class.getName());
    protected boolean storeKeyInIndexOnly;
    private Function<Object, KEY> keyTransformer;

    protected int keyBucketSize = 3;


    public LookupIndexDefault(Class<?> keyType) {


        if (log.isLoggable(Level.FINE)) {
            log.fine(String.format("key type %s ", keyType.getName()));
        }

        if (keyType == null) {
            return;
        }
        map = SPIFactory.getMapCreatorFactory().get().createMap(keyType);

    }


    protected void addManyKeys(ITEM item, List<KEY> keys) {
        for (KEY key : keys) {
            if (key != null) {
                this.put(item, key);
            }
        }
    }

    @Override
    public boolean add(ITEM item) {

        if (log.isLoggable(Level.FINE)) {
            log.fine(String.format("add item = %s", item));
        }

        KEY key = keyGetter.apply(item);
        if (key == null) {
            return false;
        }

        put(item, key);
        return true;

    }

    private void put(ITEM item, KEY key) {


        if (log.isLoggable(Level.FINE)) {
            log.fine(String.format("put item = %s with key = %s ", item, key));
        }

        key = getKey(key);


        MultiValue mv = null;
        mv = map.get(key);


        if (storeKeyInIndexOnly) {
            Object primaryKey = primaryKeyGetter.apply(item);

            mv = mvCreateOrAddToMV(mv, primaryKey);
        } else {
            mv = mvCreateOrAddToMV(mv, item);
        }

        map.put(key, mv);
    }

    private MultiValue mvCreateOrAddToMV(MultiValue mv, Object obj) {
        return MultiValue.add(mv, obj, keyBucketSize);
    }


    protected final void removeManyKeys(ITEM item, List<KEY> keys) {
        for (KEY key : keys) {
            if (key != null) {
                removeKey(item, key);
            }
        }
    }

    @Override
    public boolean delete(ITEM item) {
        KEY key = keyGetter.apply(item);


        return removeKey(item, key);

    }

    private boolean removeKey(ITEM item, KEY key) {
        key = getKey(key);

        MultiValue mv = map.get(key);

        if (mv == null) {
            return false;
        }

        mv = MultiValue.remove(mv, item);

        if (mv == null) {
            map.remove(key);
        }
        return true;
    }


    public void setKeyGetter(Function<ITEM, KEY> keyGetter) {
        Objects.requireNonNull(keyGetter, "keyGetter cannot be null");
        this.keyGetter = keyGetter;
    }

    public void setPrimaryKeyGetter(Function<ITEM, KEY> keyGetter) {
        Objects.requireNonNull(keyGetter, "keyGetter cannot be null");
        storeKeyInIndexOnly = true;
        this.primaryKeyGetter = keyGetter;
    }

    @Override
    public List<ITEM> all() {

        if (log.isLoggable(Level.FINE)) {
            log.fine("all called");
        }

        List results = new ArrayList<>(map.size());
        for (MultiValue values : map.values()) {
            values.addTo(results);
        }
        return results;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public Collection<ITEM> toCollection() {
        return (Collection<ITEM>) this.map.values();
    }


    @Override
    public ITEM get(KEY key) {

        key = getKey(key);

        MultiValue mv = map.get(key);
        if (mv == null) {
            return null;
        } else {
            return (ITEM) mv.getValue();
        }
    }

    protected KEY getKey(KEY key) {
        if (keyTransformer != null) {
            key = this.keyTransformer.apply(key);
        }
        return key;
    }


    public List<ITEM> getAll(KEY key) {
        key = getKey(key);

        MultiValue mv = map.get(key);
        if (mv == null) {
            return null;
        } else {
            return mv.getValues();
        }
    }

    @Override
    public boolean deleteByKey(KEY key) {
        key = getKey(key);

        this.map.remove(key);
        return true;
    }


    //TODO implement so we can store only primary keys in a index to make indexes smaller if
    //we ever decide to cache actual items
    @Override
    public boolean isPrimaryKeyOnly() {
        return storeKeyInIndexOnly;
    }

    @Override
    public void setInputKeyTransformer(Function<Object, KEY> func) {
        this.keyTransformer = func;
    }

    @Override
    public void setBucketSize(int size) {
        this.keyBucketSize = size;
    }

    @Override
    public void init() {
    }


    @Override
    public void clear() {

        if (log.isLoggable(Level.FINE)) {
            log.fine("clear called");
        }
        this.map.clear();
    }

}
