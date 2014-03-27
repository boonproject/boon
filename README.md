```
__________                              _____          __   .__
\______   \ ____   ____   ____   /\    /     \ _____  |  | _|__| ____    ____
 |    |  _//  _ \ /  _ \ /    \  \/   /  \ /  \\__  \ |  |/ /  |/    \  / ___\
 |    |   (  <_> |  <_> )   |  \ /\  /    Y    \/ __ \|    <|  |   |  \/ /_/  >
 |______  /\____/ \____/|___|  / \/  \____|__  (____  /__|_ \__|___|  /\___  /
        \/                   \/              \/     \/     \/       \//_____/
     ____.                     ___________   _____    ______________.___.
    |    |____ ___  _______    \_   _____/  /  _  \  /   _____/\__  |   |
    |    \__  \\  \/ /\__  \    |    __)_  /  /_\  \ \_____  \  /   |   |
/\__|    |/ __ \\   /  / __ \_  |        \/    |    \/        \ \____   |
\________(____  /\_/  (____  / /_______  /\____|__  /_______  / / ______|
              \/           \/          \/         \/        \/  \/
```              

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

Boon is in maven central repo:

http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22io.fastjson%22%20AND%20a%3A%22boon%22


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




News in Boon
===

Boon 0.11 is out. Thanks Stephane Landelle!
Boon JSON parser still faster than GSON and Jackson. Up to 3x faster.

See and fork:
https://github.com/RichardHightower/json-parsers-benchmark

Added lightweight JSON DI container that supports @Inject, @PostConstruct, @Required, @Autowire, and more.

```java

public class CoffeeApp implements Runnable {
    @Inject
    CoffeeMaker coffeeMaker;
    @Inject
    Coffee coffee;
    @Inject
    Sugar sugar;
    @Inject
    Bacon bacon;
    @Inject
    @Named( "brown" )
    Bacon brownBacon;
```

JSON support now support @JsonProperty, @JsonView, and more.
Learn more here:
http://rick-hightower.blogspot.com/2014/01/boon-json-in-five-minutes-faster-json.html

Wrote invoker library to work JSON posts. It is a better way to do REST and WebSocket with Boon.

Wrote functional library based on work that I did with EasyJava.


You can do reflection based filters or regular Predicate filters.
```java

        List<Employee> list = list( new Employee("Bob"), new Employee("Sally") );
        setListProperty( list, "salary", 200 );
        list.addAll(Lists.list(new Employee("Rick"), new Employee("Joe")));

        //Reflection
        List<Employee> filtered = filterBy(list, new Object() {
            boolean t(Employee e) { return e.salary>150; }
        });


        ...

         //Predicate based
         List<Employee> filtered = filterBy(list, new Predicate<Employee>() {
                    @Override
                    public boolean test(Employee input) {
                        return input.salary > 150;
                    }
         });

```

My goal is take some previous work that I did with invoke dynamic and make the reflection based predicate faster than
 the Predicate interface.


You can also filter with static or non-static methods
```java

        List<Employee> filtered = filterBy(list, ListsTest.class, "filterBySalary");
        ...

        List<Employee> filtered = filterBy(list, this, "filterBySalaryMethod");
```

Also don't forget that Boon ships with a full in-memory query engine that is actually faster than the predicate based filters.

```java
      List<Employee> filtered = query( list, gt("salary", 150) );
```

Learn more about the Boon data repo here:

http://rick-hightower.blogspot.com/2013/11/what-if-java-collections-and-java.html


But I digress back to functional framework:

The usual suspects are here:

```java
        ...

        //Reflection Mapper -- Convert Employee object into HRObject
        List<HRObject> wrap = (List<HRObject>) mapBy(list, new Object() {
           HRObject hr(Employee e) {return new HRObject(e);}
        });

        ...
        //Reflection static or non-static methods
        List<HRObject> wrap = (List<HRObject>) mapBy(list, ListsTest.class, "createHRO" );

        List<HRObject> wrap = (List<HRObject>) mapBy(list, this, "createHROMethod" );

        ...
        //Constructor mapping
        List<Employee> list = list(new Employee("Bob"), new Employee("Sally"));
        List<HRObject> wrap = wrap(HRObject.class, list);
        ...


        //
        List<Employee> list =  list(  new Employee("Bob"), new Employee("Sally"));
        List<HRObject> wrap =  mapBy( list, new Function<Employee, HRObject>() {
            @Override
            public HRObject apply(Employee employee) {
                return new HRObject(employee);
            }
        });


```

Here is one you don't see much:

```java

    @Test
    public void reduce() {
      long sum =  (int) reduceBy(Lists.list(1,2,3,4,5,6,7,8), new Object() {
          int sum(int s, int b) {return s+b;}
      });

      boolean ok = sum == 36 || die();
      puts (sum);



      sum =  (long) reduceBy(new Integer[]{1,2,3,4,5,6,7,8}, new Object() {
            long sum(long s, int b) {return s+b;}
      });

      ok &= sum == 36 || die();



      sum =  (long) reduceBy(new int[]{1,2,3,4,5,6,7,8}, new Object() {
            long sum(long s, int b) {return s+b;}
      });

      ok &= sum == 36 || die();


       sum =   (long) reduceBy(Lists.list(1,2,3,4,5,6,7,8), new Reducer<Integer, Integer>() {
            @Override
            public Integer apply(Integer sum, Integer v) {
                return sum == null ? v : sum + v;
            }
        }).longValue();


    }

```

EasyJava had currying and all sorts of wild stuff, so I might port some of that here.
I might not. Let me know.

String Parsing.... Boon has really fast String parsing about 2x speed what you could do with JDK readily,
 and a smaller GC foot print so instead of this:

```java

    static Pattern newLine = Pattern.compile("(\n|\r)");
    ...
        int i=0;
        String[] splitLines = newLine.split(str);
        String[] stats;

        for (String line : splitLines) {
            stats = line.split( ",");
            i += Integer.parseInt(stats[1]);
        }
        return i;
```



You can do this (for 2x speed and a lot less GC overhead):

```java
        int i=0;
        String[] splitLines = splitLines(str);
        String[] stats;

        for (String line : splitLines) {
            stats = splitComma(line);
            i += Integer.parseInt(stats[1]);
        }
        return i;
```

Or even this (2.5x faster and even less GC overhead):

```java
        char[] chars = toCharArray(csv);
        int i=0;
        char[][] splitLines = splitLines(chars);
        char[][] stats;

        for (char[] line : splitLines) {
            stats = splitComma(line);
            i += parseInt(stats[1]);
        }
        return i;
```

Boon is not just fast. Boon is a flame throwing, turbo-charged, get-out-of-the-way-I-am-coming-in, speed-demon that sips GC, and makes
CPUs and virtual cores wish they went into dentistry.


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
