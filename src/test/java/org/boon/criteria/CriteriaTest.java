package org.boon.criteria;


import org.junit.Before;
import org.junit.Test;

import static org.boon.Exceptions.die;
import static org.boon.criteria.CriteriaFactory.*;

public class CriteriaTest {

    @Before
    public void setUp () throws Exception {


    }


    @Test
    public void testMe () {
        main();

    }


    public static class MyObject {
        int myint = 5;
        float myfloat = 5.0f;
        double mydouble = 5.0;
        long mylong = 5L;

        short myshort = 5;
        byte mybyte = 5;
        char mychar = 5;

        String mystring = "five";
        String empty = "";

    }

    public static void main ( String... args ) {


        MyObject o = new MyObject();

        boolean ok = true;

        //empty
        Criteria criteria = empty( "empty" );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        //INT
        criteria = eq( "myint", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "myint", 6 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = eq( "myint", "6" );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        //This is the current behavior, but I might want to
        //have a version that does a coerce. TBD.
        criteria = eq( "myint", "5" );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        //INT NOT EQ
        criteria = notEq( "myint", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = notEq( "myint", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = gt( "myint", 4 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gt( "myint", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = lt( "myint", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = lt( "myint", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = gte( "myint", 4 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = gte( "myint", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = gte( "myint", 6 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "myint", 4 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "myint", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "myint", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = between( "myint", 4, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "myint", 5, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "myint", 4, 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "myint", 6, 10 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = in( "myint", 1, 2, 3, 4, 5, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = in( "myint", 9, 10, 244, 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        //
        //
        //Float
        criteria = eq( "myfloat", 5.0f );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "myfloat", 5.0 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "myfloat", 6.0f );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = notEq( "myfloat", 6.0f );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = notEq( "myfloat", 5.0f );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = gt( "myfloat", 4 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gt( "myfloat", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = lt( "myfloat", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = lt( "myfloat", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = gte( "myfloat", 4 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gte( "myfloat", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gte( "myfloat", 6 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "myint", 4 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "myint", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "myint", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = between( "myfloat", 4, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "myfloat", 5, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "myfloat", 4.0f, 5.0f );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "myfloat", 6, 10 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = in( "myfloat", 1, 2, 3, 4, 5, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = in( "myfloat", 9, 10, 244, 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        //
        //
        //
        //Double
        criteria = eq( "mydouble", 5.0 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "mydouble", 6.0 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "mydouble", 5.0f );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = notEq( "mydouble", 6.0 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = notEq( "mydouble", 5.0 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = gt( "mydouble", 4 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gt( "mydouble", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = lt( "mydouble", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = lt( "mydouble", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = gte( "mydouble", 4 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gte( "mydouble", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gte( "mydouble", 6 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "mydouble", 4.0 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "mydouble", 5.0 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "mydouble", 6.0 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = between( "mydouble", 4, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "mydouble", 5, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "mydouble", 4.0, 5.0 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "mydouble", 6.0, 10.0 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = in( "mydouble", 1, 2, 3, 4, 5.0, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        //
        //
        //Long
        criteria = eq( "mylong", 5L );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "mylong", 6L );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "mylong", 5.0 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "mylong", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = notEq( "mylong", 6L );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = notEq( "mylong", 5L );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = gt( "mylong", 4 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gt( "mylong", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = lt( "mylong", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = lt( "mylong", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = gte( "mylong", 4L );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gte( "mylong", 5L );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gte( "mylong", 6L );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "mylong", 4L );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "mylong", 5L );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "mylong", 6L );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = between( "mylong", 4, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "mylong", 5, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "mylong", 4.0f, 5.0f );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "mylong", 6, 10 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = in( "mylong", 1, 2, 3, 4, 5, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = in( "mylong", 9, 10, 244, 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        //String
        criteria = eq( "mystring", "five" );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "mystring", "six" );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = notEq( "mystring", "six" );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = notEq( "mystring", "five" );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        try {
            //primitive
            ok = true;
            criteria = eqFloat( "myint", 5.0f );
            ok |= QueryFactory.andTest( o, criteria ) || die();
            ok = false;
        } catch ( Exception ex ) {
            ok |= ok == true || die();
        }


        //Short
        criteria = eq( "myshort", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "myshort", 6 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "myshort", "6" );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "myshort", "5" );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = notEq( "myshort", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = notEq( "myshort", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = gt( "myshort", 4 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gt( "myshort", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = lt( "myshort", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = lt( "myshort", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = gte( "myshort", 4 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gte( "myshort", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gte( "myshort", 6 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "myshort", 4 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "myshort", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "myshort", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = between( "myshort", 4, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "myshort", 5, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "myshort", 4.0f, 5.0f );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "myshort", 6, 10 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = in( "myshort", 1, 2, 3, 4, 5, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = in( "myshort", 9, 10, 244, 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        //Byte
        criteria = eq( "mybyte", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "mybyte", 6 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "mybyte", "6" );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "mybyte", "5" );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = notEq( "mybyte", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = notEq( "mybyte", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = gt( "mybyte", 4 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gt( "mybyte", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = lt( "mybyte", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = lt( "mybyte", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = gte( "mybyte", 4 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gte( "mybyte", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gte( "mybyte", 6 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "mybyte", 4 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "mybyte", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "mybyte", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = between( "mybyte", 4, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "mybyte", 5, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "mybyte", 4.0f, 5.0f );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "mybyte", 6, 10 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = in( "mybyte", 1, 2, 3, 4, 5, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = in( "mybyte", 9, 10, 244, 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        //Char
        criteria = eq( "mychar", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "mychar", 6 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "mychar", "6" );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = eq( "mychar", "\u0005" );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = notEq( "mychar", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = notEq( "mychar", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = gt( "mychar", 4 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gt( "mychar", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();

        criteria = lt( "mychar", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = lt( "mychar", 5 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = gte( "mychar", 4 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gte( "mychar", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();
        criteria = gte( "mychar", 6 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "mychar", 4 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "mychar", 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = lte( "mychar", 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = between( "mychar", 4, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "mychar", 5, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "mychar", 4.0f, 5.0f );
        ok |= QueryFactory.andTest( o, criteria ) || die();

        criteria = between( "mychar", 6, 10 );
        ok |= !QueryFactory.andTest( o, criteria ) || die();


        criteria = in( "mychar", 1, 2, 3, 4, 5, 6 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = in( "mychar", 9, 10, 244, 5 );
        ok |= QueryFactory.andTest( o, criteria ) || die();


        criteria = in( "mychar", 9, 10, 244, 5 );
        ok |= QueryFactory.orTest( o, criteria ) || die();
        ok |= QueryFactory.test( o, criteria ) || die();


    }

}
