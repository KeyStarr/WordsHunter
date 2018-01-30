package com.keystarr.wordshunter.models.local;

import com.keystarr.wordshunter.utils.DateUtils;

/**
 * Created by Cyril on 12.08.2017.
 */

public class Week {
    private int weekInYear;
    private String formattedMonday;
    private int occurrencesCount;

    private Week(int weekInYear, int occurrencesCount, String formattedMonday) {
        this.weekInYear = weekInYear;
        this.occurrencesCount = occurrencesCount;
        this.formattedMonday = formattedMonday;
    }

    public static Week create(int weekInYear, int occurrencesCount) {
        return new Week(weekInYear, occurrencesCount,
                DateUtils.getFormattedMondayOfWeekInDayMonth(weekInYear));
    }

    public int getWeekInYear() {
        return weekInYear;
    }

    public void setWeekInYear(int weekInYear) {
        this.weekInYear = weekInYear;
    }

    public String getFormattedMonday() {
        return formattedMonday;
    }

    public int getOccurrencesCount() {
        return occurrencesCount;
    }

    public void setOccurrencesCount(int occurrencesCount) {
        this.occurrencesCount = occurrencesCount;
    }

    public void setFormattedMonday(String formattedMonday) {
        this.formattedMonday = formattedMonday;
    }
}
