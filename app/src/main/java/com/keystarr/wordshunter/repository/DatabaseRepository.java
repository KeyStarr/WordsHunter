package com.keystarr.wordshunter.repository;

import com.keystarr.wordshunter.models.local.DayDtb;
import com.keystarr.wordshunter.models.local.Limiter;
import com.keystarr.wordshunter.models.local.WordCounter;
import com.keystarr.wordshunter.models.local.WordToTrack;
import com.keystarr.wordshunter.models.local.WordsCountersGroup;
import com.keystarr.wordshunter.models.local.WordsGroupToTrack;

import java.util.List;

/**
 * Created by Cyril on 02.08.2017.
 */

public interface DatabaseRepository {

    List<WordsGroupToTrack> getWordsGroupsToTrack(boolean fetchNoTrackedWords, boolean fetchEmptyGroups,
                                                  boolean orderByListPos);

    List<DayDtb> getDaysSortByDateAscending();

    List<DayDtb> getWeek(int weekInYear);

    DayDtb getCurrentDay();

    boolean isThereAtLeastOneDay();

    Limiter getLimiterForName(String name, boolean forWord);

    List<Limiter> getAllLimitersNotifications();

    int getTotalWordsCountForLastDays(int daysCount);

    int getTotalDayWordsCount(long dayDateInMillis);

    WordCounter getYesterdayMostUsedTrackedWord();

    int getAmountOfYesterdayReachedLimits();

    void insert(DayDtb dayDtb);

    void insert(WordToTrack wordToTrack);

    void insert(WordsGroupToTrack group);

    void insert(List<WordsGroupToTrack> list);

    void insert(Limiter limiter);

    void insertLimiters(List<Limiter> limitersList);

    void insertAndUpdateID(long date, WordsCountersGroup group);

    void insertAndUpdateIDOnlyGroup(long date, WordsCountersGroup group);

    void update(WordCounter counter, long oldGroupId);

    void insertAndUpdateID(long groupID, WordCounter counter);

    void update(DayDtb dayDtb);

    void updateDays(List<DayDtb> daysList);

    void update(List<WordToTrack> wordsToTrack);

    void update(WordToTrack wordToTrack);

    void update(WordsGroupToTrack groupToTrack);

    void update(Limiter limiter);

    void updateGroupsToTrack(List<WordsGroupToTrack> groupsToTracks);

    void delete(WordsGroupToTrack groupToTrack);

    void delete(WordToTrack wordToTrack);

    void delete(WordsCountersGroup countersGroup);

    void delete(WordCounter wordCounter);

    void delete(Limiter limiter);

    void deleteLimiterByName(String name);

    void update(WordCounter wordCounter);
}
