package org.boon.criteria.internal;

import org.boon.criteria.ObjectFilter;
import org.boon.criteria.Criterion;
import org.junit.Before;
import org.junit.Test;

import static org.boon.Exceptions.die;


public class CriterionTest {

    @Before
    public void setUp() throws Exception {


    }

    @Test
    public void test() throws Exception {
        Criteria criteria = ObjectFilter.and( ObjectFilter.eq( "foo", "bar" ) );
        criteria.toString();

        Criteria criteria1 = ObjectFilter.and( ObjectFilter.eq( "foo", "bar" ) );

        boolean ok = true;
        ok &= ( criteria.equals( criteria1 ) ) || die( "Die" );


        Criterion c1 = ObjectFilter.eq( "foo", "bar" );
        Criterion c2 = ObjectFilter.eq( "foo", "bar" );

        ok &= ( c1.equals( c2 ) ) || die( "Die" );

    }
}
