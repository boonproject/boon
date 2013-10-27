package org.boon.criteria;

import org.junit.Before;
import org.junit.Test;

import static org.boon.Exceptions.die;


public class CriterionTest {

    @Before
    public void setUp() throws Exception {


    }

    @Test
    public void test() throws Exception {
        Criteria criteria = CriteriaFactory.and(CriteriaFactory.eq("foo", "bar"));
        criteria.toString();

        Criteria criteria1 = CriteriaFactory.and(CriteriaFactory.eq("foo", "bar"));

        boolean ok = true;
        ok &= (criteria.equals(criteria1)) || die("Die") ;


        Criterion c1 = CriteriaFactory.eq("foo", "bar");
        Criterion c2 = CriteriaFactory.eq("foo", "bar");

        ok &= (c1.equals(c2)) || die("Die") ;

    }
}
