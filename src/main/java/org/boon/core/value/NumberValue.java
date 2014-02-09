package org.boon.core.value;

import org.boon.Exceptions;
import org.boon.core.Conversions;
import org.boon.core.Dates;
import org.boon.core.Type;
import org.boon.core.Value;
import org.boon.core.reflection.FastStringUtils;
import org.boon.primitive.CharScanner;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.boon.primitive.CharScanner.*;

public class NumberValue extends Number implements Value {


    private char[] buffer;
    private boolean chopped;
    private int startIndex;
    private int endIndex;
    private Type type;
    private Object value;


    public NumberValue( Type type ) {
        this.type = type;
    }

    public NumberValue() {

    }

    public NumberValue( boolean chop, Type type, int startIndex, int endIndex, char[] buffer ) {
        this.type = type;


        try {
            if ( chop ) {

                this.buffer = Arrays.copyOfRange ( buffer, startIndex, endIndex );
                this.startIndex = 0;
                this.endIndex = this.buffer.length;
                chopped = true;
            } else {
                this.startIndex = startIndex;
                this.endIndex = endIndex;
                this.buffer = buffer;
            }
        } catch ( Exception ex ) {
            puts ( "exception", ex, "start", startIndex, "end", endIndex );
            Exceptions.handle ( ex );

        }
    }



    public String toString () {
        if ( startIndex == 0 && endIndex == buffer.length ) {
            return FastStringUtils.noCopyStringFromChars ( buffer );
        } else {
            return new String ( buffer, startIndex, ( endIndex - startIndex ) );
        }
    }


    @Override
    public final Object toValue () {
        return value != null ? value : (value = doToValue ()) ;
    }

    @Override
    public <T extends Enum> T toEnum( Class<T> cls ) {

       return Conversions.toEnum( cls, intValue() );
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    private final Object doToValue () {

        switch ( type ) {
            case DOUBLE:
                return doubleValue ();
            case INTEGER:



                if ( isInteger ( buffer, startIndex, endIndex - startIndex ) ) {
                    return intValue ();
                } else {
                    return longValue ();
                }
        }
        die ();
        return null;
    }

    @Override
    public boolean equals ( Object o ) {
        if ( this == o ) return true;
        if ( !( o instanceof Value ) ) return false;

        NumberValue value1 = ( NumberValue ) o;

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


    public BigDecimal bigDecimalValue () {
        return new BigDecimal ( buffer, startIndex, endIndex - startIndex );
    }

    @Override
    public BigInteger bigIntegerValue() {
        return new BigInteger ( toString () );
    }

    public String stringValue () {
            return toString ();
    }

    @Override
    public String stringValueEncoded () {
           return toString ();
    }


    @Override
    public Date dateValue () {
           return new Date ( Dates.utc ( longValue () ) );
    }


    @Override
    public int intValue () {
        if ( isInteger ( buffer, startIndex, endIndex - startIndex ) ){
            return parseIntFromTo ( buffer, startIndex, endIndex  );
        } else {
            return 0;
        }
    }

    @Override
    public long longValue () {

        if ( isInteger ( buffer, startIndex, endIndex - startIndex ) ){
            return parseIntFromTo( buffer, startIndex, endIndex  );
        } else if( isLong(  buffer, startIndex, endIndex - startIndex)){
            return parseLongFromTo( buffer, startIndex, endIndex  );
        } else {
            return 0L;
        }
    }


    public byte byteValue () {
        return ( byte ) intValue ();
    }

    public short shortValue () {
        return ( short ) intValue ();
    }


    private static float fpowersOf10[] = {
            1.0f,
            10.0f,
            100.0f,
            1_000.0f,
            10_000.0f,
            100_000.0f,
            1_000_000.0f,
            10_000_000.0f,
            100_000_000.0f,
            1_000_000_000.0f,
    };

    @Override
    public double doubleValue () {

        return CharScanner.parseDouble( this.buffer, startIndex, endIndex );

    }

    @Override
    public boolean booleanValue() {
        return Boolean.parseBoolean ( toString () );
    }

    @Override
    public float floatValue () {

        return CharScanner.parseFloat( this.buffer, startIndex, endIndex );
    }

    public final void chop () {
        if ( !chopped ) {
            this.chopped = true;
            this.buffer = Arrays.copyOfRange ( buffer, startIndex, endIndex );
            this.startIndex = 0;
            this.endIndex = this.buffer.length;
        }
    }




    @Override
    public char charValue () {
        return buffer[startIndex];
    }

}
