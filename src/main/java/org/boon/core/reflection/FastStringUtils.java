package org.boon.core.reflection;

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
    public static final long STRING_OFFSET_FIELD_OFFSET;
    public static final long STRING_COUNT_FIELD_OFFSET;
    public static final boolean ENABLED;

    private static final boolean WRITE_TO_FINAL_FIELDS = Boolean.parseBoolean( System.getProperty( "org.boon.write.to.final.fields", "false" ) );
    private static final boolean DISABLE = Boolean.parseBoolean( System.getProperty( "org.boon.faststringutils", "false" ) );

    static {

        if (!DISABLE)  {
        	Unsafe unsafe;
            try {
                Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
                unsafeField.setAccessible(true);
                unsafe = (Unsafe) unsafeField.get(null);

            } catch ( Throwable cause ) {
                unsafe = null;
            }

            UNSAFE = unsafe;
            ENABLED = unsafe != null;

            long stringValueFieldOffset = -1L;
            long stringOffsetFieldOffset = -1L;
            long stringCountFieldOffset = -1L;

            if ( ENABLED ) {
                try {
                    stringValueFieldOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("value"));
                    stringOffsetFieldOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("offset"));
                    stringCountFieldOffset = unsafe.objectFieldOffset(String.class.getDeclaredField("count"));
                } catch ( Throwable cause ) {
                }
            }

            STRING_VALUE_FIELD_OFFSET = stringValueFieldOffset;
            STRING_OFFSET_FIELD_OFFSET = stringOffsetFieldOffset;
            STRING_COUNT_FIELD_OFFSET = stringCountFieldOffset;

        } else {
            STRING_VALUE_FIELD_OFFSET = -1;
            STRING_OFFSET_FIELD_OFFSET = -1;
            STRING_COUNT_FIELD_OFFSET = -1;
            UNSAFE = null;
            ENABLED = false;
        }
    }

    public static boolean hasUnsafe() {
        return ENABLED;
    }

    public static char[] toCharArray( final String string ) {
        if ( ENABLED ) {
            char[] value = (char[]) UNSAFE.getObject(string, STRING_VALUE_FIELD_OFFSET);

            if ( STRING_OFFSET_FIELD_OFFSET != -1 ) {
                // old String version with offset and count
                int offset = (int) UNSAFE.getObject(string, STRING_OFFSET_FIELD_OFFSET);
                int count = (int) UNSAFE.getObject(string, STRING_COUNT_FIELD_OFFSET);

                if ( offset == 0 && count == value.length ) {
                    // no need to copy
                    return value;

                } else {
                    char result[] = new char[count];
                    System.arraycopy(value, offset, result, 0, count);
                    return result;
                }

            } else {
                return value;
            }

        } else {
            return string.toCharArray();
        }
    }

    public static char[] toCharArray( final CharSequence charSequence ) {
        return toCharArray( charSequence.toString() );
    }

    public static char[] toCharArrayFromBytes( final byte[] bytes, Charset charset ) {
    	return toCharArray( new String( bytes, charset != null? charset: StandardCharsets.UTF_8 ) );
    }

    public static String noCopyStringFromChars( final char[] chars ) {

        if ( WRITE_TO_FINAL_FIELDS && ENABLED ) {

            final String string = new String();
            UNSAFE.putObject( string, STRING_VALUE_FIELD_OFFSET, chars );

            if ( STRING_COUNT_FIELD_OFFSET != -1 ) {
            	UNSAFE.putObject( string, STRING_COUNT_FIELD_OFFSET, chars.length );
            }

            return string;
        } else {
            return new String( chars );
        }
    }
}
