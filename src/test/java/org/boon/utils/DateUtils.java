package org.boon.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {

	public static String getGMTString(Date date) {

        /*
        * To SL: Now I know what you mean work everywhere. :)
        * We format our dates differently here.
        * DAY/MONTH/YEAR although logical and done everywhere. :)
        * --RMH
        */
        DateFormat df =  SimpleDateFormat.getDateTimeInstance( DateFormat.SHORT,
                DateFormat.SHORT, Locale.FRANCE );
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(date);
	}
}
