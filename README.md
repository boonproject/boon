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
* find - finds an object (map, list, set, string, char sequence, array)
* findAll - finds a collection of objects (map, list, set, string, char sequence, array)


Possible future
* clone - deep copy (map, list, set, string, char sequence, array)
* str - converts object into readable string
* oStr - converts object into JSON
* sub - remove an item (map, list, set, string, char sequence, array)
* dir - list properties, methods and other information about a class

Code coverage shall always be 90% or above.
