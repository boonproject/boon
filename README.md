Java Boon
====

Simple opinionated Java for the novice to expert level Java Programmer.


Birth of Boon
===

Boon came from my frustration when moving back and forth between Groovy, Ruby, Python and Java.
I do a lot of contract programming and consulting, and I do not always get to pick my programming language.
I found that I keep dragging libs around from project to project to fill the gaps for simple things like
slice notation or reading a file quickly or HTTP posting.

This is not to make Java a competitor to Python or Ruby or Groovy, but to say hey, I
don't always get to pick the language I am using when I write software, and more often
than not I am programming in Java. I might as well enjoy it.


Brief introduction to Boon
===

```java

    //Boon works with lists, arrays, sets, maps, sorted maps, etc.
    List<String> fruitList;
    String [] fruitArray;
    Set<String> veggiesSet;
    char [] letters;
    byte [] bytes;
    NavigableMap <Integer, String> favoritesMap;
    Map<String, Integer> map;


```

In Java a TreeMap is a SortedMap and a NavigableMap by the way.
Boon comes with helper methods that allow you to easily create lists,
sets, maps, concurrent maps, sorted maps, sorted sets, etc. The helper methods
are **safeList**, **list**, **set**, **sortedSet**, **safeSet**,
**safeSortedSet**, etc.

```java

    veggiesSet  =  set( "salad", "broccoli", "spinach");
    fruitList   =  list( "apple", "oranges", "pineapple");
    fruitArray  =  array( "apple", "oranges", "pineapple");
    letters     =  array( 'a', 'b', 'c');
    bytes       =  array( new byte[]{0x1, 0x2, 0x3, 0x4});
```

There are even methods to create maps and sorted maps
called **map**, **sortedMap**, **safeMap** (concurrent) and **sortedSafeMap**
(concurrent). These was mainly created because Java does not having
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

You can index maps, lists, arrays, using the **idx** operator.

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

left off here.
The idx o

     * Negative index works with list like, array like things.
     *
     * <blockquote>
     * <pre>
     *
     *          //Negative indexes
     *
     *          assert idx( fruitList, -2 ).equals("oranges");
     *
     *          assert idx( fruitArray, -2 ).equals("oranges");
     *
     *          assert idx( letters, -2 ) == 'b';
     *
     *          assert idx( bytes, -3 )   == 0x2;
     * </pre>
     * </blockquote>



