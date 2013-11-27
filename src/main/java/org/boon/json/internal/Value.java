package org.boon.json.internal;

import org.boon.json.JsonStringDecoder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.boon.Exceptions.die;

public class Value {

    public Type type;
    public char [] buffer;
    public int startIndex;
    public int endIndex;

    public Object value;

    public static final Value TRUE = new Value(Type.TRUE);

    public static final Value FALSE = new Value(Type.FALSE);


    public static final Value NULL = new Value(Type.NULL);


    public Value ( Type type ) {
        this.type = type;
    }

    public Value ( Map<String, Object> map ) {
        this.value = map;
        this.type = Type.MAP;
    }

    public Value ( List<Object> list ) {
        this.value = list;
        this.type = Type.LIST;
    }

    public Value (  ) {
    }

    public String toString() {
        return new String(buffer, startIndex, endIndex - startIndex);
    }

    public String toKey() {
        return new String(buffer, startIndex+1, (endIndex - startIndex) -1  );
    }

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
            case DOUBLE:
                return Double.parseDouble ( toString ()  );
            case INTEGER:
                String str = toString ();
                try {
                    return Integer.parseInt ( str );
                } catch (Exception ex) {
                    return Long.parseLong ( str  );
                }
            case STRING:
                return JsonStringDecoder.decode ( buffer, startIndex, endIndex );
        }
        die();
        return null;
    }

    @Override
    public boolean equals ( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Value ) ) return false;

        Value value1 = ( Value ) o;

        if ( endIndex != value1.endIndex ) return false;
        if ( startIndex != value1.startIndex ) return false;
        if ( !Arrays.equals ( buffer, value1.buffer ) ) return false;
        if ( type != value1.type ) return false;
        if ( value != null ? !value.equals ( value1.value ) : value1.value != null ) return false;

        return true;
    }

    @Override
    public int hashCode () {
        int result = type != null ? type.hashCode () : 0;
        result = 31 * result + ( buffer != null ? Arrays.hashCode ( buffer ) : 0 );
        result = 31 * result + startIndex;
        result = 31 * result + endIndex;
        result = 31 * result + ( value != null ? value.hashCode () : 0 );
        return result;
    }
}
