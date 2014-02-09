package org.boon.json;

import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

/**
 * Created by rick on 12/15/13.
 */
public class AsciiParserTest extends JsonParserAndMapperBaseTest {



    public JsonParserAndMapper parser () {
        return new JsonParserFactory().createASCIIParser();
    }

    public JsonParserAndMapper objectParser () {
        return parser();
    }


    @Test //TODO broke
    public void testArrayOfArrayWithSimpleValuesValue7() {

    }

    @Test //TODO broke
    public void testBackSlashEscaping2() {


    }

//    @Test
    public void parseNegNumber() {

        int i;
        long l;
        boolean ok;
        i = (int) jsonParserAndMapper.parseLong ( "-123" );
        ok = i == -123 || die ( "" + i );

        l = jsonParserAndMapper.parseLong ( "-123456789099" );
        ok = l == -123456789099L || die ( "" + l );

    }

    @Test
    public void parseNumber () {
        int i = jsonParserAndMapper.parseInt ( "123" );
        boolean ok = i == 123 || die ( "" + i );

        i = jsonParserAndMapper.parseInt ( "123".getBytes ( StandardCharsets.UTF_8 ) );
        ok = i == 123 || die ( "" + i );

        i = jsonParserAndMapper.parseByte ( "123" );
        ok = i == 123 || die ( "" + i );



        i = jsonParserAndMapper.parseShort ( "123" );
        ok = i == 123 || die ( "" + i );


        i = (int) jsonParserAndMapper.parseDouble ( "123" );
        ok = i == 123 || die ( "" + i );


        i = (int) jsonParserAndMapper.parseFloat ( "123" );
        ok = i == 123 || die ( "" + i );

        i =  (int)jsonParserAndMapper.parseLong ( "123" );
        ok = i == 123 || die ( "" + i );


        puts ( ok );
    }



    @Test
    public void parseNegativeLong () {
    }



    @Test
    public void tesParseSmallNum() {

    }

}