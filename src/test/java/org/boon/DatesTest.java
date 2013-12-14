package org.boon;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.boon.Boon.puts;
import static org.boon.Exceptions.die;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DatesTest {


    @Test
    public void testIsoShortDate() {
        String test =  "1994-11-05T08:15:30Z";

        Date date = Dates.fromISO8601 ( test );
        Date date2 = Dates.fromISO8601_ ( test );

        assertEquals(date2.toString (), date.toString ());

        puts (date);
    }

    @Test
    public void testIsoLongDate() {
        String test =  "1994-11-05T08:11:22-05:00";

        Date date = Dates.fromISO8601 ( test );
        Date date2 = Dates.fromISO8601_ ( test );

        assertEquals(date2.toString (), date.toString ());

        puts (date);
    }



    /*
        var d=new Date();
        var s = JSON.stringify(d);

        document.write(s);
        document.write("<br />"+d);


        "2013-12-14T01:55:33.412Z"
        Fri Dec 13 2013 17:55:33 GMT-0800 (PST)


     */
    @Test
    public void jsonJavaScriptDate() {
        String test =  "2013-12-14T01:55:33.412Z";

        Date date = Dates.fromJsonDate ( test );
        Date date2 = Dates.fromJsonDate_ ( test );

        assertEquals(date2.toString (), "" + date);

        puts (date);
    }


    @Test
    public void testIsoLooseDate() {
        String test =  "1994-11-05T08:11:22.123";

        Date date = Dates.fromISO8601DateLoose ( test );


        puts ("loose date " + date);

        boolean ok = "Sat Nov 05 08:11:22 PST 1994".equals (  date.toString () ) || die("#"+date.toString ()+"#");
    }

    @Test
    public void testIsoLooserDate() {
        String test =  "1994-11-05";

        Date date = Dates.fromISO8601DateLoose ( test );


        puts ("loose date " + date);

        boolean ok = "Sat Nov 05 00:00:00 PST 1994".equals (  date.toString () ) || die("#"+date.toString ()+"#");
    }

    @Test
    public void testIsoLoose2Date() {
        String test =  "1994-11-05-08:11:22";

        Date date = Dates.fromISO8601DateLoose ( test );


        puts ("loose date " + date);

        boolean ok = "Sat Nov 05 08:11:22 PST 1994".equals (  date.toString () ) || die("#"+date.toString ()+"#");
    }

    @Test
    public void testDates () {
        long now = Dates.utcNow();
        Date clockTime = new Date(Dates.lastNow());
        long lClocktime = Dates.fromUtcTimeToTimeZone(now, TimeZone.getTimeZone ( "PACIFIC" ));
        Date clockTime2 = new Date(lClocktime);

        System.out.println(clockTime2);

        assertEquals(clockTime, clockTime2);
    }


    @Test
    public void testBeforeAfter () {

        puts ( TimeZone.getDefault () );


        long epic = Dates.date( 1970, Calendar.MAY, 29 );
        long forties = Dates.date( 1940, Calendar.MAY, 29 );

        testBeforeAfter(epic, forties);

    }


    @Test
    public void testUtcDate () {

        long epic = Dates.utcDate(1970, Calendar.MAY, 29);
        long forties = Dates.utcDate(1940, Calendar.MAY, 29);


        testBeforeAfter(epic, forties);

    }

    @Test
    public void testDate () {

        long epic = Dates.date( 1970, Calendar.MAY, 29 );
        long forties = Dates.date( 1940, Calendar.MAY, 29 );


        testBeforeAfter(epic, forties);

    }


    @Test
    public void wallTimeDate () {

        long epic = Dates.wallTimeDate(1970, Calendar.MAY, 29);
        long forties = Dates.wallTimeDate(1940, Calendar.MAY, 29);


        testBeforeAfter(epic, forties);

    }



    @Test
    public void wallTimeLongDate () {

        long epic = Dates.wallTimeDate(1970, Calendar.MAY, 29, 5, 5);
        long forties = Dates.wallTimeDate( 1940, Calendar.MAY, 29 );


        testBeforeAfter(epic, forties);

    }


    @Test
    public void testTZDate () {

        long epic = Dates.date(TimeZone.getTimeZone("UTC"), 1970, Calendar.MAY, 29);
        long forties = Dates.date(TimeZone.getTimeZone("UTC"), 1940, Calendar.MAY, 29);

        testBeforeAfter(epic, forties);

    }

    @Test
    public void testLongUTCDate () {

        long epic = Dates.date( 1970, Calendar.MAY, 29, 5, 5 );
        long forties = Dates.date( 1940, Calendar.MAY, 29, 5, 5 );


        testBeforeAfter(epic, forties);

    }



    @Test
    public void testLongTZDate () {

        long epic = Dates.date( TimeZone.getTimeZone("UTC"), 1970, Calendar.MAY, 29, 5, 5 );
        long forties = Dates.date( 1940, Calendar.MAY, 29, 5, 5 );

        System.out.println("NEGATIVE DATE " + forties);

        testBeforeAfter(epic, forties);

    }


    @Test
    public void testLongDate () {

        long epic = Dates.date(1970, Calendar.MAY, 29, 5, 5);
        long forties = Dates.date(1940, Calendar.MAY, 29, 5, 5);

        testBeforeAfter(epic, forties);

    }


    @Test
    public void testAdvance () {

        //years
        long epic = Dates.utcNow();
        epic = Dates.yearsFrom(epic, -42);

        long forties = Dates.utcNow();
        forties = Dates.yearsFrom(forties, -80);


        testBeforeAfter(epic, forties);


        //months
        epic = Dates.utcNow();
        epic = Dates.monthsFrom(epic, -10);

        forties = Dates.utcNow();
        forties = Dates.monthsFrom(forties, -20);

        testBeforeAfter(epic, forties);




        //weeks
        epic = Dates.utcNow();
        epic = Dates.weeksFrom(epic, -10);

        forties = Dates.utcNow();
        forties = Dates.weeksFrom(forties, -20);

        testBeforeAfter(epic, forties);


        //days
        epic = Dates.utcNow();
        epic = Dates.daysFrom(epic, -10);

        forties = Dates.utcNow();
        forties = Dates.daysFrom(forties, -20);

        testBeforeAfter(epic, forties);

        //hours
        epic = Dates.utcNow();
        epic = Dates.hoursFrom(epic, -10);

        forties = Dates.utcNow();
        forties = Dates.hoursFrom(forties, -40);
        testBeforeAfter(epic, forties);

        //minutes
        epic = Dates.utcNow();
        epic = Dates.minutesFrom(epic, -20);

        forties = Dates.utcNow();
        forties = Dates.minutesFrom(forties, -40);

        testBeforeAfter(epic, forties);


        //seconds
        epic = Dates.utcNow();
        epic = Dates.secondsFrom(epic, -20);

        forties = Dates.utcNow();
        forties = Dates.secondsFrom(forties, -40);
        testBeforeAfter(epic, forties);

    }

    public void testBeforeAfter (long epic, long forties) {


        assertTrue(

               Dates.before( forties, epic )

        );


        assertTrue(

                Dates.after( epic, forties )

        );

        System.out.println( new Date( epic ) );
        System.out.println( new Date( forties ) );

    }


}
