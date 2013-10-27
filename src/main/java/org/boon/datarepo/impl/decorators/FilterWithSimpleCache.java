package org.boon.datarepo.impl.decorators;

import org.boon.datarepo.Filter;
import org.boon.datarepo.ResultSet;
import org.boon.criteria.Criteria;
import org.boon.criteria.CriteriaFactory;
import org.boon.criteria.Group;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FilterWithSimpleCache extends FilterDecoratorBase {

    Map<Criteria, ResultSet> queryCache = new ConcurrentHashMap<>();
    AtomicInteger flushCount = new AtomicInteger();


    @Override
    public ResultSet filter(Criteria... expressions) {
        Group and = CriteriaFactory.and(expressions);
        checkCache();

        ResultSet results = queryCache.get(and);

        if (results != null) {
            return results;
        }


        results = super.filter(expressions);

        queryCache.put(and, results);
        flushCount.incrementAndGet();

        return results;
    }

    @Override
    public void invalidate() {
        queryCache.clear();
        super.invalidate();
    }

    public FilterWithSimpleCache(Filter delegate) {
        super(delegate);
    }

    private void checkCache() {
        if (flushCount.get() > 10_000 && queryCache.size() > 10_000) {
            queryCache.clear();
            flushCount.set(0);
        }
    }

}
