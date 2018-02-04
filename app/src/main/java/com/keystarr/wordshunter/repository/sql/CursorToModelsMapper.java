package com.keystarr.wordshunter.repository.sql;

import android.database.Cursor;

import com.keystarr.wordshunter.models.local.DayDtb;
import com.keystarr.wordshunter.models.local.Limiter;
import com.keystarr.wordshunter.models.local.WordCounter;
import com.keystarr.wordshunter.models.local.WordToTrack;
import com.keystarr.wordshunter.models.local.WordsCountersGroup;
import com.keystarr.wordshunter.models.local.WordsGroupToTrack;
import com.keystarr.wordshunter.repository.sql.SQLiteDtbContract.DaysTable;
import com.keystarr.wordshunter.repository.sql.SQLiteDtbContract.LimiterNotificationsTable;
import com.keystarr.wordshunter.repository.sql.SQLiteDtbContract.WordCounterTable;
import com.keystarr.wordshunter.repository.sql.SQLiteDtbContract.WordsCountersGroupsTable;
import com.keystarr.wordshunter.repository.sql.SQLiteDtbContract.WordsToTrackGroupTable;
import com.keystarr.wordshunter.repository.sql.SQLiteDtbContract.WordsToTrackTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bizarre on 17.08.2017.
 */

public class CursorToModelsMapper {
    //Terribly developed class.
    //TODO: REFACTOR MAPPER METHODS AS TO BE MORE SHORT
    public CursorToModelsMapper() {
    }

    List<DayDtb> fromManyDays(Cursor cursor) {
        List<DayDtb> daysList = new ArrayList<>();
        if (cursor.moveToNext()) {
            int dateInd = cursor.getColumnIndex(DaysTable.COLUMN_NAME_DATE),
                    weekInYearInd = cursor.getColumnIndex(DaysTable.COLUMN_NAME_WEEK_IN_YEAR),
                    typedCounterInd = cursor.getColumnIndex(DaysTable.COLUMN_NAME_WORDS_TYPED_COUNTER),
                    groupNameInd = cursor.getColumnIndex(WordsCountersGroupsTable.COLUMN_NAME_NAME),
                    wordInd = cursor.getColumnIndex(WordCounterTable.COLUMN_NAME_WORD),
                    wordCountInd = cursor.getColumnIndex(WordCounterTable.COLUMN_NAME_COUNT),
                    isTrackedInd = cursor.getColumnIndex(WordCounterTable.COLUMN_NAME_IS_TRACKED);
            long previousDate = cursor.getLong(dateInd);
            int wordsTypedCounter = cursor.getInt(typedCounterInd);
            DayDtb tempDay = DayDtb.createForRetrieve(previousDate,
                    cursor.getInt(weekInYearInd), wordsTypedCounter);
            WordsCountersGroup tempGroup = WordsCountersGroup.create(cursor.getString(groupNameInd));
            tempGroup.getWordsCountersList().add(new WordCounter(
                    cursor.getString(wordInd), cursor.getInt(wordCountInd), cursor.getInt(isTrackedInd) == 1));
            while (cursor.moveToNext()) {
                long currentDate = cursor.getLong(dateInd);
                wordsTypedCounter = cursor.getInt(typedCounterInd);
                if (previousDate != currentDate) {
                    tempDay.getWordsCountersGroupsList().add(tempGroup);
                    daysList.add(tempDay);
                    previousDate = currentDate;
                    tempDay = DayDtb.createForRetrieve(previousDate,
                            cursor.getInt(weekInYearInd), wordsTypedCounter);
                    tempGroup = WordsCountersGroup.create(cursor.getString(groupNameInd));
                    tempGroup.getWordsCountersList().add(new WordCounter(
                            cursor.getString(wordInd), cursor.getInt(wordCountInd), cursor.getInt(isTrackedInd) == 1));
                } else {
                    if (tempGroup.getName().equals(cursor.getString(groupNameInd)))
                        tempGroup.getWordsCountersList().add(new WordCounter(
                                cursor.getString(wordInd), cursor.getInt(wordCountInd), cursor.getInt(isTrackedInd) == 1));
                    else {
                        tempDay.getWordsCountersGroupsList().add(tempGroup);
                        tempGroup = WordsCountersGroup.create(cursor.getString(groupNameInd));
                        tempGroup.getWordsCountersList().add(new WordCounter(
                                cursor.getString(wordInd), cursor.getInt(wordCountInd), cursor.getInt(isTrackedInd) == 1));
                    }
                }
            }
            tempDay.getWordsCountersGroupsList().add(tempGroup);
            daysList.add(tempDay);
        }
        return daysList;
    }

    DayDtb fromOneDay(Cursor cursor) {
        DayDtb day = null;
        if (cursor.moveToNext()) {
            int dateInd = cursor.getColumnIndex(DaysTable.COLUMN_NAME_DATE),
                    weekInYearInd = cursor.getColumnIndex(DaysTable.COLUMN_NAME_WEEK_IN_YEAR),
                    typedCounterInd = cursor.getColumnIndex(DaysTable.COLUMN_NAME_WORDS_TYPED_COUNTER),
                    groupIdInd = cursor.getColumnIndex(WordsCountersGroupsTable.COLUMN_NAME_ID),
                    groupNameInd = cursor.getColumnIndex(WordsCountersGroupsTable.COLUMN_NAME_NAME),
                    wordInd = cursor.getColumnIndex(WordCounterTable.COLUMN_NAME_WORD),
                    wordCountInd = cursor.getColumnIndex(WordCounterTable.COLUMN_NAME_COUNT),
                    isTrackedInd = cursor.getColumnIndex(WordCounterTable.COLUMN_NAME_IS_TRACKED);
            day = DayDtb.createForRetrieve(cursor.getLong(dateInd),
                    cursor.getInt(weekInYearInd),cursor.getInt(typedCounterInd));
            WordsCountersGroup tempGroup = WordsCountersGroup.create(
                    cursor.getLong(groupIdInd), cursor.getString(groupNameInd));
            tempGroup.getWordsCountersList().add(new WordCounter(
                    cursor.getLong(groupIdInd), cursor.getString(wordInd),
                    cursor.getInt(wordCountInd), cursor.getInt(isTrackedInd) == 1));
            while (cursor.moveToNext()) {
                if (tempGroup.getName().equals(cursor.getString(groupNameInd)))
                    tempGroup.getWordsCountersList().add(new WordCounter(
                            cursor.getLong(groupIdInd), cursor.getString(wordInd),
                            cursor.getInt(wordCountInd), cursor.getInt(isTrackedInd) == 1));
                else {
                    day.getWordsCountersGroupsList().add(tempGroup);
                    tempGroup = WordsCountersGroup.create(
                            cursor.getLong(groupIdInd), cursor.getString(groupNameInd));
                    tempGroup.getWordsCountersList().add(new WordCounter(
                            cursor.getLong(groupIdInd), cursor.getString(wordInd),
                            cursor.getInt(wordCountInd), cursor.getInt(isTrackedInd) == 1));
                }
            }
            day.getWordsCountersGroupsList().add(tempGroup);
        }
        return day;
    }

    DayDtb fromEmptyOneDay(Cursor cursor) {
        DayDtb day = null;
        if (cursor.moveToNext()) {
            int dateInd = cursor.getColumnIndex(DaysTable.COLUMN_NAME_DATE),
                    weekInYearInd = cursor.getColumnIndex(DaysTable.COLUMN_NAME_WEEK_IN_YEAR),
                    typedCounterInd = cursor.getColumnIndex(DaysTable.COLUMN_NAME_WORDS_TYPED_COUNTER);
            day = DayDtb.createForRetrieve(cursor.getLong(dateInd),
                    cursor.getInt(weekInYearInd), cursor.getInt(typedCounterInd));
        }
        return day;
    }

    List<WordsGroupToTrack> fromManyGroupsToTrack(Cursor cursor) {
        List<WordsGroupToTrack> groupsToTrackList = null;
        if (cursor.moveToNext()) {
            int groupNameInd = cursor.getColumnIndex(WordsToTrackGroupTable.COLUMN_NAME_NAME),
                    wordInd = cursor.getColumnIndex(WordsToTrackTable.COLUMN_NAME_WORD),
                    isTrackedInd = cursor.getColumnIndex(WordsToTrackTable.COLUMN_NAME_IS_TRACKED),
                    groupListPosInd = cursor.getColumnIndex(WordsToTrackGroupTable.COLUMN_NAME_LIST_POSITION),
                    wordListPosInd = cursor.getColumnIndex(WordsToTrackTable.COLUMN_NAME_LIST_POSITION);
            groupsToTrackList = new ArrayList<>();
            WordsGroupToTrack currentGroup =
                    new WordsGroupToTrack(cursor.getString(groupNameInd), cursor.getInt(groupListPosInd));
            currentGroup.getWordsToTrack().add(new WordToTrack(cursor.getString(wordInd),
                    cursor.getString(groupNameInd), cursor.getInt(isTrackedInd) == 1,
                    cursor.getInt(wordListPosInd)));
            while (cursor.moveToNext()) {
                String group = cursor.getString(groupNameInd);
                if (!currentGroup.getGroupName().equals(group)) {
                    groupsToTrackList.add(currentGroup);
                    currentGroup = new WordsGroupToTrack(group, cursor.getInt(groupListPosInd));
                    currentGroup.getWordsToTrack().add(
                            new WordToTrack(cursor.getString(wordInd),
                                    cursor.getString(groupNameInd), cursor.getInt(isTrackedInd) == 1,
                                    cursor.getInt(wordListPosInd)));
                } else
                    currentGroup.getWordsToTrack().add(new WordToTrack(cursor.getString(wordInd),
                            cursor.getString(groupNameInd), cursor.getInt(isTrackedInd) == 1,
                            cursor.getInt(wordListPosInd)));
            }
            groupsToTrackList.add(currentGroup);
        }
        return groupsToTrackList;
    }

    void fromManyEmptyGroupsToTrack(Cursor cursor,
                                    List<WordsGroupToTrack> normalGroups) {
        while (cursor.moveToNext()) {
            int groupNameInd = cursor.getColumnIndex(WordsToTrackGroupTable.COLUMN_NAME_NAME),
                    groupListPosInd = cursor.getColumnIndex(WordsToTrackGroupTable.COLUMN_NAME_LIST_POSITION);
            String groupName = cursor.getString(groupNameInd);
            boolean found = false;
            for (WordsGroupToTrack groupToTrack : normalGroups)
                if (groupToTrack.getGroupName().equals(groupName)) {
                    found = true;
                    break;
                }
            if (!found) {
                int listPosition = cursor.getInt(groupListPosInd);
                if (listPosition > normalGroups.size())
                    listPosition = normalGroups.size() - 1;
                if (listPosition < 0)
                    listPosition = 0;
                normalGroups.add(listPosition,
                        new WordsGroupToTrack(groupName, cursor.getInt(groupListPosInd)));
            }
        }
    }

    Integer fromLastDaysWordCounts(Cursor cursor) {
        int totalPeriodCount = 0;
        while (cursor.moveToNext()) {
            totalPeriodCount += cursor.getInt(0);
        }
        return totalPeriodCount;
    }

    Integer fromAmountOfLimitsReached(Cursor amountForWords, Cursor amountForGroups) {
        int totalAmountOfLimitsReached = 0;
        if (amountForWords.moveToNext()) {
            totalAmountOfLimitsReached += amountForWords.getInt(0);
        }
        if (amountForGroups.moveToNext()) {
            totalAmountOfLimitsReached += amountForGroups.getInt(0);
        }
        return totalAmountOfLimitsReached;
    }

    WordCounter fromMostUsedTrackedWord(Cursor cursor) {
        WordCounter mostUsedWord = null;
        int wordInd = cursor.getColumnIndex(WordCounterTable.COLUMN_NAME_WORD),
                countInd = cursor.getColumnIndex(WordCounterTable.COLUMN_NAME_COUNT);
        if (cursor.moveToNext()) {
            mostUsedWord = new WordCounter(
                    cursor.getString(wordInd), cursor.getInt(countInd), false);
        }
        return mostUsedWord;
    }

    Limiter fromOneLimiterNotification(Cursor cursor) {
        Limiter limiter = null;
        while (cursor.moveToNext()) {
            int wordInd = cursor.getColumnIndex(LimiterNotificationsTable.COLUMN_NAME_WORD),
                    limitInd = cursor.getColumnIndex(LimiterNotificationsTable.COLUMN_NAME_LIMIT),
                    forWord = cursor.getColumnIndex(LimiterNotificationsTable.COLUMN_NAME_FOR_WORD);
            limiter = new Limiter(cursor.getString(wordInd),
                    cursor.getInt(limitInd), cursor.getInt(forWord) == 1);
        }
        return limiter;
    }

    List<Limiter> fromManyLimitersNotifications(Cursor cursor) {
        List<Limiter> limitersList = new ArrayList<>();
        while (cursor.moveToNext()) {
            int wordInd = cursor.getColumnIndex(LimiterNotificationsTable.COLUMN_NAME_WORD),
                    limitInd = cursor.getColumnIndex(LimiterNotificationsTable.COLUMN_NAME_LIMIT),
                    forWord = cursor.getColumnIndex(LimiterNotificationsTable.COLUMN_NAME_FOR_WORD);
            limitersList.add(new Limiter(cursor.getString(wordInd),
                    cursor.getInt(limitInd), cursor.getInt(forWord) == 1));
        }
        return limitersList;
    }
}
