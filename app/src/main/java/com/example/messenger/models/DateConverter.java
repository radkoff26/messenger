package com.example.messenger.models;

import android.annotation.SuppressLint;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.Calendar.*;

public class DateConverter {

    public static final String DATE_PATTERN = "dd.MM.yyyy H:mm:ss";

    public static String now() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN, new Locale("ru"));
        return sdf.format(date);
    }

    public static Date getTime(String n) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        return sdf.parse(n);
    }

    @SuppressLint("DefaultLocale")
    public static String lastOnline(String time) throws ParseException {
        // It receives parameter time that is formatted with DATE_PATTERN
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC+3"));
        Date date = sdf.parse(time);
        long difference = System.currentTimeMillis() - date.getTime();
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(difference);
        boolean sameYear = calendar.get(YEAR) - 1970 == 0;
        boolean sameMonth = calendar.get(MONTH) == JANUARY;
        boolean sameDay = calendar.get(DAY_OF_MONTH) - 1 == 0;
        boolean sameHour = calendar.get(HOUR_OF_DAY) == 0;
        boolean sameMinute = calendar.get(MINUTE) == 0;
        if (sameYear) {
            if (sameMonth) {
                if (sameDay) {
                    if (sameHour) {
                        if (sameMinute) {
                            return "last seen just now";
                        }
                        return String.format("last seen %d %s ago",
                                calendar.get(MINUTE),
                                calendar.get(MINUTE) == 1 ? "minute" : "minutes");
                    }
                    return String.format("last seen %d %s ago",
                            calendar.get(HOUR_OF_DAY),
                            calendar.get(HOUR_OF_DAY) == 1 ? "hour" : "hours");
                }
                return String.format("last seen %d %s ago",
                        calendar.get(DAY_OF_MONTH) - 1,
                        calendar.get(DAY_OF_MONTH) == 2 ? "day" : "days");
            }
            return String.format("last seen %d %s ago",
                    calendar.get(MONTH),
                    calendar.get(MONTH) == FEBRUARY ? "month" : "months");
        }
        return String.format("last seen %d %s ago",
                calendar.get(YEAR) - 1970,
                calendar.get(YEAR) - 1970 == 1 ? "year" : "years");
    }
}
