package com.keystarr.wordshunter.utils;

import com.keystarr.wordshunter.models.local.DayDtb;
import com.keystarr.wordshunter.models.local.Week;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cyril on 16.08.2017.
 */

public final class WeekUtils {

    private WeekUtils() {
    }

    public static List<Week> fillWeekList(List<DayDtb> daysList) {
        List<Week> weeksList = new ArrayList<>();
        int count = 0, currentWeek = daysList.get(0).getWeekInYear();
        for (int i = 0; i < daysList.size(); i++) {
            int week = daysList.get(i).getWeekInYear();
            if (currentWeek != week) {
                weeksList.add(Week.create(currentWeek, count));
                count = 0;
                currentWeek = week;
            }
            count += daysList.get(i).getDayTotalOccurrencesCount();
        }
        weeksList.add(Week.create(currentWeek, count));
        return weeksList;
    }
}
