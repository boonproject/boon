package org.boon.core;

import org.boon.*;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.primitive.Arry;
import org.boon.core.reflection.*;
import org.boon.primitive.CharBuf;
import org.boon.sort.Sorting;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static org.boon.primitive.Arry.len;
import static org.boon.Boon.sputs;
import static org.boon.Exceptions.die;
import static org.boon.core.Typ.isArray;


public class Conversions {

    private static final Logger log = Logger.getLogger( Conversions.class.getName() );



    public static BigDecimal toBigDecimal ( Object obj ) {
        if (obj instanceof  BigDecimal) {
            return (BigDecimal) obj;
        }

        if ( obj instanceof Value ) {
            return ( ( Value ) obj ).bigDecimalValue ();
        } else if ( obj instanceof String ) {
            return new BigDecimal ( (String) obj );
        } else if ( obj instanceof Number ) {
            double val = (( Number ) obj ).doubleValue ();
            return BigDecimal.valueOf ( val );
        }

        return null;
    }



    public static BigInteger toBigInteger ( Object obj ) {
        if (obj instanceof  BigInteger) {
            return (BigInteger) obj;
        }

        if ( obj instanceof Value ) {
            return ( ( Value ) obj ).bigIntegerValue ();
        } else if ( obj instanceof String ) {
            return new BigInteger ( (String) obj );
        } else if ( obj instanceof Number ) {
            long val = (( Number ) obj ).longValue ();
            return BigInteger.valueOf ( val );
        }

        return null;

    }


    public static int toInt( Object obj ) {
        return toInt( obj, Integer.MIN_VALUE );
    }

    public static int toInt( Object obj, int defaultValue ) {
        if ( obj.getClass() == int.class ) {
            return int.class.cast( obj );
        }
        try {
            if ( obj instanceof Number ) {
                return ( ( Number ) obj ).intValue();
            } else if ( obj instanceof Boolean || obj.getClass() == Boolean.class ) {
                boolean value = toBoolean( obj );
                return value ? 1 : 0;
            } else if ( obj instanceof CharSequence ) {
                try {
                    return Integer.parseInt( ( ( CharSequence ) obj ).toString() );
                } catch ( Exception ex ) {
                    char[] chars = toString( obj ).toCharArray();
                    boolean found = false;
                    CharBuf builder = CharBuf.create( chars.length );
                    for ( char c : chars ) {
                        if ( Character.isDigit( c ) && !found ) {
                            found = true;
                            builder.add( c );
                        } else if ( Character.isDigit( c ) && found ) {
                            builder.add( c );
                        } else if ( !Character.isDigit( c ) && found ) {
                        }
                    }
                    try {
                        if ( builder.len() > 0 ) {
                            return Integer.parseInt( builder.toString() );
                        }
                    } catch ( Exception ex2 ) {
                        log.warning( String.format(
                                "unable to convert to byte and there was an exception %s",
                                ex2.getMessage() ) );
                    }
                }
            } else {
            }
        } catch ( Exception ex1 ) {

            log.warning( String.format(
                    "unable to convert to byte and there was an exception %s",
                    ex1.getMessage() ) );

        }
        return defaultValue; // die throws an exception

    }



    public static byte toByte( Object obj ) {
        return toByte ( obj, Byte.MIN_VALUE );
    }

    public static byte toByte( Object obj, byte defaultByte ) {
        if ( obj.getClass() == byte.class ) {
            return byte.class.cast( obj );
        } else if ( obj instanceof Number ) {
            return ( ( Number ) obj ).byteValue();
        } else {
            return ( byte ) toInt( obj, defaultByte );
        }
    }

    public static short toShort( Object obj  ) {
        return toShort ( obj, Short.MIN_VALUE );
    }

    public static short toShort( Object obj, final short shortDefault ) {

        if ( obj.getClass() == short.class ) {
            return short.class.cast( obj );
        } else if ( obj instanceof Number ) {
            return ( ( Number ) obj ).shortValue();
        } else {
            return ( short ) toInt( obj, shortDefault );
        }
    }

    public static char toChar( Object obj ) {
           return toChar ( obj, (char) 0 );
    }

    public static char toChar( Object obj, final char defaultChar ) {
        if ( obj.getClass() == char.class ) {
            return char.class.cast( obj );
        } else if ( obj instanceof Character ) {
            return ( ( Character ) obj ).charValue();
        } else if ( obj instanceof CharSequence ) {
            return obj.toString().charAt( 0 );
        } else if ( obj instanceof Number ) {
            return ( char ) toInt( obj );
        } else if ( obj instanceof Boolean || obj.getClass() == Boolean.class ) {
            boolean value = toBoolean( obj );
            return value ? 'T' : 'F';
        } else if ( obj.getClass().isPrimitive() ) {
            return ( char ) toInt( obj );
        } else {
            String str = toString( obj );
            if ( str.length() > 0 ) {
                return str.charAt( 0 );
            } else {
                return defaultChar;
            }
        }
    }

    public static long toLong( Object obj ) {
        return toLong ( obj, Long.MIN_VALUE );
    }



    public static long toLongOrDie( Object obj ) {
        long l =  toLong ( obj, Long.MIN_VALUE );
        if ( l == Long.MIN_VALUE ) {
             die("Cannot convert", obj, "into long value", obj);
        }
        return l;
    }

    public static long toLong( Object obj, final long longDefault ) {

        if ( obj instanceof Long) {
            return (Long) obj;
        }

        try {
             if ( obj instanceof Number ) {
                return ( ( Number ) obj ).longValue();
            } else if ( obj instanceof CharSequence ) {
                try {
                    return Long.parseLong( ( ( CharSequence ) obj ).toString() );
                } catch ( Exception ex ) {
                    char[] chars = toString( obj ).toCharArray();

                    CharBuf builder = CharBuf.create( chars.length );
                    boolean found = false;
                    for ( char c : chars ) {
                        if ( Character.isDigit( c ) && !found ) {
                            found = true;
                            builder.add( c );
                        } else if ( Character.isDigit( c ) && found ) {
                            builder.add( c );
                        } else if ( !Character.isDigit( c ) && found ) {
                        }
                    }
                    try {
                        if ( builder.len() > 0 ) {
                            return Long.parseLong( builder.toString() );
                        }
                    } catch ( Exception ex2 ) {
                        log.warning( String.format(
                                "unable to convert to long and there was an exception %s",
                                ex2.getMessage() ) );

                    }
                }
            } else if (obj instanceof  Date) {
                 return ( (Date) obj).getTime ();
             }
             else {
                return toInt( obj );
            }
        } catch ( Exception ex ) {
            log.warning (  String.format (
                    "unable to convert to long and there was an exception %s",
                    ex.getMessage () ) );

        }

        return longDefault; // die throws an exception

    }

    public static boolean toBoolean( Object obj ) {

        if (obj == null) {
            return false;
        }

        if ( obj.getClass() == boolean.class ) {
            return boolean.class.cast( obj );
        } else if ( obj instanceof Boolean ) {
            return ( ( Boolean ) obj ).booleanValue();
        } else if ( obj instanceof Number || obj.getClass().isPrimitive() ) {
            int value = toInt( obj );
            return value != 0 ? true : false;
        } else if ( obj instanceof String || obj instanceof CharSequence ) {
            String str = Conversions.toString( obj );
            if ( str.length() == 0 ) {
                return false;
            } if (str.equals("false")) {
                return false;
            } else {
                return true;
            }
        } else if ( Boon.isArray( obj )  ) {
            return Boon.len( obj ) > 0;
        } else if (obj instanceof Collection) {
            List list = Lists.list((Collection) obj);
            while (list.remove(null)) {

            }
            return Lists.len(list) > 0;
        }
        else {
            return toBoolean( Conversions.toString( obj ) );
        }
    }

    public static double toDouble( Object obj ) {

        try {
            if ( obj instanceof Double ) {
                return ( Double ) obj;
            } else if ( obj instanceof Number ) {
                return ( ( Number ) obj ).doubleValue();
            } else if ( obj instanceof CharSequence ) {
                try {
                    return Double.parseDouble( ( ( CharSequence ) obj ).toString() );
                } catch ( Exception ex ) {
                    die( String.format( "Unable to convert %s to a double", obj.getClass() ) );
                    return Double.NaN;
                }
            } else {
            }
        } catch ( Exception ex ) {
            log.warning( String.format(
                    "unable to convert to double and there was an exception %s",
                    ex.getMessage() ) );
        }

        die( String.format( "Unable to convert %s to a double", obj.getClass() ) );
        return -666d; // die throws an exception

    }

    public static float toFloat( Object obj ) {
        if ( obj.getClass() == float.class ) {
            return ( Float ) obj;
        }

        try {
            if ( obj instanceof Float ) {
                return ( Float ) obj;
            } else if ( obj instanceof Number ) {
                return ( ( Number ) obj ).floatValue();
            } else if ( obj instanceof CharSequence ) {
                try {
                    return Float.parseFloat( ( ( CharSequence ) obj ).toString() );
                } catch ( Exception ex ) {
//                    String svalue = str(obj);
//                    Matcher re = Regex.re(
//                            "[-+]?[0-9]+\\.?[0-9]+([eE][-+]?[0-9]+)?", svalue);
//                    if (re.find()) {
//                        svalue = re.group(0);
//                        return Float.parseFloat(svalue);
//                    }
                    die( String.format( "Unable to convert %s to a float", obj.getClass() ) );
                    return Float.NaN;
                }
            } else {
            }
        } catch ( Exception ex ) {

            log.warning( String.format(
                    "unable to convert to float and there was an exception %s",
                    ex.getMessage() ) );
        }

        die( String.format( "Unable to convert %s to a float", obj.getClass() ) );
        return -666f; // die throws an exception

    }


    public static <T> T coerce( Class<T> clz, Object value ) {
        return coerce( Type.getType( clz ), clz, value);
    }
    public static <T> T coerce( Type coerceTo, Class<T> clz, Object value ) {
        if ( value == null)  {
            if ( coerceTo!=Type.INSTANCE && !clz.isPrimitive()) {

                return null;
            } else if (clz.isPrimitive()) {
                if (clz == boolean.class) {
                    return (T)(Boolean)false;
                }
                return (T) (Number)0;
            }
        }

        switch (coerceTo) {
            case STRING:
            case CHAR_SEQUENCE:
                return ( T ) value.toString();



            case INT:
            case INTEGER_WRAPPER:
                Integer i = toInt( value );
                return ( T ) i;

            case SHORT:
            case SHORT_WRAPPER:
                Short s = toShort( value );
                return ( T ) s;



            case BYTE:
            case BYTE_WRAPPER:
                Byte by = toByte( value );
                return ( T ) by;


            case CHAR:
            case CHAR_WRAPPER:
                Character ch = toChar( value );
                return ( T ) ch;

            case LONG:
            case LONG_WRAPPER:
                Long l = toLong ( value );
                return ( T ) l;

            case DOUBLE:
            case DOUBLE_WRAPPER:
                Double d = toDouble ( value );
                return ( T ) d;


            case FLOAT:
            case FLOAT_WRAPPER:
                Float f =   toFloat( value );
                return ( T ) f;


            case DATE:
                return (T) toDate(value);

            case BIG_DECIMAL:
                return (T) toBigDecimal( value );


            case BIG_INT:
                return (T) toBigInteger( value );

            case CALENDAR:
                return (T) toCalendar( toDate(value) );

            case BOOLEAN:
            case BOOLEAN_WRAPPER:
                return (T) (Boolean) toBoolean( value );

            case MAP:
                if ( value instanceof Map ) {
                    return ( T ) value;
                }
                return ( T ) toMap( value );

            case ARRAY:
                return toPrimitiveArrayIfPossible( clz, value );

            case COLLECTION:
                return toCollection( clz, value );

            case INSTANCE:
                if ( value instanceof Map  ) {
                    return  MapObjectConversion.fromMap ( ( Map<String, Object> ) value, clz );
                } else if (value instanceof List) {
                    return  MapObjectConversion.fromList( (List<Object>) value, clz );
                } else if (clz.isInstance( value )){
                    return (T) value;
                } else {
                    if (value==null) {
                        return Reflection.newInstance(clz);
                    }
                    ClassMeta meta = ClassMeta.classMeta(clz);
                    List<ConstructorAccess> constructors = meta.oneArgumentConstructors();

                    if (constructors.size() == 0) {
                        return null;
                    } else if (constructors.size()==1) {
                        ConstructorAccess constructorAccess = constructors.get(0);
                        Class<?> arg1Type = constructorAccess.parameterTypes()[0];
                        if (arg1Type.isInstance(value)) {
                            return (T) constructorAccess.create(value);
                        } else {
                            return  (T) constructorAccess.create( coerce( arg1Type, value ) );
                        }
                    } else {
                        for ( ConstructorAccess c : constructors ) {
                            Class<?> arg1Type = c.parameterTypes()[0];
                            if (arg1Type.isInstance(value)) {
                                return (T) c.create(value);
                            }
                        }


                        for ( ConstructorAccess c : constructors ) {
                            Class<?> arg1Type = c.parameterTypes()[0];
                            if (arg1Type.isAssignableFrom(value.getClass())) {
                                return (T) c.create(value);
                            }
                        }
                    }

                }

            case ENUM:
                return (T) toEnum( (Class<? extends Enum>)clz, value );


            default:
                return (T)value;
        }
    }

    public static <T> T coerceOrDie( Class<T> clz, Object value ) {
        return coerceOrDie( Type.getType( clz ), clz, value);
    }
    public static <T> T coerceOrDie( Type coerceTo, Class<T> clz, Object value ) {
        if ( value == null ) {
            return null;
        }

        switch (coerceTo) {
            case STRING:
            case CHAR_SEQUENCE:
                return ( T ) value.toString();

            case INT:
            case INTEGER_WRAPPER:
                Integer i = toInt( value );
                if (i==Integer.MIN_VALUE ) {
                    return (T) die(Object.class, "Unable to convert to ", coerceTo, "from", value);
                }
                return ( T ) i;


            case SHORT:
            case SHORT_WRAPPER:
                Short s = toShort( value );
                if (s==Short.MIN_VALUE ) {
                    return (T) die(Object.class, "Unable to convert to ", coerceTo, "from", value);
                }
                return ( T ) s;



            case BYTE:
            case BYTE_WRAPPER:
                Byte by = toByte( value );
                if (by==Byte.MIN_VALUE ) {
                    return (T) die(Object.class, "Unable to convert to ", coerceTo, "from", value);
                }
                return ( T ) by;


            case CHAR:
            case CHAR_WRAPPER:
                Character ch = toChar( value );
                if (ch== (char)0 ) {
                    return (T) die(Object.class, "Unable to convert to ", coerceTo, "from", value);
                }
                return ( T ) ch;

            case LONG:
            case LONG_WRAPPER:
                Long l = toLong ( value );
                if (l==Long.MIN_VALUE ) {
                    return (T) die(Object.class, "Unable to convert to ", coerceTo, "from", value);
                }

                return ( T ) l;

            case DOUBLE:
            case DOUBLE_WRAPPER:
                Double d = toDouble ( value );
                if (d==Double.MIN_VALUE ) {
                    return (T) die(Object.class, "Unable to convert to ", coerceTo, "from", value);
                }

                return ( T ) d;


            case FLOAT:
            case FLOAT_WRAPPER:
                Float f =  (Float) toFloat( value );
                if (f==Float.MIN_VALUE ) {
                    return (T) die(Object.class, "Unable to convert to ", coerceTo, "from", value);
                }
                return ( T ) f;

            case DATE:
                return (T) toDate(value);

            case BIG_DECIMAL:
                return (T) toBigDecimal( value );


            case BIG_INT:
                return (T) toBigInteger( value );

            case CALENDAR:
                return (T) toCalendar( toDate(value) );



            case BOOLEAN:
            case BOOLEAN_WRAPPER:
                return (T) (Boolean) toBoolean( value );

            case MAP:
                if ( value instanceof Map ) {
                    return ( T ) value;
                }
                return ( T ) toMap( value );

            case ARRAY:
                return toPrimitiveArrayIfPossible( clz, value );

            case COLLECTION:
                return toCollection( clz, value );

            case INSTANCE:
                if ( value instanceof Map  ) {
                    return  MapObjectConversion.fromMap ( ( Map<String, Object> ) value, clz );
                } else if (value instanceof List) {
                    return  MapObjectConversion.fromList( (List<Object>) value, clz );
                } else if (clz.isInstance( value )){
                    return (T) value;
                } else {
                    ClassMeta meta = ClassMeta.classMeta(clz);
                    List<ConstructorAccess> constructors = meta.oneArgumentConstructors();

                    if (constructors.size() == 0) {
                        return null;
                    } else if (constructors.size()==1) {
                        ConstructorAccess constructorAccess = constructors.get(0);
                        Class<?> arg1Type = constructorAccess.parameterTypes()[0];
                        if (arg1Type.isInstance(value)) {
                            return (T) constructorAccess.create(value);
                        } else {
                            return  (T) constructorAccess.create( coerce( arg1Type, value ) );
                        }
                    } else {
                        for ( ConstructorAccess c : constructors ) {
                            Class<?> arg1Type = c.parameterTypes()[0];
                            if (arg1Type.isInstance(value)) {
                                return (T) c.create(value);
                            }
                        }


                        for ( ConstructorAccess c : constructors ) {
                            Class<?> arg1Type = c.parameterTypes()[0];
                            if (arg1Type.isAssignableFrom(value.getClass())) {
                                return (T) c.create(value);
                            }
                        }

                        return (T) die(Object.class, "Unable to convert to ", coerceTo, "from", value);

                    }
                }


            case ENUM:
                return (T) toEnum( (Class<? extends Enum>)clz, value );


            default:
                return (T) die(Object.class, "Unable to convert to ", coerceTo, "from", value);
        }
    }

    @SuppressWarnings ( "unchecked" )
    public static <T> T coerceClassic( Class<T> clz, Object value ) {

        if ( value == null ) {
            return null;
        }

        if (clz == value.getClass ()) {
            return (T)value;
        }

        if ( clz == Typ.string || clz == Typ.chars ) {
            return ( T ) value.toString();
        } else if ( clz == Typ.integer || clz == Typ.intgr ) {
            Integer i = toInt( value );
            return ( T ) i;
        } else if ( clz == Typ.longWrapper || clz == Typ.lng ) {
            Long l = toLong ( value );
            return ( T ) l;
        } else if ( clz == Typ.doubleWrapper || clz == Typ.dbl ) {
            Double i = toDouble ( value );
            return ( T ) i;
        } else if ( clz == Typ.date ) {
           return (T) toDate(value);
        } else if ( clz == Typ.bigInteger ) {
            return (T) toBigInteger (value);
        } else if ( clz == Typ.bigDecimal ) {
            return (T) toBigDecimal ( value );
        }
        else if ( clz == Typ.calendar ) {
            return (T) toCalendar ( toDate ( value ) );
        }
        else if ( clz == Typ.floatWrapper || clz == Typ.flt ) {
            Float i = toFloat( value );
            return ( T ) i;
        } else if ( clz == Typ.stringArray ) {
            die( "Need to fix this" );
            return null;
        } else if ( clz == Typ.bool || clz == Typ.bln ) {
            Boolean b = toBoolean ( value );
            return ( T ) b;
        } else if ( Typ.isMap( clz ) ) {
            if ( value instanceof Map ) {
                return ( T ) value;
            }
            return ( T ) toMap( value );
        } else if ( clz.isArray() ) {
            return toPrimitiveArrayIfPossible( clz, value );
        } else if ( Typ.isCollection ( clz ) ) {
            return toCollection( clz, value );
        } else if ( clz != null && clz.getPackage() != null && !clz.getPackage().getName().startsWith( "java" )
                && Typ.isMap( value.getClass() ) && Typ.doesMapHaveKeyTypeString( value ) ) {
            return ( T ) MapObjectConversion.fromMap ( ( Map<String, Object> ) value );
        } else if ( clz.isEnum () ) {
            return (T) toEnum( (Class<? extends Enum>)clz, value );

        } else {
            return null;
        }
    }

    public static <T extends Enum> T toEnumOld( Class<T> cls, String value ) {
        try {
            return  (T) Enum.valueOf( cls, value );
        } catch ( Exception ex ) {
            return  (T) Enum.valueOf( cls, value.toUpperCase().replace( '-', '_' ) );
        }
    }

    public static <T extends Enum> T toEnum( Class<T> cls, String value ) {

        return toEnum(cls, value, null);
    }

    public static <T extends Enum> T toEnum( Class<T> cls, String value, Enum defaultEnum ) {

        T[] enumConstants = cls.getEnumConstants();
        for ( T e : enumConstants ) {
            if ( e.name().equals(value) ) {
                return e;
            }
        }


        value = value.toUpperCase().replace( '-', '_' );
        for ( T e : enumConstants ) {
            if ( e.name().equals(value) ) {
                return e;
            }
        }

        value = Str.underBarCase(value);
        for ( T e : enumConstants ) {
            if ( e.name().equals(value) ) {
                return e;
            }
        }


        return (T)defaultEnum;
    }

    public static <T extends Enum> T toEnum( Class<T> cls, int value ) {

        T[] enumConstants = cls.getEnumConstants();
        for ( T e : enumConstants ) {
            if ( e.ordinal() == value ) {
                return e;
            }
        }
        return null;
    }

    public static <T extends Enum> T toEnumOrDie( Class<T> cls, int value ) {

        T[] enumConstants = cls.getEnumConstants();
        for ( T e : enumConstants ) {
            if ( e.ordinal() == value ) {
                return e;
            }
        }
        die( "Can't convert ordinal value " + value + " into enum of type " + cls );
        return null;
    }


    public static <T extends Enum> T toEnum( Class<T> cls, Object value ) {

        if ( value instanceof Value ) {
            return (T) ( (Value) value).toEnum ( cls );
        } else if ( value instanceof CharSequence ) {
            return toEnum( cls, value.toString() );
        } else if ( value instanceof Number || value.getClass().isPrimitive() ) {

            int i = toInt( value );
            return toEnum( cls, i );
        } else {
            //die( "Can't convert  value " + value + " into enum of type " + cls ); //TODO Fix this
            return null;
        }
    }

    @SuppressWarnings ( "unchecked" )
    public static <T> T toPrimitiveArrayIfPossible( Class<T> clz, Object value ) {
        if ( clz == Typ.intArray ) {
            return ( T ) iarray( value );
        } else if ( clz == Typ.byteArray ) {
            return ( T ) barray( value );
        } else if ( clz == Typ.charArray ) {
            return ( T ) carray( value );
        } else if ( clz == Typ.shortArray ) {
            return ( T ) sarray( value );
        } else if ( clz == Typ.longArray ) {
            return ( T ) larray( value );
        } else if ( clz == Typ.floatArray ) {
            return ( T ) farray( value );
        } else if ( clz == Typ.doubleArray ) {
            return ( T ) darray( value );
        } else if ( value.getClass() == clz ) {
            return ( T ) value;
        } else {
            int index = 0;
            Object newInstance = Array.newInstance( clz.getComponentType(), Boon.len( value ) );
            Iterator<Object> iterator = iterator( Typ.object, value );
            while ( iterator.hasNext() ) {
                BeanUtils.idx ( newInstance, index, iterator.next () );
                index++;
            }
            return ( T ) newInstance;
        }
    }


    public static double[] darray( Object value ) {
        //You could handleUnexpectedException shorts, bytes, longs and chars more efficiently
        if ( value.getClass() == Typ.shortArray ) {
            return ( double[] ) value;
        }
        double[] values = new double[ Boon.len( value ) ];
        int index = 0;
        Iterator<Object> iterator = iterator( Object.class, value );
        while ( iterator.hasNext() ) {
            values[ index ] = toFloat( iterator.next() );
            index++;
        }
        return values;
    }

    public static float[] farray( Object value ) {
        //You could handleUnexpectedException shorts, bytes, longs and chars more efficiently
        if ( value.getClass() == Typ.floatArray ) {
            return ( float[] ) value;
        }
        float[] values = new float[ Boon.len( value ) ];
        int index = 0;
        Iterator<Object> iterator = iterator( Object.class, value );
        while ( iterator.hasNext() ) {
            values[ index ] = toFloat( iterator.next() );
            index++;
        }
        return values;
    }

    public static long[] larray( Object value ) {
        //You could handleUnexpectedException shorts, bytes, longs and chars more efficiently
        if ( value.getClass() == Typ.shortArray ) {
            return ( long[] ) value;
        }
        long[] values = new long[ Boon.len( value ) ];
        int index = 0;
        Iterator<Object> iterator = iterator( Object.class, value );
        while ( iterator.hasNext() ) {
            values[ index ] = toLong( iterator.next() );
            index++;
        }
        return values;
    }

    public static short[] sarray( Object value ) {
        //You could handleUnexpectedException shorts, bytes, longs and chars more efficiently
        if ( value.getClass() == Typ.shortArray ) {
            return ( short[] ) value;
        }
        short[] values = new short[ Boon.len( value ) ];
        int index = 0;
        Iterator<Object> iterator = iterator( Object.class, value );
        while ( iterator.hasNext() ) {
            values[ index ] = toShort( iterator.next() );
            index++;
        }
        return values;
    }

    public static int[] iarray( Object value ) {
        //You could handleUnexpectedException shorts, bytes, longs and chars more efficiently
        if ( value.getClass() == Typ.intArray ) {
            return ( int[] ) value;
        }
        int[] values = new int[ Boon.len( value ) ];
        int index = 0;
        Iterator<Object> iterator = iterator( Object.class, value );
        while ( iterator.hasNext() ) {
            values[ index ] = toInt( iterator.next() );
            index++;
        }
        return values;
    }

    public static byte[] barray( Object value ) {
        //You could handleUnexpectedException shorts, ints, longs and chars more efficiently
        if ( value.getClass() == Typ.byteArray ) {
            return ( byte[] ) value;
        }
        byte[] values = new byte[ Boon.len( value ) ];
        int index = 0;
        Iterator<Object> iterator = iterator( Object.class, value );
        while ( iterator.hasNext() ) {
            values[ index ] = toByte( iterator.next() );
            index++;
        }
        return values;
    }

    public static char[] carray( Object value ) {
        //You could handleUnexpectedException shorts, ints, longs and chars more efficiently
        if ( value.getClass() == Typ.charArray ) {
            return ( char[] ) value;
        }
        char[] values = new char[ Boon.len( value ) ];
        int index = 0;
        Iterator<Object> iterator = iterator( Typ.object, value );
        while ( iterator.hasNext() ) {
            values[ index ] = toChar( iterator.next() );
            index++;
        }
        return values;
    }

    @SuppressWarnings ( "unchecked" )
    public static Iterator iterator( final Object value ) {
        return iterator( null, value );
    }

    public static <T> Iterator<T> iterator( Class<T> class1, final Object value ) {

        if (value == null) {
            return Collections.EMPTY_LIST.iterator();
        }

        if ( Boon.isArray( value ) ) {
            final int length = Arry.len(value);

            return new Iterator<T>() {
                int i = 0;

                @Override
                public boolean hasNext() {
                    return i < length;
                }

                @Override
                public T next() {
                    T next = ( T ) BeanUtils.idx ( value, i );
                    i++;
                    return next;
                }

                @Override
                public void remove() {
                }
            };
        } else if ( Typ.isCollection( value.getClass() ) ) {
            return ( ( Collection<T> ) value ).iterator();
        } else {
            return ( Iterator<T> ) Collections.singleton( value ).iterator();
        }
    }

    @SuppressWarnings ( "unchecked" )
    public static <T> T toCollection( Class<T> clz, Object value ) {
        if ( Typ.isList( clz ) ) {
            return ( T ) toList( value );
        } else if ( Typ.isSortedSet( clz ) ) {
            return ( T ) toSortedSet( value );
        } else if ( Typ.isSet( clz ) ) {
            return ( T ) toSet( value );
        } else {
            return ( T ) toList( value );
        }
    }

    @SuppressWarnings ( { "rawtypes", "unchecked" } )
    public static List toList( Object value ) {
        if ( value instanceof List ) {
            return ( List ) value;
        } else if ( value instanceof Collection ) {
            return new ArrayList( ( Collection ) value );
        } else if (value == null ) {
            return new ArrayList( );
        } else {
            ArrayList list = new ArrayList( Boon.len( value ) );
            Iterator<Object> iterator = iterator( Typ.object, value );
            while ( iterator.hasNext() ) {
                list.add( iterator.next() );
            }
            return list;
        }
    }

    @SuppressWarnings ( { "rawtypes", "unchecked" } )
    public static Set toSet( Object value ) {
        if ( value instanceof Set ) {
            return ( Set ) value;
        } else if ( value instanceof Collection ) {
            return new HashSet( ( Collection ) value );
        } else {
            HashSet set = new HashSet( Boon.len( value ) );
            Iterator<Object> iterator = iterator( Typ.object, value );
            while ( iterator.hasNext() ) {
                set.add( iterator.next() );
            }
            return set;
        }
    }

    @SuppressWarnings ( { "rawtypes", "unchecked" } )
    public static SortedSet toSortedSet( Object value ) {
        if ( value instanceof Set ) {
            return ( SortedSet ) value;
        } else if ( value instanceof Collection ) {
            return new TreeSet( ( Collection ) value );
        } else {
            TreeSet set = new TreeSet();
            Iterator<Object> iterator = iterator( Typ.object, value );
            while ( iterator.hasNext() ) {
                set.add( iterator.next() );
            }
            return set;
        }
    }


    public static Map<String, Object> toMap( Object value ) {
        return MapObjectConversion.toMap ( value );
    }


    public static String toString( Object obj ) {
        return String.valueOf( obj );
    }

    public static Number toWrapper( long l ) {
        if ( l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE ) {
            return toWrapper( ( int ) l );
        } else {
            return Long.valueOf( l );
        }
    }

    public static Number toWrapper( int i ) {
        if ( i >= Byte.MIN_VALUE && i <= Byte.MAX_VALUE ) {
            return Byte.valueOf( ( byte ) i );
        } else if ( i >= Short.MIN_VALUE && i <= Short.MAX_VALUE ) {
            return Short.valueOf( ( short ) i );
        } else {
            return Integer.valueOf( i );
        }
    }

    public static Object wrapAsObject( boolean i ) {
        return Boolean.valueOf( i );
    }


    public static Object wrapAsObject( byte i ) {
        return Byte.valueOf( i );
    }

    public static Object wrapAsObject( short i ) {
        return Short.valueOf( i );
    }

    public static Object wrapAsObject( int i ) {
        return Integer.valueOf( i );
    }

    public static Object wrapAsObject( long i ) {
        return Long.valueOf( i );
    }

    public static Object wrapAsObject( double i ) {
        return Double.valueOf( i );
    }

    public static Object wrapAsObject( float i ) {
        return Float.valueOf( i );
    }

    public static Object toArrayGuessType( Collection<?> value ) {
        Class<?> componentType = Reflection.getComponentType( value );
        Object array = Array.newInstance( componentType, value.size() );
        @SuppressWarnings ( "unchecked" )
        Iterator<Object> iterator = ( Iterator<Object> ) value.iterator();
        int index = 0;
        while ( iterator.hasNext() ) {
            BeanUtils.idx ( array, index, iterator.next () );
            index++;
        }
        return array;
    }


//    public static Object toArray( Class<?> componentType, Collection<?> value ) {
//        Object array = Array.newInstance( componentType, value.size() );
//        @SuppressWarnings ( "unchecked" )
//        Iterator<Object> iterator = ( Iterator<Object> ) value.iterator();
//        int index = 0;
//        while ( iterator.hasNext() ) {
//            BeanUtils.idx ( array, index, iterator.next () );
//            index++;
//        }
//        return array;
//    }


    public static <T> T[] toArray( Class<T> componentType, Collection<T> collection ) {
        T[] array = (T[]) Array.newInstance(componentType, collection.size());
        return collection.toArray(array);
    }

//    public static <V> V[] array( Class<V> type, final Collection<V> array ) {
//        return ( V[] ) Conversions.toArray( type, array );
//    }

    public static <V> V[] array( Class<V> type, final Collection<V> array ) {
        return Conversions.toArray( type, array );
    }

    public static Date toDate( Object object ) {

        if (object instanceof Date) {
            return (Date) object;
        } else if (object instanceof Value) {
            return ( (Value) object).dateValue ();
        } else if (object instanceof Calendar) {
            return ( (Calendar) object).getTime ();
        } else if ( object instanceof Long) {
            return new Date( (long) object);
        } else if ( object instanceof String) {
            String val = (String) object;
            char [] chars = FastStringUtils.toCharArray ( val );
            if ( Dates.isISO8601QuickCheck ( chars ) ) {
                return Dates.fromISO8601DateLoose ( chars  );
            } else {
                return toDateUS ( val );
            }
        }
        return null;
    }

    public static Calendar toCalendar( Date date ) {

        final Calendar calendar = Calendar.getInstance ();
        calendar.setTime ( date );
        return calendar;

    }

    public static Date toDate( Calendar c ) {
        return c.getTime();

    }

    public static Date toDate( long value ) {
        return new Date( value );
    }

    public static Date toDate( Long value ) {
        return new Date( value );
    }

    public static Date toDate( String value ) {
        try {
            return toDateUS( value );
        } catch ( Exception ex ) {
            try {
                return DateFormat.getDateInstance( DateFormat.SHORT ).parse( value );
            } catch ( ParseException e ) {
                die( "Unable to parse date" );
                return null;
            }

        }
    }


    public static Date toDateUS( String string ) {

        String[] split = StringScanner.splitByChars( string, new char[]{ '.', '\\', '/', ':' } );

        if ( split.length == 3 ) {
            return Dates.getUSDate( toInt( split[ 0 ] ), toInt( split[ 1 ] ), toInt( split[ 2 ] ) );
        } else if ( split.length >= 6 ) {
            return Dates.getUSDate( toInt( split[ 0 ] ), toInt( split[ 1 ] ), toInt( split[ 2 ] ),
                    toInt( split[ 3 ] ), toInt( split[ 4 ] ), toInt( split[ 5 ] )
            );
        } else {
            die( String.format( "Not able to parse %s into a US date", string ) );
            return null;
        }

    }

    public static Date toEuroDate( String string ) {

        String[] split = StringScanner.splitByChars( string, new char[]{ '.', '\\', '/', ':' } );

        if ( split.length == 3 ) {
            return Dates.getEuroDate( toInt( split[ 0 ] ), toInt( split[ 1 ] ), toInt( split[ 2 ] ) );
        } else if ( split.length >= 6 ) {
            return Dates.getEuroDate( toInt( split[ 0 ] ), toInt( split[ 1 ] ), toInt( split[ 2 ] ),
                    toInt( split[ 3 ] ), toInt( split[ 4 ] ), toInt( split[ 5 ] )
            );
        } else {
            die( String.format( "Not able to parse %s into a Euro date", string ) );
            return null;
        }

    }

    public static Collection<Object> createCollection( Class<?> type, int size ) {

        if ( type == List.class ) {
            return new ArrayList<>( size );
        } else if ( type == SortedSet.class ) {
            return new TreeSet<>();
        } else if ( type == Set.class ) {
            return new LinkedHashSet<>( size );
        } else if ( Typ.isList( type ) ) {
            return new ArrayList<>();
        } else if ( Typ.isSortedSet( type ) ) {
            return new TreeSet<>();
        } else if ( Typ.isSet( type ) ) {
            return new LinkedHashSet<>( size );
        } else {
            return new ArrayList( size );
        }

    }


    public static Map<?,?> createMap( Class<?> type, int size ) {

        if ( type == HashMap.class ) {
            return new HashMap<>( size );
        } else if ( type == TreeMap.class ) {
            return new TreeMap<>();
        } else if ( type == SortedMap.class ) {
            return new TreeMap<>();
        } else if ( type == ConcurrentHashMap.class) {
            return new ConcurrentHashMap<>();
        }  else {
            return new HashMap( size );
        }

    }



    public static <TO, FROM> List<TO> mapFilterNulls( Function<FROM, TO> converter,
                                                      List<FROM> fromList ) {

        ArrayList<TO> toList = new ArrayList<TO>( fromList.size() );

        for ( FROM from : fromList ) {
            TO converted = converter.apply( from );
            if ( converted != null ) {
                toList.add( converted );
            }
        }

        return toList;
    }


    public static Object unifyList( Object o ) {
        return unifyList( o, null );
    }

    public static Object unifyList( Object o, List list ) {

        if ( list == null && !Boon.isArray( o ) && !( o instanceof Iterable ) ) {
            return o;
        }

        if ( list == null ) {
            list = new ArrayList( 400 );
        }
        if ( Boon.isArray( o ) ) {
            int length = Boon.len( o );
            for ( int index = 0; index < length; index++ ) {
                unifyList( BeanUtils.idx ( o, index ), list );
            }
        } else if ( o instanceof Iterable ) {
            Iterable i = ( ( Iterable ) o );
            for ( Object item : i ) {
                list = ( List ) unifyList( item, list );
            }
        } else {
            list.add( o );
        }

        return list;


    }

    /**
     * Cast an object to a comparable object.
     * @param comparable
     * @return
     */
    public static Comparable comparable( Object comparable ) {
        return ( Comparable ) comparable;
    }


    public Number coerceNumber( Object inputArgument, Class<?> paraType ) {
        Number number = ( Number ) inputArgument;
        if ( paraType == int.class || paraType == Integer.class ) {
            return number.intValue();
        } else if ( paraType == double.class || paraType == Double.class ) {
            return number.doubleValue();
        } else if ( paraType == float.class || paraType == Float.class ) {
            return number.floatValue();
        } else if ( paraType == short.class || paraType == Short.class ) {
            return number.shortValue();
        } else if ( paraType == byte.class || paraType == Byte.class ) {
            return number.byteValue();
        }
        return null;
    }


    public static int lengthOf(Object obj) {
        return len(obj);
    }

    public static int len(Object obj) {
        if ( isArray( obj ) ) {
            return Arry.len(obj);
        } else if ( obj instanceof CharSequence ) {
            return ( ( CharSequence ) obj ).length();
        } else if ( obj instanceof Collection ) {
            return ( ( Collection<?> ) obj ).size();
        } else if ( obj instanceof Map ) {
            return ( ( Map<?, ?> ) obj ).size();
        } else if ( obj == null ) {
            return 0;
        } else {
            die( sputs("Not an array like object", obj, obj.getClass()) );
            return 0; //will never get here.
        }

    }
}
