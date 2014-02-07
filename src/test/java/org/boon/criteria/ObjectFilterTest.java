package org.boon.criteria;

import static org.boon.Exceptions.die;
import static org.boon.Maps.*;
import static org.boon.Lists.*;
import static org.boon.criteria.ObjectFilter.*;
import static org.boon.criteria.ObjectFilter.matches;

import org.boon.Lists;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ObjectFilterTest {

    @Test
    public void test() {

        Map<String, Object> map = map("name", (Object) "Rick", "salary", 1);

        boolean ok = true;

        ok &= matches( map, eq("name", "Rick"), eq("salary", 1) ) || die();

        ok &= matches( map, eq("name", "Rick"), gt("salary", 0) ) || die();

        ok &= matches( map, eq("name", "Rick"), gte( "salary", 0 ) ) || die();

        ok &= !matches( map, eq("name", "Rick"), lt( "salary", 0 ) ) || die();

        ok &= !matches( map, not( eq("name", "Rick") ), lt( "salary", 1 ) ) || die();

    }



    @Test
    public void testList() {

        Map<String, Object> prototype = map("name", (Object) "Rick", "salary", 1);

        List<Map<String, Object>> list = list( copy( prototype ), copy( prototype ), copy( prototype ) );

        prototype.put( "salary", 100 );
        add( list,  copy( prototype ), copy( prototype ), copy( prototype ) );



        boolean ok = true;

        ok &= filter( list, eq("name", "Rick"), gte( "salary", 0 ) ).size() == 6 || die();

        ok &= filter( list, eq("name", "Rick"), gte( "salary", 50 ) ).size() == 3 || die();

    }



}
