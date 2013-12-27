package org.boon.json;

import org.boon.json.implementation.JsonSerializerImpl;
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
        String sRick = new JsonSerializerImpl ()
                .serialize ( rick ).toString ();
        boolean ok = sRick.equals ( "{\"name\":\"Rick\"}" ) || die ( sRick );
    }

    @Test
    public void testWithType () {

        Employee rick = new Employee ();
        String sRick = new JsonSerializerFactory ().useFields ().setOutputType ( true ).create ()
                .serialize ( rick ).toString ();
        boolean ok = sRick.equals ( "{\"class\":\"org.boon.json.JsonSerializeTest$Employee\",\"name\":\"Rick\"}" ) || die ( sRick );
    }



}
