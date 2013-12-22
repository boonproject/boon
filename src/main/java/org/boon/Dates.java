package org.boon;

import org.boon.core.Sys;
import org.boon.core.reflection.FastStringUtils;
import org.boon.core.reflection.Reflection;
import org.boon.json.JsonException;
import org.boon.primitive.CharBuf;
import org.boon.primitive.CharScanner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Dates {

    private static TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone( "UTC" );
    private static volatile long lastNow;
    private static long MILLI_SECOND = 1;
    private static long SECOND = MILLI_SECOND * 1000;
    private static long MINUTE = 60 * SECOND;
    private static long HOUR = 60 * MINUTE;
    private static long DAY = 24 * HOUR;
    private static long WEEK = 7 * DAY;
    private static long MONTH = ( long ) ( 30.4167 * DAY );
    private static long YEAR = ( long ) ( 365.2425 * DAY );


    public static long utcNow() {
        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( now );
        calendar.setTimeZone( UTC_TIME_ZONE );
        long utcNow = calendar.getTime().getTime();
        lastNow = now;
        return utcNow;
    }

    public static long utc( long time ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( time );
        calendar.setTimeZone( UTC_TIME_ZONE );
        long utcNow = calendar.getTime().getTime();
        lastNow = time;
        return utcNow;
    }

    /**
     * For testing only, avoids potential timing issue.
     */
    static long lastNow() {
        return lastNow;
    }


    public static long fromUtcTimeToTimeZone( long utcTime, TimeZone timeZone ) {

        Calendar calendar = Calendar.getInstance( UTC_TIME_ZONE );
        calendar.setTimeInMillis( utcTime );
        calendar.setTimeZone( timeZone );
        return calendar.getTime().getTime();
    }


    public static boolean before( long isThis, long beforeThis ) {
        return isThis < beforeThis;
    }


    public static boolean after( long isThis, long afterThis ) {
        return isThis > afterThis;
    }


    public static long secondsFrom( long time, int seconds ) {
        return time + ( seconds * SECOND );
    }

    public static long minutesFrom( long time, int minutes ) {
        return time + ( minutes * MINUTE );
    }

    public static long hoursFrom( long time, int hours ) {
        return time + ( hours * HOUR );
    }

    public static long daysFrom( long time, int days ) {
        return time + ( days * DAY );
    }

    public static long weeksFrom( long time, int weeks ) {
        return time + ( weeks * WEEK );
    }

    public static long monthsFrom( long time, int months ) {
        return time + ( months * MONTH );
    }

    public static long yearsFrom( long time, int years ) {
        return time + ( years * YEAR );
    }

    public static long utcDate( int year, int month, int day ) {
        Calendar calendar = Calendar.getInstance();

        /* Set to midnight. */
        midnight( calendar );

        /* This might change the date, but when you convert it
        back to the clocktime timezone, it will be correct.
         */
        calendar.setTimeZone( UTC_TIME_ZONE );


        return internalDate( year, month, day, calendar );
    }

    public static long utcDate( int year, int month, int day,
                                int hour, int minute ) {
        Calendar calendar = Calendar.getInstance();
        midnight( calendar );

        /* This might change the date, but when you convert it
        back to the clocktime timezone, it will be correct.
         */
        calendar.setTimeZone( UTC_TIME_ZONE );

        return internalDateLong( year, month, day, hour, minute, calendar );
    }

    private static long internalDateLong( int year, int month, int day, int hour, int minute, Calendar calendar ) {

        return internalDate( year, month, day, hour, minute, calendar ).getTime();

    }


    private static Date internalDate( int year, int month, int day, int hour, int minute, Calendar calendar ) {
        calendar.set( Calendar.YEAR, year );
        calendar.set( Calendar.MONTH, month );
        calendar.set( Calendar.DAY_OF_MONTH, day );
        calendar.set( Calendar.HOUR_OF_DAY, hour );
        calendar.set( Calendar.MINUTE, minute );

        return calendar.getTime();
    }


    private static Date internalDate( TimeZone tz, int year, int month, int day, int hour, int minute, int second ) {

        Calendar calendar = Calendar.getInstance();

        calendar.set( Calendar.YEAR, year );
        calendar.set( Calendar.MONTH, month - 1 );
        calendar.set( Calendar.DAY_OF_MONTH, day );
        calendar.set( Calendar.HOUR_OF_DAY, hour );
        calendar.set( Calendar.MINUTE, minute );
        calendar.set( Calendar.SECOND, second );
        calendar.setTimeZone( tz );

        return calendar.getTime();
    }


    private static Date internalDate( TimeZone tz, int year, int month, int day, int hour,
                                      int minute, int second, int miliseconds ) {

        Calendar calendar = Calendar.getInstance();

        calendar.set( Calendar.YEAR, year );
        calendar.set( Calendar.MONTH, month - 1 );
        calendar.set( Calendar.DAY_OF_MONTH, day );
        calendar.set( Calendar.HOUR_OF_DAY, hour );
        calendar.set( Calendar.MINUTE, minute );
        calendar.set( Calendar.SECOND, second );
        calendar.set( Calendar.MILLISECOND, miliseconds );

        calendar.setTimeZone( tz );

        return calendar.getTime();
    }

    public static long wallTimeDate( int year, int month, int day ) {
        Calendar calendar = Calendar.getInstance();

        /* Set to midnight. */
        midnight( calendar );


        return internalDate( year, month, day, calendar );
    }


    public static long date( int year, int month, int day ) {
        return utcDate( year, month, day );
    }

    public static long date( int year, int month, int day,
                             int hour, int minute ) {
        return utcDate( year, month, day, hour, minute );

    }


    public static long date( TimeZone tz, int year, int month, int day ) {
        Calendar calendar = Calendar.getInstance();

        /* Set to midnight. */
        midnight( calendar );

        calendar.setTimeZone( tz );

        return internalDate( year, month, day, calendar );
    }

    private static long internalDate( int year, int month, int day, Calendar calendar ) {
        calendar.set( Calendar.YEAR, year );
        calendar.set( Calendar.MONTH, month );
        calendar.set( Calendar.DAY_OF_MONTH, day );

        return calendar.getTime().getTime();
    }

    public static long wallTimeDate( int year, int month, int day,
                                     int hour, int minute ) {
        Calendar calendar = Calendar.getInstance();
        midnight( calendar );


        return internalDateLong( year, month, day, hour, minute, calendar );
    }


    public static Date toDate( TimeZone tz, int year, int month, int day,
                               int hour, int minute, int second ) {
        return internalDate( tz, year, month, day, hour, minute, second );
    }


    public static Date toDate( TimeZone tz, int year, int month, int day,
                               int hour, int minute, int second, int miliseconds ) {
        return internalDate( tz, year, month, day, hour, minute, second, miliseconds );
    }

    public static Date toDate( int year, int month, int day,
                               int hour, int minute, int second, int miliseconds ) {
        return internalDate( TimeZone.getDefault(), year, month, day, hour, minute, second, miliseconds );
    }

    public static long date( TimeZone tz, int year, int month, int day,
                             int hour, int minute ) {
        Calendar calendar = Calendar.getInstance();
        midnight( calendar );
        calendar.setTimeZone( tz );

        return internalDateLong( year, month, day, hour, minute, calendar );
    }

    private static void midnight( Calendar calendar ) {
        /* Set to midnight. */
        calendar.set( Calendar.HOUR_OF_DAY, 0 );
        calendar.set( Calendar.MINUTE, 0 );
        calendar.set( Calendar.SECOND, 0 );
        calendar.set( Calendar.MILLISECOND, 0 );
    }

    /**
     * Useful for generating string versions of timestamps
     *
     * @return euro style format.
     */
    public static String euroUTCSystemDateNowString() {
        long now = System.currentTimeMillis();
        return euroUTCSystemDateString( now );
    }


    /**
     * Useful for generated file names and generated work directories.
     *
     * @param timestamp the timestamp
     * @return euro style format.
     */
    public static String euroUTCSystemDateString( long timestamp ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis( timestamp );
        calendar.setTimeZone( UTC_TIME_ZONE );
        int day = calendar.get( Calendar.DAY_OF_MONTH );
        int month = calendar.get( Calendar.MONTH );
        int year = calendar.get( Calendar.YEAR );
        int hour = calendar.get( Calendar.HOUR_OF_DAY );
        int minute = calendar.get( Calendar.MINUTE );
        int second = calendar.get( Calendar.SECOND );

        CharBuf buf = CharBuf.create( 16 );
        buf.add( Str.zfill( day, 2 ) ).add( '_' );
        buf.add( Str.zfill( month, 2 ) ).add( '_' );
        buf.add( year ).add( '_' );
        buf.add( Str.zfill( hour, 2 ) ).add( '_' );
        buf.add( Str.zfill( minute, 2 ) ).add( '_' );
        buf.add( Str.zfill( second, 2 ) ).add( "_utc_euro" );

        return buf.toString();
    }


    public static void main( String... args ) {

        Sys.println( euroUTCSystemDateNowString() );

    }


    public static Date year( int year ) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
        c.set( 1970, Calendar.JANUARY, 2, 0, 0, 0 );
        c.set( Calendar.YEAR, year );
        return c.getTime();
    }

    public static Date getUSDate( int month, int day, int year ) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
        c.set( year, month - 1, day + 1, 0, 0, 0 );
        return c.getTime();
    }


    public static Date getUSDate( int month, int day, int year, int hour, int minute, int second ) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
        c.set( year, month - 1, day + 1, hour, minute, second );
        return c.getTime();
    }

    public static Date getEuroDate( int day, int month, int year ) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
        c.set( year, month - 1, day + 1, 0, 0, 0 );
        return c.getTime();
    }

    public static Date getEuroDate( int day, int month, int year, int hour, int minute, int second ) {
        Calendar c = Calendar.getInstance();
        c.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
        c.set( year, month - 1, day + 1, hour, minute, second );
        return c.getTime();
    }


    public static Date fromISO8601_( String string ) {

        try {

            return new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssXXX" ).parse( string );
        } catch ( ParseException e ) {
            return Exceptions.handle( Date.class, "Not a valid ISO8601", e );
        }


    }

    public static Date fromJsonDate_( String string ) {

        try {

            return new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" ).parse( string );
        } catch ( ParseException e ) {
            return Exceptions.handle( Date.class, "Not a valid JSON date", e );
        }


    }


    public static Date fromJsonDate( String string ) {

        return fromJsonDate( FastStringUtils.toCharArray( string ), 0, string.length() );

    }

    public static Date fromISO8601( String string ) {

        return fromISO8601( FastStringUtils.toCharArray( string ), 0, string.length() );

    }

    public static Date fromISO8601DateLoose( String string ) {
        return fromISO8601DateLoose( FastStringUtils.toCharArray( string ), 0, string.length() );

    }


    final static int SHORT_ISO_8601_TIME_LENGTH = "1994-11-05T08:15:30Z".length();
    // 01234567890123456789012
    final static int LONG_ISO_8601_TIME_LENGTH = "1994-11-05T08:15:30-05:00".length();
    // 01234567890123456789012
    final static int JSON_TIME_LENGTH = "2013-12-14T01:55:33.412Z".length();

    public static Date fromISO8601DateLoose( char[] buffer, int startIndex, int endIndex ) {

        if ( Dates.isISO8601QuickCheck( buffer, startIndex, endIndex ) ) {

            if ( Dates.isJsonDate( buffer, startIndex, endIndex ) ) {
                return Dates.fromJsonDate( buffer, startIndex, endIndex );

            } else if ( Dates.isISO8601( buffer, startIndex, endIndex ) ) {
                return Dates.fromISO8601( buffer, startIndex, endIndex );
            } else {
                try {
                    return looseParse( buffer, startIndex, endIndex );
                } catch ( Exception ex ) {
                    throw new JsonException( "unable to do a loose parse", ex );
                }
            }
        } else {

            try {
                return looseParse( buffer, startIndex, endIndex );
            } catch ( Exception ex ) {
                throw new JsonException( "unable to do a loose parse", ex );
            }
        }


    }

    private static Date looseParse( char[] buffer, int startIndex, int endIndex ) {
        final char[][] parts = CharScanner.splitByCharsNoneEmpty( buffer, startIndex, endIndex, '-', ':', 'T', '.' );
        int year = 0;
        int month = 0;
        int day = 0;

        int hour = 0;
        int minutes = 0;
        int seconds = 0;

        int mili = 0;

        if ( parts.length >= 3 ) {
            year = CharScanner.parseInt( parts[ 0 ] );
            month = CharScanner.parseInt( parts[ 1 ] );
            day = CharScanner.parseInt( parts[ 2 ] );
        }

        if ( parts.length >= 6 ) {
            hour = CharScanner.parseInt( parts[ 3 ] );
            minutes = CharScanner.parseInt( parts[ 4 ] );
            seconds = CharScanner.parseInt( parts[ 5 ] );
        }

        if ( parts.length >= 7 ) {
            mili = CharScanner.parseInt( parts[ 6 ] );
        }


        return toDate( year, month, day, hour, minutes, seconds, mili );
    }

    public static Date fromISO8601( char[] charArray, int from, int to ) {

        int length = to - from;
        if ( isISO8601( charArray, from, to ) ) {
            int year = CharScanner.parseIntFromTo( charArray, from + 0, from + 4 );
            int month = CharScanner.parseIntFromTo( charArray, from + 5, from + 7 );
            int day = CharScanner.parseIntFromTo( charArray, from + 8, from + 10 );
            int hour = CharScanner.parseIntFromTo( charArray, from + 11, from + 13 );

            int minute = CharScanner.parseIntFromTo( charArray, from + 14, from + 16 );

            int second = CharScanner.parseIntFromTo( charArray, from + 17, from + 19 );
            TimeZone tz = null;

            if ( charArray[ from + 19 ] == 'Z' ) {

                tz = TimeZone.getTimeZone( "GMT" );

            } else {

                StringBuilder builder = new StringBuilder( 9 );
                builder.append( "GMT" );
                builder.append( charArray, from + 19, 6 );
                String tzStr = builder.toString();
                tz = TimeZone.getTimeZone( tzStr );

            }
            return toDate( tz, year, month, day, hour, minute, second );

        } else {
            return null;
        }

    }

    public static Date fromJsonDate( char[] charArray, int from, int to ) {

        if ( isJsonDate( charArray, from, to ) ) {
            int year = CharScanner.parseIntFromTo( charArray, from + 0, from + 4 );
            int month = CharScanner.parseIntFromTo( charArray, from + 5, from + 7 );
            int day = CharScanner.parseIntFromTo( charArray, from + 8, from + 10 );
            int hour = CharScanner.parseIntFromTo( charArray, from + 11, from + 13 );

            int minute = CharScanner.parseIntFromTo( charArray, from + 14, from + 16 );

            int second = CharScanner.parseIntFromTo( charArray, from + 17, from + 19 );

            int miliseconds = CharScanner.parseIntFromTo( charArray, from + 20, from + 23 );

            TimeZone tz = TimeZone.getTimeZone( "GMT" );


            return toDate( tz, year, month, day, hour, minute, second, miliseconds );

        } else {
            return null;
        }

    }

    public static boolean isISO8601( String string ) {

        return isISO8601( FastStringUtils.toCharArray( string ) );
    }


    public static boolean isISO8601( char[] charArray ) {
        return isISO8601( charArray, 0, charArray.length );
    }

    public static boolean isISO8601( char[] charArray, int start, int to ) {
        boolean valid = true;
        final int length = to - start;

        if ( length == SHORT_ISO_8601_TIME_LENGTH ) {
            valid &= ( charArray[ start + 19 ] == 'Z' );

        } else if ( length == LONG_ISO_8601_TIME_LENGTH ) {
            valid &= ( charArray[ start + 19 ] == '-' || charArray[ start + 19 ] == '+' );
            valid &= ( charArray[ start + 22 ] == ':' );

        } else {
            return false;
        }

        //  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4
        // "1 9 9 4 - 1 1 - 0 5 T 0 8 : 1 5 : 3 0 - 0 5 : 0 0

        valid &= ( charArray[ start + 4 ] == '-' ) &&
                ( charArray[ start + 7 ] == '-' ) &&
                ( charArray[ start + 10 ] == 'T' ) &&
                ( charArray[ start + 13 ] == ':' ) &&
                ( charArray[ start + 16 ] == ':' );

        return valid;
    }

    public static boolean isISO8601QuickCheck( char[] charArray, int start, int to ) {
        final int length = to - start;

        if ( length == JSON_TIME_LENGTH || length == LONG_ISO_8601_TIME_LENGTH
                || length == SHORT_ISO_8601_TIME_LENGTH || ( length >= 16 && ( charArray[ start + 16 ] == ':' ) )
                ) {

            if ( length >= 16 && ( charArray[ start + 16 ] == ':' ) ) {
                return true;
            }
        }

        return false;

    }

    public static boolean isJsonDate( char[] charArray, int start, int to ) {
        boolean valid = true;
        final int length = to - start;

        if ( length != JSON_TIME_LENGTH ) {
            return false;
        }

        valid &= ( charArray[ start + 19 ] == '.' );

        if ( !valid ) {
            return false;
        }


        valid &= ( charArray[ start + 4 ] == '-' ) &&
                ( charArray[ start + 7 ] == '-' ) &&
                ( charArray[ start + 10 ] == 'T' ) &&
                ( charArray[ start + 13 ] == ':' ) &&
                ( charArray[ start + 16 ] == ':' );

        return valid;
    }

}
