package org.boon.json.internal;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonList extends AbstractList<Object> implements List<Object> {

    List<Object> list = new ArrayList<>( 5 );

    private final boolean lazyChop;


    public JsonList ( boolean lazyChop ) {
        this.lazyChop = lazyChop;
    }

    @Override
    public Object get ( int index ) {

        Object obj = list.get( index );

        if ( obj instanceof ValueBase ) {
            obj = convert( ( ValueBase ) obj );
            list.set( index, obj );
        }

        chopIfNeeded( obj );
        return obj;

    }


    private Object convert ( ValueBase value ) {
        return value.toValue();
    }

    @Override
    public int size () {
        return list.size();
    }

    @Override
    public Iterator<Object> iterator () {
        convertAllIfNeeded();
        return list.iterator();
    }

    boolean converted = false;

    private void convertAllIfNeeded () {
        if ( !converted ) {
            converted = true;
            for ( int index = 0; index < list.size(); index++ ) {
                this.get( index );
            }
        }

    }


    @Override
    public void clear () {
        list.clear();
    }


    public boolean add ( Object obj ) {
        return list.add( obj );
    }


    public void chopList () {

        for ( Object obj : list ) {
            if ( obj == null ) continue;

            if ( obj instanceof Value ) {
                Value value = ( Value ) obj;
                if ( value.isContainer() ) {
                    chopContainer( value );
                } else {
                    value.chop();
                }
            }
        }
    }

    private void chopIfNeeded ( Object object ) {
        if ( lazyChop ) {
            if ( object instanceof JsonMap ) {
                JsonMap m = ( JsonMap ) object;
                m.chopMap();
            } else if ( object instanceof JsonList ) {
                JsonList list = ( JsonList ) object;
                list.chopList();
            }
        }

    }


    void chopContainer ( Value value ) {
        Object obj = value.toValue();
        if ( obj instanceof JsonMap ) {
            JsonMap map = ( JsonMap ) obj;
            map.chopMap();
        } else if ( obj instanceof JsonList ) {
            JsonList list = ( JsonList ) obj;
            list.chopList();
        }
    }
}
