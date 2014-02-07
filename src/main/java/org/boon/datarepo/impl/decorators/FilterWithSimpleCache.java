package org.boon.datarepo.impl.decorators;

import org.boon.cache.Cache;
import org.boon.cache.CacheType;
import org.boon.cache.SimpleConcurrentCache;
import org.boon.criteria.ObjectFilter;
import org.boon.criteria.internal.Criteria;
import org.boon.criteria.internal.Group;
import org.boon.datarepo.Filter;
import org.boon.datarepo.ResultSet;

public class FilterWithSimpleCache extends FilterDecoratorBase {

    /* The fifo cache is meant for a routine that is maybe using a few queries in a loop. */
    private Cache<Criteria, ResultSet> fifoCache = new SimpleConcurrentCache<>( 50, false, CacheType.FIFO );
    private Cache<Criteria, ResultSet> lruCache = new SimpleConcurrentCache<>( 1_000, false, CacheType.LRU );


    @Override
    public ResultSet filter( Criteria... expressions ) {
        Group and = ObjectFilter.and( expressions );

        ResultSet results = fifoCache.get( and );


        if ( results == null ) {
            results = lruCache.get( and );
            if ( results != null ) {
                fifoCache.put( and, results );
                return results;
            }
        }


        results = super.filter( expressions );

        fifoCache.put( and, results );
        lruCache.put( and, results );

        return results;
    }

    @Override
    public void invalidate() {

          /* The fifo cache is meant for a routine that is maybe using a few queries in a loop. */
        fifoCache = new SimpleConcurrentCache<>( 50, false, CacheType.FIFO );
        lruCache = new SimpleConcurrentCache<>( 1_000, false, CacheType.LRU );
        super.invalidate();
    }

    public FilterWithSimpleCache( Filter delegate ) {
        super( delegate );
    }


}
