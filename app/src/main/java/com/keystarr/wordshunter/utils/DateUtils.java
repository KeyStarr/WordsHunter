package com.keystarr.wordshunter.utils;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.temporal.ChronoUnit;
import org.threeten.bp.temporal.TemporalField;
import org.threeten.bp.temporal.WeekFields;

import java.util.Locale;

/**
 * Created by Cyril on 05.07.2017.
 */

public final class DateUtils {

    private DateUtils() {
    }

    public static long getCurrentDayDateInMillis() {
        Instant instant = Instant.now();
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
        return zonedDateTime.toInstant().toEpochMilli();
    }

    public static long getTomorrowDayDateInMillis() {
        Instant instant = Instant.now();
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        zonedDateTime = zonedDateTime.plusDays(1);
        zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
        return zonedDateTime.toInstant().toEpochMilli();
    }

    public static long getYesterdayDayDateInMillis() {
        Instant instant = Instant.now();
        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
        zonedDateTime = zonedDateTime.minusDays(1);
        zonedDateTime = zonedDateTime.truncatedTo(ChronoUnit.DAYS);
        return zonedDateTime.toInstant().toEpochMilli();
    }

    private static String getDateFromLongToStringWithPattern(String pattern, long date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern)
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault());
        return formatter.format(Instant.ofEpochMilli(date));
    }

    public static String getDateFromLongToStringInDayMonthYear(long date) {
        //TODO: maybe change pattern for local
        return getDateFromLongToStringWithPattern("dd.MM.YY", date);
    }

    public static String getDateFromLongToStringInDayMonth(long date) {
        return getDateFromLongToStringWithPattern("d MMM", date);
    }

    public static String getDateFromLongToStringInDayMonthYearWords(long date) {
        return getDateFromLongToStringWithPattern("d MMM YYY", date);
    }

    public static String getFormattedMondayOfWeekInDayMonth(long date) {
        LocalDate now = Instant
                .ofEpochMilli(date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        TemporalField fieldISO = WeekFields.of(Locale.FRANCE).dayOfWeek();
        return now.with(fieldISO, 1).format(DateTimeFormatter.ofPattern("d MMM"));
    }

    public static String getDateFromLongToStringWeekInYear(long date) {
        return getDateFromLongToStringWithPattern("w", date);
    }

    public static String getFormattedMondayOfWeekInDayMonth(int weekInYear) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        LocalDate ldt = LocalDate.now()
                .withYear(LocalDate.now().getYear())
                .with(weekFields.weekOfYear(), weekInYear)
                .with(weekFields.dayOfWeek(), 1);
        return ldt.format(DateTimeFormatter.ofPattern("d MMM"));
    }
}
