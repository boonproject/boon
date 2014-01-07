package org.boon.json;

import org.boon.Lists;
import org.boon.core.reflection.fields.FieldAccessMode;
import org.boon.json.implementation.JsonParserLax;
import org.boon.utils.DateUtils;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.boon.Boon.putl;
import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Maps.idx;
import static org.boon.Str.lines;

/**
 * Created by rick on 12/12/13.
 * <p/>
 * Make sure it can handle these
 * https://code.google.com/p/json-smart/wiki/FeaturesTests
 */
public class JsonLaxTest extends JsonParserBaseTest {


    public JsonParser parser () {
        return new JsonParserLax ( FieldAccessMode.create( FieldAccessMode.FIELD ), false, true, true );

    }

    public JsonParser objectParser () {
        return new JsonParserLax ( FieldAccessMode.create( FieldAccessMode.FIELD ), true, false, false );

    }

    @Test
    public void testLax () {

        Object obj = jsonParser.parse ( Map.class,
                " {foo: hi mom hi dad how are you? }  "
        );

        boolean ok = true;

        ok &= obj instanceof Map || die ( "Object was not a map" + obj );

        Map<String, Object> map = ( Map<String, Object> ) obj;

        System.out.println ( obj );

        System.out.println ( idx ( map, "foo" ) );

        inspectMap ( map );

        ok &= idx ( map, "foo" ).equals ( "hi mom hi dad how are you?" ) || die ( "I did not find:" + idx ( map, "foo" ) + "#" );


    }

    @Test
    public void testComment () {

        String testString = " {foo:\"bar\", //hi mom \n" +
                " foo2:baz }  ";

        Map<String, Object> map = jsonParser.parse ( Map.class, testString );

        puts ( "map = " + map );

        inspectMap ( map );

        boolean ok = idx ( map, "foo" ).equals ( "bar" ) || die ( "I did not find:" + idx ( map, "foo" ) + "#" );
        ok = idx ( map, "foo2" ).equals ( "baz" ) || die ( "I did not find:" + idx ( map, "foo2" ) + "#" );


    }

    @Test
    public void testComment2 () {

        String testString = " {foo:bar, #hi mom \n" +
                " foo2:baz }  ";

        Map<String, Object> map = jsonParser.parse ( Map.class, testString );

        puts ( "map = " + map );
        inspectMap ( map );

        boolean ok = idx ( map, "foo" ).equals ( "bar" ) || die ( "I did not find:" + idx ( map, "foo" ) + "#" );
        ok = idx ( map, "foo2" ).equals ( "baz" ) || die ( "I did not find:" + idx ( map, "foo2" ) + "#" );


    }

    @Test
    public void testComment3 () {

        String testString = " {foo:bar, /* hi mom */" +
                " foo2:baz }  ";

        Map<String, Object> map = jsonParser.parse ( Map.class, testString );

        puts ( "map = " + map );

        boolean ok = idx ( map, "foo" ).equals ( "bar" ) || die ( "I did not find:" + idx ( map, "foo" ) + "#" );
        ok = idx ( map, "foo2" ).equals ( "baz" ) || die ( "I did not find:" + idx ( map, "foo2" ) + "#" );


    }

    @Test
    public void testLax2 () {

        String testString = " {foo: hi mom hi dad how are you?,\n" +
                "thanks:I am good thanks for asking,\t\n" +
                "list:[love, rocket, fire],\t" +
                " num:1, " +
                "mix: [ true, false, 1, 2, blue, true\n,\t,false\t,foo\n,], }  ";
        Object obj = jsonParser.parse ( Map.class, testString
        );

        puts ( testString );


        boolean ok = true;

        ok &= obj instanceof Map || die ( "Object was not a map" );

        Map<String, Object> map = ( Map<String, Object> ) obj;

        puts ( map );

        inspectMap ( map );


        ok &= idx ( map, "foo" ).equals ( "hi mom hi dad how are you?" ) || die ( "I did not find:" + idx ( map, "foo" ) + "#" );
        ok &= idx ( map, "thanks" ).equals ( "I am good thanks for asking" ) || die ( "I did not find:" + idx ( map, "foo" ) + "#" );


        List<Object> list = ( List<Object> ) idx ( map, "list" );


        ok &= Lists.idx ( list, 0 ).equals ( "love" ) || die ( "I did not find love:" + Lists.idx ( list, 0 ) );


        ok &= Lists.idx ( list, 1 ).equals ( "rocket" ) || die ( "I did not find rocket:" + Lists.idx ( list, 1 ) );

        ok &= Lists.idx ( list, 2 ).equals ( "fire" ) || die ( "I did not find fire:" + Lists.idx ( list, 2 ) );

        ok &= idx ( map, "num" ).equals ( 1 ) || die ( "I did not find 1:" + idx ( map, "num" ) + "#" );


        List<Object> mix = ( List<Object> ) idx ( map, "mix" );


        ok &= Lists.idx ( mix, 0 ).equals ( true ) || die ( "I did not find true:" + Lists.idx ( mix, 0 ) );
        ok &= Lists.idx ( mix, 1 ).equals ( false ) || die ( "I did not find false:" + Lists.idx ( mix, 1 ) );


        ok &= Lists.idx ( mix, 2 ).equals ( 1 ) || die ( "I did not find 1:" + Lists.idx ( mix, 2 ) );


        ok &= Lists.idx ( mix, 4 ).equals ( "blue" ) || die ( "I did not find blue:" + Lists.idx ( mix, 3 ) );

        puts ( "testLax2?", ok );

    }

    @Test
    public void testLax3 () {

        String testString = "/* in theory you can put a comment here. */ " +
                " {foo: hi mom hi dad how are you?, //here too\n" +
                "thanks:I am good thanks for asking, #I hear you can do it here\t\n" +
                "list:[love, rocket, fire],\t" +
                " num:1, " +
                "mix: [ true, false, 1, 2, blue, true\n,\t,false\t,foo\n,], " +
                "date: \"1994-11-05T08:15:30Z\" } ";
        Object obj = jsonParser.parse ( Map.class, testString
        );

        puts ( testString );


        boolean ok = true;

        ok &= obj instanceof Map || die ( "Object was not a map" );

        Map<String, Object> map = ( Map<String, Object> ) obj;

        puts ( map );


        String dateGMTString = DateUtils.getGMTString((Date) idx ( map, "date" ));
        ok &= dateGMTString.equals ( "05/11/94 08:15" ) || die ( "I did not find:" + dateGMTString + "#" );

        ok &= idx ( map, "foo" ).equals ( "hi mom hi dad how are you?" ) || die ( "I did not find:" + idx ( map, "foo" ) + "#" );
        ok &= idx ( map, "thanks" ).equals ( "I am good thanks for asking" ) || die ( "I did not find:" + idx ( map, "foo" ) + "#" );


        List<Object> list = ( List<Object> ) idx ( map, "list" );


        ok &= Lists.idx ( list, 0 ).equals ( "love" ) || die ( "I did not find love:" + Lists.idx ( list, 0 ) );


        ok &= Lists.idx ( list, 1 ).equals ( "rocket" ) || die ( "I did not find rocket:" + Lists.idx ( list, 1 ) );

        ok &= Lists.idx ( list, 2 ).equals ( "fire" ) || die ( "I did not find fire:" + Lists.idx ( list, 2 ) );

        ok &= idx ( map, "num" ).equals ( 1 ) || die ( "I did not find 1:" + idx ( map, "num" ) + "#" );


        List<Object> mix = ( List<Object> ) idx ( map, "mix" );


        ok &= Lists.idx ( mix, 0 ).equals ( true ) || die ( "I did not find true:" + Lists.idx ( mix, 0 ) );
        ok &= Lists.idx ( mix, 1 ).equals ( false ) || die ( "I did not find false:" + Lists.idx ( mix, 1 ) );


        ok &= Lists.idx ( mix, 2 ).equals ( 1 ) || die ( "I did not find 1:" + Lists.idx ( mix, 2 ) );


        ok &= Lists.idx ( mix, 4 ).equals ( "blue" ) || die ( "I did not find blue:" + Lists.idx ( mix, 3 ) );

        puts ( "testLax2?", ok );

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

    }


    @Test ()
    public void testBooleanParseError () {

        Object obj = jsonParser.parse ( Map.class,
                "  tbone  "
        );

    }


    @Test ()
    public void doubleQuoteInsideOfSingleQuote () {


        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":'ab\"c'}"
                )
        );

    }

    @Test ()
    public void supportSimpleQuoteInNonProtectedStringValue () {

        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":It's'Work}"
                )
        );
    }

    @Test ()
    public void supportNonProtectedStrings () {
        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ a:1234}"
                )
        );

    }

    @Test ()
    public void crapInAnArray () {
        jsonParser.parse ( Map.class,
                lines (

                        "[ a,bc]"
                )
        );

    }


    @Test ()
    public void randomStringAsValuesWithSpaces () {
        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":s1 s2}"
                )
        );

    }


    @Test ()
    public void randomStringAsValuesWithSpaceAndMoreSpaces () {
        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ \"v\":s1 s2 }"
                )
        );

    }


    @Test ()
    public void singleQuotes () {
        Map<String, Object> map = ( Map<String, Object> ) jsonParser.parse ( Map.class,
                lines (

                        "{ 'value':'string'}"
                )
        );

        boolean ok = idx ( map, "value" ).equals ( "string" ) || die ();


    }


}
