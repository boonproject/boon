package org.boon.cache;

import org.boon.Lists;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

/**
 * Created by rick on 12/16/13.
 */
public class SortableConcurrentListTest {

    private SortableConcurrentList list;

    @Before
    public void before() {

        list = new SortableConcurrentList();
    }

    @After
    public void after() {

    }


    @Test
    public void test() {
        list.add( 9 );
        list.add( 66 );
        list.add( 7 );
        list.add( 55 );
        list.add( 5 );
        list.add( 33 );
        list.add( 3 );
        list.add( 2 );
        list.add( 1 );
        list.add( 0 );
        list.sort();
        boolean ok = Lists.list( 0, 1, 2, 3, 5, 7, 9, 33, 55, 66 ).equals( list ) || die();
        final List purgeList = list.sortAndReturnPurgeList( 0.20f );
        ok |= Lists.list( 0, 1 ).equals( purgeList ) || die();
        ok |= Lists.list( 2, 3, 5, 7, 9, 33, 55, 66 ).equals( list ) || die();
        puts( "test", ok );

    }

}
