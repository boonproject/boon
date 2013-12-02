package org.boon.json;

import org.boon.IO;
import org.boon.Lists;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.boon.Exceptions.die;
import static org.boon.Lists.list;
import static org.boon.Maps.idx;
import static org.boon.Maps.map;
import static org.boon.Str.lines;
import static org.junit.Assert.assertEquals;

public class JSONParse2Test {


    @Test public void objectSerialization() {


        //        {
        //
        //            "myInt" : 1,
        //                "myFloat" : 1.1,
        //                "myDouble" : 1.2,
        //                "myShort" : 1,
        //                "myBoolean" : true,
        //                "string" : "test",
        //                "string2" : "test \"I love bacon \", do you?",
        //
         //        }
        String fileContents = IO.read ( "files/AllTypes.json" );
        AllTypes types = JsonLazyEncodeParser.parseInto ( AllTypes.class, fileContents );
        validateAllTypes ( types );

        validateAllTypes ( types.getAllType () );

        boolean ok = true;
        ok |=  types.getAllTypes ().size () == 3 || die ("" + types.getAllTypes ().size());

        for (AllTypes allType : types.getAllTypes ()) {
            validateAllTypes ( allType );
        }

        ok |= types.getString2 ().equals ( "test \\\"I love bacon \\\", do you?" ) || die("bacon hater");;

    }

    @Test public void objectSerializationEncodeStrings() {


        //        {
        //
        //            "myInt" : 1,
        //                "myFloat" : 1.1,
        //                "myDouble" : 1.2,
        //                "myShort" : 1,
        //                "myBoolean" : true,
        //                "string" : "test"
        //        }
        String fileContents = IO.read ( "files/AllTypes.json" );
        AllTypes types = JsonLazyEncodeParser.fullParseInto ( AllTypes.class, fileContents );


        validateAllTypes ( types );

        validateAllTypes ( types.getAllType () );

        boolean ok = true;


        ok |=  types.getAllTypes ().size () == 3 || die ("" + types.getAllTypes ().size());

        for (AllTypes allType : types.getAllTypes ()) {
            validateAllTypes ( allType );
        }

        ok |= types.getString2 ().equals ( "test \"I love bacon \", do you?" ) || die("bacon hater " + types.getString2 ());

    }

    private void validateAllTypes( AllTypes types ) {
        boolean ok = true;
        ok |= types.getMyInt () == 1 || die("" + types.getMyInt ());



        ok |= types.getMyFloat () == 1.1f || die("" + types.getMyFloat ());

        ok |= types.getMyDouble () == 1.2 || die("" + types.getMyDouble ());


        ok |= types.isMyBoolean () == true || die("" + types.isMyBoolean ());

        ok |= types.getMyShort () == 2 || die("" + types.getMyShort ());

        ok |= types.getMyByte () == 3 || die("" + types.getMyByte ());

        ok |= types.getString ().equals ("test") || die("" + types.getString ());
    }



    @Test
    public void testParserSimpleMapWithNumber() {

        Object obj = JsonLazyEncodeParser.parse (
                " { 'foo': 1 }  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Map || die("Object was not a map");

        Map<String, Object> map = (Map<String, Object>)obj;

        Object foo = idx(map, "foo");

        ok &=  foo.equals(1) || die("I did not find 1 foo=" + foo);
    }



    @Test
    public void testParseFalse() {

        Object obj = JsonLazyEncodeParser.parse (
                " { 'foo': false }  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Map || die("Object was not a map");

        Map<String, Object> map = (Map<String, Object>)obj;

        System.out.println(obj);

        ok &=  idx(map, "foo").equals( false ) || die("I did not find  false");
    }

    @Test
    public void testParseNull() {

        Object obj = JsonLazyEncodeParser.parse (
                " { 'foo': null }  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Map || die("Object was not a map");

        Map<String, Object> map = (Map<String, Object>)obj;

        System.out.println(obj);

        ok &=  idx(map, "foo") == ( null ) || die("I did not find null");
    }

    @Test
    public void testParserSimpleMapWithBoolean() {

        Object obj = JsonLazyEncodeParser.parse (
                " { 'foo': true }  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Map || die("Object was not a map");

        Map<String, Object> map = (Map<String, Object>)obj;

        System.out.println(obj);

        ok &=  idx(map, "foo").equals(true) || die("I did not find true");
    }


    @Test
    public void testParserSimpleMapWithList() {

        Object obj = JsonLazyEncodeParser.parse (
                " { 'foo': [0,1,2] }  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Map || die("Object was not a map");

        Map<String, Object> map = (Map<String, Object>)obj;

        System.out.println(obj);

        ok &=  idx(map, "foo").equals(list(0,1,2)) || die("I did not find (0,1,2)");
    }

    @Test
    public void testParserSimpleMapWithString() {

        Object obj = JsonLazyEncodeParser.parse (
                " { 'foo': 'str ' }  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        System.out.println("%%%%%%" + obj);

        ok &= obj instanceof Map || die("Object was not a map");

        Map<String, Object> map = (Map<String, Object>)obj;

        System.out.println(obj);

        final Object foo = idx ( map, "foo" );
        ok &=  foo.equals("str ") || die("I did not find 'str '" + foo + "$");
    }



    @Test
    public void testLists() {
        String [][] testLists = {
                {"emptyList", "[]"},                  //0
                {"emptyList", " [ ]"},                  //1  fails
                {"oddly spaced", "[ 0 , 1 ,2, 3, '99' ]"},   //2
                {"nums and strings", "[ 0 , 1 ,'bar', 'foo', 'baz' ]"}, //3
                {"nums stings map", "[ 0 , 1 ,'bar', 'foo', {'baz':1} ]"}, //4
                {"nums strings map with listStream", "[ 0 , 1 ,'bar', 'foo', {'baz':1, 'lst':[1,2,3]} ]"},//5
                {"nums strings map with listStream", "[ {'bar': {'zed': 1}} , 1 ,'bar', 'foo', {'baz':1, 'lst':[1,2,3]} ]"},//6
                {"tightly spaced", "[0,1,2,3,99]"},

        };

        List<?>[] lists  = {
                Collections.EMPTY_LIST,    //0
                Collections.EMPTY_LIST,    //1
                Lists.list ( 0, 1, 2, 3, "99" ),  //2
                Lists.list(0, 1, "bar", "foo", "baz"),//3
                Lists.list(0, 1, "bar", "foo", map("baz", 1)),//4
                Lists.list(0, 1,
                        "bar",
                        "foo",
                        map( "baz", 1,
                                "lst", list(1,2,3)
                        )
                ),//5
                Lists.list(map("bar", map("zed", 1)), 1, "bar", "foo", map("baz", 1, "lst", list(1,2,3))),//6
                Lists.list(0, 1, 2, 3, 99)
        };

        for (int index = 0; index < testLists.length; index++)  {
            String name = testLists[index][0];
            String json = testLists[index][1];

            helper(name, json, lists[index]);
        }
    }



    public void helper(String name, String json, Object compareTo) {

        System.out.printf("%s, %s, %s", name, json, compareTo);

        Object obj = JsonLazyEncodeParser.parse (
                json.replace ( '\'', '"' )
        );

        boolean ok = true;



        System.out.printf("\nNAME=%s \n \t parsed obj=%s\n \t json=%s\n \t compareTo=%s\n", name, obj, json, compareTo);
        ok &= compareTo.equals(obj) || die(name + " :: List has items " + json);


    }


    @Test
    public void testNumber() {

        Object obj = JsonLazyEncodeParser.parse (
                "1".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Integer || die("Object was not an Integer");

        int i = (Integer) obj;

        ok &=  i == 1 || die("I did see i equal to 1");

        System.out.println(obj.getClass());
    }

    @Test
    public void testBoolean() {

        Object obj = JsonLazyEncodeParser.parse (
                "  true  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Boolean || die("Object was not a Boolean");

        boolean value = (Boolean) obj;

        ok &=  value == true || die("I did see value equal to true");

        System.out.println(obj.getClass());
    }

    @Test(expected = JsonException.class)
    public void testBooleanParseError() {

        Object obj = JsonLazyEncodeParser.parse (
                "  tbone  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Boolean || die("Object was not a Boolean");

        boolean value = (Boolean) obj;

        ok &=  value == true || die("I did see value equal to true");

        System.out.println(obj.getClass());
    }

    @Test
    public void testString() {

        String testString =
                ("  'this is all sort of text, " +
                        "   do you think it is \\'cool\\' '").replace('\'', '"');


        Object obj = JsonLazyEncodeParser.fullParse ( testString );

        System.out.println("here is what I got " + obj);

        boolean ok = true;

        ok &= obj instanceof String || die("Object was not a String");

        String value = (String) obj;

        assertEquals("this is all sort of text,    do you think it is \"cool\" ", obj);

        System.out.println(obj.getClass());
    }


    @Test
    public void testStringInsideOfList() {

        String testString = (
                "  [ 'this is all sort of text, " +
                        "   do you think it is \\'cool\\' '] ").replace('\'', '"');



        Object obj = JsonLazyEncodeParser.fullParse ( testString );



        System.out.println("here is what I got " + obj);

        boolean ok = true;

        ok &= obj instanceof List || die("Object was not a List");

        List<String> value = (List<String>) obj;


        assertEquals("this is all sort of text,    do you think it is \"cool\" ",
                Lists.idx ( value, 0 ));

        System.out.println(obj.getClass());
    }

    @Test
    public void testStringInsideOfList2() {

        String testString =
                "[ 'abc','def' ]".replace('\'', '"');



        Object obj = JsonLazyEncodeParser.parse ( testString );
        System.out.println("here is what I got " + obj);

        boolean ok = true;

        ok &= obj instanceof List || die("Object was not a List");

        List<String> value = (List<String>) obj;


        assertEquals("abc",
                Lists.idx ( value, 0 ));

        System.out.println(obj.getClass());
    }

    @Test
    public void textInMiddleOfArray() {

        try {
            Object obj = JsonLazyEncodeParser.parse (
                    lines ( "[A, 0]"
                    ).replace ( '\'', '"' )
            );

        } catch (Exception ex) {
            //success
            return;
        }
        die("The above should cause an exception");

    }

    @Test
    public void oddlySpaced2() {

        Object obj = JsonLazyEncodeParser.parse (
                lines ( "[   2   ,    1, 0]"
                ).replace ( '\'', '"' )
        );

        boolean ok = true;

        System.out.println(obj);

    }

    @Test
    public void complex() {

        Object obj = JsonLazyEncodeParser.parse (
                lines (

                        "{    'num' : 1   , ",
                        "     'bar' : { 'foo': 1  },  ",
                        "     'nums': [0  ,1,2,3,4,5,'abc'] } "
                ).replace ( '\'', '"' )
        );

        boolean ok = true;

        System.out.println(obj);
        //die();
    }

}
