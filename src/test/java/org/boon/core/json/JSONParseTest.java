package org.boon.core.json;

import org.boon.core.json.JSONParser;
import org.boon.core.json.JSONStringParser;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.boon.core.Exceptions.die;
import static org.boon.core.Lists.list;
import static org.boon.core.Maps.idx;
import static org.boon.core.Lists.idx;
import static org.junit.Assert.assertEquals;

public class JSONParseTest {

    @Test
    public void testParserSimpleMapWithNumber() {

        Object obj = JSONParser.decodeObject(
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

        Object obj = JSONParser.decodeObject(
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

        Object obj = JSONParser.decodeObject(
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

        Object obj = JSONParser.decodeObject(
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
    public void testSimpleArray() {

        Object obj = JSONParser.decodeObject(
                "  [0,1,2,3,99]  ".replace('\'', '"')
        );

        boolean ok = true;

        ok &= obj instanceof List || die("Object was not a list");

        ok &=  idx((List)obj, 0) == 0 || die("I did not find 0 at 0");
        ok &=  idx((List)obj, 3) == 3 || die("I did not find 3 at 3");
        ok &=  idx((List)obj, 3) == 3 || die("I did not find 99 at 4");

    }

    @Test
    public void testNumber() {

        Object obj = JSONParser.decodeObject(
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

        Object obj = JSONParser.decodeObject(
                "  true  ".replace('\'', '"')
        );

        boolean ok = true;

        ok &= obj instanceof Boolean || die("Object was not a Boolean");

        boolean value = (Boolean) obj;

        ok &=  value == true || die("I did see value equal to true");

        System.out.println(obj.getClass());
    }

    @Test    //broke need to fix
    public void testString() {

        String testString =
            "  'this is all sort of text, " +
            "   do you think it is \\'cool\\' ' ".replace('\'', '"');


        Object obj = JSONParser.decodeObject(testString);

        System.out.println("here is what I got " + obj);

//        boolean ok = true;
//
//        ok &= obj instanceof String || die("Object was not a String");
//
//        String value = (String) obj;
//
//
//        ok &=  value.equals("") || die("I did see i equal to true");
//
//        System.out.println(obj.getClass());
    }


    @Test
    public void testStringInsideOfList() {

        String testString = (
                "  [ 'this is all sort of text, " +
                        "   do you think it is \\'cool\\' '] ").replace('\'', '"');


        System.out.println(JSONStringParser.decode(testString)
        );


        Object obj = JSONParser.decodeObject(testString);



        System.out.println("here is what I got " + obj);

        boolean ok = true;

        ok &= obj instanceof List || die("Object was not a List");

        List<String> value = (List<String>) obj;


        assertEquals("this is all sort of text,    do you think it is \"cool\" ",
                idx(value, 0));

        System.out.println(obj.getClass());
    }

}
