License
=======
*Apache 2* 


Philosophy
==========
Use it as you wish.  Give me some credit if you fork it or copy major portions of it. 
Use the lib or copy it into your code, tweak it. 
Blog about. Use it. Give me feedback. I am doing this for the street cred and to learn.




Java Boon
====

Simple opinionated Java for the novice to expert level Java Programmer.

Low Ceremony. High Productivity.

Boon Home: http://richardhightower.github.io/site/Boon/Welcome.html

There is a maven repo at:

http://richardhightower.github.io/site/releases/mvn/repo/



Brief introduction to Boon
===

Here are some basic Java types, list, array, veggies, primitive char array,
and a primitive byte array.


```java

    //Boon works with lists, arrays, sets, maps, sorted maps, etc.
    List<String> fruitList;
    String [] fruitArray;
    Set<String> veggiesSet;
    char [] letters;
    byte [] bytes;
    NavigableMap <Integer, String> favoritesMap;
    Map<String, Integer> map;

    //In Java a TreeMap is a SortedMap and a NavigableMap by the way.


```

Boon comes with helper methods that allow you to easily create lists,
sets, maps, concurrent maps, sorted maps, sorted sets, etc. The helper methods
are **safeList**, **list**, **set**, **sortedSet**, **safeSet**,
**safeSortedSet**, etc. The idea is to make Java feel more
like list and maps are built in types.

```java

    veggiesSet  =  set( "salad", "broccoli", "spinach");
    fruitList   =  list( "apple", "oranges", "pineapple");
    fruitArray  =  array( "apple", "oranges", "pineapple");
    letters     =  array( 'a', 'b', 'c');
    bytes       =  array( new byte[]{0x1, 0x2, 0x3, 0x4});
```

There are even methods to create maps and sorted maps
called **map**, **sortedMap**, **safeMap** (concurrent) and **sortedSafeMap**
(concurrent). These were mainly created because Java does not have
literals for lists, maps, etc.


```java

     favoritesMap = sortedMap(
            2, "pineapple",
            1, "oranges",
            3, "apple"
     );


     map =    map (
        "pineapple",  2,
        "oranges",    1,
        "apple",      3
     );

```

You can index maps, lists, arrays, etc. using the **idx** operator.

```java

     //Using idx to access a value.

     assert idx( veggiesSet, "b").equals("broccoli");

     assert idx( fruitList, 1 ).equals("oranges");

     assert idx( fruitArray, 1 ).equals("oranges");

     assert idx( letters, 1 ) == 'b';

     assert idx( bytes, 1 )      == 0x2;

     assert idx( favoritesMap, 2 ).equals("pineapple");

     assert idx( map, "pineapple" )  == 2;

```

The **idx** operators works with negative indexes as well.

```java


               //Negative indexes

                assert idx( fruitList, -2 ).equals("oranges");

                assert idx( fruitArray, -2 ).equals("oranges");

                assert idx( letters, -2 ) == 'b';

                assert idx( bytes, -3 )   == 0x2;

```

*Ruby, Groovy and Python* have this feature. Now you can use this in Java as well.
The Java version (Boon) works with primitive arrays (with no autoboxing).

Boon has the concept of universal operators similar to Python like **len**.

```java


     // Getting the length
     assert len( veggiesSet )        == 3;
     assert len( fruitList )         == 3;
     assert len( fruitArray )        == 3;
     assert len( letters )           == 3;
     assert len( bytes )             == 4;
     assert len( favoritesMap )      == 3;
     assert len( map )               == 3;

```


Boon utility methods
===

Boon can read in an entire file in one line of code:

```java
        File testFile = new File(testDir, "testfile.txt");
        List<String> lines = IO.readLines(testFile);
```

No really!

```java
        File testFile = new File(testDir, "testfile.txt");
        List<String> lines = IO.readLines("~/github/boon/testfiles/testfile.txt");
```

There is also support for lambda expressions:

```java
        File testFile = new File(testDir, "testfile.txt");



        IO.eachLine(testFile.toString(), (line, index) -> {
            System.out.println(index + " " + line);
            return true;
        });

        }

```

The readLines and read methods can read from URIs as well:

```java
        List<String> lines = IO.readLines("http://localhost:9666/test");
```







Thoughts
===

Thoughts? Write me at richard high tower AT g mail dot c-o-m (Rick Hightower).

Further Reading:
===

If you are new to boon start here: 

* [Java Boon Byte Buffer Builder](https://github.com/RichardHightower/boon/wiki/Boon's-Byte-Buffer-Builder)
* [Java Boon Slice Notation](https://github.com/RichardHightower/boon/wiki/Boon-Slice-Notation)
* [Java Boon Slice's work with TreeSets](https://github.com/RichardHightower/boon/wiki/Sets-and-Slice-Notation-for-Java-Boon!)
* [Java Boon Description](https://github.com/RichardHightower/boon/wiki)
* [More...](https://github.com/RichardHightower/boon/wiki/_pages)
* [Boon Home](https://github.com/RichardHightower/boon/wiki)
* [Boon Source](https://github.com/RichardHightower/boon/wiki)
* [Introducing Boon October 2013](http://rick-hightower.blogspot.com/2013/10/introducing-boon-for-java.html)
* [Java Slice Notation](http://rick-hightower.blogspot.com/2013/10/java-slice-notation-to-split-up-strings.html)
* [What if Java collections were easy to search and sort?](http://rick-hightower.blogspot.com/2013/11/what-if-java-collections-and-java.html)
* [Boon HTTP utils](http://rick-hightower.blogspot.com/2013/11/stackoverflow-question-on-posting-http.html)
* [Boon Java JSON parser Benchmarks or hell yeah JSON parsing is damn fast!](http://rick-hightower.blogspot.com/2013/11/benchmark-for-json-parsing-boon-scores.html)
* [Boon JSON parser is really damn fast! Part II](http://rick-hightower.blogspot.com/2013/12/boon-fastest-way-to-turn-json-into.html)
* [Boon JSON parser Round III now just not fast as but much faster than other Java JSON parsers](http://rick-hightower.blogspot.com/2013/12/here-we-go-again-latest-round-of.html)
* [Boon World's fastest Java JSON parser Round IV from fast to blazing to rocket fuel aka Braggers going to brag](http://rick-hightower.blogspot.com/2013/12/worlds-fastest-json-parser.html)
* [Boon gets adopted by JSON Path as the default Java JSON parser](http://rick-hightower.blogspot.com/2013/12/jsonpath-decides-boon-is-fastest-way-to.html)
* [Boon graphics showing just how fast Boon JSON parsing is - about 50% to 200% faster than the graphs shown here now so wicked fast became wickeder - just got sick of making graphics](http://rick-hightower.blogspot.com/2013/12/boon-json-parser-seems-to-be-fastest.html)
* [10 minute guide to Boon JSON parsing after I added @JsonIgnore, @JsonProperty, @JsonView, @Exposes, etc.](http://rick-hightower.blogspot.com/2014/01/boon-json-in-five-minutes-faster-json.html)
* [Hightower speaks to the master of Java JSON parsing, the king of speed The COW TOWN CODER!](http://rick-hightower.blogspot.com/2014/01/boon-jackson-discussion-between.html)
* [Boon provides easy Java objects from lists, from maps and from JSON.](http://rick-hightower.blogspot.com/2014/02/boon-fromlist-frommap-and-fromjson.html)


Why Boon?
====
Easily read in files into lines or a giant string with one method call.
Works with files, URLs, class-path, etc. Boon IO support will surprise you how easy it is.
Boon has Slice notation for dealing with Strings, Lists, primitive arrays, Tree Maps, etc.
If you are from Groovy land, Ruby land, Python land, or whatever land, and you have to use
Java then Boon might give you some relief from API bloat. 
If you are like me, and you like to use Java, then Boon is for you too.
Boon lets Java be Java, but adds the missing productive APIs from Python, Ruby, and Groovy.
Boon may not be Ruby or Groovy, but its a real Boon to Java development.

Core Boon Philosophy
===
Core Boon will never have any dependencies.
It will always be able to run as a single jar.
This is not just NIH, but it is partly.
My view of what Java needs is more inline with what Python, Ruby and Groovy provide.
Boon is an addition on top of the JVM to make up the difference between the harder to use APIs that come with Java and the types of utilities that are built into Ruby, Python, PHP, Groovy etc. 
Boon is a Java centric view of those libs. 
The vision of Boon and the current implementation is really far apart.


===

Contact Info


[blog](http://rick-hightower.blogspot.com/)|[twitter](https://twitter.com/RickHigh|[infoq]http://www.infoq.com/author/Rick-Hightower|[stackoverflow](http://stackoverflow.com/users/2876739/rickhigh)|[java lobby](http://java.dzone.com/users/rhightower)|Other | richard high tower AT g mail dot c-o-m (Rick Hightower)|[work](http://www.mammatustech.com/)|[cloud](http://cloud.mammatustech.com/)|[nosql](http://nosql.mammatustech.com/)
