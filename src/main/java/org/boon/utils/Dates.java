package org.boon.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Dates {

        private static TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");


        public static long utcNow() {
            return Calendar.getInstance(UTC_TIME_ZONE).getTime().getTime();
        }

        public static long utcToTimeZone (long utc, TimeZone timeZone) {

            Calendar calendar = Calendar.getInstance(UTC_TIME_ZONE);
            calendar.setTime(new Date(utc));
            calendar.setTimeZone(timeZone);
            return  calendar.getTime().getTime();
        }

        public static boolean before (long isThis, long beforeThis) {
            return isThis < beforeThis;
        }


        public static boolean after (long isThis, long afterThis) {
            return isThis > afterThis;
        }



}
