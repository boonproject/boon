package org.boon.datarepo.impl.decorators;

import org.boon.cache.Cache;
import org.boon.core.Supplier;
import org.boon.criteria.Criteria;
import org.boon.criteria.CriteriaFactory;
import org.boon.criteria.Group;
import org.boon.datarepo.Filter;
import org.boon.datarepo.ResultSet;

public class FilterWithCache extends FilterDecoratorBase {

    private final Supplier<Cache<Group, ResultSet>> cacheFactory;

    private Cache<Group, ResultSet> cache;

    public FilterWithCache ( final Filter delegate, final Supplier<Cache<Group, ResultSet>> cacheFactory ) {
        super ( delegate );
        this.cacheFactory = cacheFactory;
        this.cache = cacheFactory.get ();
    }

    @Override
    public ResultSet filter ( Criteria... expressions ) {
        Group and = CriteriaFactory.and ( expressions );

        ResultSet results = cache.get ( and );


        if ( results != null ) {
            cache.put ( and, results );
            return results;
        }


        results = super.filter ( expressions );

        cache.put ( and, results );

        return results;
    }

    @Override
    public void invalidate () {

        cache = this.cacheFactory.get ();
        super.invalidate ();
    }


}
