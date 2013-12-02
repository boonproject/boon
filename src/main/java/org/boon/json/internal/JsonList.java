package org.boon.json.internal;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonList extends AbstractList<Object> implements List<Object> {

    List<Object> list = new ArrayList<> ( 5 );

    @Override
    public Object get ( int index ) {

        Object obj = list.get ( index );

        if ( obj instanceof ValueBase ) {
            obj = convert ( ( ValueBase ) obj );
            list.set ( index, obj );
        }
        return obj;

    }


    private Object convert ( ValueBase value ) {
        return value.toValue ();
    }

    @Override
    public int size () {
        return list.size ();
    }

    @Override
    public Iterator<Object> iterator () {
        convertAllIfNeeded ();
        return list.iterator ();
    }

    boolean converted = false;

    private void convertAllIfNeeded () {
        if ( !converted ) {
            converted = true;
            for ( int index = 0; index < list.size (); index++ ) {
                this.get ( index );
            }
        }

    }


    @Override
    public void clear () {
        list.clear ();
    }


    public boolean add(Object obj) {
         return list.add(obj);
    }



}
