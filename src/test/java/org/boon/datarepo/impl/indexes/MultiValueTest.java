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

package org.boon.datarepo.impl.indexes;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.boon.Lists.list;

public class MultiValueTest {

    private MultiValue mv;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAdd() throws Exception {

        MultiValue mv = MultiValue.add( null, "Rick", 3 );
        Assert.assertEquals( "Rick", mv.getValue() );

    }

    @Test
    public void testMany() throws Exception {

        List<String> strings = list( "Rick", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13" );


        for ( String item : strings ) {
            mv = MultiValue.add( mv, item, 3 );

        }

        Assert.assertEquals( "Rick", mv.getValue() );
        Assert.assertEquals( 13, mv.size() );


        for ( Object item : mv.getValues() ) {
            junit.framework.Assert.assertNotNull( item );
        }

    }

    @Test
    public void testManyThenAddTo() throws Exception {

        List<String> strings = list( "Rick", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13" );


        for ( String item : strings ) {
            mv = MultiValue.add( mv, item, 3 );

        }

        List<String> results = list( "Rick", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13" );

        mv.addTo( results );

        for ( Object item : mv.getValues() ) {
            junit.framework.Assert.assertNotNull( item );
        }


    }

    @Test
    public void testManyUseAddTo() throws Exception {

        MultiValue mv = MultiValue.add( null, "Rick", 3 );

        Assert.assertEquals( "Rick", mv.getValue() );

        List<String> results = list( "Rick", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13" );

        mv.addTo( results );

//        results.forEach((item) -> {
//            assertNotNull(item);
//            //print(item);
//        });
//
//        mv.getValues().forEach((item) -> {
//            assertNotNull(item);
//            //print(item);
//        });
//
//        results.forEach((item) -> {
//            assertNotNull(item);
//            //print(item);
//        });

    }

}
