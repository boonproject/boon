package org.boon.datarepo.impl.indexes;

import org.boon.datarepo.spi.SearchIndex;
import org.boon.predicates.Function;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;


/**
 * Allows more complex indexes.
 *
 * @see NestedKeySearchIndex
 * @see TypeHierarchyIndex
 */
public abstract class BaseIndexWrapper implements SearchIndex {
    protected final String[] path;
    protected SearchIndexDefault index = new SearchIndexDefault ( Object.class );

    public BaseIndexWrapper ( String... path ) {
        this.path = path;
    }

    @Override
<<<<<<< HEAD
    public Object findFirst () {
=======
    public Object findFirst() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return index.findFirst ();
    }

    @Override
<<<<<<< HEAD
    public Object findLast () {
=======
    public Object findLast() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return index.findLast ();
    }

    @Override
<<<<<<< HEAD
    public Object findFirstKey () {
=======
    public Object findFirstKey() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return index.findFirstKey ();
    }

    @Override
<<<<<<< HEAD
    public Object findLastKey () {
=======
    public Object findLastKey() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return index.findLastKey ();
    }

    @Override
    public List findEquals ( Object o ) {
        return index.findEquals ( o );
    }

    @Override
    public List findStartsWith ( Object keyFrag ) {
        return index.findEquals ( keyFrag );
    }

    @Override
    public List findEndsWith ( Object keyFrag ) {
        return index.findEndsWith ( keyFrag );
    }

    @Override
    public List findContains ( Object keyFrag ) {
        return index.findContains ( keyFrag );
    }

    @Override
    public List findBetween ( Object start, Object end ) {
        return index.findBetween ( start, end );
    }

    @Override
    public List findGreaterThan ( Object o ) {
        return index.findGreaterThan ( o );
    }

    @Override
    public List findLessThan ( Object o ) {
        return index.findLessThan ( o );
    }

    @Override
    public List findGreaterThanEqual ( Object o ) {
        return index.findGreaterThanEqual ( o );

    }

    @Override
    public List findLessThanEqual ( Object o ) {
        return index.findLessThanEqual ( o );
    }

    @Override
<<<<<<< HEAD
    public Object min () {
=======
    public Object min() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return index.min ();
    }

    @Override
<<<<<<< HEAD
    public Object max () {
=======
    public Object max() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return index.max ();
    }

    @Override
    public int count ( Object o ) {
        return index.count ( o );
    }

    @Override
    public void setComparator ( Comparator collator ) {
        index.setComparator ( collator );
    }

    @Override
    public Object get ( Object o ) {
        return index.get ( o );
    }

    @Override
    public void setKeyGetter ( Function keyGetter ) {
        index.setKeyGetter ( keyGetter );
    }

    @Override
    public List getAll ( Object o ) {
        return index.getAll ( o );
    }

    @Override
    public boolean deleteByKey ( Object o ) {
        return index.deleteByKey ( o );
    }

    @Override
<<<<<<< HEAD
    public boolean isPrimaryKeyOnly () {
=======
    public boolean isPrimaryKeyOnly() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return index.isPrimaryKeyOnly ();
    }

    @Override
<<<<<<< HEAD
    public void init () {
=======
    public void init() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        index.init ();
    }

    @Override
    public void setInputKeyTransformer ( Function func ) {
        index.setInputKeyTransformer ( func );
    }

    @Override
    public abstract boolean add ( Object o );

    protected abstract List getKeys ( Object o );

    @Override
    public abstract boolean delete ( Object o );

    @Override
<<<<<<< HEAD
    public List all () {
=======
    public List all() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return index.all ();
    }

    @Override
<<<<<<< HEAD
    public int size () {
=======
    public int size() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return index.size ();
    }

    @Override
<<<<<<< HEAD
    public Collection toCollection () {
=======
    public Collection toCollection() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return index.toCollection ();
    }

    @Override
<<<<<<< HEAD
    public void clear () {
=======
    public void clear() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        index.clear ();
    }

    public void setBucketSize ( int size ) {
        index.setBucketSize ( size );
    }


    public boolean has ( Object key ) {
        return index.has ( key );
    }
}
