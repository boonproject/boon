package org.boon;


import org.boon.core.reflection.Reflection;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;

public class Sets {

    /* Creation */
    public static <V> Set<V> set ( Collection<V> collection ) {
        return new LinkedHashSet<> ( collection );
    }


<<<<<<< HEAD
    public static <V> Enumeration<V> enumeration ( final Set<V> set ) {
        final Iterator<V> iter = set.iterator ();
        return new Enumeration<V> () {
            @Override
            public boolean hasMoreElements () {
=======
    public static <V> Enumeration<V> enumeration( final Set<V> set ) {
        final Iterator<V> iter = set.iterator ();
        return new Enumeration<V> () {
            @Override
            public boolean hasMoreElements() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                return iter.hasNext ();
            }

            @Override
<<<<<<< HEAD
            public V nextElement () {
=======
            public V nextElement() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                return iter.next ();
            }
        };

    }


<<<<<<< HEAD
    public static <V> Set<V> set ( Class<V> clazz ) {
        return new LinkedHashSet<> ();
    }

    public static <V> Set<V> set ( Iterable<V> iterable ) {
=======
    public static <V> Set<V> set( Class<V> clazz ) {
        return new LinkedHashSet<> ();
    }

    public static <V> Set<V> set( Iterable<V> iterable ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        Set<V> set = new LinkedHashSet<> ();
        for ( V o : iterable ) {
            set.add ( o );
        }
        return set;
    }

<<<<<<< HEAD
    public static <V> Set<V> set ( Enumeration<V> enumeration ) {
=======
    public static <V> Set<V> set( Enumeration<V> enumeration ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        Set<V> set = new LinkedHashSet<> ();
        while ( enumeration.hasMoreElements () ) {
            set.add ( enumeration.nextElement () );
        }
        return set;
    }


<<<<<<< HEAD
    public static <V> Set<V> set ( Iterator<V> iterator ) {
=======
    public static <V> Set<V> set( Iterator<V> iterator ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        Set<V> set = new LinkedHashSet<> ();
        while ( iterator.hasNext () ) {
            set.add ( iterator.next () );
        }
        return set;
    }


    @SafeVarargs
    public static <V> Set<V> set ( final V... array ) {


        Set<V> set = new LinkedHashSet<> ();

        for ( V v : array ) {
            set.add ( v );
        }
        return set;
    }


<<<<<<< HEAD
    public static <V> NavigableSet<V> sortedSet ( Iterator<V> iterator ) {
=======
    public static <V> NavigableSet<V> sortedSet( Iterator<V> iterator ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        NavigableSet<V> set = new TreeSet<> ();
        while ( iterator.hasNext () ) {
            set.add ( iterator.next () );
        }
        return set;
    }

<<<<<<< HEAD
    public static <V> NavigableSet<V> sortedSet ( Class<V> clazz ) {
        return new TreeSet<> ();
    }

    public static <V> NavigableSet<V> sortedSet ( Iterable<V> iterable ) {
=======
    public static <V> NavigableSet<V> sortedSet( Class<V> clazz ) {
        return new TreeSet<> ();
    }

    public static <V> NavigableSet<V> sortedSet( Iterable<V> iterable ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        NavigableSet<V> set = new TreeSet<> ();
        for ( V o : iterable ) {
            set.add ( o );
        }
        return set;
    }

<<<<<<< HEAD
    public static <V> NavigableSet<V> sortedSet ( Enumeration<V> enumeration ) {
=======
    public static <V> NavigableSet<V> sortedSet( Enumeration<V> enumeration ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        NavigableSet<V> set = new TreeSet<> ();
        while ( enumeration.hasMoreElements () ) {
            set.add ( enumeration.nextElement () );
        }
        return set;
    }

    @SafeVarargs
<<<<<<< HEAD
    public static <V> NavigableSet<V> sortedSet ( final V... array ) {
=======
    public static <V> NavigableSet<V> sortedSet( final V... array ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        NavigableSet<V> set = new TreeSet<> ();

        for ( V v : array ) {
            set.add ( v );
        }
        return set;
    }

    public static <V> NavigableSet<V> sortedSet ( Collection<V> collection ) {
        return new TreeSet<> ( collection );
    }


<<<<<<< HEAD
    public static <V> NavigableSet<V> safeSortedSet ( Iterator<V> iterator ) {
=======
    public static <V> NavigableSet<V> safeSortedSet( Iterator<V> iterator ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        NavigableSet<V> set = new ConcurrentSkipListSet<> ();
        while ( iterator.hasNext () ) {
            set.add ( iterator.next () );
        }
        return set;
    }

<<<<<<< HEAD
    public static <V> NavigableSet<V> safeSortedSet ( Class<V> clazz ) {
        return new ConcurrentSkipListSet<> ();
    }

    public static <V> NavigableSet<V> safeSortedSet ( Iterable<V> iterable ) {
=======
    public static <V> NavigableSet<V> safeSortedSet( Class<V> clazz ) {
        return new ConcurrentSkipListSet<> ();
    }

    public static <V> NavigableSet<V> safeSortedSet( Iterable<V> iterable ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        NavigableSet<V> set = new ConcurrentSkipListSet<> ();
        for ( V o : iterable ) {
            set.add ( o );
        }
        return set;
    }

<<<<<<< HEAD
    public static <V> NavigableSet<V> safeSortedSet ( Enumeration<V> enumeration ) {
=======
    public static <V> NavigableSet<V> safeSortedSet( Enumeration<V> enumeration ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        NavigableSet<V> set = new ConcurrentSkipListSet<> ();
        while ( enumeration.hasMoreElements () ) {
            set.add ( enumeration.nextElement () );
        }
        return set;
    }

    @SafeVarargs
    public static <V> NavigableSet<V> safeSortedSet ( final V... array ) {

        NavigableSet<V> set = new ConcurrentSkipListSet<> ();

        for ( V v : array ) {
            set.add ( v );
        }
        return set;

    }


    public static <V> NavigableSet<V> safeSortedSet ( Collection<V> collection ) {
        return new ConcurrentSkipListSet<> ( collection );
    }

<<<<<<< HEAD
    public static <V> Set<V> safeSet ( Class<V> clazz ) {
        return new CopyOnWriteArraySet<> ();
    }

    public static <V> Set<V> safeSet ( Iterable<V> iterable ) {
=======
    public static <V> Set<V> safeSet( Class<V> clazz ) {
        return new CopyOnWriteArraySet<> ();
    }

    public static <V> Set<V> safeSet( Iterable<V> iterable ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        Set<V> set = new CopyOnWriteArraySet<> ();
        for ( V o : iterable ) {
            set.add ( o );
        }
        return set;
    }

<<<<<<< HEAD
    public static <V> Set<V> safeSet ( Enumeration<V> enumeration ) {
=======
    public static <V> Set<V> safeSet( Enumeration<V> enumeration ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        Set<V> set = new CopyOnWriteArraySet<> ();
        while ( enumeration.hasMoreElements () ) {
            set.add ( enumeration.nextElement () );
        }
        return set;
    }


<<<<<<< HEAD
    public static <V> Set<V> safeSet ( Iterator<V> iterator ) {
=======
    public static <V> Set<V> safeSet( Iterator<V> iterator ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        Set<V> set = new CopyOnWriteArraySet<> ();
        while ( iterator.hasNext () ) {
            set.add ( iterator.next () );
        }
        return set;
    }


    @SafeVarargs
<<<<<<< HEAD
    public static <V> Set<V> safeSet ( final V... array ) {
=======
    public static <V> Set<V> safeSet( final V... array ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        Set<V> set = new CopyOnWriteArraySet<> ();

        for ( V v : array ) {
            set.add ( v );
        }
        return set;
    }

    public static <V> Set<V> safeSet ( Collection<V> collection ) {
        return new CopyOnWriteArraySet<> ( collection );
    }


    @Universal
<<<<<<< HEAD
    public static int len ( Set<?> set ) {
=======
    public static int len( Set<?> set ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return set.size ();
    }

    @Universal
    public static <V> boolean in ( V value, Set<?> set ) {
        return set.contains ( value );
    }

    @Universal
    public static <V> void add ( Set<V> set, V value ) {
        set.add ( value );
    }

    @Universal
    public static <T> T idx ( NavigableSet<T> set, final T index ) {

        return set.higher ( index );
    }

    @Universal
    public static <T> T idx ( Set<T> set, final T index ) {

        if ( set instanceof NavigableSet ) {
            return idx ( ( NavigableSet<T> ) set, index );
        } else {
            throw new IllegalArgumentException ( "Set must be a NavigableSet for idx operation to work" );
        }
    }

    public static <T> T after ( NavigableSet<T> set, final T index ) {

        return set.higher ( index );
    }

    public static <T> T before ( NavigableSet<T> set, final T index ) {

        return set.lower ( index );
    }

    @Universal
    public static <V> SortedSet<V> slc ( NavigableSet<V> set, V startIndex, V endIndex ) {
        return set.subSet ( startIndex, endIndex );
    }


    @Universal
    public static <V> SortedSet<V> slcEnd ( NavigableSet<V> set, V fromIndex ) {
        return set.tailSet ( fromIndex );
    }


    @Universal
    public static <V> SortedSet<V> slc ( NavigableSet<V> set, V toIndex ) {
        return set.headSet ( toIndex );
    }

    @Universal
    public static <V> Set<V> copy ( HashSet<V> collection ) {
        return new LinkedHashSet<> ( collection );
    }

    @Universal
    public static <V> NavigableSet<V> copy ( TreeSet<V> collection ) {
        return new TreeSet<> ( collection );
    }

    @Universal
    public static <V> Set<V> copy ( CopyOnWriteArraySet<V> collection ) {
        return new CopyOnWriteArraySet<> ( collection );
    }

    @Universal
    public static <V> NavigableSet<V> copy ( ConcurrentSkipListSet<V> collection ) {
        return new ConcurrentSkipListSet<> ( collection );
    }


    @Universal
    public static <V> NavigableSet<V> copy ( NavigableSet<V> collection ) {
        if ( collection instanceof ConcurrentSkipListSet ) {
            return copy ( ( ConcurrentSkipListSet<V> ) collection );
        } else {
            return copy ( ( TreeSet<V> ) collection );
        }
    }


    @Universal
    public static <V> Set<V> copy ( Set<V> collection ) {
        if ( collection instanceof NavigableSet ) {

            return copy ( ( NavigableSet<V> ) collection );


        } else if ( collection instanceof CopyOnWriteArraySet ) {

            return copy ( ( CopyOnWriteArraySet<V> ) collection );

        } else {

            return copy ( ( LinkedHashSet<V> ) collection );
        }
    }


    //end universal

    public static List<Map<String, Object>> toListOfMaps ( Set<?> list ) {
        return Reflection.toListOfMaps ( list );

    }


}
