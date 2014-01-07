package org.boon.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

	public static String getGMTString(Date date) {
        DateFormat df = new SimpleDateFormat();
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(date);
	}
}
