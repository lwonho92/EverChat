package com.lwonho92.everchat.data;

import java.util.Calendar;

/**
 * Created by GIGAMOLE on 8/18/16.
 */
public class Utils {
    /*public static int getGmtMinutesOffset() {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        int mGMTOffset = mTimeZone.getRawOffset();

        return (int)(TimeUnit.MINUTES.convert(mGMTOffset, TimeUnit.MILLISECONDS));
    }*/

    public static String getMillisToStr(Long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
//        cal.add(Calendar.MINUTE, getGmtMinutesOffset());

        String ampm = "";
        switch(cal.get(Calendar.AM_PM)) {
            case Calendar.AM:
                ampm = "am";
                break;
            case Calendar.PM:
                ampm = "pm";
                break;
        }
        return String.format("%s %d : %02d", ampm, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }
}
