package org.boon.datarepo.impl.indexes;

import org.boon.datarepo.LookupIndex;
import org.boon.datarepo.spi.SPIFactory;
import org.boon.predicates.Function;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.boon.Lists.list;

public class UniqueLookupIndex<KEY, ITEM> implements LookupIndex<KEY, ITEM> {

    protected Function<ITEM, KEY> keyGetter;
    protected Map<KEY, ITEM> map = null;


    private Logger log = Logger.getLogger ( UniqueLookupIndex.class.getName () );

    private Function<Object, KEY> keyTransformer;

    public UniqueLookupIndex ( Class<?> keyType ) {
        if ( keyType == null ) {
            return;
        }
        map = SPIFactory.getMapCreatorFactory ().get ().createMap ( keyType );

    }

    @Override
    public ITEM get ( KEY key ) {
        key = getKey ( key );
        return map.get ( key );
    }

    @Override
    public void setKeyGetter ( Function<ITEM, KEY> keyGetter ) {
        this.keyGetter = keyGetter;
    }

    @Override
    public boolean add ( ITEM item ) {

        if ( log.isLoggable ( Level.FINE ) ) {
            log.fine ( String.format ( "add item = %s", item ) );
        }

        KEY key = keyGetter.apply ( item );
        if ( key == null ) {
            return false;
        }

        /* You can not add the same key twice. */
        if ( this.map.containsKey ( key ) ) {
            return false;
        }

        map.put ( key, item );
        return true;

    }

    @Override
    public boolean delete ( ITEM item ) {

        if ( log.isLoggable ( Level.FINE ) ) {
            log.fine ( String.format ( "delete item = %s", item ) );
        }

        KEY key = keyGetter.apply ( item );
        key = getKey ( key );
        return map.remove ( key ) != null;
    }

    @Override
<<<<<<< HEAD
    public List<ITEM> all () {
=======
    public List<ITEM> all() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

        if ( log.isLoggable ( Level.FINE ) ) {
            log.fine ( "all called " );
        }

        return new ArrayList<> ( map.values () );
    }

    @Override
    public List<ITEM> getAll ( KEY key ) {

        if ( log.isLoggable ( Level.FINE ) ) {
            log.fine ( "getAll called " );
        }

        return list ( this.get ( key ) );
    }

    @Override
<<<<<<< HEAD
    public int size () {
=======
    public int size() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return this.map.size ();
    }

    @Override
<<<<<<< HEAD
    public Collection<ITEM> toCollection () {
=======
    public Collection<ITEM> toCollection() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new HashSet ( this.map.values () );
    }

    @Override
<<<<<<< HEAD
    public void clear () {
=======
    public void clear() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        this.map.clear ();
    }

    @Override
    public boolean deleteByKey ( KEY key ) {
        key = getKey ( key );
        return this.map.remove ( key ) != null;
    }

    @Override
<<<<<<< HEAD
    public boolean isPrimaryKeyOnly () {
=======
    public boolean isPrimaryKeyOnly() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return false;
    }

    @Override
<<<<<<< HEAD
    public void init () {
=======
    public void init() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

    }

    @Override
    public boolean has ( KEY key ) {
        if ( key == null ) {
            return false;
        }
        return this.map.containsKey ( key );
    }


    @Override
    public void setInputKeyTransformer ( Function<Object, KEY> func ) {
        this.keyTransformer = func;
    }

    @Override
    public void setBucketSize ( int size ) {

    }

    protected KEY getKey ( KEY key ) {
        if ( keyTransformer != null ) {
            key = this.keyTransformer.apply ( key );
        }
        return key;
    }


}
