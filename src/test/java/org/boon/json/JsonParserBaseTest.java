package org.boon.json;

import org.boon.IO;
import org.boon.Lists;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Lists.list;
import static org.boon.Maps.idx;
import static org.boon.Maps.map;
import static org.boon.Str.lines;
import static org.junit.Assert.assertEquals;

public class JsonParserBaseTest {


    JsonParser jsonParser;


    public JsonParserFactory factory () {
        return new JsonParserFactory ();
    }

    @Before
    public void setup () {

        jsonParser = factory ().create ();

    }


    @Test
    public void objectSerialization () {


        String fileContents = IO.read ( "files/AllTypes.json" );

        AllTypes types = jsonParser.parse ( AllTypes.class, fileContents );
        validateAllTypes ( types );

        validateAllTypes ( types.getAllType () );

        boolean ok = true;
        ok |= types.getAllTypes ().size () == 3 || die ( "" + types.getAllTypes ().size () );

        for ( AllTypes allType : types.getAllTypes () ) {
            validateAllTypes ( allType );
        }


    }

    @Test
    public void testFiles () {


        final List<String> list = IO.listByExt ( "files", ".json" );

        for ( String file : list ) {


            puts ( "testing", file );

            final Map<String, Object> map = jsonParser.parse ( Map.class, IO.read ( file ) );

        }


    }

    private void validateAllTypes ( AllTypes types ) {
        boolean ok = true;
        ok |= types.getMyInt () == 1 || die ( "" + types.getMyInt () );

        ok |= types.getMyFloat () == 1.1f || die ( "" + types.getMyFloat () );

        ok |= types.getMyDouble () == 1.2 || die ( "" + types.getMyDouble () );

        ok |= types.isMyBoolean () == true || die ( "" + types.isMyBoolean () );

        ok |= types.getMyShort () == 2 || die ( "" + types.getMyShort () );

        ok |= types.getMyByte () == 3 || die ( "" + types.getMyByte () );

        ok |= types.getString ().equals ( "test" ) || die ( "" + types.getString () );
    }


    @Test
    public void testParserSimpleMapWithNumber () {

        Object obj = jsonParser.parse ( Map.class,
                " { 'foo': 1 }  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Map || die ( "Object was not a map" );

        Map<String, Object> map = ( Map<String, Object> ) obj;

        System.out.println ( obj );

        ok &= idx ( map, "foo" ).equals ( 1 ) || die ( "I did not find 1" + idx ( map, "foo" ) );
    }


    @Test
    public void testParseFalse () {

        Object obj = jsonParser.parse ( Map.class,
                " { 'foo': false }  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Map || die ( "Object was not a map" );

        Map<String, Object> map = ( Map<String, Object> ) obj;

        System.out.println ( obj );

        ok &= idx ( map, "foo" ).equals ( false ) || die ( "I did not find  false" );
    }

    @Test
    public void testParseNull () {

        Object obj = jsonParser.parse ( Map.class,
                " { 'foo': null }  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Map || die ( "Object was not a map" );

        Map<String, Object> map = ( Map<String, Object> ) obj;

        System.out.println ( obj );

        ok &= idx ( map, "foo" ) == ( null ) || die ( "I did not find null" );
    }

    @Test
    public void testParserSimpleMapWithBoolean () {

        Object obj = jsonParser.parse ( Map.class,
                " { 'foo': true }  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Map || die ( "Object was not a map" );

        Map<String, Object> map = ( Map<String, Object> ) obj;

        System.out.println ( obj );

        ok &= idx ( map, "foo" ).equals ( true ) || die ( "I did not find true" );
    }


    @Test
    public void testParserSimpleMapWithList () {

        Object obj = jsonParser.parse ( Map.class,
                " { 'foo': [0,1,2] }  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Map || die ( "Object was not a map" );

        Map<String, Object> map = ( Map<String, Object> ) obj;

        System.out.println ( obj );

        ok &= idx ( map, "foo" ).equals ( list ( 0, 1, 2 ) ) || die ( "I did not find (0,1,2)" );
    }

    @Test
    public void testParserSimpleMapWithString () {

        Object obj = jsonParser.parse ( Map.class,
                " { 'foo': 'str ' }  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        System.out.println ( "%%%%%%" + obj );

        ok &= obj instanceof Map || die ( "Object was not a map" );

        Map<String, Object> map = ( Map<String, Object> ) obj;

        System.out.println ( obj );

        final Object foo = idx ( map, "foo" );
        ok &= foo.equals ( "str " ) || die ( "I did not find 'str'" + foo );
    }


    @Test
    public void testLists () {
        String[][] testLists = {
                { "emptyList", "[]" },                  //0
                { "emptyList", " [ ]" },                  //1  fails
                { "oddly spaced", "[ 0 , 1 ,2, 3, '99' ]" },   //2
                { "nums and strings", "[ 0 , 1 ,'bar', 'foo', 'baz' ]" }, //3
                { "nums stings map", "[ 0 , 1 ,'bar', 'foo', {'baz':1} ]" }, //4
                { "nums strings map with listStream", "[ 0 , 1 ,'bar', 'foo', {'baz':1, 'lst':[1,2,3]} ]" },//5
                { "nums strings map with listStream", "[ {'bar': {'zed': 1}} , 1 ,'bar', 'foo', {'baz':1, 'lst':[1,2,3]} ]" },//6
                { "tightly spaced", "[0,1,2,3,99]" },

        };

        List<?>[] lists = {
                Collections.EMPTY_LIST,    //0
                Collections.EMPTY_LIST,    //1
                Lists.list ( 0, 1, 2, 3, "99" ),  //2
                Lists.list ( 0, 1, "bar", "foo", "baz" ),//3
                Lists.list ( 0, 1, "bar", "foo", map ( "baz", 1 ) ),//4
                Lists.list ( 0, 1,
                        "bar",
                        "foo",
                        map ( "baz", 1,
                                "lst", list ( 1, 2, 3 )
                        )
                ),//5
                Lists.list ( map ( "bar", map ( "zed", 1 ) ), 1, "bar", "foo", map ( "baz", 1, "lst", list ( 1, 2, 3 ) ) ),//6
                Lists.list ( 0, 1, 2, 3, 99 )
        };

        for ( int index = 0; index < testLists.length; index++ ) {
            String name = testLists[ index ][ 0 ];
            String json = testLists[ index ][ 1 ];

            helper ( name, json, lists[ index ] );
        }
    }


    public void helper ( String name, String json, Object compareTo ) {

        System.out.printf ( "%s, %s, %s", name, json, compareTo );

        Object obj = jsonParser.parse ( Map.class,
                json.replace ( '\'', '"' )
        );

        boolean ok = true;


        System.out.printf ( "\nNAME=%s \n \t parsed obj=%s\n \t json=%s\n \t compareTo=%s\n", name, obj, json, compareTo );
        ok &= compareTo.equals ( obj ) || die ( name + " :: List has items " + json );


    }


    @Test
    public void testNumber () {

        Object obj = jsonParser.parse ( Map.class,
                "1".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Integer || die ( "Object was not an Integer" );

        int i = ( Integer ) obj;

        ok &= i == 1 || die ( "I did see i equal to 1" );

        System.out.println ( obj.getClass () );
    }

    @Test
    public void testBoolean () {

        Object obj = jsonParser.parse ( Map.class,
                "  true  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Boolean || die ( "Object was not a Boolean" );

        boolean value = ( Boolean ) obj;

        ok &= value == true || die ( "I did see value equal to true" );

        System.out.println ( obj.getClass () );
    }

    @Test (expected = JsonException.class)
    public void testBooleanParseError () {

        Object obj = jsonParser.parse ( Map.class,
                "  tbone  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Boolean || die ( "Object was not a Boolean" );

        boolean value = ( Boolean ) obj;

        ok &= value == true || die ( "I did see value equal to true" );

        System.out.println ( obj.getClass () );
    }

    @Test
    public void testString () {

        String testString =
                ( "  'this is all sort of text, " +
                        "   do you think it is \\'cool\\' '" ).replace ( '\'', '"' );


        Object obj = jsonParser.parse ( Map.class, testString );

        System.out.println ( "here is what I got " + obj );

        boolean ok = true;

        ok &= obj instanceof String || die ( "Object was not a String" );

        String value = ( String ) obj;

        assertEquals ( "this is all sort of text,    do you think it is \"cool\" ", obj );

        System.out.println ( obj.getClass () );
    }


    @Test
    public void testStringInsideOfList () {

        String testString = (
                "  [ 'this is all sort of text, " +
                        "   do you think it is \\'cool\\' '] " ).replace ( '\'', '"' );


        Object obj = jsonParser.parse ( Map.class, testString );


        System.out.println ( "here is what I got " + obj );

        boolean ok = true;

        ok &= obj instanceof List || die ( "Object was not a List" );

        List<String> value = ( List<String> ) obj;


        assertEquals ( "this is all sort of text,    do you think it is \"cool\" ",
                Lists.idx ( value, 0 ) );

        System.out.println ( obj.getClass () );
    }

    @Test
    public void testStringInsideOfList2 () {

        String testString =
                "[ 'abc','def' ]".replace ( '\'', '"' );


        Object obj = jsonParser.parse ( Map.class, testString );
        System.out.println ( "here is what I got " + obj );

        boolean ok = true;

        ok &= obj instanceof List || die ( "Object was not a List" );

        List<String> value = ( List<String> ) obj;


        assertEquals ( "abc",
                Lists.idx ( value, 0 ) );

        System.out.println ( obj.getClass () );
    }

    @Test
    public void textInMiddleOfArray () {

        try {
            Object obj = jsonParser.parse ( Map.class,
                    lines ( "[A, 0]"
                    ).replace ( '\'', '"' ).getBytes ( StandardCharsets.UTF_8 )
            );

        } catch ( Exception ex ) {
            //success
            return;
        }
        die ( "The above should cause an exception" );

    }

    @Test
    public void oddlySpaced2 () {

        Object obj = jsonParser.parse ( Map.class,
                lines ( "[   2   ,    1, 0]"
                ).replace ( '\'', '"' )
        );

        boolean ok = true;

        System.out.println ( obj );

    }

    @Test
    public void complex () {


        Object obj = jsonParser.parse ( Map.class,
                lines (

                        "{    'num' : 1   , ",
                        "     'bar' : { 'foo': 1  },  ",
                        "     'nums': [0  ,1,2,3,4,5,'abc'] } "
                ).replace ( '\'', '"' ).getBytes ( StandardCharsets.UTF_8 )
        );

        boolean ok = true;

        System.out.println ( obj );
        //die();
    }

    @Test
    public void bug2 () {


        Object obj = jsonParser.parse ( Map.class,
                lines (

                        "    [ {'bar': {'zed': 1}} , 1]\n "
                ).replace ( '\'', '"' ).getBytes ( StandardCharsets.UTF_8 )
        );

        boolean ok = true;

        System.out.println ( obj );
        //die();
    }

    //{ "PI":3.141E-10}


    @Test
    public void complianceFromJsonSmartForPI () {


        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"PI\":3.141E-10} "
                )
        );


        boolean ok = map.get ( "PI" ).equals ( 3.141E-10 ) || die ( "" + map.get ( "PI" ) );
    }


    @Test
    public void complianceForLowerCaseNumber () {


        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":\"\\u00c1\\u00e1\"}"
                ).replace ( '\'', '"' ).getBytes ( StandardCharsets.UTF_8 )
        );


        boolean ok = map.get ( "v" ).equals ( "Áá" ) || die ( "map " + map.get ( "v" ) );
    }

    @Test
    public void complianceForUpperCaseNumber () {


        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":\"\\u00C1\\u00E1\"}"
                ).replace ( '\'', '"' ).getBytes ( StandardCharsets.UTF_8 )
        );


        boolean ok = map.get ( "v" ).equals ( "Áá" ) || die ( "map " + map.get ( "v" ) );
    }


    @Test
    public void doublePrecisionFloatingPoint () {


        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":1.7976931348623157E308}"
                ).replace ( '\'', '"' ).getBytes ( StandardCharsets.UTF_8 )
        );


        boolean ok = map.get ( "v" ).equals ( 1.7976931348623157E308 ) || die ( "map " + map.get ( "v" ) );
    }

    //


    @Test (expected = JsonException.class)
    public void doubleQuoteInsideOfSingleQuote () {


        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":'ab\"c'}"
                )
        );

    }

    @Test (expected = JsonException.class)
    public void supportSimpleQuoteInNonProtectedStringValue () {

        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":It's'Work}"
                )
        );
    }

    @Test (expected = JsonException.class)
    public void supportNonProtectedStrings () {
        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ a:1234}"
                )
        );

    }

    @Test (expected = JsonException.class)
    public void crapInAnArray () {
        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "[ a,bc]"
                )
        );

    }


    @Test (expected = JsonException.class)
    public void randomStringAsValuesWithSpaces () {
        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":s1 s2}"
                )
        );

    }


    @Test (expected = JsonException.class)
    public void randomStringAsValuesWithSpaceAndMoreSpaces () {
        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":s1 s2 }"
                )
        );

    }


    @Test ()
    public void garbageAtEndOfString () {
        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"a\":\"foo.bar\"}#toto"
                )
        );
        puts ( map );
    }


    @Test (expected = JsonException.class)
    public void singleQuotes () {
        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ 'value':'string'}"
                )
        );

    }


}
