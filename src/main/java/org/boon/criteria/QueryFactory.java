package org.boon.criteria;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class QueryFactory {

    public static boolean test ( Object obj, Criteria exp ) {
        return exp.test( obj );
    }

    public static boolean andTest ( Object obj, Criteria... exp ) {
        return CriteriaFactory.and( exp ).test( obj );
    }

    public static boolean orTest ( Object obj, Criteria... exp ) {
        return CriteriaFactory.or( exp ).test( obj );
    }


    public static <T> List<T> filter ( Collection<T> items, Criteria exp ) {
        if ( items.size() == 0 ) {
            return Collections.EMPTY_LIST;
        }

        List<T> results = new ArrayList<>();
        for ( T item : items ) {
            if ( exp.test( item ) ) {
                results.add( item );
            }
        }
        return results;
    }


}
