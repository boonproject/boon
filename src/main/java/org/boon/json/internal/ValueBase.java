package org.boon.json.internal;


import org.boon.Dates;
import org.boon.core.reflection.Conversions;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.boon.Exceptions.die;

public class ValueBase extends Number implements Value, CharSequence, org.boon.core.Value {
    public int startIndex;
    public int endIndex;
    public Object value;

    public Type type;
    private boolean container;

    public boolean decodeStrings;

<<<<<<< HEAD
    public ValueBase ( int startIndex, int endIndex, Object value, Type type, boolean decodeStrings ) {
=======
    public ValueBase( int startIndex, int endIndex, Object value, Type type, boolean decodeStrings ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.value = value;
        this.type = type;
        this.decodeStrings = decodeStrings;
    }

    public ValueBase ( Type type ) {
        this.type = type;
    }

    public ValueBase ( Map<String, Object> map ) {
        this.value = map;
        this.type = Type.MAP;
        this.container = true;
    }

    public ValueBase ( List<Object> list ) {
        this.value = list;
        this.type = Type.LIST;

        this.container = true;
    }

    public ValueBase () {
    }


    @Override
<<<<<<< HEAD
    public int intValue () {
=======
    public int intValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return Integer.parseInt ( toString () );
    }

    @Override
<<<<<<< HEAD
    public long longValue () {
=======
    public long longValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return Long.parseLong ( toString () );
    }


    @Override
    public boolean booleanValue () {

        switch ( type ) {
            case FALSE:
                return false;
            case TRUE:
                return true;
        }
        die ();
        return false;

    }


    @Override
    public String stringValue () {
        return type.toString ();
    }

    @Override
    public String stringValueEncoded () {
        return toString ();
    }


<<<<<<< HEAD
    public String toString () {
=======
    public String toString() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return type.toString ();
    }

    @Override
<<<<<<< HEAD
    public Object toValue () {
=======
    public Object toValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        if ( value != null ) {
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
        die ();
        return null;

    }

    @Override
    public Enum toEnum ( Class<? extends Enum> cls ) {
        switch ( type ) {
            case STRING:
                return Conversions.toEnum ( cls, stringValue () );
            case INTEGER:
                return Conversions.toEnum ( cls, intValue () );
            case NULL:
                return null;
        }
        die ( "toEnum " + cls + " value was " + stringValue () );
        return null;

    }

    @Override
    public boolean isContainer () {
        return container;

    }

    @Override
    public void chop () {
    }

    @Override
    public int length () {
        return 0;
    }

    @Override
    public char charAt ( int index ) {
        return '0';
    }

    @Override
    public CharSequence subSequence ( int start, int end ) {
        return "";
    }


    @Override
    public Date dateValue () {


        if ( type == Type.STRING ) {

            String string = stringValue ();

            return Dates.fromISO8601 ( string );
        } else {

            return new Date ( Dates.utc ( longValue () ) );
        }

    }


<<<<<<< HEAD
    public byte byteValue () {
        return Byte.parseByte ( toString () );
    }

    public short shortValue () {
=======
    public byte byteValue() {
        return Byte.parseByte ( toString () );
    }

    public short shortValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return Short.parseShort ( toString () );
    }


<<<<<<< HEAD
    public BigDecimal bigDecimalValue () {
        return new BigDecimal ( toString () );
    }

    public BigInteger bigIntegerValue () {
=======
    public BigDecimal bigDecimalValue() {
        return new BigDecimal ( toString () );
    }

    public BigInteger bigIntegerValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return new BigInteger ( toString () );
    }


    @Override
<<<<<<< HEAD
    public double doubleValue () {
=======
    public double doubleValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return Double.parseDouble ( toString () );

    }


    @Override
<<<<<<< HEAD
    public float floatValue () {
=======
    public float floatValue() {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        return Float.parseFloat ( toString () );
    }


}
