package org.boon.json;

import org.boon.Lists;
import org.boon.core.reflection.Reflection;
import org.boon.json.implementation.JsonSerializerImpl;
import org.boon.json.implementation.JsonStringDecoder;
import org.junit.Test;

import java.util.Date;

import static org.boon.Boon.puts;
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


    @Test
    public void testBug() {
        Employee rick = new Employee ();

        JsonSerializer serializer =  new JsonSerializerFactory()
                .setIncludeEmpty ( true ).setUseAnnotations ( false )
                .setCacheInstances ( false )
                .setIncludeNulls ( true )
                .create ();

        String sRick = serializer.serialize ( rick ).toString ();

        puts (sRick);

        AllTypes foo = new AllTypes ();
        foo.ingnoreMe = "THIS WILL NOT PASS";
        foo.ignoreMe2 = "THIS WILL NOT PASS EITHER";
        foo.ignoreMe3 = "THIS WILL NOT PASS TOO";

        foo.setDate ( new Date () );
        foo.setBar ( FooEnum.BAR );
        foo.setFoo ( FooEnum.FOO );
        foo.setString ( "Hi Mom" );
        AllTypes foo2 = Reflection.copy ( foo );
        foo.setAllType ( foo2 );
        foo2.setString ( "Hi Dad" );
        foo.setAllTypes ( Lists.list ( Reflection.copy ( foo2 ), Reflection.copy ( foo2 ) ) );


        String sFoo = serializer.serialize ( foo ).toString ();
        puts (sFoo);
    }


    @Test public void bug2 () {

        Employee rick = new Employee ();

        JsonSerializer serializer = new JsonSerializerFactory()
                .setIncludeEmpty ( true ).setUseAnnotations ( false )
                .setCacheInstances ( false )
                .create ();

        String sRick = serializer.serialize ( rick ).toString ();

        puts (sRick);

        AllTypes foo = new AllTypes ();
        foo.ingnoreMe = "THIS WILL NOT PASS";
        foo.ignoreMe2 = "THIS WILL NOT PASS EITHER";
        foo.ignoreMe3 = "THIS WILL NOT PASS TOO";

        foo.setDate ( new Date () );
        foo.setBar ( FooEnum.BAR );
        foo.setFoo ( FooEnum.FOO );
        foo.setString ( "Hi Mom" );
        AllTypes foo2 = Reflection.copy ( foo );
        foo.setAllType ( foo2 );
        foo2.setString ( "Hi Dad" );
        foo.setAllTypes ( Lists.list ( Reflection.copy ( foo2 ), Reflection.copy ( foo2 ) ) );


        String sFoo = serializer.serialize ( foo ).toString ();
        puts (sFoo);

    }


}
