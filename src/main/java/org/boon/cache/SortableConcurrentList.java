package org.boon.cache;


import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SortableConcurrentList<T extends Comparable> implements List<T> {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock ();
    private final List<T> list;

<<<<<<< HEAD
    public SortableConcurrentList ( List<T> list ) {
        this.list = list;
    }

    public SortableConcurrentList () {
        this.list = new ArrayList<> ();
    }

    public boolean remove ( Object o ) {
=======
    public SortableConcurrentList( List<T> list ) {
        this.list = list;
    }

    public SortableConcurrentList() {
        this.list = new ArrayList<> ();
    }

    public boolean remove( Object o ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.writeLock ().lock ();
        boolean ret;
        try {
            ret = list.remove ( o );
        } finally {
            readWriteLock.writeLock ().unlock ();
        }
        return ret;
    }

    @Override
<<<<<<< HEAD
    public boolean containsAll ( Collection<?> c ) {
=======
    public boolean containsAll( Collection<?> c ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.readLock ().lock ();
        try {
            return list.containsAll ( c );
        } finally {
            readWriteLock.readLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public boolean addAll ( Collection<? extends T> c ) {
=======
    public boolean addAll( Collection<? extends T> c ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.writeLock ().lock ();
        try {
            return list.addAll ( c );
        } finally {
            readWriteLock.writeLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public boolean addAll ( int index, Collection<? extends T> c ) {
=======
    public boolean addAll( int index, Collection<? extends T> c ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.writeLock ().lock ();
        try {
            return list.addAll ( index, c );
        } finally {
            readWriteLock.writeLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public boolean removeAll ( Collection<?> c ) {
=======
    public boolean removeAll( Collection<?> c ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.writeLock ().lock ();
        try {
            return list.removeAll ( c );
        } finally {
            readWriteLock.writeLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public boolean retainAll ( Collection<?> c ) {
=======
    public boolean retainAll( Collection<?> c ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.writeLock ().lock ();
        try {
            return list.retainAll ( c );
        } finally {
            readWriteLock.writeLock ().unlock ();
        }
    }

<<<<<<< HEAD
    public boolean add ( T t ) {
=======
    public boolean add( T t ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.writeLock ().lock ();
        boolean ret;
        try {
            ret = list.add ( t );
        } finally {
            readWriteLock.writeLock ().unlock ();
        }
        return ret;
    }

<<<<<<< HEAD
    public void clear () {
=======
    public void clear() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.writeLock ().lock ();
        try {
            list.clear ();
        } finally {
            readWriteLock.writeLock ().unlock ();
        }
    }


<<<<<<< HEAD
    public int size () {
=======
    public int size() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.readLock ().lock ();
        try {
            return list.size ();
        } finally {
            readWriteLock.readLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public boolean isEmpty () {
=======
    public boolean isEmpty() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.readLock ().lock ();
        try {
            return list.isEmpty ();
        } finally {
            readWriteLock.readLock ().unlock ();
        }
    }

<<<<<<< HEAD
    public boolean contains ( Object o ) {
=======
    public boolean contains( Object o ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.readLock ().lock ();
        try {
            return list.contains ( o );
        } finally {
            readWriteLock.readLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public Iterator<T> iterator () {
=======
    public Iterator<T> iterator() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.readLock ().lock ();
        try {
            return new ArrayList<> ( list ).iterator ();
        } finally {
            readWriteLock.readLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public Object[] toArray () {
=======
    public Object[] toArray() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        readWriteLock.readLock ().lock ();
        try {
            return list.toArray ();
        } finally {
            readWriteLock.readLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public <T> T[] toArray ( final T[] a ) {
=======
    public <T> T[] toArray( final T[] a ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        readWriteLock.readLock ().lock ();
        try {
            return list.toArray ( a );
        } finally {
            readWriteLock.readLock ().unlock ();
        }
    }

<<<<<<< HEAD
    public T get ( int index ) {
=======
    public T get( int index ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.readLock ().lock ();
        try {
            return list.get ( index );
        } finally {
            readWriteLock.readLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public T set ( int index, T element ) {
=======
    public T set( int index, T element ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.writeLock ().lock ();
        try {
            return list.set ( index, element );
        } finally {
            readWriteLock.writeLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public void add ( int index, T element ) {
=======
    public void add( int index, T element ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.writeLock ().lock ();
        try {
            list.add ( index, element );
        } finally {
            readWriteLock.writeLock ().unlock ();
        }

    }

    @Override
<<<<<<< HEAD
    public T remove ( int index ) {
=======
    public T remove( int index ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.writeLock ().lock ();
        try {
            return list.remove ( index );
        } finally {
            readWriteLock.writeLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public int indexOf ( Object o ) {
=======
    public int indexOf( Object o ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.readLock ().lock ();
        try {
            return list.indexOf ( o );
        } finally {
            readWriteLock.readLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public int lastIndexOf ( Object o ) {
=======
    public int lastIndexOf( Object o ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.readLock ().lock ();
        try {
            return list.lastIndexOf ( o );
        } finally {
            readWriteLock.readLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public ListIterator<T> listIterator () {
=======
    public ListIterator<T> listIterator() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.readLock ().lock ();
        try {
            return new ArrayList ( list ).listIterator ();
        } finally {
            readWriteLock.readLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public ListIterator<T> listIterator ( int index ) {
=======
    public ListIterator<T> listIterator( int index ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.readLock ().lock ();
        try {
            return new ArrayList ( list ).listIterator ( index );
        } finally {
            readWriteLock.readLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public List<T> subList ( int fromIndex, int toIndex ) {
=======
    public List<T> subList( int fromIndex, int toIndex ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.readLock ().lock ();
        try {
            return list.subList ( fromIndex, toIndex );
        } finally {
            readWriteLock.readLock ().unlock ();
        }
    }

    @Override
<<<<<<< HEAD
    public String toString () {
=======
    public String toString() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.readLock ().lock ();
        try {
            return list.toString ();
        } finally {
            readWriteLock.readLock ().unlock ();
        }
    }


<<<<<<< HEAD
    public void sort () {
=======
    public void sort() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.writeLock ().lock ();
        try {

            Collections.sort ( list );
        } finally {
            readWriteLock.writeLock ().unlock ();
        }
    }


<<<<<<< HEAD
    public List<T> sortAndReturnPurgeList ( float removePercent ) {
=======
    public List<T> sortAndReturnPurgeList( float removePercent ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        readWriteLock.writeLock ().lock ();
        try {
            int size = list.size ();
            int removeSize = ( int ) ( size - ( size * removePercent ) );
            int start = size - removeSize;

            Collections.sort ( list );

            List<T> removeList = new ArrayList<> ( list.subList ( 0, start ) );
            list.removeAll ( removeList );
            return removeList;
        } finally {
            readWriteLock.writeLock ().unlock ();
        }
    }
}