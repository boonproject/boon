package org.boon.utils;

import org.boon.Str;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilsTest {

    @Test
    public void testCamel() throws Exception {
        String myFoo = "MY FOO_BAR_FUN\t_STUFF";

        String camelCaseUpper = Str.camelCaseUpper(myFoo);
        assertEquals("MyFooBarFunStuff", camelCaseUpper);

        String camelCaseLower = Str.camelCaseLower(myFoo);
        assertEquals("myFooBarFunStuff", camelCaseLower);

    }

    @Test
    public void testUnderBarCase() throws Exception {
        String myFoo = "FooFunFaceFact";

        String underBar = Str.underBarCase(myFoo);
        assertEquals("FOO_FUN_FACE_FACT", underBar);

    }

    @Test
    public void testUnderBarCase2() throws Exception {
        String myFoo = "FooFunFaceFact Fire Free FOO foo\tbar";

        String underBar = Str.underBarCase(myFoo);
        assertEquals("FOO_FUN_FACE_FACT_FIRE_FREE_FOO_FOO_BAR", underBar);

    }

}
