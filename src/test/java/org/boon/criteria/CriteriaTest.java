package org.boon.criteria;


import org.junit.Before;
import org.junit.Test;

import static org.boon.Exceptions.die;
import static org.boon.criteria.CriteriaFactory.*;

public class CriteriaTest {

    @Before
    public void setUp() throws Exception {


    }


    @Test
    public void testMe() {
        main();
    }


    public static class MyObject {
        int myint = 5;
        float myfloat = 5.0f;
        double mydouble = 5.0;
        String mystring = "five";
    }

    public static void main (String... args) {


        MyObject o = new MyObject ();

        boolean ok = true;

        Criteria criteria = eq ( "myint", 5  );
        ok |= QueryFactory.andTest ( o, criteria ) || die();



        criteria = eq ( "myint", 6  );
        ok |= !QueryFactory.andTest ( o, criteria ) || die();


        criteria = eq ( "myint", "6"  );
        ok |= !QueryFactory.andTest ( o, criteria ) || die();


        //This is the current behavior, but I might want to
        //have a version that does a coerce. TBD.
        criteria = eq ( "myint", "5"  );
        ok |= !QueryFactory.andTest ( o, criteria ) || die();

        //primitive

        criteria = eqInt ( "myint", 5  );
        ok |= QueryFactory.andTest ( o, criteria ) || die();



        criteria = eqInt ( "myint", 6  );
        ok |= !QueryFactory.andTest ( o, criteria ) || die();


        criteria = eqFloat ( "myfloat", 5.0f  );
        ok |= QueryFactory.andTest ( o, criteria ) || die();



        criteria = eqFloat ( "myfloat", 6.0f  );
        ok |= !QueryFactory.andTest ( o, criteria ) || die();



        criteria = eqDouble ( "mydouble", 5.0  );
        ok |= QueryFactory.andTest ( o, criteria ) || die();



        criteria = eqDouble ( "mydouble", 6.0 );
        ok |= !QueryFactory.andTest ( o, criteria ) || die();



        criteria = eq ( "mystring", "five"  );
        ok |= QueryFactory.andTest ( o, criteria ) || die();



        criteria = eq ( "mystring", "six" );
        ok |= !QueryFactory.andTest ( o, criteria ) || die();


        try {
        //primitive
            ok = true;
            criteria = eqFloat ( "myint", 5.0f  );
            ok |= QueryFactory.andTest ( o, criteria ) || die();
            ok = false;
        }catch (Exception ex) {
            ok |= ok == true || die();
        }


    }

}
