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

package org.boon.di;

import org.boon.Lists;
import org.boon.Maps;
import org.junit.Test;

import static org.boon.Exceptions.die;
import static org.boon.json.JsonFactory.toJson;

/**
 * Created by Richard on 2/17/14.
 */
public class ContextInvoker {


    public static class HelloWorldArg {

        String hi;
        int i;
        int i2;

        public HelloWorldArg( String hi, int i ) {
            this.hi = hi;
            this.i = i;
        }

        public HelloWorldArg( String hi, int i, int i2 ) {
            this.hi = hi;
            this.i = i;
            this.i2 = i2;
        }

        @Override
        public boolean equals( Object o ) {
            if ( this == o ) return true;
            if ( o == null || getClass() != o.getClass() ) return false;

            HelloWorldArg that = ( HelloWorldArg ) o;

            if ( i != that.i ) return false;
            if ( i2 != that.i2 ) return false;
            if ( hi != null ? !hi.equals( that.hi ) : that.hi != null ) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = hi != null ? hi.hashCode() : 0;
            result = 31 * result + i;
            result = 31 * result + i2;
            return result;
        }
    }


    public static class HelloWorld {

        boolean invokeMe;
        private void invokeMe() {
            invokeMe = true;

        }



        boolean invokeMe2;
        private void invokeMe2(String str, int i) {

            if (i == 5 && str.equals( "hi" )) {
                invokeMe2 = true;
            }


        }



        private HelloWorldArg invokeMe3(HelloWorldArg hw, int i) {

            hw.i2 = i;
            return hw;

        }




        private HelloWorldArg invokeMe4(HelloWorldArg hw) {

            hw.i2 = 4;
            return hw;

        }



    }

    @Test
    public void test() {
        HelloWorld hw = new HelloWorld();
        Context context = DependencyInjection.fromMap(Maps.map("hw", hw));
        context.invoke( "hw", "invokeMe", null );

        if (!hw.invokeMe) {
            die();
        }

    }




    @Test
    public void test2() {
        HelloWorld hw = new HelloWorld();
        Context context = DependencyInjection.fromMap(Maps.map("hw", hw));
        context.invoke( "hw", "invokeMe2", Lists.list("hi", 5) );

        if (!hw.invokeMe2) {
            die();
        }

    }



    @Test
    public void test3() {
        HelloWorld hw = new HelloWorld();
        Context context = DependencyInjection.fromMap(Maps.map("hw", hw));
        HelloWorldArg hwa = ( HelloWorldArg ) context.invoke( "hw", "invokeMe3", Lists.list(Lists.list( "hi", 5 ), 6) );

        if (!hwa.equals( new HelloWorldArg( "hi", 5, 6 ) )) {
             die();
        }
    }





    @Test
    public void test4() {
        HelloWorld hw = new HelloWorld();
        Context context = DependencyInjection.fromMap(Maps.map("hw", hw));
        HelloWorldArg hwa = ( HelloWorldArg ) context.invoke( "hw", "invokeMe4", Lists.list( "hi", 5 ) );

        if (!hwa.equals( new HelloWorldArg( "hi", 5, 4 ) )) {
            die();
        }
    }




    @Test
    public void test5() {
        HelloWorld hw = new HelloWorld();
        Context context = DependencyInjection.fromMap(Maps.map("hw", hw));
        HelloWorldArg hwa = ( HelloWorldArg ) context.invoke( "hw", "invokeMe4", Lists.list((Object)Lists.list( "hi", 5 )) );

        if (!hwa.equals( new HelloWorldArg( "hi", 5, 4 ) )) {
            die();
        }
    }





    @Test
    public void test6() {
        HelloWorld hw = new HelloWorld();
        Context context = DependencyInjection.fromMap(Maps.map("hw", hw));
        HelloWorldArg hwa = ( HelloWorldArg ) context.invoke( "hw", "invokeMe4", Maps.map("hi", "hello", "i", 6, "i2", "6"  ) );

        if (!hwa.equals( new HelloWorldArg( "hello", 6, 4 ) )) {
            die();
        }
    }



    @Test
    public void test7() {
        HelloWorld hw = new HelloWorld();
        Context context = DependencyInjection.fromMap(Maps.map("hw", hw));
        HelloWorldArg hwa = ( HelloWorldArg ) context.invoke( "hw", "invokeMe4", Lists.list( Maps.map( "hi", "hello", "i", 7, "i2", "6" ) ) );

        if (!hwa.equals( new HelloWorldArg( "hello", 7, 4 ) )) {
            die();
        }
    }



    @Test
    public void test8() {
        HelloWorld hw = new HelloWorld();
        Context context = DependencyInjection.fromMap(Maps.map("hw", hw));
        HelloWorldArg hwa = ( HelloWorldArg ) context.invokeFromJson( "hw", "invokeMe4",
                toJson( Lists.list( Maps.map( "hi", "hello", "i", 8, "i2", "6" ) ) ) );

        if (!hwa.equals( new HelloWorldArg( "hello", 8, 4 ) )) {
            die();
        }
    }



    @Test
    public void test9() {
        HelloWorld hw = new HelloWorld();
        Context context = DependencyInjection.fromMap(Maps.map("hw", hw));
        HelloWorldArg hwa = ( HelloWorldArg ) context.invokeOverloadFromJson( "hw", "invokeMe4",
                toJson( Lists.list( Maps.map( "hi", "hello", "i", 9, "i2", "6" ) ) ) );

        if (!hwa.equals( new HelloWorldArg( "hello", 9, 4 ) )) {
            die();
        }
    }
}
