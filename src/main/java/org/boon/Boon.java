package org.boon;


import org.boon.core.Sys;
import org.boon.core.reflection.BeanUtils;
import org.boon.primitive.CharBuf;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static org.boon.Exceptions.die;
import static org.boon.Lists.toListOrSingletonList;

public class Boon {


    public static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    public static void println( String message ) {
        Sys.println( message );
    }

    public static void println() {
        Sys.println( "" );
    }

    public static void println( Object message ) {

        print( message );
        println();
    }

    public static void print( String message ) {
        Sys.print( message );
    }

    public static void print( Object message ) {

        if ( message == null ) {
            print( "<NULL>" );
        } else if ( message.getClass().isArray() ) {
            print( toListOrSingletonList( message ).toString() );
        } else {
            print( message.toString() );
        }
    }

    public static void puts( Object... messages ) {

        for ( Object message : messages ) {
            print( message );
            print( ' ' );
        }
        println();

    }

    public static void putl( Object... messages ) {

        for ( Object message : messages ) {
            print( message );
            println();
        }
        println();

    }

    public static String sputl( Object... messages ) {
        CharBuf buf = CharBuf.create( 100 );
        return sputl( buf, messages );
    }

    public static String sputs( Object... messages ) {
        StringBuilder buf = new StringBuilder(  );
        return sputs( buf, messages );
    }


    public static String sputl( CharBuf buf, Object... messages ) {

        for ( Object message : messages ) {
            if ( message == null ) {
                buf.add( "<NULL>" );
            } else if ( message.getClass().isArray() ) {
                buf.add( toListOrSingletonList( message ).toString() );
            } else {
                buf.add( message.toString() );
            }
            buf.add( '\n' );
        }
        buf.add( '\n' );

        return buf.toString();


    }

    public static String sputs( CharBuf buf, Object... messages ) {

        int index = 0;
        for ( Object message : messages ) {
            if ( index != 0 ) {
                buf.add( ' ' );
            }
            index++;

            if ( message == null ) {
                buf.add( "<NULL>" );
            } else if ( message.getClass().isArray() ) {
                buf.add( toListOrSingletonList( message ).toString() );
            } else {
                buf.add( message.toString() );
            }
        }
        buf.add( '\n' );

        return buf.toString();

    }

    public static String sputs( StringBuilder buf, Object... messages ) {

        int index = 0;
        for ( Object message : messages ) {
            if ( index != 0 ) {
                buf.append( ' ' );
            }
            index++;

            if ( message == null ) {
                buf.append( "<NULL>" );
            } else if ( message.getClass().isArray() ) {
                buf.append( toListOrSingletonList( message ).toString() );
            } else {
                buf.append( message.toString() );
            }
        }
        buf.append( '\n' );

        return buf.toString();

    }

    public static boolean isArray( Object obj ) {
        if ( obj == null ) return false;
        return obj.getClass().isArray();
    }

    public static int len( Object obj ) {
        if ( isArray( obj ) ) {
            return arrayLength( obj );
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

    public static int arrayLength( Object obj ) {
        return Array.getLength( obj );
    }

    public static Iterator iterator( final Object o ) {
        if ( o instanceof Collection ) {
            return ( ( Collection ) o ).iterator();
        } else if ( isArray( o ) ) {
            return new Iterator() {
                int index = 0;
                int length = len( o );

                @Override
                public boolean hasNext() {
                    return index < length;
                }

                @Override
                public Object next() {
                    Object value = BeanUtils.idx( o, index );
                    index++;
                    return value;
                }

                @Override
                public void remove() {
                }
            };
        }
        return null;
    }

    public static String joinBy( char delim, Object... args ) {
        CharBuf builder = CharBuf.create( 256 );
        int index = 0;
        for ( Object arg : args ) {
            builder.add( arg.toString() );
            if ( !( index == args.length - 1 ) ) {
                builder.add( delim );
            }
            index++;
        }
        return builder.toString();
    }
}