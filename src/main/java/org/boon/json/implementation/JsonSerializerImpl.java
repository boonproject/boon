package org.boon.json.implementation;

import org.boon.Exceptions;
import org.boon.core.Dates;
import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.json.JsonSerializer;
import org.boon.primitive.CharBuf;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by rick on 12/18/13.
 */
public class JsonSerializerImpl implements JsonSerializer {

    private final boolean outputType;
    private final boolean useProperties;
    private final boolean useFields;
    private final boolean includeNulls;

    private Map<Class,  Map<String, FieldAccess>> fieldMap = new ConcurrentHashMap<> ( );


    public JsonSerializerImpl () {
        this.outputType = false;
        this.useProperties = true;
        this.useFields = true;
        includeNulls = false;
    }

    public JsonSerializerImpl ( final boolean outputType, final boolean useProperties,
                                final boolean useFields, final boolean includeNulls ) {
        this.outputType = outputType;
        this.useFields = useFields;
        this.useProperties = useProperties;
        this.includeNulls = includeNulls;
    }

    private final void serializeString( String str, CharBuf builder ) {
        builder.addChar( '\"' );
        char[] charArray = str.toCharArray();

        for ( int index = 0; index < charArray.length; index++ ) {
            char c = charArray[ index ];

            switch ( c ) {
                case '\"':
                    builder.addChar( '\\' ).addChar( '\"' );
                    break;
                case '\\':
                    builder.addChar( '\\' ).addChar( '\\' );
                    break;
                case '/':
                    builder.addChar( '\\' ).addChar( '/' );
                    break;
                case '\b':
                    builder.addChar( '\\' ).addChar( 'b' );
                    break;
                case '\f':
                    builder.addChar( '\\' ).addChar( 'f' );
                    break;
                case '\n':
                    builder.addChar( '\\' ).addChar( 'n' );
                    break;
                case '\r':
                    builder.addChar( '\\' ).addChar( 'r' );
                    break;
                case '\t':
                    builder.addChar( '\\' ).addChar( 't' );
                    break;

                default:
                /* Encode unicode character. */
                    // if (!asciiEncoder.canEncode(c)){ //This works to but worried
                    // it might be too slow http://en.wikipedia.org/wiki/ASCII
                    if ( c > 0x7F ) { // See if it is out of range of ASCII
                        // I don't like this for performance, I am going to roll my
                        // own.
                        // builder.append(String.format("\\u%4H", c).replace(' ',
                        // '0'));
                        String hexString = Integer.toHexString( c ).toUpperCase();
                        builder.addChar( '\\' ).addChar( 'u' );

                        if ( hexString.length() >= 4 ) {
                            builder.add( hexString );
                        } else {
                            int howMany0 = 4 - hexString.length();
                            for ( int i = 0; i < howMany0; i++ ) {
                                builder.addChar( '0' );
                            }
                            builder.add( hexString );
                        }
                    } else {
                        builder.addChar( c );
                    }

            }
        }
        builder.addChar( '\"' );
    }

    public CharBuf serialize( Object obj ) {
        CharBuf builder = CharBuf.create( 64 );

        try {
            serializeObject( obj, builder );
        } catch ( Exception ex ) {
            return Exceptions.handle( CharBuf.class, "unable to serializeObject", ex );
        }
        return builder;
    }

    private final void serializeObject( Object obj, CharBuf builder ) throws Exception {

        if ( obj == null ) {
            builder.add ( "null" );
        } else if ( obj instanceof Number || obj instanceof Boolean ) {
            builder.add ( obj.toString () );
        } else if ( obj instanceof String ) {
            serializeString ( ( String ) obj, builder );
        } else if ( obj instanceof Collection ) {
            Collection<?> collection = ( Collection<?> ) obj;
            serializeCollection ( collection, builder );
        } else if ( obj.getClass().isArray() ) {
            serializeArray ( ( Object[] ) obj, builder );
        } else if ( obj instanceof Date ) {
            Dates.jsonDateChars ( ( Date ) obj, builder );
        } else if ( obj instanceof Map ) {
            serializeMap( ( Map ) obj, builder );
        } else if ( obj instanceof CharSequence ) {
            serializeString( obj.toString (), builder );
        } else if ( obj instanceof Enum ) {
            serializeString( obj.toString (), builder );
        } else {
            serializeInstance ( obj, builder );
        }
    }

    private final void serializeInstance ( Object obj, CharBuf builder ) throws Exception {
        builder.addChar( '{' );
        if ( outputType ) {
            builder.add( "\"class\":\"" );
            builder.add( obj.getClass().getName() );
            builder.addChar( '"' );
        }


        final Map<String, FieldAccess> fieldAccessors = getFields(obj.getClass ());

        int index = 0;
        Collection<FieldAccess> values = fieldAccessors.values();
        int length = values.size();

        if ( outputType && length > 0 ) {
            builder.addChar( ',' );
        }

        if (!includeNulls) {
            List<FieldAccess> nonNullValues = new LinkedList<> ( values  );
            for ( FieldAccess fieldAccess : values ) {
                Object value = fieldAccess.getValue( obj );
                if (value == null) {
                    nonNullValues.remove ( fieldAccess );
                    length--;
                }
            }
            values = nonNullValues;
        }

        for ( FieldAccess fieldAccess : values ) {

            builder.addChar( '\"' );
            builder.add( fieldAccess.getName() );
            builder.addChar( '\"' );
            builder.addChar( ':' );
            serializeObject( fieldAccess.getValue( obj ), builder );

            if ( index + 1 != length ) {
                    builder.addChar( ',' );
            }
            index++;
        }


        builder.addChar( '}' );
    }

    private final Map<String, FieldAccess> getFields ( Class<? extends Object> aClass ) {
        Map<String, FieldAccess> map = fieldMap.get ( aClass );
        if (map == null) {
            map = doGetFields ( aClass );
            fieldMap.put ( aClass, map );
        }
        return map;
    }

    private final Map<String, FieldAccess> doGetFields ( Class<? extends Object> aClass ) {


        if ( useProperties && !useFields ) {
            return Reflection.getPropertyFieldAccessors ( aClass );
        } else if ( !useProperties && useFields ) {
            return Reflection.getAllAccessorFields ( aClass );
        } else {
            return Reflection.getPropertyFieldAccessMapFieldFirst ( aClass );
        }
    }

    private final void serializeMap( Map<Object, Object> map, CharBuf builder ) throws Exception {
        final Set<Map.Entry<Object, Object>> entrySet = map.entrySet();
        for ( Map.Entry<Object, Object> entry : entrySet ) {
            builder.addChar( '\"' );
            builder.add( entry.getKey().toString() );
            builder.addChar( '\"' );
            builder.addChar( ':' );
            serializeObject( entry.getValue(), builder );
        }
    }

    private final void serializeCollection( Collection<?> collection, CharBuf builder ) throws Exception {
        builder.addChar( '[' );

        final int length = collection.size ();
        int index = 0;
        for ( Object o : collection ) {
            serializeObject( o, builder );

            if ( index != length - 1 ) {
                builder.append( ',' );
            }

            index++;
        }
        builder.addChar( ']' );

    }

    private final void serializeArray( Object[] array, CharBuf builder ) throws Exception {
        builder.addChar( '[' );
        for ( int index = 0; index < array.length; index++ ) {
            serializeObject( array[ index ], builder );
            if ( index != array.length - 1 ) {
                builder.append( ',' );
            }
        }
        builder.addChar( ']' );
    }

}
