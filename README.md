boon
====

Simple opinionated Java for the novice to expert level Java Programmer.

Whenever possible no overloading (except for language literal simulations).
Whenever possible toString shall produce valid JSON.

Universal methods to work with list, appendable, strings, sets, maps, etc. if applicable:

* len   - length (map, list, set, string, char sequence, array)
* in    - checks if value is in (map (checks for key), list, set, string, char sequence, array)
* idx   - indexes object (map, list, set, string, char sequence, array)
* idx   - does set (map, list, set, string, char sequence, array)
* add   - adds item to object (map (adds entry), list, set, string, char sequence, array)
* copy  - shallow copy (map, list, set, string, char sequence, array)
* slc   - slices using slc(2, -2), slc(-2) syntax (map, list, set, string, char sequence, array)
* slcEnd - slices the end off
* before - tree maps, tree sets, (refers to the item before an "index")
* after -  tree maps, tree sets (refers to an item after an "index")
* insert - inserts an item into a list like thing. (so far for lists)
* remove or substract or sub or minus

Maps has valueIn which is like in.





Possible future   (maybe, maybe not)
* pop (list, array)
* push
* find - finds an object (map, list, set, string, char sequence, array)
* findAll - finds a collection of objects (map, list, set, string, char sequence, array)
* clone - deep copy (map, list, set, string, char sequence, array)
* str - converts object into readable string
* oStr - converts object into JSON
* sub - remove an item (map, list, set, string, char sequence, array)
* dir - list properties, methods and other information about a class
* dice - like slice but creates lists of lists or arrays of arrays, etc.
* divide - like dice but different - divide(2, list) would break up the list evenly into two lists.
* sum - add a list or array of values together
* max - find the max value in an array, map, set or list.
* min - find the min value in an array, map set or list.
* avg - find the avg value in an array, map, set or list.
* mode - find the mode
* median - find the median
* sort - add the ability to sort things (for lists array, if not primitive then
               check to see if comparable, then check if it has a toString defined,
               then do string order on toString)
* filter - add filtering (see DataRepo)
* see http://groovy.codehaus.org/GDK+Extensions+to+Object
* any
* every
* collect
* each
* each with index
* find and findAll
* equal (using truth)
* notEqual (using not truth)
* JMX support
* EL style lang support #{} (use Ruby style) since # is less common than $
* puts "Hello #{name.capitalize}!"
* puts ("Hello #{cap(name}", ctx(this))
* Create ReflectionUtils.respondsTo
* Create ReflectionUtils.invoke
* Create ReflectionUtils.ctx

<pre>

    if (respondsTo(bird, "fly")) {
        invoke(bird, "fly", far, high);
        puts ("Hello #{height}", ctx(bird));
    }

    if (respondsTo(bird, "fly", integer, integer)) {
        invoke(bird, "fly", far, high);
        puts ("Hello #{height}", ctx(bird));
    }

</pre>

* templates built in
* GPath style path support for any java object, map, json or dom.
* ObjectGraphBuilder would be nice for testing graphs for data repo
* queue utilities
* add Queue as a FCC to the mix
* Process support
* add Regex support
* JDBC support
* Mongo support



Code coverage shall always be 90% or above.


Ideas
http://docs.python.org/2/library/functions.html



TODO:
* Add comparators to Sets.
* Add Arrays (len, in, idx, copy, slc, slcEnd, insert, add, conversion from collection)
* Add Strings (split single char, split list of chars, slc, len, in, idx, copy, slc, slcEnd, insert, add)
* Write article on universal methods
* Add classpath:// and directory scanning support to IO
* Write article on I/O utilities and JDK 7 FileSystem
* Add Reflection utilities (deep dive properties)
* Write article
* Create universal Object to Map/List utility
* Write article
* Create universal Binary serializer / deserializer
* Write article
* Create universal JSON serializer / deserializer
* Write article
* Add DataRepo Support to boon
* Write article
* Create proxies using interface
* Write article
* Add support for Ruby respondsTo
