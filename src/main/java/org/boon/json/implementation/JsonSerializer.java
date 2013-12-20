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

    public void serializeString( String str, CharBuf builder ) {
        builder.addChar ( '\"' );
        char[] charArray = str.toCharArray ();

        for ( int index = 0; index < charArray.length; index++ ) {
<<<<<<< HEAD
            char c = charArray[ index ];
=======
            char c = charArray[index];
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

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

<<<<<<< HEAD
    public CharBuf serialize ( Object obj ) {
        CharBuf builder = CharBuf.create ( 64 );

        try {
            serializeObject ( obj, builder );
        } catch ( Exception ex ) {
            return Exceptions.handle ( CharBuf.class, "unable to serializeObject", ex );
=======
    public CharBuf serializeObject( Object obj ) {
        CharBuf builder = CharBuf.create ( 64 );

        try {
            serialize ( obj, builder );
        } catch ( Exception ex ) {
            return Exceptions.handle ( CharBuf.class, "unable to serialize", ex );
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        }
        return builder;
    }

<<<<<<< HEAD
    public void serializeObject ( Object obj, CharBuf builder ) throws Exception {
=======
    public void serialize( Object obj, CharBuf builder ) throws Exception {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

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
<<<<<<< HEAD
                builder.add ( "\"class\":\"" );
=======
                builder.add ( "\"java_type\":\"" );
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                builder.add ( obj.getClass ().getName () );
                builder.addChar ( '"' );
            }

<<<<<<< HEAD

            final Map<String, FieldAccess> fieldAccessors = Reflection.getPropertyFieldAccessors ( obj.getClass () );

            int index = 0;
            Collection<FieldAccess> values = fieldAccessors.values ();
            final int length = values.size ();
=======
            Method[] methods = obj.getClass ().getMethods ();

            List<Method> methodList = new ArrayList<> ( methods.length );

            for ( int index = 0; index < methods.length; index++ ) {
                Method method = methods[index];
                String name = method.getName ();

                if ( method.getParameterTypes ().length > 0
                        || method.getReturnType () == Void.class
                        || !( name.startsWith ( "get" ) || name.startsWith ( "is" ) )
                        || name.equals ( "getClass" ) ) {
                    continue;
                }
                methodList.add ( method );
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc

            if ( outputType && length > 0 ) {
                builder.addChar ( ',' );
            }

<<<<<<< HEAD
            for ( FieldAccess fieldAccess : values ) {

                builder.addChar ( '\"' );
                builder.add ( fieldAccess.getName () );
                builder.addChar ( '\"' );
                builder.addChar ( ':' );
                serializeObject ( fieldAccess.getObject ( obj ), builder );

                if ( index + 1 != length ) {
=======
            if ( methodList.size () > 0 ) {
                builder.append ( ',' );
            }

            for ( int index = 0; index < methodList.size (); index++ ) {
                Method method = methodList.get ( index );
                String name = method.getName ();
                if ( name.charAt ( 0 ) == 'g' ) {
                    name = name.substring ( 3 );
                } else {
                    name = name.substring ( 2 );
                }
                name = "" + Character.toLowerCase ( name.charAt ( 0 ) )
                        + name.substring ( 1 );
                builder.addChar ( '\"' );
                builder.add ( name );
                builder.addChar ( '\"' );
                builder.addChar ( ':' );
                Object object = method.invoke ( obj );
                serialize ( object, builder );

                if ( index + 1 != methodList.size () ) {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
                    builder.addChar ( ',' );
                }
                index++;
            }


            builder.addChar ( '}' );
        }
    }

<<<<<<< HEAD
    private void serializeMap ( Map<Object, Object> map, CharBuf builder ) throws Exception {
=======
    private void serializeMap( Map<Object, Object> map, CharBuf builder ) throws Exception {
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
        final Set<Map.Entry<Object, Object>> entrySet = map.entrySet ();
        for ( Map.Entry<Object, Object> entry : entrySet ) {
            builder.addChar ( '\"' );
            builder.add ( entry.getKey ().toString () );
            builder.addChar ( '\"' );
            builder.addChar ( ':' );
            serializeObject ( entry.getValue (), builder );
        }
    }

<<<<<<< HEAD
    private void serializeCollection ( Collection<?> collection, CharBuf builder ) throws Exception {
        for ( Object o : collection ) {
            serializeObject ( o, builder );
        }
    }

    private void serializeArray ( Object[] array, CharBuf builder ) throws Exception {
        builder.addChar ( '[' );
        for ( int index = 0; index < array.length; index++ ) {
            serializeObject ( array[ index ], builder );
=======
    private void serializeCollection( Collection<?> collection, CharBuf builder ) throws Exception {
        for ( Object o : collection ) {
            serialize ( o, builder );
        }
    }

    private void serializeArray( Object[] array, CharBuf builder ) throws Exception {
        builder.addChar ( '[' );
        for ( int index = 0; index < array.length; index++ ) {
            serialize ( array[index], builder );
>>>>>>> 6573736791d65b6ea53d0b71a4c23db4a87188fc
            if ( index != array.length - 1 ) {
                builder.append ( ',' );
            }
        }
        builder.addChar ( ']' );
    }

}
