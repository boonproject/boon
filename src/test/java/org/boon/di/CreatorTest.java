package org.boon.di;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Maps.map;
import static org.boon.di.Creator.create;
import static org.boon.di.Creator.newOf;

import static org.boon.json.JsonCreator.*;

import static org.boon.di.ContextFactory.*;

import org.boon.core.Supplier;
import org.junit.Test;

import java.util.Map;

/**
 * Created by Richard on 2/4/14.
 */
public class CreatorTest {



    @Named
    public static class SomeClass {

        String someField = "someclass";

    }


    public static class Bar {

        String name = "bar";

    }

    public static class Foo {

        /* Works with standard @Inject or boon @Inject or Guice/Dagger Inject or Spring @Autowire*/
        @In("bar")
        Bar bar;

        /*Works with Boon @Id, @Named also can use @Named form CDI
        or Guice or use @Qualifer from Spring works with all
         */
        @Inject @Named("someClass")
        SomeClass object;


        @Inject
        SomeClass object2;

    }


    public static class Foo2 {

        /* Works with standard @Inject or boon @Inject or Guice/Dagger Inject or Spring @Autowire*/
        @Inject
        Bar bar;

        /*Works with Boon @Id, @Named also can use @Named form CDI
        or Guice or use @Qualifer from Spring works with all
         */
        @Inject @Named("someClass")
        SomeClass object;


        @Inject
        SomeClass object2;

    }


    Class<Foo> fooType = Foo.class;

    Class<Foo2> fooType2 = Foo2.class;

    @Test
    public void testCreateWithInstance1() {

        Bar bar = new Bar();
        bar.name = "baz";
        Map myMap = map( "bar", bar );

        Foo foo = create( fooType, myMap );

        boolean ok = foo != null || die();
        ok = foo.bar != null || die();
        ok = foo.bar.name.equals( "baz" )  || die();

    }



    @Test
    public void testCreateWithInstance() {

        Bar bar = new Bar();
        bar.name = "baz";
        Map myMap = map( "bar", bar );

        Foo foo = create( fooType, map( "bar", bar ) );

        boolean ok = foo != null || die();
        ok = foo.bar != null || die();
        ok = foo.bar.name.equals( "baz" )  || die();

    }



    @Test
    public void testCreateWithInstance2() {

        Bar bar = new Bar();
        bar.name = "baz";

        Foo foo = newOf( fooType, "bar", bar );

        boolean ok = foo != null || die();
        ok = foo.bar != null || die();
        ok = foo.bar.name.equals( "baz" )  || die();

    }



    @Test
    public void testCreateWithInstance3() {

        Bar bar = new Bar();
        bar.name = "baz";

        Foo foo = newOf( fooType, "bar", bar );

        boolean ok = foo != null || die();
        ok = foo.bar != null || die();
        ok = foo.bar.name.equals( "baz" )  || die();

    }



    @Test
    public void testCreateWithInstance4() {


        Foo foo = newOf( fooType,
                "bar",
                map("class",
                "org.boon.di.CreatorTest$Bar", "name", "barFromMap") );

        boolean ok = foo != null || die();
        ok = foo.bar != null || die();
        ok = foo.bar.name.equals( "barFromMap" )  || die();

    }



    @Test
    public void testCreateWithInstance5() {

        Foo foo = createFromJsonMap( fooType, "{'bar':{'class':'org.boon.di.CreatorTest$Bar', 'name': 'barFromJson'}}" );
        boolean ok = foo != null || die();
        ok = foo.bar != null || die();
        ok = foo.bar.name.equals( "barFromJson" )  || die();

    }





    @Test
    public void testCreateWithInstance6() {


        Foo foo = create( fooType,
                map("bar",
                map("class",
                        "org.boon.di.CreatorTest$Bar", "name", "barFromMap") ));

        boolean ok = foo != null || die();
        ok = foo.bar != null || die();
        ok = foo.bar.name.equals( "barFromMap" )  || die();

    }



    @Test
    public void testCreateWithInstance7() {

        Foo foo = createFromJsonMapResource( fooType, "classpath://config/config.json" );
        boolean ok = foo != null || die();
        ok = foo.bar != null || die();
        ok = foo.bar.name.equals( "barFromJsonResource" )  || die();

    }



    @Test
    public void testCreateWithInstance8() {

        Foo foo = createFromJsonMapResource( fooType, "classpath://config/" );
        boolean ok = foo != null || die();
        ok = foo.bar != null || die();
        ok = foo.bar.name.equals( "barFromJsonResource" )  || die();

    }

    @Test
    public void testCreateWithInstance9() {

        Context context = createContextFromJSONResources(  "classpath://config/", "classpath://fooConfig/" );
        Foo foo = (Foo) context.get( "foo" );
        boolean ok = foo != null || die();
        ok = foo.bar != null || die();
        ok = foo.bar.name.equals( "barFromJsonResource" )  || die();

    }

    @Test
    public void testCreateWithClass() {

        Foo foo = create( fooType, map( "bar", Bar.class ) );

        boolean ok = foo != null || die();
        ok = foo.bar != null || die();
        ok = foo.bar.name.equals( "bar" )  || die();

    }



    public static class BarProvider implements Supplier<Bar> {

        @Override
        public Bar get() {
            Bar bar = new Bar();
            bar.name = "fromProvider";
            return bar;
        }
    }

    @Test
    public void testCreateWithProvider() {

        Foo foo = create( fooType, map( "bar", new BarProvider() ) );

        boolean ok = foo != null || die();
        ok = foo.bar != null || die();
        ok = foo.bar.name.equals( "fromProvider" )  || die();

    }


    @Test
    public void testCreateWithContext() {

        Context context = context( classes( Foo2.class, Bar.class ) );
        Foo2 foo = create( fooType2, context  );

        boolean ok = foo != null || die();
        ok = foo.bar != null || die();
        ok = foo.bar.name.equals( "bar" )  || die();

    }



    @Test
    public void testCreateWithStuff() {

        Foo foo = newOf( fooType, "bar", Bar.class,
                "someClass", new SomeClass(),
                "object2", SomeClass.class );

        boolean ok = foo != null || die();
        ok = foo.bar != null || die();

        ok = foo.object != null || die();
        ok = foo.object2 != null || die();

        ok = foo.object != foo.object2 || die();

        ok = foo.bar.name.equals( "bar" )  || die();

    }





    @Test
    public void testCreateWithStuff2() {

        Foo foo = newOf( fooType, "bar", Bar.class,
                "someClass", new SomeClass() );

        boolean ok = foo != null || die();
        ok = foo.bar != null || die();

        ok = foo.object != null || die();
        ok = foo.object2 != null || die();

        //One was injected by type and one by name, but it is the same object
        ok = foo.object == foo.object2 || die();


        ok = foo.bar.name.equals( "bar" )  || die();

    }



    @Test
    public void namedConfig() {


        Context context = configFromNameSpace("dev", "classpath://config_files/");
        Bar bar = context.get( Bar.class );
        boolean ok = bar != null || die();
        puts(bar.name);

    }

}
