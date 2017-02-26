package com.lwonho92.everchat.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.gun0912.tedpicker.Config;
import com.gun0912.tedpicker.ImagePickerActivity;
import com.lwonho92.everchat.R;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
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

    public static Uri convertSourceMessageToUri(String sourceMessage, String sourceLanguage, String targetLanguage) {

        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority("translate.google.com")
                .appendPath("m")
                .appendEncodedPath("translate#" + sourceLanguage)
                .appendPath(targetLanguage)
                .appendPath(sourceMessage);

        return builder.build();
    }
}
