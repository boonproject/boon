package org.boon.json.implementation;

import org.boon.Exceptions;
import org.boon.core.reflection.Reflection;
import org.boon.core.reflection.fields.FieldAccess;
import org.boon.primitive.CharBuf;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by rick on 12/18/13.
 */
public class JsonSerializer {

    private final boolean outputType;

    public JsonSerializer () {
        this.outputType = false;

    }

    public JsonSerializer ( final boolean outputType ) {
        this.outputType = outputType;

    }

    public void serializeString ( String str, CharBuf builder ) {
        builder.addChar ( '\"' );
        char[] charArray = str.toCharArray ();

        for ( int index = 0; index < charArray.length; index++ ) {
            char c = charArray[ index ];

            switch ( c ) {
                case '\"':
                    builder.addChar ( '\\' ).addChar ( '\"' );
                    break;
                case '\\':
                    builder.addChar ( '\\' ).addChar ( '\\' );
                    break;
                case '/':
                    builder.addChar ( '\\' ).addChar ( '/' );
                    break;
                case '\b':
                    builder.addChar ( '\\' ).addChar ( 'b' );
                    break;
                case '\f':
                    builder.addChar ( '\\' ).addChar ( 'f' );
                    break;
                case '\n':
                    builder.addChar ( '\\' ).addChar ( 'n' );
                    break;
                case '\r':
                    builder.addChar ( '\\' ).addChar ( 'r' );
                    break;
                case '\t':
                    builder.addChar ( '\\' ).addChar ( 't' );
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
                        String hexString = Integer.toHexString ( c ).toUpperCase ();
                        builder.addChar ( '\\' ).addChar ( 'u' );

                        if ( hexString.length () >= 4 ) {
                            builder.add ( hexString );
                        } else {
                            int howMany0 = 4 - hexString.length ();
                            for ( int i = 0; i < howMany0; i++ ) {
                                builder.addChar ( '0' );
                            }
                            builder.add ( hexString );
                        }
                    } else {
                        builder.addChar ( c );
                    }

            }
        }
        builder.addChar ( '\"' );
    }

    public CharBuf serialize ( Object obj ) {
        CharBuf builder = CharBuf.create ( 64 );

        try {
            serializeObject ( obj, builder );
        } catch ( Exception ex ) {
            return Exceptions.handle ( CharBuf.class, "unable to serializeObject", ex );
        }
        return builder;
    }

    public void serializeObject ( Object obj, CharBuf builder ) throws Exception {

        if ( obj == null ) {
            builder.add ( "null" );
        }
        if ( obj instanceof Number || obj instanceof Boolean ) {
            builder.add ( obj.toString () );
        } else if ( obj instanceof String ) {
            serializeString ( ( String ) obj, builder );
        } else if ( obj instanceof Collection ) {
            Collection<?> collection = ( Collection<?> ) obj;
            serializeCollection ( collection, builder );
        } else if ( obj.getClass ().isArray () ) {
            serializeArray ( ( Object[] ) obj, builder );
        } else if ( obj instanceof Map ) {
            serializeMap ( ( Map ) obj, builder );
        } else {
            builder.addChar ( '{' );
            if ( outputType ) {
                builder.add ( "\"class\":\"" );
                builder.add ( obj.getClass ().getName () );
                builder.addChar ( '"' );
            }


            final Map<String, FieldAccess> fieldAccessors = Reflection.getPropertyFieldAccessors ( obj.getClass () );

            int index = 0;
            Collection<FieldAccess> values = fieldAccessors.values ();
            final int length = values.size ();

            if ( outputType && length > 0 ) {
                builder.addChar ( ',' );
            }

            for ( FieldAccess fieldAccess : values ) {

                builder.addChar ( '\"' );
                builder.add ( fieldAccess.getName () );
                builder.addChar ( '\"' );
                builder.addChar ( ':' );
                serializeObject ( fieldAccess.getObject ( obj ), builder );

                if ( index + 1 != length ) {
                    builder.addChar ( ',' );
                }
                index++;
            }


            builder.addChar ( '}' );
        }
    }

    private void serializeMap ( Map<Object, Object> map, CharBuf builder ) throws Exception {
        final Set<Map.Entry<Object, Object>> entrySet = map.entrySet ();
        for ( Map.Entry<Object, Object> entry : entrySet ) {
            builder.addChar ( '\"' );
            builder.add ( entry.getKey ().toString () );
            builder.addChar ( '\"' );
            builder.addChar ( ':' );
            serializeObject ( entry.getValue (), builder );
        }
    }

    private void serializeCollection ( Collection<?> collection, CharBuf builder ) throws Exception {
        for ( Object o : collection ) {
            serializeObject ( o, builder );
        }
    }

    private void serializeArray ( Object[] array, CharBuf builder ) throws Exception {
        builder.addChar ( '[' );
        for ( int index = 0; index < array.length; index++ ) {
            serializeObject ( array[ index ], builder );
            if ( index != array.length - 1 ) {
                builder.append ( ',' );
            }
        }
        builder.addChar ( ']' );
    }

}
