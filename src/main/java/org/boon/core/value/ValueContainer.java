package org.boon.core.value;


import org.boon.core.Type;
import org.boon.core.Value;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.boon.Exceptions.die;

public class ValueContainer implements CharSequence, Value {

    public static final Value TRUE = new ValueContainer ( Type.TRUE );
    public static final Value FALSE = new ValueContainer ( Type.FALSE );
    public static final Value NULL = new ValueContainer ( Type.NULL );

    public Object value;

    public Type type;
    private boolean container;

    public boolean decodeStrings;

    public ValueContainer(  Object value, Type type, boolean decodeStrings ) {
        this.value = value;
        this.type = type;
        this.decodeStrings = decodeStrings;
    }

    public ValueContainer( Type type ) {
        this.type = type;
    }

    public ValueContainer( Map<String, Object> map ) {
        this.value = map;
        this.type = Type.MAP;
        this.container = true;
    }

    public ValueContainer( List<Object> list ) {
        this.value = list;
        this.type = Type.LIST;
        this.container = true;
    }

    @Override
    public int intValue() {
        return die(int.class, "intValue not supported");
    }

    @Override
    public long longValue() {
        return die(long.class, "intValue not supported");
    }


    @Override
    public boolean booleanValue() {

        switch ( type ) {
            case FALSE:
                return false;
            case TRUE:
                return true;
        }
        die();
        return false;

    }


    @Override
    public String stringValue() {
        if (type == Type.NULL)  {
            return null;
        } else {
            return type.toString();
        }
    }

    @Override
    public String stringValueEncoded() {
        return toString();
    }


    public String toString() {
        return type.toString();
    }

    @Override
    public  Object toValue() {
        if ( value != null ) {
            return value;
        }
        switch ( type ) {
            case FALSE:
                return (value = false);

            case TRUE:
                return (value = true);
            case NULL:
                return null;
        }
        die();
        return null;

    }

    @Override
    public  <T extends Enum> T toEnum( Class<T> cls ) {
        return (T) value;
    }

    @Override
    public boolean isContainer() {
        return container;
    }

    @Override
    public void chop() {
    }

    @Override
    public char charValue () {
        return 0;
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


    @Override
    public Date dateValue() {
        return null;
    }


    public byte byteValue() {
           return 0;
    }

    public short shortValue() {
        return 0;
    }


    public BigDecimal bigDecimalValue() {
        return null;
    }

    public BigInteger bigIntegerValue() {
        return null;
    }


    @Override
    public double doubleValue() {
        return 0;
    }


    @Override
    public float floatValue() {
        return 0;
    }


}
