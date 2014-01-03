package org.boon.json.serializers.impl;


import org.boon.Exceptions;
import org.boon.core.Type;
import org.boon.core.reflection.FastStringUtils;
import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.primitive.CharBuf;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is a simple fast serializer.
 * It excludes default values, i.e., int v by default is 0.
 * It excludes nulls and empties as well.
 */
public class JsonSimpleSerializerImpl implements JsonSerializerInternal {
    private final Map <Class<?>,  Map<String, FieldAccess>> fieldMap = new ConcurrentHashMap<>( );



    private CharBuf builder = CharBuf.create( 4000 );


    public final void serializeString( String str, CharBuf builder ) {
          builder.addJsonEscapedString ( str );

    }


    public CharBuf serialize( Object obj ) {

        builder.readForRecycle ();
        try {
            serializeObject( obj, builder );
        } catch ( Exception ex ) {
            return Exceptions.handle ( CharBuf.class, "unable to serializeObject", ex );
        }
        return builder;
    }


    public final boolean serializeField ( Object parent, FieldAccess fieldAccess, CharBuf builder ) {

        final String fieldName = fieldAccess.getName ();
        final Type typeEnum = fieldAccess.typeEnum ();
        switch ( typeEnum ) {
            case INT:
                int value = fieldAccess.getInt ( parent );
                if (value !=0) {
                    serializeFieldName ( fieldName, builder );
                    builder.addInt ( value  );
                    return true;
                }
                return false;
            case BOOLEAN:
                boolean bvalue = fieldAccess.getBoolean ( parent );
                if ( bvalue ) {
                    serializeFieldName ( fieldName, builder );
                    builder.addBoolean ( bvalue  );
                    return true;
                }
                return false;

            case BYTE:
                byte byvalue = fieldAccess.getByte ( parent );
                if ( byvalue != 0 ) {
                    serializeFieldName ( fieldName, builder );
                    builder.addByte ( byvalue  );
                    return true;
                }
                return false;
            case LONG:
                long lvalue = fieldAccess.getLong ( parent );
                if ( lvalue != 0 ) {
                    serializeFieldName ( fieldName, builder );
                    builder.addLong ( lvalue  );
                    return true;
                }
                return false;
            case DOUBLE:
                double dvalue = fieldAccess.getDouble ( parent );
                if ( dvalue != 0 ) {
                    serializeFieldName ( fieldName, builder );
                    builder.addDouble ( dvalue  );
                    return true;
                }
                return false;
            case FLOAT:
                float fvalue = fieldAccess.getFloat ( parent );
                if ( fvalue != 0 ) {
                    serializeFieldName ( fieldName, builder );
                    builder.addFloat ( fvalue  );
                    return true;
                }
                return false;
            case SHORT:
                short svalue = fieldAccess.getShort( parent );
                if ( svalue != 0 ) {
                    serializeFieldName ( fieldName, builder );
                    builder.addShort ( svalue  );
                    return true;
                }
                return false;
            case CHAR:
                char cvalue = fieldAccess.getChar( parent );
                if ( cvalue != 0 ) {
                    serializeFieldName ( fieldName, builder );
                    builder.addChar( cvalue  );
                    return true;
                }
                return false;

        }

        Object value = fieldAccess.getObject ( parent );

        if ( value == null ) {
            return false;
        }

        /* Avoid back reference and infinite loops. */
        if ( value == parent ) {
            return false;
        }

        switch ( typeEnum )  {
            case BIG_DECIMAL:
                serializeFieldName ( fieldName, builder );
                builder.addBigDecimal ( (BigDecimal) value );
                return true;
            case BIG_INT:
                serializeFieldName ( fieldName, builder );
                builder.addBigInteger ( ( BigInteger ) value );
                return true;
            case DATE:
                serializeFieldName ( fieldName, builder );
                serializeDate ( ( Date ) value, builder );
                return true;
            case STRING:
                serializeFieldName ( fieldName, builder );
                serializeString ( ( String ) value, builder );
                return true;
            case CHAR_SEQUENCE:
                serializeFieldName ( fieldName, builder );
                serializeString ( value.toString (), builder );
                return true;
            case INTEGER_WRAPPER:
                serializeFieldName ( fieldName, builder );
                builder.addInt ( ( Integer ) value );
                return true;
            case LONG_WRAPPER:
                serializeFieldName ( fieldName, builder );
                builder.addLong ( ( Long ) value );
                return true;
            case FLOAT_WRAPPER:
                serializeFieldName ( fieldName, builder );
                builder.addFloat ( ( Float ) value );
                return true;
            case DOUBLE_WRAPPER:
                serializeFieldName ( fieldName, builder );
                builder.addDouble ( ( Double ) value );
                return true;
            case SHORT_WRAPPER:
                serializeFieldName ( fieldName, builder );
                builder.addShort ( ( Short ) value );
                return true;
            case BYTE_WRAPPER:
                serializeFieldName ( fieldName, builder );
                builder.addByte ( ( Byte ) value );
                return true;
            case CHAR_WRAPPER:
                serializeFieldName ( fieldName, builder );
                builder.addChar ( ( Character ) value );
                return true;
            case ENUM:
                serializeFieldName ( fieldName, builder );
                builder.addQuoted ( value.toString () );
                return true;
            case COLLECTION:
            case LIST:
            case SET:
                Collection collection = (Collection) value;
                if ( collection.size () > 0) {
                    serializeFieldName ( fieldName, builder );
                    this.serializeCollection ( collection, builder );
                    return true;
                }
                return false;
            case MAP:
                Map map = (Map) value;
                if ( map.size () > 0) {
                    serializeFieldName ( fieldName, builder );
                    this.serializeMap ( map, builder );
                    return true;
                }
                return false;
            case ARRAY:
                Object []  array  = (Object []) value;
                if ( array.length > 0) {
                    serializeFieldName ( fieldName, builder );
                    this.serializeArray ( ( Object[] ) value, builder );
                    return true;
                }
                return false;
            default:
                serializeFieldName ( fieldName, builder );
                serializeInstance ( value, builder );
                return true;
        }

    }




    public final void serializeDate ( Date date, CharBuf builder ) {
        builder.addLong(date.getTime ());
    }


    public final void serializeObject( Object obj, CharBuf builder )  {


        Type type = Type.getInstanceType(obj);

        switch ( type ) {

            case NULL:
                return;
            case INT:
                builder.addInt ( int.class.cast ( obj ) );
                return;
            case BOOLEAN:
                builder.addBoolean ( boolean.class.cast ( obj ) );
                return;
            case BYTE:
                builder.addByte ( byte.class.cast ( obj ) );
                return;
            case LONG:
                builder.addLong ( long.class.cast ( obj ) );
                return;
            case DOUBLE:
                builder.addDouble ( double.class.cast ( obj ) );
                return;
            case FLOAT:
                builder.addFloat ( float.class.cast ( obj ) );
                return;
            case SHORT:
                builder.addShort ( short.class.cast ( obj ) );
                return;
            case CHAR:
                builder.addChar ( char.class.cast ( obj ) );
                return;
            case BIG_DECIMAL:
                builder.addBigDecimal ( ( BigDecimal ) obj );
                return;
            case BIG_INT:
                builder.addBigInteger ( ( BigInteger ) obj );
                return;
            case DATE:
                serializeDate ( ( Date ) obj, builder );
                return;
            case STRING:
                serializeString ( ( String ) obj, builder );
                return;
            case CHAR_SEQUENCE:
                serializeString ( obj.toString(), builder );
                return;
            case BOOLEAN_WRAPPER:
                builder.addBoolean ( ( Boolean ) obj );
                return;
            case INTEGER_WRAPPER:
                builder.addInt ( (Integer) obj);
                return;
            case LONG_WRAPPER:
                builder.addLong ( (Long) obj);
                return;
            case FLOAT_WRAPPER:
                builder.addFloat ( (Float) obj);
                return;
            case DOUBLE_WRAPPER:
                builder.addDouble ( (Double) obj);
                return;
            case SHORT_WRAPPER:
                builder.addShort ( (Short) obj);
                return;
            case BYTE_WRAPPER:
                builder.addByte ( (Byte) obj);
                return;
            case CHAR_WRAPPER:
                builder.addChar ( (Character) obj);
                return;
            case ENUM:
                builder.addQuoted ( obj.toString () );
                return;

            case COLLECTION:
            case LIST:
            case SET:
                this.serializeCollection ( (Collection) obj, builder );
                return;
            case MAP:
                this.serializeMap ( (Map) obj, builder );
                return;
            case ARRAY:
                this.serializeArray (  obj, builder );
                return;
            case INSTANCE:
                serializeInstance ( obj, builder );
                return;
            default:
                serializeUnknown ( obj, builder );

        }


    }

    public void serializeUnknown ( Object obj, CharBuf builder ) {
        builder.addQuoted ( obj.toString () );
    }



    public final void serializeInstance ( Object obj, CharBuf builder )  {

        final Map<String, FieldAccess> fieldAccessors = getFields(obj.getClass ());
        final Collection<FieldAccess> values = fieldAccessors.values ();



        builder.addChar( '{' );

        int index = 0;
        for ( FieldAccess fieldAccess : values ) {
             if (serializeField ( obj, fieldAccess, builder ) ) {
                 builder.addChar ( ',' );
                 index++;
             }
        }
        if ( index > 0 ) {
            builder.removeLastChar();
        }
        builder.addChar( '}' );

    }

    public final Map<String, FieldAccess> getFields ( Class<? extends Object> aClass ) {
        Map<String, FieldAccess> map = fieldMap.get( aClass );
        if (map == null) {
            map = doGetFields ( aClass );
            fieldMap.put ( aClass, map );
        }
        return map;
    }

    private final Map<String, FieldAccess> doGetFields ( Class<? extends Object> aClass ) {
            return Reflection.getPropertyFieldAccessors ( aClass );
    }


    private static final char [] EMPTY_MAP_CHARS = {'{', '}'};

    public final void serializeMap( Map<String, Object> map, CharBuf builder )  {


        if ( map.size () == 0 ) {
            builder.addChars ( EMPTY_MAP_CHARS );
            return;
        }


        builder.addChar( '{' );

        final Set<Map.Entry<String, Object>> entrySet = map.entrySet();
        for ( Map.Entry<String, Object> entry : entrySet ) {
            serializeFieldName ( entry.getKey ().toString (), builder );
            serializeObject( entry.getValue(), builder );
            builder.addChar ( ',' );
        }
        builder.removeLastChar ();
        builder.addChar( '}' );

    }

    @Override
    public void serializeArray ( Object array, CharBuf builder ) {

        if ( Array.getLength (array) == 0 ) {
            builder.addChars ( EMPTY_LIST_CHARS );
            return;
        }

        builder.addChar( '[' );
        final int length = Array.getLength( array );
        for ( int index = 0; index < length; index++ ) {
            serializeObject( Array.get( array, index ), builder );
            builder.addChar ( ',' );
        }
        builder.removeLastChar ();
        builder.addChar( ']' );

    }

    private void serializeFieldName ( String name, CharBuf builder ) {
        builder.addJsonFieldName ( FastStringUtils.toCharArray ( name ) );
    }


    private static final char [] EMPTY_LIST_CHARS = {'[', ']'};

    public final void serializeCollection( Collection<?> collection, CharBuf builder )  {

        if ( collection.size () == 0 ) {
             builder.addChars ( EMPTY_LIST_CHARS );
             return;
        }

        builder.addChar( '[' );
        for ( Object o : collection ) {
            serializeObject( o, builder );
            builder.addChar ( ',' );
        }
        builder.removeLastChar ();
        builder.addChar( ']' );

    }


}

