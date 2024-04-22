package com.creatorjohn.helpers.logging;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalField;
import java.util.logging.LogRecord;

public class Formatter extends java.util.logging.Formatter {

    private String formatNumber(int number) {
        return number < 10 ? "0" + number : "" + number;
    }

    @Override
    public String format(LogRecord record) {
        LocalDateTime dt = LocalDateTime.ofInstant(record.getInstant(), ZoneId.systemDefault());
        int year = dt.getYear();
        int month = dt.getMonthValue();
        int day = dt.getDayOfMonth();
        int hour = dt.getHour();
        int minute = dt.getMinute();
        String classname = record.getLoggerName();
        String method = record.getSourceMethodName();
        String message = record.getMessage();

        return "[" + record.getLevel() + "] " + formatNumber(day) + "/" + formatNumber(month) + "/" + formatNumber(year)
                + " " + formatNumber(hour) + ":" + formatNumber(minute) + " >> " + classname + "." + method + "\n"
                + message + "\n";
    }
}
