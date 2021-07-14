package com.example.messenger.models;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateConverter {

    public static final String DATE_PATTERN = "dd.MM.yyyy H:mm:ss";

    public static String now() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN, new Locale("ru"));
        return sdf.format(date);
    }

    public static Date getTime(String n) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC+3"));
        return sdf.parse(n);
    }
}
