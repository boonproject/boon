package org.boon.utils;


import org.junit.Test;

import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.boon.utils.Maps.entry;

public class MapsTest {

    class Dog {
        String name = "dog";
        Dog (String name) {
            this.name = name;
        }
    }

    @Test
    public void testHashMap() {
        Map<String,Dog> dogMap = Maps.hashMap("dog", new Dog("dog"));
        assertEquals("dog", dogMap.get("dog").name);


        dogMap = Maps.hashMap(
                new String[]{"dog0", "dog1", "dog2"},
                new Dog[]{new Dog("dog0"),
                        new Dog("dog1"), new Dog("dog2")}
        );
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);

        dogMap = Maps.hashMap(
                Arrays.asList(new String[] {"dog0", "dog1", "dog2"}),
                Arrays.asList(new Dog[]    {new Dog("dog0"),
                        new Dog("dog1"), new Dog("dog2")})
        );
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);


        dogMap = Maps.hashMap("dog", new Dog("dog"),
                               "dog1", new Dog("dog1")
                );
        assertEquals("dog", dogMap.get("dog").name);
        assertEquals("dog1", dogMap.get("dog1").name);

        dogMap = Maps.hashMap("dog", new Dog("dog"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2")

        );
        assertEquals("dog", dogMap.get("dog").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);


        dogMap = Maps.hashMap(
                entry("dog0", new Dog("dog0")),
                entry("dog1", new Dog("dog1")),
                entry("dog2", new Dog("dog2"))

        );
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);


        dogMap = Maps.hashMap("dog", new Dog("dog"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3")

        );
        assertEquals("dog", dogMap.get("dog").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog3", dogMap.get("dog3").name);



        dogMap = Maps.hashMap(
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4")
        );
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);
        assertEquals("dog3", dogMap.get("dog3").name);
        assertEquals("dog4", dogMap.get("dog4").name);


        dogMap = Maps.hashMap(
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5")
        );
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);
        assertEquals("dog3", dogMap.get("dog3").name);
        assertEquals("dog4", dogMap.get("dog4").name);
        assertEquals("dog5", dogMap.get("dog5").name);


        dogMap = Maps.hashMap(
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6")
        );
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);
        assertEquals("dog3", dogMap.get("dog3").name);
        assertEquals("dog4", dogMap.get("dog4").name);
        assertEquals("dog5", dogMap.get("dog5").name);
        assertEquals("dog6", dogMap.get("dog6").name);

        dogMap = Maps.hashMap(
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6"),
                "dog7", new Dog("dog7")
        );
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);
        assertEquals("dog3", dogMap.get("dog3").name);
        assertEquals("dog4", dogMap.get("dog4").name);
        assertEquals("dog5", dogMap.get("dog5").name);
        assertEquals("dog6", dogMap.get("dog6").name);
        assertEquals("dog7", dogMap.get("dog7").name);


        dogMap = Maps.hashMap(
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
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);
        assertEquals("dog3", dogMap.get("dog3").name);
        assertEquals("dog4", dogMap.get("dog4").name);
        assertEquals("dog5", dogMap.get("dog5").name);
        assertEquals("dog6", dogMap.get("dog6").name);
        assertEquals("dog7", dogMap.get("dog7").name);
        assertEquals("dog8", dogMap.get("dog8").name);


        dogMap = Maps.hashMap(
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
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);
        assertEquals("dog3", dogMap.get("dog3").name);
        assertEquals("dog4", dogMap.get("dog4").name);
        assertEquals("dog5", dogMap.get("dog5").name);
        assertEquals("dog6", dogMap.get("dog6").name);
        assertEquals("dog7", dogMap.get("dog7").name);
        assertEquals("dog8", dogMap.get("dog8").name);
        assertEquals("dog9", dogMap.get("dog9").name);

        assertEquals(10, Maps.size(dogMap));

    }




    @Test
    public void testTreeMap() {
        Map<String,Dog> dogMap = Maps.treeMap("dog", new Dog("dog"));
        assertEquals("dog", dogMap.get("dog").name);


        dogMap = Maps.treeMap(
                Arrays.asList(new String[] {"dog0", "dog1", "dog2"}),
                Arrays.asList(new Dog[]    {new Dog("dog0"),
                        new Dog("dog1"), new Dog("dog2")})
        );
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);



        dogMap = Maps.treeMap(
                entry("dog0", new Dog("dog0")),
                entry("dog1", new Dog("dog1")),
                entry("dog2", new Dog("dog2"))

        );
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);

        dogMap = Maps.treeMap(
                new String[] {"dog0", "dog1", "dog2"},
                new Dog[]    {new Dog("dog0"),
                              new Dog("dog1"), new Dog("dog2")}
        );
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);


        dogMap = Maps.treeMap("dog", new Dog("dog"),
                "dog1", new Dog("dog1")
        );
        assertEquals("dog", dogMap.get("dog").name);
        assertEquals("dog1", dogMap.get("dog1").name);

        dogMap = Maps.treeMap("dog", new Dog("dog"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2")

        );
        assertEquals("dog", dogMap.get("dog").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);



        dogMap = Maps.treeMap("dog", new Dog("dog"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3")

        );
        assertEquals("dog", dogMap.get("dog").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog3", dogMap.get("dog3").name);



        dogMap = Maps.treeMap(
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4")
        );
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);
        assertEquals("dog3", dogMap.get("dog3").name);
        assertEquals("dog4", dogMap.get("dog4").name);


        dogMap = Maps.treeMap(
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5")
        );
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);
        assertEquals("dog3", dogMap.get("dog3").name);
        assertEquals("dog4", dogMap.get("dog4").name);
        assertEquals("dog5", dogMap.get("dog5").name);


        dogMap = Maps.treeMap(
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6")
        );
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);
        assertEquals("dog3", dogMap.get("dog3").name);
        assertEquals("dog4", dogMap.get("dog4").name);
        assertEquals("dog5", dogMap.get("dog5").name);
        assertEquals("dog6", dogMap.get("dog6").name);

        dogMap = Maps.treeMap(
                "dog0", new Dog("dog0"),
                "dog1", new Dog("dog1"),
                "dog2", new Dog("dog2"),
                "dog3", new Dog("dog3"),
                "dog4", new Dog("dog4"),
                "dog5", new Dog("dog5"),
                "dog6", new Dog("dog6"),
                "dog7", new Dog("dog7")
        );
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);
        assertEquals("dog3", dogMap.get("dog3").name);
        assertEquals("dog4", dogMap.get("dog4").name);
        assertEquals("dog5", dogMap.get("dog5").name);
        assertEquals("dog6", dogMap.get("dog6").name);
        assertEquals("dog7", dogMap.get("dog7").name);


        dogMap = Maps.treeMap(
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
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);
        assertEquals("dog3", dogMap.get("dog3").name);
        assertEquals("dog4", dogMap.get("dog4").name);
        assertEquals("dog5", dogMap.get("dog5").name);
        assertEquals("dog6", dogMap.get("dog6").name);
        assertEquals("dog7", dogMap.get("dog7").name);
        assertEquals("dog8", dogMap.get("dog8").name);


        dogMap = Maps.treeMap(
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
        assertEquals("dog0", dogMap.get("dog0").name);
        assertEquals("dog1", dogMap.get("dog1").name);
        assertEquals("dog2", dogMap.get("dog2").name);
        assertEquals("dog3", dogMap.get("dog3").name);
        assertEquals("dog4", dogMap.get("dog4").name);
        assertEquals("dog5", dogMap.get("dog5").name);
        assertEquals("dog6", dogMap.get("dog6").name);
        assertEquals("dog7", dogMap.get("dog7").name);
        assertEquals("dog8", dogMap.get("dog8").name);
        assertEquals("dog9", dogMap.get("dog9").name);

    }

}
