package nz.co.enhance.HelperClasses;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class DateHelper {

    //Pass in a date format as a string and optionally pass in days you want to add or subtract (negative) from today's date
    public static String calculateDate(String df) {
        DateFormat dateFormat = new SimpleDateFormat(df);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String calculateDate(String df, int diff) {
        DateFormat dateFormat = new SimpleDateFormat(df);
        Date date = new Date();
        date = addDays(date, diff);
        return dateFormat.format(date);
    }

    public static String calculateDateAndTimeWithMinutes(String df, int diff) {
        DateFormat dateFormat = new SimpleDateFormat(df);
        Date date = new Date();
        date = addMinutes(date, diff);
        return dateFormat.format(date);
    }

    public static String calculateDateAndTimeWithHours(String df, int diff) {
        DateFormat dateFormat = new SimpleDateFormat(df);
        Date date = new Date();
        date = addHours(date, diff);
        return dateFormat.format(date);
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static Date addMinutes(Date date, int minutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minutes); //minus number would decrement the minutes
        return cal.getTime();
    }

    public static Date addHours(Date date, int hours) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, hours); //minus number would decrement the hours
        return cal.getTime();
    }

    public static String reformatDate(String dateToParse, String inputDF, String outputDF) {
        DateFormat inputDateFormat = new SimpleDateFormat(inputDF);
        Date date = null;
        try {
            date = inputDateFormat.parse(dateToParse);
        } catch (Exception e) {
            System.out.println("Could not parse date");
            return null;
        }
        DateFormat dateFormat = new SimpleDateFormat(outputDF);
        return dateFormat.format(date);
    }

    public static Date parseDate(String dateToParse, String dateFormat) {
        DateFormat inputDateFormat = new SimpleDateFormat(dateFormat);
        Date date = null;
        try {
            date = inputDateFormat.parse(dateToParse);
        } catch (Exception e) {
            System.out.println("Could not parse date");
            return null;
        }
        return date;
    }

    public static Date getCurrentDate() {
        return new Date();
    }

    public static long getCurrentEpoch() {
        return System.currentTimeMillis() / 1000;
    }

    public static String convertEpochToDateTime(long time) {
        Date date = new Date(time * 1000);
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }

    public static String calculateDateTimeWithTimezone(String df, TimeZone timezone) {
        DateFormat dateFormat = new SimpleDateFormat(df);
        dateFormat.setTimeZone(timezone);
        Calendar cal = Calendar.getInstance();
        Date date = new Date();
        cal.setTime(date);
        cal.setTimeZone(timezone);
        date = cal.getTime();
        return dateFormat.format(date);
    }

    //Convert time formatted as "x:x:x" or "x:x" in seconds
    public static int calculateTimeIntoSeconds(String time){
        String[] times = time.split(":");
        int seconds = 0;
        if (times.length == 3) {
            seconds = Integer.parseInt(times[2]) + (60 * Integer.parseInt(times[1])) + (3600 * Integer.parseInt(times[0]));
        } else if (times.length == 2) {
            seconds = Integer.parseInt(times[1]) + (60 * Integer.parseInt(times[0]));
        }
        return seconds;
    }

}