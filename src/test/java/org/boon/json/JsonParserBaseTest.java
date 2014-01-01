package org.boon.json;

import org.boon.IO;
import org.boon.Lists;
import org.boon.core.Conversions;
import org.boon.core.Dates;
import org.boon.core.Function;
import org.boon.core.reflection.Reflection;
import org.boon.json.implementation.JsonSimpleSerializerImpl;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.boon.Boon.putl;
import static org.boon.Boon.puts;
import static org.boon.Boon.sputs;
import static org.boon.Exceptions.die;
import static org.boon.Lists.list;
import static org.boon.Maps.idx;
import static org.boon.Maps.map;
import static org.boon.Str.lines;
import static org.junit.Assert.assertEquals;

public class JsonParserBaseTest {


    JsonParser jsonParser;


    protected void inspectMap ( Map<String, Object> map ) {
        final Set<Map.Entry<String, Object>> entrySet = map.entrySet ();
        putl ( "map", map, "size", map.size (), "keys", map.keySet (), "values", map.values () );

        for ( String key : map.keySet () ) {
            puts ( "key", "#" + key + "#" );
        }

        for ( Object value : map.values () ) {
            puts ( "value", "#" + value + "#" );
        }

    }


    public JsonParserFactory factory () {
        return new JsonParserFactory ();
    }

    public JsonParser parser () {
        return factory ().create ();
    }

    public JsonParser objectParser () {
        return factory ().create ();
    }


    @Before
    public void setup () {

        jsonParser = parser ();

    }



    @Test
    public void roundTrip() {
        AllTypes foo = new AllTypes ();
        foo.ingnoreMe = "THIS WILL NOT PASS";
        foo.ignoreMe2 = "THIS WILL NOT PASS EITHER";
        foo.ignoreMe3 = "THIS WILL NOT PASS TOO";

        foo.setDate ( new Date() );
        foo.setBar ( FooEnum.BAR );
        foo.setFoo ( FooEnum.FOO );
        foo.setString ( "Hi Mom" );
        AllTypes foo2 = Reflection.copy ( foo );
        foo.setAllType ( foo2 );
        foo2.setString ( "Hi Dad" );
        foo.setAllTypes ( Lists.list(Reflection.copy ( foo2 ), Reflection.copy(foo2)) );

        final JsonSerializer serializer = new JsonSerializerFactory ()
                .useAnnotations ()
                .addFilter ( new Function<FieldSerializationData, Boolean> () {
            @Override
            public Boolean apply ( FieldSerializationData fieldSerializationData ) {
                if (fieldSerializationData.fieldName.equals (  "ignoreMe3" ) ) {
                    return true;
                } else {
                    return false;
                }
            }
        } ).addPropertySerializer ( new Function<FieldSerializationData, Boolean> () {
            @Override
            public Boolean apply ( FieldSerializationData fieldSerializationData ) {

                if ( fieldSerializationData.type.equals ( long.class ) &&
                        fieldSerializationData.fieldName.endsWith ( "Date" ) ) {

                    Date date = Conversions.toDate ( fieldSerializationData.value );

                    final String jsonDateString = Dates.jsonDate ( date );

                    fieldSerializationData.output.add ( jsonDateString);
                    return true;
                } else {
                    return false;
                }

            }
        } ).addTypeSerializer ( FooBasket.class, new Function<ObjectSerializationData, Boolean> () {
            @Override
            public Boolean apply ( ObjectSerializationData objectSerializationData ) {
                objectSerializationData.output.add("[\"wiki\",\"wiki\",\"wiki\"]");
                return true;
            }
        } )
                .create ();
        String json = serializer.serialize ( foo ).toString ();

        boolean ok = json.contains  ("[\"wiki\",\"wiki\",\"wiki\"]" ) || die();


        puts (json);
        AllTypes testMe = jsonParser.parse( AllTypes.class, json);

         ok |= testMe.equals ( foo ) || die();




        ok |= testMe.ingnoreMe == null || die();

        puts (testMe.ignoreMe2);
        ok |= testMe.ignoreMe2 == null || die();

        puts (testMe.ignoreMe3);
        ok |= testMe.ignoreMe3 == null || die();

        ok |= testMe.someDate > 0 || die();

    }




    @Test
    public void roundTrip2() {
        AllTypes foo = new AllTypes ();
        foo.ingnoreMe = "THIS WILL NOT PASS";
        foo.ignoreMe2 = "THIS WILL NOT PASS EITHER";
        foo.ignoreMe3 = "THIS WILL NOT PASS TOO";

        foo.setDate ( new Date() );
        foo.setBar ( FooEnum.BAR );
        foo.setFoo ( FooEnum.FOO );
        foo.setString ( "Hi Mom" );
        AllTypes foo2 = Reflection.copy ( foo );
        foo.setAllType ( foo2 );
        foo2.setString ( "Hi Dad" );
        foo.setAllTypes ( Lists.list(Reflection.copy ( foo2 ), Reflection.copy(foo2)) );

        final JsonSerializer serializer = new JsonSerializerFactory ().create ();

        String json = serializer.serialize ( foo ).toString ();

        boolean ok = true;

        puts (json);
        AllTypes testMe = jsonParser.parse( AllTypes.class, json);

        ok |= testMe.equals ( foo ) || die();


    }

    @Test
    public void testParserSimpleMapWithNumber () {

        Object obj = jsonParser.parse ( Map.class,
                " { 'foo': 1 }  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Map || die ( "Object was not a map" );

        Map<String, Object> map = ( Map<String, Object> ) obj;

        inspectMap ( map );

        System.out.println ( obj );

        puts ( idx ( map, "foo" ).getClass () );

        ok &= idx ( map, "foo" ).equals ( 1 ) || die ( "I did not find 1 " + idx ( map, "foo" ) );
    }


    @Test
    public void objectSerialization () {


        String fileContents = IO.read ( "files/AllTypes.json" );

        AllTypes types = objectParser ().parse ( AllTypes.class, fileContents );



        puts ( types );
        validateAllTypes ( types );

        validateAllTypes ( types.getAllType () );

        boolean ok = true;


        //        puts ("################", types.getBigDecimal (), types.getDate (), types.getBigInteger ());

        ok |= types.getBigDecimal ().equals ( new BigDecimal ( "99" ) ) || die();

        ok |= types.getBigInteger ().equals ( new BigInteger ( "101" ) ) || die();

        ok |= types.getDate().toString().startsWith ( "Fri Dec 1" ) || die("" + types.getDate());
        ok |= types.getFoo ().toString().equals ( "FOO" ) || die();
        ok |= types.getBar ().toString().equals ( "BAR" ) || die();

        ok |= types.getAllTypes ().size () == 3 || die ( "" + types.getAllTypes ().size () );

        for ( AllTypes allType : types.getAllTypes () ) {
            validateAllTypes ( allType );
        }

    }

    @Test
    public void objectSerializationList () {


        String fileContents = IO.read ( "files/arrayOfAllType.json" );

        List<AllTypes> types = objectParser ().parseList ( AllTypes.class, fileContents );

        puts (types);

    }

    @Test
    public void testFiles () {


        final List<String> list = IO.listByExt ( "files", ".json" );

        for ( String file : list ) {


            puts ( "testing", file );

            Object object =  jsonParser.parse ( IO.read ( file ) );
            //puts ( "FILE _________\n\n\n", file, object.getClass (), object);



        }
        puts ("done");

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

        Object obj = jsonParser.parse ( Integer.class,
                "1".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Integer || die ( "Object was not an Integer " + obj + " " + obj.getClass () );

        int i = ( Integer ) obj;

        ok &= i == 1 || die ( "I did see i equal to 1" );

        System.out.println ( obj.getClass () );
    }

    @Test
    public void testBoolean () {

        Object obj = jsonParser.parse ( Boolean.class,
                "  true  ".replace ( '\'', '"' )
        );

        boolean ok = true;

        ok &= obj instanceof Boolean || die ( "Object was not a Boolean" );

        boolean value = ( Boolean ) obj;

        ok &= value == true || die ( "I did see value equal to true" );

        System.out.println ( obj.getClass () );
    }

    @Test ( expected = JsonException.class )
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


        Object obj = jsonParser.parse ( String.class, testString );

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
    public void parseArray () {

        String testString = "[0, 2, 4, 8, 16]";
        int [] compareArray = {0, 2, 4, 8, 16};
        long [] compareLongArray = {0L, 2L, 4L, 8L, 16L};
        byte [] compareByteArray = {0, 2, 4, 8, 16};
        short [] compareShortArray = {0, 2, 4, 8, 16};
        float [] compareFloatArray = {0, 2, 4, 8, 16};
        double [] compareDoubleArray = {0, 2, 4, 8, 16};

        final int[] array = jsonParser.parseIntArray ( testString );

        boolean ok = Arrays.equals (compareArray, array) || die( sputs(array));


        final long[] larray = jsonParser.parseLongArray ( testString );

        ok = Arrays.equals (compareLongArray, larray) || die( sputs(larray));


        final byte[] barray = jsonParser.parseByteArray ( testString );
        ok |= Arrays.equals (compareByteArray, barray) || die( sputs(barray));

        final short[] sarray = jsonParser.parseShortArray ( testString );
        ok |= Arrays.equals (compareShortArray, sarray) || die( sputs(sarray));

        final float[] farray = jsonParser.parseFloatArray ( testString );
        ok |= Arrays.equals (compareFloatArray, farray) || die( sputs(farray));

        final double[] darray = jsonParser.parseDoubleArray ( testString );
        ok |= Arrays.equals (compareDoubleArray, darray) || die( sputs(darray));

        puts ("parseArray", ok);

    }

    @Test
    public void parseNumber () {
        int i = jsonParser.parseInt ( "123" );
        boolean ok = i == 123 || die ( "" + i );

        i = jsonParser.parseInt ( "123".getBytes ( StandardCharsets.UTF_8 ) );
        ok = i == 123 || die ( "" + i );

        i = jsonParser.parseByte ( "123" );
        ok = i == 123 || die ( "" + i );



        i = jsonParser.parseShort ( "123" );
        ok = i == 123 || die ( "" + i );


        i = (int)jsonParser.parseDouble ( "123" );
        ok = i == 123 || die ( "" + i );


        i = (int)jsonParser.parseFloat ( "123" );
        ok = i == 123 || die ( "" + i );

        i = (int)jsonParser.parseLong ( "123" );
        ok = i == 123 || die ( "" + i );

        puts ( ok );
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
                )
        );

        puts ( map );
        boolean ok = map.get ( "v" ).equals ( "Áá" ) || die ( "map " + map.get ( "v" ) );
    }

    @Test
    public void complianceForUpperCaseNumber () {


        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":\"\\u00C1\\u00E1\"}"
                )
        );


        puts ( map );
        boolean ok = map.get ( "v" ).equals ( "Áá" ) || die ( "map " + map.get ( "v" ) );
    }


    @Test
    public void doublePrecisionFloatingPoint () {


        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":1.7976931348623157E308}"
                )
        );


        boolean ok = map.get ( "v" ).equals ( 1.7976931348623157E308 ) || die ( "map " + map.get ( "v" ) );
    }

    //


    @Test ( expected = JsonException.class )
    public void doubleQuoteInsideOfSingleQuote () {


        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":'ab\"c'}"
                )
        );

    }

    @Test ( expected = JsonException.class )
    public void supportSimpleQuoteInNonProtectedStringValue () {

        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":It's'Work}"
                )
        );
    }

    @Test ( expected = JsonException.class )
    public void supportNonProtectedStrings () {
        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ a:1234}"
                )
        );

    }

    @Test ( expected = JsonException.class )
    public void crapInAnArray () {
        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "[ a,bc]"
                )
        );

    }


    @Test ( expected = JsonException.class )
    public void randomStringAsValuesWithSpaces () {
        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":s1 s2}"
                )
        );

    }


    @Test ( expected = JsonException.class )
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


    @Test ( expected = JsonException.class )
    public void singleQuotes () {
        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ 'value':'string'}"
                )
        );

    }



    @Test
    public void simpleFloat () {


        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":1.1}"
                )
        );


        boolean ok = map.get ( "v" ).equals ( 1.1 ) || die ( "map " + map.get ( "v" ) );
    }


}
