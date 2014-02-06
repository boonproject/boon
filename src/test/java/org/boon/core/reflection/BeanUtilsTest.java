package org.boon.core.reflection;

import org.boon.Lists;
import org.junit.Test;

import java.util.List;

import static org.boon.Exceptions.die;

public class BeanUtilsTest {

    public static class TestClass {
        private String id="foo";
        private  Long time;
        TestClass child =null;

        void init()
        {
            child = new TestClass ();

            child.id = "child";
            child.time = 1L;
        }
        List<Player> players = Player.players (
                Player.player ( "1", "Rick", "Hightower"  ),
                Player.player ( "2", "Diana", "Hightower"  ) );
    }



    public static class TestPrime {
        private String id="bar";
        private  long time;

        TestPrime child;
        List<String> players;

    }





    public static class Player {

        private final String id;
        private final String firstName;
        private final String lastName;



        private Player( final String nflId, final String firstName, final String lastName ) {
            this.id = nflId;
            this.firstName = firstName;
            this.lastName = lastName;
        }


        public static Player player(final String id, final String firstName, final String lastName) {
            return new Player ( id, firstName, lastName );
        }


        public static Player player() {
            return Reflection.newInstance ( Player.class );
        }

        public static List<Player> players( Player... players ) {
            return Lists.list ( players ) ;
        }




        public String id() {
            return id;
        }


        public String firstName() {
            return firstName;
        }

        public String lastName() {
            return lastName;
        }


        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass () != o.getClass () ) return false;

            Player player = ( Player ) o;

            if ( !firstName.equals ( player.firstName ) ) return false;
            if ( !lastName.equals ( player.lastName ) ) return false;
            if ( !id.equals ( player.id ) ) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id.hashCode ();
            result = 31 * result + firstName.hashCode ();
            result = 31 * result + lastName.hashCode ();
            return result;
        }

        @Override
        public String toString() {
            return id;
        }
    }

    @Test
    public void test() {
        TestClass tc = new TestClass ();
        tc.init ();
        TestPrime prime = new TestPrime ();
        BeanUtils.copyProperties ( tc, prime );
        boolean ok = prime.id.equals ( "foo" ) || die();

        ok = prime.child.id.equals ( "child" ) || die();
        ok &= Lists.list("1", "2").equals ( prime.players ) || die("" + prime.players);
    }


    @Test
    public void test2() {
        TestClass tc = new TestClass ();
        tc.init();

        TestPrime prime = new TestPrime ();
        BeanUtils.copyProperties ( tc, prime, "id" );
        boolean ok = prime.id.equals ( "bar" ) || die(prime.id);

        ok = prime.child.id.equals ( "bar" ) || die(prime.child.id);

        ok = prime.child.time == 1L  || die();
        ok &= Lists.list("1", "2").equals ( prime.players ) || die("" + prime.players);
    }
}
