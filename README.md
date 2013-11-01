Java Boon
====

Simple opinionated Java for the novice to expert level Java Programmer.

Low Ceremony. High Productivity.





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



Right now I have a JDK 1.8 branch and and a JDK 1.7 branch.

Why Boon
====
Easily read in files into lines or a giant string with one method call.
Slice notation for dealing with Strings, Lists, primitive arrays, etc.
If you are from Groovy land, Ruby land, Python land, or whatever land, and you have to use
Java then Boon might give you some relief.

Core Boon Philosophy
===
Core Boon will never have any dependencies.
It will always be able to run as a single jar.


Further Reading
===


+ [Boon's Universal Operations and the BBBB Java does have a ByteBuilder sort of](https://github.com/RichardHightower/boon/wiki/Boon's-Byte-Buffer-Builder)
+ [Java Boon Sets and slice notation](https://github.com/RichardHightower/boon/wiki/Sets-and-Slice-Notation-for-Java-Boon!)
+ [Java Boon Slice notation](https://github.com/RichardHightower/boon/wiki/Boon-Slice-Notation)
+ [Random thoughts and TODO](https://github.com/RichardHightower/boon/wiki/Random-thoughts-and-TODO-for-Boon)
+ [Birth of Boon](https://github.com/RichardHightower/boon/wiki/Birth-of-Boon)
+ [Javadocs](http://richardhightower.github.io/site/javadocs/index.html)
+ [jar file](http://richardhightower.github.io/site/releases/)
