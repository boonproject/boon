package org.boon.json;

import org.boon.json.implementation.JsonSerializer;
import org.boon.json.implementation.JsonStringDecoder;
import org.junit.Test;

import static org.boon.Exceptions.die;

/**
 * Created by rick on 12/18/13.
 */
public class JsonSerializeTest {

    public static class Employee {

        String name = "Rick";

        public String getName () {
            return name;
        }

        public void setName ( String name ) {
            this.name = name;
        }
    }

    @Test
    public void test () {

        Employee rick = new Employee ();
        String sRick = new JsonSerializer ()
                .serialize ( rick ).toString ();
        boolean ok = sRick.equals ( "{\"name\":\"Rick\"}" ) || die ( sRick );
    }

    @Test
    public void testWithType () {

        Employee rick = new Employee ();
        String sRick = new JsonSerializer ( true )
                .serialize ( rick ).toString ();
        boolean ok = sRick.equals ( "{\"class\":\"org.boon.json.JsonSerializeTest$Employee\",\"name\":\"Rick\"}" ) || die ( sRick );
    }

    @Test
    public void stringUnicodeEncoderTest () throws Exception {
        String str = "§¾”–king bad~\u007f\u0080~"; //Range checking
        JsonSerializer encoder = new JsonSerializer ();
        // boolean ok = "\"\\u00DF\\u00E6\\u00E7\\u00EE\\u00F1king bad~\u007f\\u0080~\""
        //         .toString ().equals ( encoder.serialize ( str ) ) || die ();

    }

    @Test
    public void stringOtherEncoderTest () throws Exception {
        String str = "\\/\b\f\r\n\t";
        JsonSerializer encoder = new JsonSerializer ();


        JsonStringDecoder decoder = new JsonStringDecoder ();

        String str2 = encoder.serialize ( str ).toString ();
        System.out.println ( decoder.decode ( str2 ) );

        //boolean ok = "\"\\\\\\/\\b\\f\\r\\n\\t\"".equals ( encoder.serialize ( str ) );

    }


}
