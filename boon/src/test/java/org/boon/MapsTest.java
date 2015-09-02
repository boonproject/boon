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

package org.boon;


import org.boon.core.reflection.BeanUtils;
import org.junit.Test;

import java.util.Arrays;


import java.util.*;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.Lists.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@SuppressWarnings ( "unchecked" )
public class MapsTest {


    @Test
    public void prettyPrintMap() {
        Map<String, Object> map = Maps.map(
                "name", (Object)"Rick",
                "age", 45,
                "wife", Maps.map("name", "Diana"),
                "children", Lists.list(
                        Maps.map("name", "Whitney"),
                        Maps.map("name", "Maya"),
                        Maps.map("name", "Lucas"),
                        Maps.map("name", "Ryan"),
                        Maps.map("name", "Noah")
                        ),
                "fruit", Lists.list("apple", "orange", "strawberry")
                );


        puts(Maps.asPrettyJsonString(map));
        puts(Boon.toPrettyJson(map));

        final Object o = Boon.fromJson(Boon.toPrettyJson(map));

        Boon.equalsOrDie("Values are equal", map, o);


    }


    @Test
    public void mergeMaps() {
        Map<String, Object> dest = Maps.map(
                "foo", (Object)"bar"
        );

        Map<String, Object> src = Maps.map(
                "bar", (Object)"foo"
        );

        BeanUtils.copyPropertiesFromMap(dest, src);


        puts(Maps.asPrettyJsonString(dest));

    }


    @Test
    public void mergeMapsNested() {
        Map<String, Object> dest = Maps.map(
                "foo", (Object)"bar",
                "innerMap", Maps.map(
                        "bar", "baz"
                )
        );

        Map<String, Object> src = Maps.map(
                "bar", (Object)"foo",
                "innerMap", Maps.map(
                        "rick", "high"
                ),
                "mapTwo", Maps.map(
                        "rick", "high"
                )

        );

        BeanUtils.copyPropertiesFromMap(dest, src);


        puts(Maps.asPrettyJsonString(dest));

    }



    @Test
    public void mergeMapsDeeplyNested() {
        Map<String, Object> dest = Maps.map(
                "foo", (Object)"bar",
                "innerMap", Maps.map(
                        "bar", "baz",
                        "innerMap2", Maps.map(
                                "how", "deep"
                        )

                )
        );

        Map<String, Object> src = Maps.map(
                "bar", (Object)"foo",
                "innerMap", Maps.map(
                        "rick", "high",
                        "innerMap2", Maps.map(
                                "so", "deep"
                        )
                ),
                "mapTwo", Maps.map(
                        "rick", "high"
                )

        );

        /* Merge the maps. */
        BeanUtils.copyPropertiesFromMap(dest, src);

        String howDeep = BeanUtils.idxStr(dest, "innerMap.innerMap2.so");

        assertEquals("deep", howDeep);
        puts(Maps.asPrettyJsonString(dest));

    }

    class Dog {
        String name = "dog";

        Dog( String name ) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Dog{\"name\":\"" + name + "\"}";
        }
    }


    Dog dog1 = new Dog( "Spot" );
    Dog dog2 = new Dog( "Fido" );

    final List<Dog> dogs = Lists.list( dog1, dog2 );



    @Test
    public void testToMap() {


        final Map<String, Dog> dogMap = Maps.toMap( "name", dogs );

        validateForToMap( dogMap );

    }

    @Test
    public void testToSafeMap() {


        final Map<String, Dog> dogMap = Maps.toSafeMap( "name", dogs );

        validateForToMap( dogMap );

    }

    @Test
    public void testToSortedMap() {


        final Map<String, Dog> dogMap = Maps.toSortedMap( "name", dogs );

        validateForToMap( dogMap );

    }

    @Test
    public void testToSafeSortedMap() {


        final Map<String, Dog> dogMap = Maps.toSafeSortedMap( "name", dogs );

        validateForToMap( dogMap );

    }

    private void validateForToMap( Map<String, Dog> dogMap ) {
        boolean ok = true;

        ok &= dogMap.size() == 2 || die( "should be 2 and was " + Maps.len(dogMap) );

        ok &= dogMap.get( "Fido" ).name.equals( "Fido" ) || die( "No Fido" );

        ok &= dogMap.get( "Spot" ).name.equals( "Spot" ) || die( "No Spot" );

        System.out.println( ok );

    }


    @Test
    public void testEntry() {

        Dog dog = new Dog( "dog" );
        Entry<String, Dog> entry = Maps.entry("dog", dog);
        assertEquals( "dog", entry.key() );
        assertEquals( dog, entry.value() );

        assertTrue( entry.equals( entry ) );

        assertTrue( entry.equals( ( Object ) entry ) );


        Entry<String, Dog> entry4 = Maps.entry("dog4", new Dog("dog4"));
        assertFalse( entry.equals( ( Object ) entry4 ) );


        assertTrue( entry.hashCode()
                == ( new Pair( entry ).hashCode() ) );

        assertEquals( "{\"k\":dog, \"v\":Dog{\"name\":\"dog\"}}",
                entry.toString() );

        new Pair();
    }

    @Test
    public void testUniversal() {

        Dog dog = new Dog( "dog" );
        Map<String, Dog> dogMap = Maps.map("dog", dog);
        assertEquals( "dog", dogMap.get( "dog" ).name );

        assertEquals( true, Maps.in("dog", dogMap) );
        assertEquals( 1, Maps.len(dogMap) );
        assertEquals( dog, Maps.idx(dogMap, "dog") );


        Dog fido = new Dog( "fido" );
        Maps.add(dogMap, Maps.entry("fido", fido));
        assertEquals( 2, Maps.len(dogMap) );
        assertEquals( true, Maps.valueIn(fido, dogMap) );

        Map<String, Dog> dogMap2 = Maps.copy(dogMap);

        assertEquals( dogMap.hashCode(), dogMap2.hashCode() );


        SortedMap<String, Dog> dogMapT = Maps.sortedMap("dog", new Dog("dog"));
        SortedMap<String, Dog> dogMapT2 = Maps.copy(dogMapT);
        assertEquals( dogMapT.hashCode(), dogMapT2.hashCode() );

        Maps.idx(dogMap, "foo", new Dog("foo"));
        assertEquals( "foo", Maps.idx(dogMap, "foo").name );


    }


    @Test
    public void testHashMap() {
        Map<String, Dog> dogMap = Maps.map( "dog", new Dog( "dog" ) );
        assertEquals( "dog", dogMap.get( "dog" ).name );


        dogMap = Maps.map(
                new String[]{ "dog0", "dog1", "dog2" },
                new Dog[]{ new Dog( "dog0" ),
                        new Dog( "dog1" ), new Dog( "dog2" ) }
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );

        dogMap = Maps.map(
                Lists.list( new String[]{ "dog0", "dog1", "dog2" } ),
                Lists.list( new Dog( "dog0" ),
                        new Dog( "dog1" ), new Dog( "dog2" ) )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.map(
                ( Iterable ) Lists.list( "dog0", "dog1", "dog2" ),
                ( Iterable ) Lists.list( new Dog( "dog0" ),
                        new Dog( "dog1" ), new Dog( "dog2" ) )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );

        dogMap = Maps.map( "dog", new Dog( "dog" ),
                "dog1", new Dog( "dog1" )
        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );

        dogMap = Maps.map( "dog", new Dog( "dog" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" )

        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.map(
                Maps.entry("dog0", new Dog("dog0")),
                Maps.entry("dog1", new Dog("dog1")),
                Maps.entry("dog2", new Dog("dog2"))

        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.map( "dog", new Dog( "dog" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" )

        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );


        dogMap = Maps.map(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );


        dogMap = Maps.map(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" ),
                "dog5", new Dog( "dog5" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );


        dogMap = Maps.map(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" ),
                "dog5", new Dog( "dog5" ),
                "dog6", new Dog( "dog6" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );

        dogMap = Maps.map(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" ),
                "dog5", new Dog( "dog5" ),
                "dog6", new Dog( "dog6" ),
                "dog7", new Dog( "dog7" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );


        dogMap = Maps.map(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" ),
                "dog5", new Dog( "dog5" ),
                "dog6", new Dog( "dog6" ),
                "dog7", new Dog( "dog7" ),
                "dog8", new Dog( "dog8" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );
        assertEquals( "dog8", dogMap.get( "dog8" ).name );


        dogMap = Maps.map(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" ),
                "dog5", new Dog( "dog5" ),
                "dog6", new Dog( "dog6" ),
                "dog7", new Dog( "dog7" ),
                "dog8", new Dog( "dog8" ),
                "dog9", new Dog( "dog9" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );
        assertEquals( "dog8", dogMap.get( "dog8" ).name );
        assertEquals( "dog9", dogMap.get( "dog9" ).name );

        assertEquals( 10, Maps.len(dogMap) );

    }


    @Test
    public void testTreeMap() {
        Map<String, Dog> dogMap = Maps.sortedMap("dog", new Dog("dog"));
        assertEquals( "dog", dogMap.get( "dog" ).name );


        dogMap = Maps.sortedMap(
                java.util.Arrays.asList(new String[]{"dog0", "dog1", "dog2"}),
                Arrays.asList(new Dog("dog0"),
                        new Dog("dog1"), new Dog("dog2"))
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.sortedMap(
                (Iterable) Arrays.asList("dog0", "dog1", "dog2"),
                (Iterable) Arrays.asList(new Dog("dog0"),
                        new Dog("dog1"), new Dog("dog2"))
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.sortedMap(list(
                        Maps.entry("dog0", new Dog("dog0")),
                        Maps.entry("dog1", new Dog("dog1")),
                        Maps.entry("dog2", new Dog("dog2")))

        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );

        dogMap = Maps.sortedMap(
                new String[]{"dog0", "dog1", "dog2"},
                new Dog[]{new Dog("dog0"),
                        new Dog("dog1"), new Dog("dog2")}
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.sortedMap("dog", new Dog("dog"),
                "dog1", new Dog("dog1")
        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );

        dogMap = Maps.sortedMap("dog", new Dog("dog"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2")

        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.sortedMap("dog", new Dog("dog"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3")

        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );


        dogMap = Maps.sortedMap(
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );


        dogMap = Maps.sortedMap(
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );


        dogMap = Maps.sortedMap(
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );

        dogMap = Maps.sortedMap(
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6"),
                "dog7", new Dog("dog7")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );


        dogMap = Maps.sortedMap(
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6"),
                "dog7", new Dog("dog7"),
                "dog8", new Dog("dog8")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );
        assertEquals( "dog8", dogMap.get( "dog8" ).name );


        dogMap = Maps.sortedMap(
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6"),
                "dog7", new Dog("dog7"),
                "dog8", new Dog("dog8"),
                "dog9", new Dog("dog9")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );
        assertEquals( "dog8", dogMap.get( "dog8" ).name );
        assertEquals( "dog9", dogMap.get( "dog9" ).name );

    }

    @Test
    public void testComparator() {

        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare( String o1, String o2 ) {
                return o1.toString().compareTo( o2.toString() );
            }
        };


        Map<String, Dog> dogMap = Maps.sortedMap(comparator, "dog", new Dog("dog"));
        assertEquals( "dog", dogMap.get( "dog" ).name );


        dogMap = Maps.sortedMap(comparator,
                Arrays.asList(new String[]{"dog0", "dog1", "dog2"}),
                Arrays.asList(new Dog("dog0"),
                        new Dog("dog1"), new Dog("dog2"))
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.sortedMap(comparator, list(
                        Maps.entry("dog0", new Dog("dog0")),
                        Maps.entry("dog1", new Dog("dog1")),
                        Maps.entry("dog2", new Dog("dog2")))

        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );

        dogMap = Maps.sortedMap(comparator,
                new String[]{"dog0", "dog1", "dog2"},
                new Dog[]{new Dog("dog0"),
                        new Dog("dog1"), new Dog("dog2")}
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.sortedMap(comparator, "dog", new Dog("dog"),
                "dog1", new Dog("dog1")
        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );

        dogMap = Maps.sortedMap(comparator, "dog", new Dog("dog"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2")

        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.sortedMap(comparator, "dog", new Dog("dog"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3")

        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );


        dogMap = Maps.sortedMap(comparator,
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );


        dogMap = Maps.sortedMap(comparator,
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );


        dogMap = Maps.sortedMap(comparator,
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );

        dogMap = Maps.sortedMap(comparator,
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6"),
                "dog7", new Dog("dog7")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );


        dogMap = Maps.sortedMap(comparator,
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6"),
                "dog7", new Dog("dog7"),
                "dog8", new Dog("dog8")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );
        assertEquals( "dog8", dogMap.get( "dog8" ).name );


        dogMap = Maps.sortedMap(comparator,
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6"),
                "dog7", new Dog("dog7"),
                "dog8", new Dog("dog8"),
                "dog9", new Dog("dog9")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );
        assertEquals( "dog8", dogMap.get( "dog8" ).name );
        assertEquals( "dog9", dogMap.get( "dog9" ).name );

    }


    @Test
    public void testSafeMap() {

        Map<String, Dog> dogMap = Maps.safeMap( "dog", new Dog( "dog" ) );
        assertEquals( "dog", dogMap.get( "dog" ).name );


        dogMap = Maps.safeMap(
                new String[]{ "dog0", "dog1", "dog2" },
                new Dog[]{ new Dog( "dog0" ),
                        new Dog( "dog1" ), new Dog( "dog2" ) }
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );

        dogMap = Maps.safeMap(
                Arrays.asList( new String[]{ "dog0", "dog1", "dog2" } ),
                Arrays.asList( new Dog( "dog0" ),
                        new Dog( "dog1" ), new Dog( "dog2" ) )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.safeMap(
                ( Iterable ) Arrays.asList( "dog0", "dog1", "dog2" ),
                ( Iterable ) Arrays.asList( new Dog( "dog0" ),
                        new Dog( "dog1" ), new Dog( "dog2" ) )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );

        dogMap = Maps.safeMap( "dog", new Dog( "dog" ),
                "dog1", new Dog( "dog1" )
        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );

        dogMap = Maps.safeMap( "dog", new Dog( "dog" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" )

        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.safeMap(
                Maps.entry("dog0", new Dog("dog0")),
                Maps.entry("dog1", new Dog("dog1")),
                Maps.entry("dog2", new Dog("dog2"))

        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.safeMap( "dog", new Dog( "dog" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" )

        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );


        dogMap = Maps.safeMap(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );


        dogMap = Maps.safeMap(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" ),
                "dog5", new Dog( "dog5" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );


        dogMap = Maps.safeMap(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" ),
                "dog5", new Dog( "dog5" ),
                "dog6", new Dog( "dog6" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );

        dogMap = Maps.safeMap(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" ),
                "dog5", new Dog( "dog5" ),
                "dog6", new Dog( "dog6" ),
                "dog7", new Dog( "dog7" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );


        dogMap = Maps.safeMap(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" ),
                "dog5", new Dog( "dog5" ),
                "dog6", new Dog( "dog6" ),
                "dog7", new Dog( "dog7" ),
                "dog8", new Dog( "dog8" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );
        assertEquals( "dog8", dogMap.get( "dog8" ).name );


        dogMap = Maps.safeMap(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" ),
                "dog5", new Dog( "dog5" ),
                "dog6", new Dog( "dog6" ),
                "dog7", new Dog( "dog7" ),
                "dog8", new Dog( "dog8" ),
                "dog9", new Dog( "dog9" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );
        assertEquals( "dog8", dogMap.get( "dog8" ).name );
        assertEquals( "dog9", dogMap.get( "dog9" ).name );

        assertEquals( 10, Maps.len(dogMap) );

    }


    @Test
    public void testComparatorSkipMap() {

        Comparator<String> comparator = new Comparator<String>() {
            @Override
            public int compare( String o1, String o2 ) {
                return o1.toString().compareTo( o2.toString() );
            }
        };


        Map<String, Dog> dogMap = Maps.safeSortedMap(comparator, "dog", new Dog("dog"));
        assertEquals( "dog", dogMap.get( "dog" ).name );


        dogMap = Maps.safeSortedMap(comparator,
                Arrays.asList(new String[]{"dog0", "dog1", "dog2"}),
                Arrays.asList(new Dog("dog0"),
                        new Dog("dog1"), new Dog("dog2"))
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.safeSortedMap(comparator, list(
                        Maps.entry("dog0", new Dog("dog0")),
                        Maps.entry("dog1", new Dog("dog1")),
                        Maps.entry("dog2", new Dog("dog2")))

        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );

        dogMap = Maps.safeSortedMap(comparator,
                new String[]{"dog0", "dog1", "dog2"},
                new Dog[]{new Dog("dog0"),
                        new Dog("dog1"), new Dog("dog2")}
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.safeSortedMap(comparator, "dog", new Dog("dog"),
                "dog1", new Dog("dog1")
        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );

        dogMap = Maps.safeSortedMap(comparator, "dog", new Dog("dog"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2")

        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.safeSortedMap(comparator, "dog", new Dog("dog"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3")

        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );


        dogMap = Maps.safeSortedMap(comparator,
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );


        dogMap = Maps.safeSortedMap(comparator,
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );


        dogMap = Maps.safeSortedMap(comparator,
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );

        dogMap = Maps.safeSortedMap(comparator,
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6"),
                "dog7", new Dog("dog7")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );


        dogMap = Maps.safeSortedMap(comparator,
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6"),
                "dog7", new Dog("dog7"),
                "dog8", new Dog("dog8")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );
        assertEquals( "dog8", dogMap.get( "dog8" ).name );


        dogMap = Maps.safeSortedMap(comparator,
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6"),
                "dog7", new Dog("dog7"),
                "dog8", new Dog("dog8"),
                "dog9", new Dog("dog9")
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );
        assertEquals( "dog8", dogMap.get( "dog8" ).name );
        assertEquals( "dog9", dogMap.get( "dog9" ).name );

    }


    @Test
    public void testSkipMap() {

        Map<String, Dog> dogMap = Maps.safeSortedMap( "dog", new Dog( "dog" ) );
        assertEquals( "dog", dogMap.get( "dog" ).name );


        dogMap = Maps.safeSortedMap(
                new String[]{ "dog0", "dog1", "dog2" },
                new Dog[]{ new Dog( "dog0" ),
                        new Dog( "dog1" ), new Dog( "dog2" ) }
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );

        dogMap = Maps.safeSortedMap(
                Arrays.asList( new String[]{ "dog0", "dog1", "dog2" } ),
                Arrays.asList( new Dog( "dog0" ),
                        new Dog( "dog1" ), new Dog( "dog2" ) )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.safeSortedMap(
                ( Iterable ) Arrays.asList( "dog0", "dog1", "dog2" ),
                ( Iterable ) Arrays.asList( new Dog( "dog0" ),
                        new Dog( "dog1" ), new Dog( "dog2" ) )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );

        dogMap = Maps.safeSortedMap( "dog", new Dog( "dog" ),
                "dog1", new Dog( "dog1" )
        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );

        dogMap = Maps.safeSortedMap( "dog", new Dog( "dog" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" )

        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.safeSortedMap(
                Maps.entry("dog0", new Dog("dog0")),
                Maps.entry("dog1", new Dog("dog1")),
                Maps.entry("dog2", new Dog("dog2"))

        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );


        dogMap = Maps.safeSortedMap( "dog", new Dog( "dog" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" )

        );
        assertEquals( "dog", dogMap.get( "dog" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );


        dogMap = Maps.safeSortedMap(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );


        dogMap = Maps.safeSortedMap(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" ),
                "dog5", new Dog( "dog5" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );


        dogMap = Maps.safeSortedMap(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" ),
                "dog5", new Dog( "dog5" ),
                "dog6", new Dog( "dog6" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );

        dogMap = Maps.safeSortedMap(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" ),
                "dog5", new Dog( "dog5" ),
                "dog6", new Dog( "dog6" ),
                "dog7", new Dog( "dog7" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );


        dogMap = Maps.safeSortedMap(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" ),
                "dog5", new Dog( "dog5" ),
                "dog6", new Dog( "dog6" ),
                "dog7", new Dog( "dog7" ),
                "dog8", new Dog( "dog8" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );
        assertEquals( "dog8", dogMap.get( "dog8" ).name );


        dogMap = Maps.safeSortedMap(
                "dog0", new Dog( "dog0" ),
                "dog1", new Dog( "dog1" ),
                "dog2", new Dog( "dog2" ),
                "dog3", new Dog( "dog3" ),
                "dog4", new Dog( "dog4" ),
                "dog5", new Dog( "dog5" ),
                "dog6", new Dog( "dog6" ),
                "dog7", new Dog( "dog7" ),
                "dog8", new Dog( "dog8" ),
                "dog9", new Dog( "dog9" )
        );
        assertEquals( "dog0", dogMap.get( "dog0" ).name );
        assertEquals( "dog1", dogMap.get( "dog1" ).name );
        assertEquals( "dog2", dogMap.get( "dog2" ).name );
        assertEquals( "dog3", dogMap.get( "dog3" ).name );
        assertEquals( "dog4", dogMap.get( "dog4" ).name );
        assertEquals( "dog5", dogMap.get( "dog5" ).name );
        assertEquals( "dog6", dogMap.get( "dog6" ).name );
        assertEquals( "dog7", dogMap.get( "dog7" ).name );
        assertEquals( "dog8", dogMap.get( "dog8" ).name );
        assertEquals( "dog9", dogMap.get( "dog9" ).name );

        assertEquals( 10, Maps.len(dogMap) );

    }




    @Test
    public void testMapEquals() {


        final Map<String, Dog> dogMap = Maps.toMap( "name", dogs );

        HashMap<String, Dog> hashMap = new HashMap<>( dogMap );

        boolean ok = dogMap.equals( hashMap ) || die();

    }


}
