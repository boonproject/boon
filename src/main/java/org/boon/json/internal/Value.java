package org.boon.json.internal;

import org.boon.json.JSONStringParser;

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
                return JSONStringParser.decode ( buffer, startIndex, endIndex );
        }
        die();
        return null;
    }

}
