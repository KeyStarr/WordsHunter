package com.keystarr.wordshunter.ui.stats;

import com.github.mikephil.charting.data.Entry;
import com.keystarr.wordshunter.models.local.DayDtb;
import com.keystarr.wordshunter.models.local.StatsRecyclerEntry;
import com.keystarr.wordshunter.models.local.WordCounter;
import com.keystarr.wordshunter.models.local.WordsCountersGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.keystarr.wordshunter.ui.stats.StatsChartListFragment.MODE_BY_DAYS;
import static com.keystarr.wordshunter.ui.stats.StatsChartListFragment.MODE_BY_GROUPS;
import static com.keystarr.wordshunter.ui.stats.StatsChartListFragment.MODE_BY_WEEKS;
import static com.keystarr.wordshunter.ui.stats.StatsChartListFragment.MODE_BY_WORDS;

/**
 * Created by Cyril on 13.08.2017.
 */

class ChartListEntriesFactory {

    List<StatsRecyclerEntry> createListEntries(DayDtb day,
                                               int statsDisplayMode) {
        List<StatsRecyclerEntry> entries = null;
        if (statsDisplayMode == MODE_BY_WORDS) {
            entries = createListEntriesForDaysByWords(day);
        } else if (statsDisplayMode == MODE_BY_GROUPS) {
            entries = createListEntriesForDaysByGroups(day);
        }
        sortAndAssignNumberToListEntries(entries);
        assignRelativeCountToSortedListEntries(entries);
        return entries;
    }

    private List<StatsRecyclerEntry> createListEntriesForDaysByWords(DayDtb day) {
        List<StatsRecyclerEntry> entries = new ArrayList<>();
        for (WordsCountersGroup group : day.getWordsCountersGroupsList())
            for (WordCounter counter : group.getWordsCountersList()) {
                entries.add(new StatsRecyclerEntry(counter.getWord(),
                        counter.getCount()));
            }
        return entries;
    }

    private List<StatsRecyclerEntry> createListEntriesForDaysByGroups(DayDtb day) {
        List<StatsRecyclerEntry> entries = new ArrayList<>();
        for (WordsCountersGroup group : day.getWordsCountersGroupsList()) {
            entries.add(new StatsRecyclerEntry(group.getName(),
                    group.getOverallGroupCounter()));
        }
        return entries;
    }

    List<StatsRecyclerEntry> createListEntries(List<DayDtb> daysWeekList,
                                               int statsDisplayMode) {
        List<StatsRecyclerEntry> entries = null;
        if (statsDisplayMode == MODE_BY_WORDS)
            entries = createListEntriesForWeekByWords(daysWeekList);
        else if (statsDisplayMode == MODE_BY_GROUPS)
            entries = createListEntriesForWeekByGroups(daysWeekList);
        sortAndAssignNumberToListEntries(entries);
        assignRelativeCountToSortedListEntries(entries);
        return entries;
    }

    private List<StatsRecyclerEntry> createListEntriesForWeekByWords(List<DayDtb> daysWeekList) {
        List<StatsRecyclerEntry> entries = new ArrayList<>();
        for (DayDtb day : daysWeekList)
            for (WordsCountersGroup group : day.getWordsCountersGroupsList())
                for (WordCounter counter : group.getWordsCountersList()) {
                    int index = entries.indexOf(
                            new StatsRecyclerEntry(counter.getWord(), 0));
                    if (index != -1)
                        entries.get(index).increaseCount(counter.getCount());
                    else
                        entries.add(new StatsRecyclerEntry(counter.getWord(),
                                counter.getCount()));
                }
        return entries;
    }

    private List<StatsRecyclerEntry> createListEntriesForWeekByGroups(List<DayDtb> daysWeekList) {
        List<StatsRecyclerEntry> entries = new ArrayList<>();
        for (DayDtb day : daysWeekList)
            for (WordsCountersGroup group : day.getWordsCountersGroupsList()) {
                int index = entries.indexOf(
                        new StatsRecyclerEntry(group.getName(), 0));
                if (index != -1)
                    entries.get(index).increaseCount(group.getOverallGroupCounter());
                else
                    entries.add(new StatsRecyclerEntry(group.getName(),
                            group.getOverallGroupCounter()));
            }
        return entries;
    }

    private void sortAndAssignNumberToListEntries(List<StatsRecyclerEntry> entries) {
        Collections.sort(entries);
        for (int i = 0; i < entries.size(); i++)
            entries.get(i).setNumber(i + 1);
    }

    private void assignRelativeCountToSortedListEntries(List<StatsRecyclerEntry> entries) {
        int maxCount = entries.get(0).getCount();
        for (StatsRecyclerEntry entry : entries)
            if (maxCount != 0)
                entry.setRelativeCount(((float) entry.getCount()) / maxCount * 100);
            else
                entry.setRelativeCount(0);
    }

    List<Entry> createChartEntries(List<DayDtb> daysList,
                                   int statsRangeMode) {
        if (statsRangeMode == MODE_BY_DAYS)
            return createChartEntriesByDays(daysList);
        else if (statsRangeMode == MODE_BY_WEEKS)
            return createChartEntriesByWeeks(daysList);
        return null;
    }

    private List<Entry> createChartEntriesByWeeks(List<DayDtb> daysList) {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 0));
        int count = 0, currentWeek = daysList.get(0).getWeekInYear();
        for (int i = 0; i < daysList.size(); i++) {
            int week = daysList.get(i).getWeekInYear();
            if (currentWeek != week) {
                int place = entries.size();
                entries.add(place, new Entry(place, count));
                count = 0;
                currentWeek = week;
            }
            count += daysList.get(i).getDayTotalOccurrencesCount();
        }
        entries.add(entries.size(), new Entry(entries.size(), count));
        entries.add(entries.size(), new Entry(entries.size(), 0));
        return entries;
    }

    private List<Entry> createChartEntriesByDays(List<DayDtb> daysList) {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 0));
        int i = 1;
        for (DayDtb dayDtb : daysList) {
            entries.add(i, new Entry(i, dayDtb.getDayTotalOccurrencesCount(), dayDtb));
            i++;
        }
        entries.add(i, new Entry(i, 0));
        return entries;
    }
}
