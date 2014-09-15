/*
 * Copyright 2013-2014 Richard M. Hightower
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * __________                              _____          __   .__
 * \______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 *  |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 *  |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 *  |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
 *         \/                   \/              \/     \/     \/       \//_____/
 *      ____.                     ___________   _____    ______________.___.
 *     |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
 *     |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
 * /\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
 * \________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
 *               \/           \/          \/         \/        \/  \/
 */

package org.boon.core.reflection;

import org.boon.Boon;
import org.boon.Lists;
import org.boon.primitive.Arry;
import org.boon.primitive.Int;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;

public class BeanUtilsTest {

    public static class TestClass {
        private String id="foo";
        private  Long time;
        TestClass child =null;

        int [] score = Int.array(1,2,3);

        void init()
        {
            child = new TestClass ();

            child.id = "child";
            child.time = 1L;
        }
        List<Player> players = Player.players (
                Player.player ( "1", "Rick", "Hightower"  ),
                Player.player ( "2", "Diana", "Hightower"  ) );

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TestClass)) return false;

            TestClass testClass = (TestClass) o;

            if (child != null ? !child.equals(testClass.child) : testClass.child != null) return false;
            if (id != null ? !id.equals(testClass.id) : testClass.id != null) return false;
            if (players != null ? !players.equals(testClass.players) : testClass.players != null) return false;
            if (!Arrays.equals(score, testClass.score)) return false;
            if (time != null ? !time.equals(testClass.time) : testClass.time != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + (time != null ? time.hashCode() : 0);
            result = 31 * result + (child != null ? child.hashCode() : 0);
            result = 31 * result + (score != null ? Arrays.hashCode(score) : 0);
            result = 31 * result + (players != null ? players.hashCode() : 0);
            return result;
        }
    }


    @Test
    public void testPrettyPrint() {
        puts(BeanUtils.asPrettyJsonString(new TestClass()));

        final String s = BeanUtils.asPrettyJsonString(new TestClass());

        final Object o = Boon.fromJson(s, TestClass.class);

        Boon.equalsOrDie("not the same", o, new TestClass());
    }

    @Test
    public void testPrettyPrintWithTypes() {
        puts(Boon.toPrettyJsonWithTypes(new TestClass()));

        final String s = BeanUtils.asPrettyJsonString(new TestClass());

        final Object o = Boon.fromJson(s, TestClass.class);

        Boon.equalsOrDie("not the same", o, new TestClass());
    }


    public static class TestPrime {
        private String id="bar";
        private  long time;

        TestPrime child;
        List<String> players;

    }





    public static class Player {

        private  String id;
        private  String firstName;
        private  String lastName;



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
