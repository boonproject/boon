package org.boon.core.json;

import org.boon.core.Lists;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.boon.core.Exceptions.die;
import static org.boon.core.Lists.list;
import static org.boon.core.Maps.idx;
import static org.boon.core.Lists.idx;
import static org.boon.core.Maps.map;
import static org.boon.core.Str.lines;
import static org.junit.Assert.assertEquals;

public class JSONParseTest {

    @Test
    public void testParserSimpleMapWithNumber() {

        Object obj = JSONParser.parse(
                " { 'foo': 1 }  ".replace('\'', '"')
        );

        boolean ok = true;

        ok &= obj instanceof Map || die("Object was not a map");

        Map<String, Object> map = (Map<String, Object>)obj;

        System.out.println(obj);

        ok &=  idx(map, "foo").equals(1) || die("I did not find 1");
    }

    @Test
    public void testParserSimpleMapWithBoolean() {

        Object obj = JSONParser.parse(
                " { 'foo': true }  ".replace('\'', '"')
        );

        boolean ok = true;

        ok &= obj instanceof Map || die("Object was not a map");

        Map<String, Object> map = (Map<String, Object>)obj;

        System.out.println(obj);

        ok &=  idx(map, "foo").equals(true) || die("I did not find true");
    }


    @Test
    public void testParserSimpleMapWithList() {

        Object obj = JSONParser.parse(
                " { 'foo': [0,1,2] }  ".replace('\'', '"')
        );

        boolean ok = true;

        ok &= obj instanceof Map || die("Object was not a map");

        Map<String, Object> map = (Map<String, Object>)obj;

        System.out.println(obj);

        ok &=  idx(map, "foo").equals(list(0,1,2)) || die("I did not find (0,1,2)");
    }

    @Test
    public void testParserSimpleMapWithString() {

        Object obj = JSONParser.parse(
                " { 'foo': 'str ' }  ".replace('\'', '"')
        );

        boolean ok = true;

        System.out.println("%%%%%%" + obj);

        ok &= obj instanceof Map || die("Object was not a map");

        Map<String, Object> map = (Map<String, Object>)obj;

        System.out.println(obj);

        ok &=  idx(map, "foo").equals("str ") || die("I did not find 'str'");
    }



    @Test
    public void testLists() {
         String [][] testLists = {
                 {"emptyList", "[]"},                  //0
                 {"emptyList", " [ ]"},                  //1  fails
                 {"oddly spaced", "[ 0 , 1 ,2, 3, '99' ]"},   //2
                 {"nums and strings", "[ 0 , 1 ,'bar', 'foo', 'baz' ]"}, //3
                 {"nums stings map", "[ 0 , 1 ,'bar', 'foo', {'baz':1} ]"}, //4
                 {"nums strings map with list", "[ 0 , 1 ,'bar', 'foo', {'baz':1, 'lst':[1,2,3]} ]"},//5
                 {"nums strings map with list", "[ {'bar': {'zed': 1}} , 1 ,'bar', 'foo', {'baz':1, 'lst':[1,2,3]} ]"},//6
                 {"tightly spaced", "[0,1,2,3,99]"},

         };

        List<?>[] lists  = {
                Collections.EMPTY_LIST,    //0
                Collections.EMPTY_LIST,    //1
                Lists.list(0, 1, 2, 3, "99"),  //2
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




    @Test
    public void testMaps() {
        String [][] tests = {
                {"empty map", "{}"},                  //1
                {"map with list", "{'lst': [1,2,3]}"},      //2
                {"map with list, num, str", "{'lst': [1,2,3] , 'num' : 5, 'str': 'Maya!!!!' }"} ,     //3
                {"same as above with odd spacing", "\t{\n'lst'\r\n: \t[1\t,\n2,3] , \n'num' : 5, " +
                        "'str': 'Maya!!!!' }"} ,     //4
                {"more stuff", "\t{\n'lst'\r\n: \t[1\t,\n2,3, {'a':'b'}] " +
                        "\t, \n" +
                        "\n'num' : [5, {}], " +
                                "'str': 'Maya!!!!' }"} ,     //5

        };

        Map<?,?>[] maps  = {
                Collections.EMPTY_MAP,    //1
                map("lst", list( 1,2,3 ) ), //  2
                map("lst", list( 1,2,3 ), "num", 5, "str", "Maya!!!!"), //  3
                map("lst", list( 1,2,3 ), "num", 5, "str", "Maya!!!!"), //  4
                map("lst", list( 1,2,3, map("a", "b") ),
                    "num", list(5, Collections.EMPTY_MAP),
                     "str", "Maya!!!!"), //  5

        };

        for (int index = 0; index < tests.length; index++)  {
            String name = tests[index][0];
            String json = tests[index][1];

            helper(name, json, maps[index]);
        }
    }



    @Test
    public void testNumbersTable() {
        String [][] tests = {
                {"seven", "1.23E+11 "}, //6

                {"one", " 1 "},                  //0
                {"two", "1.1 "},                  //1
                {"three", " 1.8 "},                  //2
                {"four", " 9.99 "},                  //3
                {"six", "123456789012345678 "},//                //5

        };

        Number[] nums = {
                1.23e11,
                1,
                1.1,
                1.8,
                9.99,
                123456789012345678L,
                };



        for (int index = 0; index < tests.length; index++)  {
            String name = tests[index][0];
            String json = tests[index][1];

            helper(name, json, nums[index]);
        }
    }

    public void helper(String name, String json, Object compareTo) {

        System.out.printf("%s, %s, %s", name, json, compareTo);

        Object obj = JSONParser.parse(
                json.replace('\'', '"')
        );

        boolean ok = true;



        System.out.printf("\nNAME=%s \n \t parsed obj=%s\n \t json=%s\n \t compareTo=%s\n", name, obj, json, compareTo);
        ok &= compareTo.equals(obj) || die(name + " :: List has items " + json);


    }

    @Test
    public void testNumber() {

        Object obj = JSONParser.parse(
                "  1  ".replace('\'', '"')
        );

        boolean ok = true;

        ok &= obj instanceof Integer || die("Object was not an Integer");

        int i = (Integer) obj;

        ok &=  i == 1 || die("I did see i equal to 1");

        System.out.println(obj.getClass());
    }

    @Test
    public void testNumberBoolean() {

        Object obj = JSONParser.parse(
                "  true  ".replace('\'', '"')
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


        Object obj = JSONParser.parse(testString);

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


        System.out.println(JSONStringParser.decode(testString)
        );


        Object obj = JSONParser.parse(testString);



        System.out.println("here is what I got " + obj);

        boolean ok = true;

        ok &= obj instanceof List || die("Object was not a List");

        List<String> value = (List<String>) obj;


        assertEquals("this is all sort of text,    do you think it is \"cool\" ",
                idx(value, 0));

        System.out.println(obj.getClass());
    }

    @Test
    public void textInMiddleOfArray() {

        try {
            Object obj = JSONParser.parse(
                    lines("[A, 0]"
                    ).replace('\'', '"')
            );

        } catch (Exception ex) {
          //success
            return;
        }
        die("The above should cause an exception");

    }

    @Test
    public void oddlySpaced2() {

        Object obj = JSONParser.parse(
                lines("[       1, 0]"
                ).replace('\'', '"')
        );

        boolean ok = true;

        System.out.println(obj);

    }

    @Test
    public void complex() {

        Object obj = JSONParser.parse(
                lines(

                        "{    'num' : 1   , ",
                        "     'bar' : { 'foo': 1  },  ",
                        "     'nums': [0  ,1,2,3,4,5] } "
                ).replace('\'', '"')
        );

        boolean ok = true;

        System.out.println(obj);
        //die();
    }


}
