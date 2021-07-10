package com.example.messenger.models;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateConverter {

    public static final String DATE_PATTERN = "dd.MM.yyyy k:mm";

    public static String now() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        return sdf.format(date);
    }
}
