package org.boon.core.reflection;

import org.boon.core.Sys;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by rick on 12/15/13.
 */
public class FastStringUtils {


    public static final Unsafe UNSAFE;
    public static final long STRING_VALUE_FIELD_OFFSET;
    public static final boolean ENABLED;

    private static final boolean WRITE_TO_FINAL_FIELDS = Boolean.parseBoolean( System.getProperty( "org.boon.dont.write.to.final.fields", "false" ) );
    private static final boolean DISABLE = Boolean.parseBoolean( System.getProperty( "org.boon.faststringutils", "false" ) );

    static {

        if (!DISABLE && is17Build40OrLater() )  {
            Unsafe unsafe;
            try {
                Field unsafeField = Unsafe.class.getDeclaredField( "theUnsafe" );
                unsafeField.setAccessible( true );
                unsafe = ( Unsafe ) unsafeField.get( null );

            } catch ( Throwable cause ) {
                unsafe = null;

            }

            UNSAFE = unsafe;
            ENABLED = unsafe != null;

            long stringValueFieldOffset = -1L;

            if ( ENABLED ) {
                try {
                    stringValueFieldOffset = unsafe.objectFieldOffset( String.class.getDeclaredField( "value" ) );
                } catch ( Throwable cause ) {
                }
            }
            STRING_VALUE_FIELD_OFFSET = stringValueFieldOffset;
        } else {
            STRING_VALUE_FIELD_OFFSET = 0;
            UNSAFE = null;
            ENABLED = false;

        }

    }

    private static boolean is17Build40OrLater () {
        return  Sys.is1_8() ||  ( Sys.is1_7() && Sys.buildNumber() >= 40 );
    }

    public static boolean hasUnsafe() {
        return ENABLED;
    }

    public static char[] toCharArray( final String string ) {
        return ENABLED ?
                ( char[] ) UNSAFE.getObject( string, STRING_VALUE_FIELD_OFFSET ) :
                string.toCharArray();

    }


    public static char[] toCharArray( final CharSequence charSequence ) {
        return ENABLED ?
                ( char[] ) UNSAFE.getObject( charSequence.toString(), STRING_VALUE_FIELD_OFFSET ) :
                charSequence.toString().toCharArray();

    }

    public static char[] toCharArrayFromBytes( final byte[] bytes, Charset charset ) {
        final String string = new String( bytes, StandardCharsets.UTF_8 );
        return ENABLED ?
                ( char[] ) UNSAFE.getObject( string, STRING_VALUE_FIELD_OFFSET ) :
                string.toCharArray();
    }


    public static String noCopyStringFromChars( final char[] chars ) {

        if ( WRITE_TO_FINAL_FIELDS && ENABLED ) {

            final String string = new String();
            UNSAFE.putObject( string, STRING_VALUE_FIELD_OFFSET, chars );
            return string;
        } else {
            return new String( chars );
        }
    }

}


