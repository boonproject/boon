package org.boon.datarepo.impl.decorators;

import org.boon.criteria.Criteria;
import org.boon.criteria.CriteriaFactory;
import org.boon.criteria.Group;
import org.boon.datarepo.Filter;
import org.boon.datarepo.ResultSet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FilterWithSimpleCache extends FilterDecoratorBase {

    Map<Criteria, ResultSet> queryCache = new ConcurrentHashMap<> ();
    AtomicInteger flushCount = new AtomicInteger ();


    @Override
    public ResultSet filter ( Criteria... expressions ) {
        Group and = CriteriaFactory.and ( expressions );
        checkCache ();

        ResultSet results = queryCache.get ( and );

        if ( results != null ) {
            return results;
        }


        results = super.filter ( expressions );

        queryCache.put ( and, results );
        flushCount.incrementAndGet ();

        return results;
    }

    @Override
<<<<<<< HEAD
    public void invalidate () {
=======
    public void invalidate() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        queryCache.clear ();
        super.invalidate ();
    }

    public FilterWithSimpleCache ( Filter delegate ) {
        super ( delegate );
    }

<<<<<<< HEAD
    private void checkCache () {
=======
    private void checkCache() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( flushCount.get () > 10_000 && queryCache.size () > 10_000 ) {
            queryCache.clear ();
            flushCount.set ( 0 );
        }
    }

}
