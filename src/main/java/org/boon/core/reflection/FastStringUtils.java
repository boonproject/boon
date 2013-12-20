package org.boon.core.reflection;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;

/**
 * Created by rick on 12/15/13.
 */
public class FastStringUtils {

    public static final Unsafe UNSAFE;
    public static final long STRING_VALUE_FIELD_OFFSET;
    public static final boolean HAS_UNSAFE;

    private static final boolean WRITE_TO_FINAL_FIELDS = Boolean.parseBoolean ( System.getProperty ( "org.boon.dont.write.to.final.fields", "false" ) );

    static {
        Unsafe unsafe;
        try {
            Field unsafeField = Unsafe.class.getDeclaredField ( "theUnsafe" );
            unsafeField.setAccessible ( true );
            unsafe = ( Unsafe ) unsafeField.get ( null );

        } catch ( Throwable cause ) {
            unsafe = null;

        }

        UNSAFE = unsafe;
        HAS_UNSAFE = unsafe != null;

        long stringValueFieldOffset = -1L;

        if ( HAS_UNSAFE ) {
            try {
                stringValueFieldOffset = unsafe.objectFieldOffset ( String.class.getDeclaredField ( "value" ) );
            } catch ( Throwable cause ) {
            }
        }
        STRING_VALUE_FIELD_OFFSET = stringValueFieldOffset;

    }

    public static boolean hasUnsafe() {
        return HAS_UNSAFE;
    }

    public static char[] toCharArray( final String string ) {
        return HAS_UNSAFE ?
                ( char[] ) UNSAFE.getObject ( string, STRING_VALUE_FIELD_OFFSET ) :
                string.toCharArray ();

    }


    public static char[] toCharArrayFromBytes( final byte[] bytes ) {
        final String string = new String ( bytes, StandardCharsets.UTF_8 );
        return HAS_UNSAFE ?
                ( char[] ) UNSAFE.getObject ( string, STRING_VALUE_FIELD_OFFSET ) :
                string.toCharArray ();
    }


    public static String noCopyStringFromChars( final char[] chars ) {

        if ( HAS_UNSAFE && WRITE_TO_FINAL_FIELDS ) {

            final String string = new String ();
            UNSAFE.putObject ( string, STRING_VALUE_FIELD_OFFSET, chars );
            return string;
        } else {
            return new String ( chars );
        }
    }

}


