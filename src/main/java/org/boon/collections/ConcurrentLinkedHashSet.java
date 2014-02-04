package org.boon.collections;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConcurrentLinkedHashSet<T> implements Set<T> {

    List<T> list = new CopyOnWriteArrayList<>();
    Set<T> set = new ConcurrentHashSet<>();

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return set.isEmpty();
    }

    @Override
    public boolean contains( Object o ) {
        return set.contains( o );
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray( new Object[list.size()] );
    }

    @Override
    public <T1> T1[] toArray( T1[] a ) {

        return list.toArray( a );
    }

    @Override
    public synchronized boolean add( T t ) {
        list.remove( t );
        list.add( t );
        return set.add( t );
    }


    public synchronized boolean addFirst( T t ) {
        list.remove( t );
        list.add(0, t );
        return set.add( t );
    }

    @Override
    public synchronized boolean remove( Object o ) {
        list.remove( o );
        return set.remove( o );
    }

    @Override
    public boolean containsAll( Collection<?> c ) {
        return set.containsAll( c );
    }

    @Override
    public boolean addAll( Collection<? extends T> c ) {
        return set.addAll( c );
    }

    @Override
    public synchronized boolean retainAll( Collection<?> c ) {
        list.retainAll( c );
        return set.retainAll( c );
    }

    @Override
    public synchronized boolean removeAll( Collection<?> c ) {
        list.removeAll( c );
        return set.removeAll( c );
    }

    @Override
    public synchronized void clear() {
        set.clear();
        list.clear();
    }
}
