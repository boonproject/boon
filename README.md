Dec 22:
java -jar target/microbenchmarks.jar ".*" -wi 2 -i 10 -f 2 -t 8


Optimized number parsing for lax

```
Benchmark                                                        Mode Thr     Count  Sec         Mean   Mean error    Units
i.g.b.j.GatlingBoonBenchmark.parseBytesPrecompiledRoundRobin    thrpt   8        20    1   180456.895     1663.941    ops/s
i.g.b.j.GatlingBoonBenchmark.parseCharsPrecompiledRoundRobin    thrpt   8        20    1   179554.927     1889.281    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseBytesPrecompiledRoundRobin      thrpt   8        20    1   160238.785     6537.182    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseCharsPrecompiledRoundRobin      thrpt   8        20    1   158483.165     6443.420    ops/s
```

```
Benchmark                                                        Mode Thr     Count  Sec         Mean   Mean error    Units
i.g.b.j.GatlingBoonBenchmark.parseBytesPrecompiledRoundRobin    thrpt   8        20    1   180696.684      815.499    ops/s
i.g.b.j.GatlingBoonBenchmark.parseCharsPrecompiledRoundRobin    thrpt   8        20    1   174979.648     3126.555    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseBytesPrecompiledRoundRobin      thrpt   8        20    1   164817.132     2903.663    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseCharsPrecompiledRoundRobin      thrpt   8        20    1   167799.771     3027.962    ops/s
```


```
Benchmark                                                        Mode Thr     Count  Sec         Mean   Mean error    Units
i.g.b.j.GatlingBoonBenchmark.parseBytesPrecompiledRoundRobin    thrpt   8        20    1   180843.734     1128.916    ops/s
i.g.b.j.GatlingBoonBenchmark.parseCharsPrecompiledRoundRobin    thrpt   8        20    1   173554.706     2978.916    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseBytesPrecompiledRoundRobin      thrpt   8        20    1   158631.125     3448.064    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseCharsPrecompiledRoundRobin      thrpt   8        20    1   156412.933    10563.560    ops/s

```
Seems it made it slower for lax but faster for boon.

By itself

```
Benchmark                                                      Mode Thr     Count  Sec         Mean   Mean error    Units
i.g.b.j.LazyBoonJsonPathBM.parseBytesPrecompiledRoundRobin    thrpt   8        20    1   170684.429     3376.096    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseCharsPrecompiledRoundRobin    thrpt   8        20    1   161108.162     3246.075    ops/s
```

With more warmup (1 more)

```
Benchmark                                                        Mode Thr     Count  Sec         Mean   Mean error    Units
i.g.b.j.GatlingBoonBenchmark.parseBytesPrecompiledRoundRobin    thrpt   8        10    1   177902.067     3181.219    ops/s
i.g.b.j.GatlingBoonBenchmark.parseCharsPrecompiledRoundRobin    thrpt   8        10    1   173999.228     3256.031    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseBytesPrecompiledRoundRobin      thrpt   8        10    1   164292.095     3765.761    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseCharsPrecompiledRoundRobin      thrpt   8        10    1   159421.362     4799.286    ops/s
```
IDK. Inconsistent results.


After optimizing parse numbers on just fastparser not lax parser

```
Benchmark                                                        Mode Thr     Count  Sec         Mean   Mean error    Units
i.g.b.j.GatlingBoonBenchmark.parseBytesPrecompiledRoundRobin    thrpt   8        20    1   181154.149      853.376    ops/s
i.g.b.j.GatlingBoonBenchmark.parseCharsPrecompiledRoundRobin    thrpt   8        20    1   173765.456     3785.255    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseBytesPrecompiledRoundRobin      thrpt   8        20    1   163532.508     2479.094    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseCharsPrecompiledRoundRobin      thrpt   8        20    1   163687.094     2573.242    ops/s
```

```
Benchmark                                                        Mode Thr     Count  Sec         Mean   Mean error    Units
i.g.b.j.GatlingBoonBenchmark.parseBytesPrecompiledRoundRobin    thrpt   8        20    1   180125.347     1199.084    ops/s
i.g.b.j.GatlingBoonBenchmark.parseCharsPrecompiledRoundRobin    thrpt   8        20    1   176800.518     2313.143    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseBytesPrecompiledRoundRobin      thrpt   8        20    1   163876.991     9557.374    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseCharsPrecompiledRoundRobin      thrpt   8        20    1   164751.372     4207.011    ops/s
```


After optimizing skipwhitespace



```
Benchmark                                                        Mode Thr     Count  Sec         Mean   Mean error    Units
i.g.b.j.GatlingBoonBenchmark.parseBytesPrecompiledRoundRobin    thrpt   8        20    1   178765.577     2229.768    ops/s
i.g.b.j.GatlingBoonBenchmark.parseCharsPrecompiledRoundRobin    thrpt   8        20    1   174199.639     2819.390    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseBytesPrecompiledRoundRobin      thrpt   8        20    1   164392.211     4795.673    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseCharsPrecompiledRoundRobin      thrpt   8        20    1   163164.736     2556.659    ops/s
```

```
Benchmark                                                        Mode Thr     Count  Sec         Mean   Mean error    Units
i.g.b.j.GatlingBoonBenchmark.parseBytesPrecompiledRoundRobin    thrpt   8        20    1   180020.827     1676.876    ops/s
i.g.b.j.GatlingBoonBenchmark.parseCharsPrecompiledRoundRobin    thrpt   8        20    1   176785.194     3263.132    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseBytesPrecompiledRoundRobin      thrpt   8        20    1   162313.406     2656.726    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseCharsPrecompiledRoundRobin      thrpt   8        20    1   159877.643     9686.265    ops/s
```

```
Benchmark                                                              Mode Thr     Count  Sec         Mean   Mean error    Units
i.g.b.j.GatlingBoonBenchmark.parseBytesPrecompiledRoundRobin          thrpt   8        20    1   179390.544      991.578    ops/s
i.g.b.j.GatlingBoonBenchmark.parseCharsPrecompiledRoundRobin          thrpt   8        20    1   169935.569    11198.875    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseBytesPrecompiledRoundRobin            thrpt   8        20    1   159682.546     8256.127    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseCharsPrecompiledRoundRobin            thrpt   8        20    1   162384.147     3062.526    ops/s

i.g.b.j.GatlingJacksonBenchmark.parseBytesPrecompiledRoundRobin       thrpt   8        20    1   104227.318     2174.766    ops/s
i.g.b.j.GatlingJacksonBenchmark.parseStringPrecompiledRoundRobin      thrpt   8        20    1    80949.772     1241.775    ops/s
i.g.b.j.GatlingJsonSmartBenchmark.parseStringPrecompiledRoundRobin    thrpt   8        20    1    78965.211     1249.766    ops/s
i.g.b.j.JaywayJacksonBenchmark.parseBytesPrecompiledRoundRobin        thrpt   8        20    1    65212.748    13126.064    ops/s
i.g.b.j.JaywayJacksonBenchmark.parseStringPrecompiledRoundRobin       thrpt   8        20    1    51972.546    11263.750    ops/s
```

```
Benchmark                                                              Mode Thr     Count  Sec         Mean   Mean error    Units
i.g.b.j.GatlingBoonBenchmark.parseBytesPrecompiledRoundRobin          thrpt   8        20    1   176613.569      787.934    ops/s
i.g.b.j.GatlingBoonBenchmark.parseCharsPrecompiledRoundRobin          thrpt   8        20    1   172349.518     3992.700    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseBytesPrecompiledRoundRobin            thrpt   8        20    1   160389.828     3164.086    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseCharsPrecompiledRoundRobin            thrpt   8        20    1   164795.583     2695.261    ops/s

i.g.b.j.GatlingJacksonBenchmark.parseBytesPrecompiledRoundRobin       thrpt   8        20    1   101437.358     4610.474    ops/s
i.g.b.j.GatlingJacksonBenchmark.parseStringPrecompiledRoundRobin      thrpt   8        20    1    77044.002     3432.467    ops/s
i.g.b.j.GatlingJsonSmartBenchmark.parseStringPrecompiledRoundRobin    thrpt   8        20    1    78429.806     2191.953    ops/s
i.g.b.j.JaywayJacksonBenchmark.parseBytesPrecompiledRoundRobin        thrpt   8        20    1    64107.115    15146.686    ops/s
i.g.b.j.JaywayJacksonBenchmark.parseStringPrecompiledRoundRobin       thrpt   8        20    1    51684.554    12778.574    ops/s
```

```
Benchmark                                                              Mode Thr     Count  Sec         Mean   Mean error    Units
i.g.b.j.GatlingBoonBenchmark.parseBytesPrecompiledRoundRobin          thrpt   8        20    1   145593.767     1725.258    ops/s
i.g.b.j.GatlingBoonBenchmark.parseCharsPrecompiledRoundRobin          thrpt   8        20    1   144883.342     1044.217    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseBytesPrecompiledRoundRobin            thrpt   8        20    1   169421.773     1623.552    ops/s
i.g.b.j.LazyBoonJsonPathBM.parseCharsPrecompiledRoundRobin            thrpt   8        20    1   169955.733     1787.279    ops/s

i.g.b.j.GatlingJacksonBenchmark.parseBytesPrecompiledRoundRobin       thrpt   8        20    1   106658.228     2070.469    ops/s
i.g.b.j.GatlingJacksonBenchmark.parseStringPrecompiledRoundRobin      thrpt   8        20    1    84122.743     1707.541    ops/s
i.g.b.j.GatlingJsonSmartBenchmark.parseStringPrecompiledRoundRobin    thrpt   8        20    1    81782.459     1623.683    ops/s
i.g.b.j.JaywayJacksonBenchmark.parseBytesPrecompiledRoundRobin        thrpt   8        20    1    67230.863    14216.193    ops/s
i.g.b.j.JaywayJacksonBenchmark.parseStringPrecompiledRoundRobin       thrpt   8        20    1    56791.035    12197.784    ops/s
```

Dec 21:
```
Benchmark                                                              Mode Thr     Count  Sec         Mean   Mean error    Units
i.g.b.j.Gatling BoonBenchmark.parseBytesPrecompiledRoundRobin          thrpt   8         6    1   125093.867     2447.914    ops/s
i.g.b.j.Gatling BoonBenchmark.parseCharsPrecompiledRoundRobin          thrpt   8         6    1   117296.464     5713.760    ops/s
i.g.b.j.Lazy BoonJsonPathBM.parseBytesPrecompiledRoundRobin            thrpt   8         6    1    93338.431    12747.054    ops/s
i.g.b.j.Lazy BoonJsonPathBM.parseCharsPrecompiledRoundRobin            thrpt   8         6    1    96480.847     3097.341    ops/s

i.g.b.j.Gatling JacksonBenchmark.parseBytesPrecompiledRoundRobin       thrpt   8         6    1    83151.900     1084.591    ops/s
i.g.b.j.Gatling JacksonBenchmark.parseStringPrecompiledRoundRobin      thrpt   8         6    1    62074.683     1025.949    ops/s
i.g.b.j.Gatling JsonSmartBenchmark.parseStringPrecompiledRoundRobin    thrpt   8         6    1    58692.028     1583.224    ops/s
i.g.b.j.Jayway JacksonBenchmark.parseBytesPrecompiledRoundRobin        thrpt   8         6    1    23116.650    13294.959    ops/s
i.g.b.j.Jayway JacksonBenchmark.parseStringPrecompiledRoundRobin       thrpt   8         6    1    17084.089    12348.386    ops/s
$ java -jar target/microbenchmarks.jar ".*" -wi 3 -i 3 -f 2 -t 8

```
Dec 17: 0.3 released.

For the next 15 minutes, before Cowboy coder wakes up and tunes Jackson, Boon has the fastest JSON parser.
Boo yah!

https://github.com/RichardHightower/parsers-in-java


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
