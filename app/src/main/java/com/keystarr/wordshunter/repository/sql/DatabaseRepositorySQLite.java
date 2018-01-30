package com.keystarr.wordshunter.repository.sql;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.keystarr.wordshunter.models.local.DayDtb;
import com.keystarr.wordshunter.models.local.Limiter;
import com.keystarr.wordshunter.models.local.WordCounter;
import com.keystarr.wordshunter.models.local.WordToTrack;
import com.keystarr.wordshunter.models.local.WordsCountersGroup;
import com.keystarr.wordshunter.models.local.WordsGroupToTrack;
import com.keystarr.wordshunter.repository.DatabaseRepository;
import com.keystarr.wordshunter.repository.sql.SQLiteDtbContract.DaysTable;
import com.keystarr.wordshunter.repository.sql.SQLiteDtbContract.LimiterNotificationsTable;
import com.keystarr.wordshunter.repository.sql.SQLiteDtbContract.WordCounterTable;
import com.keystarr.wordshunter.repository.sql.SQLiteDtbContract.WordsCountersGroupsTable;
import com.keystarr.wordshunter.repository.sql.SQLiteDtbContract.WordsToTrackGroupTable;
import com.keystarr.wordshunter.repository.sql.SQLiteDtbContract.WordsToTrackTable;
import com.keystarr.wordshunter.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Cyril on 15.08.2017.
 */

public class DatabaseRepositorySQLite implements DatabaseRepository {
    //It was decided to leave all of the queries with
    //the hardcoded tables's names and columns due to the
    //readability issues that would arise if i was to replace
    //those with the actual references to contract's variables

    private static final String QUERY_SELECT_CURRENT_DAY =
            "SELECT * " +
                    "FROM days,wordsCountersGroups,wordsCounters " +
                    "WHERE days.date = ?" +
                    "AND wordsCountersGroups.dayDate=days.date " +
                    "AND wordsCounters.groupId=wordsCountersGroups._id " +
                    "ORDER BY wordsCountersGroups._id ";

    private static final String QUERY_SELECT_WEEK_OF_DAYS_ASC =
            "SELECT days.date,days.wordsTypedCounter,days.weekInYear,days.sent," +
                    "wordsCountersGroups.name,wordsCounters.word,wordsCounters.count, wordsCounters.isTracked " +
                    "FROM days,wordsCountersGroups,wordsCounters " +
                    "WHERE days.weekInYear = ?" +
                    "AND wordsCountersGroups.dayDate=days.date " +
                    "AND wordsCounters.groupId=wordsCountersGroups._id " +
                    "ORDER BY days.date, wordsCountersGroups._id";

    private static final String QUERY_SELECT_ALL_DAYS_ASC =
            "SELECT days.date,days.wordsTypedCounter,days.weekInYear, days.sent," +
                    " wordsCountersGroups.name,wordsCounters.word,wordsCounters.count, wordsCounters.isTracked " +
                    "FROM days,wordsCountersGroups,wordsCounters " +
                    "WHERE %1$s wordsCountersGroups.dayDate=days.date " +
                    "AND wordsCounters.groupId=wordsCountersGroups._id " +
                    "ORDER BY days.date, wordsCountersGroups._id %2$s";

    private static final String QUERY_SELECT_ALL_WORDS_TO_TRACK_GROUPS =
            "SELECT wordsToTrackGroup.name, wordsToTrackGroup.groupListPos, wordsToTrack.word, wordsToTrack.isTracked, wordsToTrack.wordListPos " +
                    "FROM wordsToTrack, wordsToTrackGroup " +
                    "WHERE wordsToTrack.groupName=wordsToTrackGroup.name%1$s " +
                    "ORDER BY%2$s";

    private static final String QUERY_SELECT_TOTAL_DAYS_COUNTS_DESC =
            "SELECT sum(wordsCounters.count) " +
                    "FROM days, wordsCountersGroups, wordsCounters " +
                    "WHERE days.date = wordsCountersGroups.dayDate AND wordsCountersGroups._id = wordsCounters.groupId " +
                    "GROUP BY days.date ORDER BY days.date DESC LIMIT %1$d";

    private static final String QUERY_SELECT_TOTAL_DAY_COUNT =
            "SELECT sum(wordsCounters.count) " +
                    "FROM days, wordsCountersGroups, wordsCounters " +
                    "WHERE days.date = ? " +
                    "AND wordsCountersGroups.dayDate = days.date " +
                    "AND wordsCountersGroups._id = wordsCounters.groupId ";

    private static final String QUERY_COUNT_WORDS_LIMITS_REACHED_FOR_DAY =
            "SELECT count(*) AS \"count\"" +
                    "FROM limiters, wordsCounters, wordsCountersGroups " +
                    "WHERE wordsCountersGroups.dayDate = ? " +
                    "AND wordsCounters.groupId = wordsCountersGroups._id " +
                    "AND wordsCounters.word = limiters.word " +
                    "AND wordsCounters.count > limiters.limitCount";

    private static final String QUERY_COUNT_GROUPS_LIMITS_REACHED_FOR_DAY =
            "SELECT count(*) AS \"count\" " +
                    "FROM limiters, wordsCountersGroups " +
                    "WHERE wordsCountersGroups.dayDate = ? " +
                    "AND wordsCountersGroups.name = limiters.word " +
                    "AND (" +
                    "SELECT count(wordsCounters.count) " +
                    "FROM wordsCounters, wordsCountersGroups " +
                    "WHERE wordsCounters.groupId = wordsCountersGroups._id" +
                    ") > limiters.limitCount";

    private static final String QUERY_MOST_USED_TRACKED_WORD =
            "SELECT wordsCounters.word, MAX(wordsCounters.count) AS \"count\" " +
                    "FROM wordsCounters, wordsCountersGroups " +
                    "WHERE wordsCountersGroups.dayDate = ? " +
                    "AND wordsCounters.groupId = wordsCountersGroups._id";

    private SQLiteDatabase database;
    private CursorToModelsMapper toModelsMapper;
    private ModelsToContentValuesMapper toContentValuesMapper;

    public DatabaseRepositorySQLite(Context context) {
        SQLiteHelper helper = new SQLiteHelper(context);
        database = helper.getWritableDatabase();
        toModelsMapper = new CursorToModelsMapper();
        toContentValuesMapper = new ModelsToContentValuesMapper();
    }

    @Override
    public List<DayDtb> getDaysSortByDateAscending() {
        String query = String.format(QUERY_SELECT_ALL_DAYS_ASC, "", "");
        Cursor cursor = database
                .rawQuery(query, new String[]{});
        List<DayDtb> daysList =
                toModelsMapper.fromManyDays(cursor);
        cursor.close();
        return daysList;
    }

    @Override
    public List<DayDtb> getAllUnsentDays() {
        long currentDate = DateUtils.getCurrentDayDateInMillis();
        String query = String.format(QUERY_SELECT_ALL_DAYS_ASC,
                "days.sent = 0 AND days.date != " + currentDate + " AND",
                "");
        Cursor cursor = database.rawQuery(query, new String[]{});
        List<DayDtb> unsentDaysList =
                toModelsMapper.fromManyDays(cursor);
        cursor.close();
        return unsentDaysList;
    }

    @Override
    public List<DayDtb> getWeek(int weekInYear) {
        Cursor cursor = database.rawQuery(
                QUERY_SELECT_WEEK_OF_DAYS_ASC,
                new String[]{String.valueOf(weekInYear)});
        List<DayDtb> daysList =
                toModelsMapper.fromManyDays(cursor);
        cursor.close();
        return daysList;
    }

    @Override
    public DayDtb getCurrentDay() {
        long date = DateUtils.getCurrentDayDateInMillis();
        Cursor cursor = database.rawQuery(
                QUERY_SELECT_CURRENT_DAY,
                new String[]{String.valueOf(date)});
        DayDtb day = toModelsMapper.fromOneDay(cursor);
        cursor.close();
        if (day == null)
            day = getEmptyDay(date);
        return day;
    }

    @Override
    public boolean isThereAtLeastOneDay() {
        String rawQuery = String.format(QUERY_SELECT_ALL_DAYS_ASC, "", "LIMIT 1");
        Cursor cursor = database.rawQuery(rawQuery, new String[]{});
        boolean result = cursor.moveToNext();
        cursor.close();
        return result;
    }

    @Override
    public Limiter getLimiterForName(String name, boolean forWord) {
        Cursor cursor = database.query(
                LimiterNotificationsTable.TABLE_NAME,
                new String[]{LimiterNotificationsTable.COLUMN_NAME_WORD,
                        LimiterNotificationsTable.COLUMN_NAME_LIMIT,
                        LimiterNotificationsTable.COLUMN_NAME_FOR_WORD},
                LimiterNotificationsTable.COLUMN_NAME_FOR_WORD + " = ? AND "
                        + LimiterNotificationsTable.COLUMN_NAME_WORD + " = ?",
                new String[]{String.valueOf(forWord ? 1 : 0), name},
                null, null, null);
        Limiter limiter = toModelsMapper.fromOneLimiterNotification(cursor);
        cursor.close();
        return limiter;
    }

    @Override
    public List<Limiter> getAllLimitersNotifications() {
        Cursor cursor = database.query(
                LimiterNotificationsTable.TABLE_NAME,
                new String[]{LimiterNotificationsTable.COLUMN_NAME_WORD,
                        LimiterNotificationsTable.COLUMN_NAME_LIMIT,
                        LimiterNotificationsTable.COLUMN_NAME_FOR_WORD},
                "", new String[]{}, null, null, null);
        List<Limiter> limitersList =
                toModelsMapper.fromManyLimitersNotifications(cursor);
        cursor.close();
        return limitersList;
    }

    @Override
    public int getTotalWordsCountForLastDays(int daysCount) {
        String rawQuery = String.format(Locale.US, QUERY_SELECT_TOTAL_DAYS_COUNTS_DESC, daysCount);
        Cursor cursor = database.rawQuery(rawQuery, new String[]{});//////////top line not needed
        int totalCount = toModelsMapper.fromLastDaysWordCounts(cursor);
        cursor.close();
        return totalCount;
    }

    @Override
    public int getTotalDayWordsCount(long dayDateInMillis) {
        Cursor cursor = database.rawQuery(QUERY_SELECT_TOTAL_DAY_COUNT,
                new String[]{String.valueOf(dayDateInMillis)});
        int totalCount = toModelsMapper.fromLastDaysWordCounts(cursor);
        cursor.close();
        return totalCount;
    }

    @Override
    public WordCounter getYesterdayMostUsedTrackedWord() {
        String todayMillis = String.valueOf(DateUtils.getYesterdayDayDateInMillis());
        Cursor cursor = database.rawQuery(QUERY_MOST_USED_TRACKED_WORD, new String[]{todayMillis});
        WordCounter mostUsedWord = toModelsMapper.fromMostUsedTrackedWord(cursor);
        cursor.close();
        return mostUsedWord;
    }

    @Override
    public int getAmountOfYesterdayReachedLimits() {
        String todayMillis = String.valueOf(DateUtils.getYesterdayDayDateInMillis());
        Cursor amountForWords = database.rawQuery(QUERY_COUNT_WORDS_LIMITS_REACHED_FOR_DAY, new String[]{todayMillis});
        Cursor amountForGroups = database.rawQuery(QUERY_COUNT_GROUPS_LIMITS_REACHED_FOR_DAY, new String[]{todayMillis});
        int amountOfLimitsReached = toModelsMapper.fromAmountOfLimitsReached(amountForWords, amountForGroups);
        amountForWords.close();
        amountForGroups.close();
        return amountOfLimitsReached;
    }


    private DayDtb getEmptyDay(long date) {
        Cursor cursor = database.query(
                DaysTable.TABLE_NAME,
                new String[]{DaysTable.COLUMN_NAME_DATE, DaysTable.COLUMN_NAME_WEEK_IN_YEAR, DaysTable.COLUMN_NAME_SENT},
                DaysTable.COLUMN_NAME_DATE + " = ?",
                new String[]{String.valueOf(date)},
                null, null, null);
        DayDtb day = toModelsMapper.fromEmptyOneDay(cursor);
        cursor.close();
        return day;
    }

    @Override
    public void insert(DayDtb dayDtb) {
        database.insert(DaysTable.TABLE_NAME, null,
                toContentValuesMapper.from(dayDtb));
        for (WordsCountersGroup group : dayDtb.getWordsCountersGroupsList()) {
            insertAndUpdateID(dayDtb.getDate(), group);
        }
    }

    @Override
    public void insertAndUpdateID(long date, WordsCountersGroup group) {
        long _id = database.insert(WordsCountersGroupsTable.TABLE_NAME, null,
                toContentValuesMapper.fromWithoutID(date, group));
        group.set_id(_id);
        for (WordCounter counter : group.getWordsCountersList())
            insertAndUpdateID(_id, counter);
    }

    @Override
    public void insertAndUpdateID(long groupID, WordCounter counter) {
        counter.setGroupID(groupID);
        database.insert(WordCounterTable.TABLE_NAME, null,
                toContentValuesMapper.from(counter));
    }

    @Override
    public void insertAndUpdateIDOnlyGroup(long date, WordsCountersGroup group) {
        long _id = database.insert(WordsCountersGroupsTable.TABLE_NAME, null,
                toContentValuesMapper.fromWithoutID(date, group));
        group.set_id(_id);
    }

    @Override
    public void update(DayDtb dayDtb) {
        updateDayDtb(dayDtb);
        for (WordsCountersGroup group : dayDtb.getWordsCountersGroupsList()) {
            updateWordsCountersGroup(dayDtb.getDate(), group);
        }
    }

    @Override
    public void updateDays(List<DayDtb> daysList) {
        for (DayDtb dayDtb : daysList)
            update(dayDtb);
    }

    @Override
    public void update(List<WordToTrack> wordsToTrack) {
        for (WordToTrack wordToTrack : wordsToTrack)
            update(wordToTrack);
    }

    private void updateDayDtb(DayDtb dayDtb) {
        database.update(
                DaysTable.TABLE_NAME,
                toContentValuesMapper.from(dayDtb),
                DaysTable.COLUMN_NAME_DATE + " = ?",
                new String[]{String.valueOf(dayDtb.getDate())});
    }

    private void updateWordsCountersGroup(long date, WordsCountersGroup group) {
        database.update(
                WordsCountersGroupsTable.TABLE_NAME,
                toContentValuesMapper.from(date, group),
                WordsCountersGroupsTable.COLUMN_NAME_ID + " = ?",
                new String[]{String.valueOf(group.get_id())});
        for (WordCounter counter : group.getWordsCountersList()) {
            counter.setGroupID(group.get_id());
            update(counter);
        }
    }

    @Override
    public void update(WordCounter counter, long oldGroupId) {
        database.update(
                WordCounterTable.TABLE_NAME,
                toContentValuesMapper.from(counter),
                WordCounterTable.COLUMN_NAME_GROUP_ID + " = ? " +
                        "AND " + WordCounterTable.COLUMN_NAME_WORD + " = ?",
                new String[]{String.valueOf(oldGroupId),
                        String.valueOf(counter.getWord())});
    }

    @Override
    public void update(WordCounter counter) {
        update(counter, counter.getGroupID());
    }

    @Override
    public void update(WordToTrack wordToTrack) {
        database.update(
                WordsToTrackTable.TABLE_NAME,
                toContentValuesMapper.from(wordToTrack),
                WordsToTrackTable.COLUMN_NAME_WORD + " = ?",
                new String[]{wordToTrack.getWord()});
    }

    @Override
    public void update(WordsGroupToTrack groupToTrack) {
        database.update(
                WordsToTrackGroupTable.TABLE_NAME,
                toContentValuesMapper.from(groupToTrack),
                WordsToTrackGroupTable.COLUMN_NAME_NAME + " = ?",
                new String[]{groupToTrack.getGroupName()});
    }

    @Override
    public void update(Limiter limiter) {
        database.update(
                LimiterNotificationsTable.TABLE_NAME,
                toContentValuesMapper.from(limiter),
                LimiterNotificationsTable.COLUMN_NAME_WORD + " = ?",
                new String[]{limiter.getLimitedWord()});
    }

    @Override
    public void updateGroupsToTrack(List<WordsGroupToTrack> groupsToTracks) {
        for (WordsGroupToTrack groupToTrack : groupsToTracks)
            update(groupToTrack);
    }

    @Override
    public void insert(List<WordsGroupToTrack> list) {
        for (WordsGroupToTrack wordsGroupToTrack : list)
            insert(wordsGroupToTrack);
    }

    @Override
    public void insert(Limiter limiter) {
        database.insert(
                LimiterNotificationsTable.TABLE_NAME,
                null,
                toContentValuesMapper.from(limiter));
    }

    @Override
    public void insertLimiters(List<Limiter> limitersList) {
        for (Limiter limiter : limitersList)
            insert(limiter);
    }

    @Override
    public void insert(WordsGroupToTrack group) {
        database.insert(
                WordsToTrackGroupTable.TABLE_NAME,
                null,
                toContentValuesMapper.from(group));
        for (WordToTrack wordToTrack : group.getWordsToTrack())
            insert(wordToTrack);
    }

    @Override
    public void insert(WordToTrack wordToTrack) {
        database.insert(
                WordsToTrackTable.TABLE_NAME, null,
                toContentValuesMapper.from(wordToTrack));
    }

    @Override
    public List<WordsGroupToTrack> getWordsGroupsToTrack(boolean fetchNotTrackedWords,
                                                         boolean fetchEmptyGroups,
                                                         boolean orderByListPos) {
        String query = String.format(QUERY_SELECT_ALL_WORDS_TO_TRACK_GROUPS,
                (fetchNotTrackedWords) ? "" : " AND wordsToTrack.isTracked=1 ",
                orderByListPos ? " wordsToTrackGroup." + WordsToTrackGroupTable.COLUMN_NAME_LIST_POSITION
                        + ", wordsToTrack.wordListPos"
                        : " wordsToTrackGroup.name");
        Cursor cursor = database.rawQuery(query, null);
        List<WordsGroupToTrack> groupToTracks =
                toModelsMapper.fromManyGroupsToTrack(cursor);
        cursor.close();
        if (fetchEmptyGroups) {
            if (groupToTracks == null)
                groupToTracks = new ArrayList<>();
            addEmptyGroupsToTrackList(groupToTracks);
        }
        return groupToTracks;
    }

    public void addEmptyGroupsToTrackList(List<WordsGroupToTrack> normalGroups) {
        Cursor cursor =
                database.query(WordsToTrackGroupTable.TABLE_NAME,
                        new String[]{WordsToTrackGroupTable.COLUMN_NAME_NAME, WordsToTrackGroupTable.COLUMN_NAME_LIST_POSITION},
                        null, null, null, null, WordsToTrackGroupTable.COLUMN_NAME_LIST_POSITION + " ASC");
        toModelsMapper.fromManyEmptyGroupsToTrack(cursor, normalGroups);
        cursor.close();
    }

    @Override
    public void delete(WordsGroupToTrack groupToTrack) {
        database.delete(
                WordsToTrackGroupTable.TABLE_NAME,
                WordsToTrackGroupTable.COLUMN_NAME_NAME + " = ?",
                new String[]{groupToTrack.getGroupName()});
    }

    @Override
    public void delete(WordToTrack wordToTrack) {
        database.delete(
                WordsToTrackTable.TABLE_NAME,
                WordsToTrackTable.COLUMN_NAME_WORD + " = ?",
                new String[]{wordToTrack.getWord()});
    }

    @Override
    public void delete(WordsCountersGroup countersGroup) {
        database.delete(
                WordsCountersGroupsTable.TABLE_NAME,
                WordsCountersGroupsTable.COLUMN_NAME_ID + " = ?",
                new String[]{String.valueOf(countersGroup.get_id())}
        );
        for (WordCounter counter : countersGroup.getWordsCountersList())
            delete(counter);
    }

    @Override
    public void delete(WordCounter wordCounter) {
        database.delete(
                WordCounterTable.TABLE_NAME,
                WordCounterTable.COLUMN_NAME_WORD + " = ?"
                        + " AND " + WordCounterTable.COLUMN_NAME_GROUP_ID + " = ?",
                new String[]{wordCounter.getWord(), String.valueOf(wordCounter.getGroupID())}
        );
    }

    @Override
    public void delete(Limiter limiter) {
        database.delete(
                LimiterNotificationsTable.TABLE_NAME,
                LimiterNotificationsTable.COLUMN_NAME_WORD + " = ?",
                new String[]{limiter.getLimitedWord()});
    }

    @Override
    public void deleteLimiterByName(String name) {
        delete(new Limiter(name, -1, false));
    }
}
