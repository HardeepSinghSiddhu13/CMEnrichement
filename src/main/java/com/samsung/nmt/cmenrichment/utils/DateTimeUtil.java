package com.samsung.nmt.cmenrichment.utils;

import java.util.Calendar;

import com.samsung.nmt.cmenrichment.constants.Constants;

public class DateTimeUtil {

    private DateTimeUtil() {
    }

    private static DateTimeUtil calendarUtil = new DateTimeUtil();

    public static DateTimeUtil getInstance() {
        return calendarUtil;
    }

    public Calendar currTime() {
        return Calendar.getInstance(Constants.TIME_ZONE);
    }
}
