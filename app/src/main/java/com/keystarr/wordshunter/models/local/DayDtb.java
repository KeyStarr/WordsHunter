package com.keystarr.wordshunter.models.local;

import android.support.annotation.NonNull;

import com.keystarr.wordshunter.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by Cyril on 11.07.2017.
 */

public class DayDtb implements Comparable<DayDtb> {

    private long date;
    private int weekInYear;
    private List<WordsCountersGroup> wordsGroupsList;
    private int wordsTypedCounter;

    private DayDtb(long date, int weekInYear,
                   List<WordsCountersGroup> wordsGroupsList, int wordsTypedCounter) {
        this.date = date;
        this.weekInYear = weekInYear;
        this.wordsGroupsList = wordsGroupsList;
        this.wordsTypedCounter = wordsTypedCounter;
    }

    public static DayDtb createBrandNew(long date, int weekInYear,
                                        List<WordsGroupToTrack> groupsToTrackList, int wordsTypedCounter) {
        DayDtb dayDtb = new DayDtb(date, weekInYear,
                new ArrayList<WordsCountersGroup>(), wordsTypedCounter);
        if (groupsToTrackList != null) {
            for (WordsGroupToTrack groupToTrack : groupsToTrackList) {
                dayDtb.getWordsCountersGroupsList().add(
                        WordsCountersGroup.create(groupToTrack));
            }
        }
        return dayDtb;
    }


    public static DayDtb createForRetrieve(long date, int weekInYear, int wordsTypedCounter) {
        DayDtb dayDtb = new DayDtb(date, weekInYear,
                new ArrayList<WordsCountersGroup>(), wordsTypedCounter);
        dayDtb.setWordsCounterGroupsList(new ArrayList<WordsCountersGroup>());
        return dayDtb;
    }

    public static DayDtb createForDebug(long date, int weekInYear,
                                        List<WordsGroupToTrack> groupsToTrackList) {
        Random rand = new Random();
        DayDtb dayDtb = new DayDtb(date, weekInYear,
                new ArrayList<WordsCountersGroup>(), rand.nextInt(1001) + 1000);
        if (groupsToTrackList != null) {
            for (WordsGroupToTrack groupToTrack : groupsToTrackList) {
                dayDtb.getWordsCountersGroupsList().add(
                        WordsCountersGroup.createForDebug(groupToTrack));
            }
        }
        return dayDtb;
    }

    public static DayDtb createForCurrentDate(List<WordsGroupToTrack> groupsToTrackList) {
        long date = DateUtils.getCurrentDayDateInMillis();
        return createBrandNew(date,
                Integer.valueOf(DateUtils.getDateFromLongToStringWeekInYear(date)),
                groupsToTrackList, 0);
    }

    public List<WordCounter> getAllWordCountersList() {
        List<WordCounter> wordCounterList = new ArrayList<>();
        for (WordsCountersGroup countersGroup : wordsGroupsList) {
            wordCounterList.addAll(countersGroup.getWordsCountersList());
        }
        return wordCounterList;
    }

    public int getDayTotalOccurrencesCount() {
        int count = 0;
        for (WordsCountersGroup groupCounters : wordsGroupsList) {
            for (WordCounter wordCounter : groupCounters.getWordsCountersList())
                count += wordCounter.getCount();
        }
        return count;
    }


    @Override
    public int compareTo(@NonNull DayDtb d2) {
        if (getDayTotalOccurrencesCount() > d2.getDayTotalOccurrencesCount()) {
            return 1;
        } else if (getDayTotalOccurrencesCount() < d2.getDayTotalOccurrencesCount())
            return -1;
        else
            return 0;
    }


    public void incrementWordsTypedCounter() {
        wordsTypedCounter++;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<WordsCountersGroup> getWordsCountersGroupsList() {
        return wordsGroupsList;
    }

    public void setWordsCounterGroupsList(List<WordsCountersGroup> wordsGroupsList) {
        this.wordsGroupsList = wordsGroupsList;
    }

    public int getWeekInYear() {
        return weekInYear;
    }

    public void setWeekInYear(int weekInYear) {
        this.weekInYear = weekInYear;
    }

    public int getWordsTypedCounter() {
        return wordsTypedCounter;
    }

    public void setWordsTypedCounter(int wordsTypedCounter) {
        this.wordsTypedCounter = wordsTypedCounter;
    }

}
