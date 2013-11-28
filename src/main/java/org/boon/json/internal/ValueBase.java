package org.boon.json.internal;


import java.util.List;
import java.util.Map;

import static org.boon.Exceptions.die;

public  class ValueBase extends Number implements Value, CharSequence {
    public int startIndex;
    public int endIndex;
    public Object value;

    public Type type;




    public ValueBase( Type type ) {
        this.type = type;
    }

    public ValueBase( Map<String, Object> map ) {
        this.value = map;
        this.type = Type.MAP;
    }

    public ValueBase( List<Object> list ) {
        this.value = list;
        this.type = Type.LIST;
    }

    public ValueBase() {
    }

    @Override
    public int intValue() {
        return 0;
    }

    @Override
    public long longValue() {
        return 0;
    }

    @Override
    public float floatValue() {
        return 0;
    }

    @Override
    public double doubleValue() {
        return 0;
    }

    @Override
    public String toKey() {
        return null;
    }


    public String toString() {
        return type.toString ();
    }

    @Override
    public Object toValue() {
        if (value!=null) {
            return value;
        }
        switch ( type ) {
            case FALSE:
                return false;
            case TRUE:
                return true;
            case NULL:
                return null;
        }
        die();
        return null;

    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt( int index ) {
        return '0';
    }

    @Override
    public CharSequence subSequence( int start, int end ) {
        return "";
    }
}
