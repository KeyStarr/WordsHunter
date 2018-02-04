package com.keystarr.wordshunter.repository.sql;

import android.content.ContentValues;

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

/**
 * Created by Cyril on 16.08.2017.
 */

class ModelsToContentValuesMapper {
    //TODO: MAKE forUpdateFrom and forInsertFrom

    public ModelsToContentValuesMapper() {
    }

    ContentValues from(DayDtb dayDtb) {
        ContentValues values = new ContentValues();
        values.put(DaysTable.COLUMN_NAME_DATE, dayDtb.getDate());
        values.put(DaysTable.COLUMN_NAME_WORDS_TYPED_COUNTER, dayDtb.getWordsTypedCounter());
        values.put(DaysTable.COLUMN_NAME_WEEK_IN_YEAR, dayDtb.getWeekInYear());
        return values;
    }

    ContentValues from(long date, WordsCountersGroup group) {
        ContentValues values = new ContentValues();
        values.put(WordsCountersGroupsTable.COLUMN_NAME_ID, group.get_id());
        values.put(WordsCountersGroupsTable.COLUMN_NAME_DAY_DATE, date);
        values.put(WordsCountersGroupsTable.COLUMN_NAME_NAME, group.getName());
        return values;
    }

    ContentValues fromWithoutID(long date,
                                WordsCountersGroup group) {
        //id means row id so if group is for inserting it doesn't have it
        //thus, this method is needed to not insert group at index 0
        //as 0 is default long's field class value
        //and let database set group where it sees group fits
        ContentValues values = new ContentValues();
        values.put(WordsCountersGroupsTable.COLUMN_NAME_DAY_DATE, date);
        values.put(WordsCountersGroupsTable.COLUMN_NAME_NAME, group.getName());
        return values;
    }

    ContentValues from(WordCounter wordCounter) {
        ContentValues values = new ContentValues();
        values.put(WordCounterTable.COLUMN_NAME_WORD, wordCounter.getWord());
        values.put(WordCounterTable.COLUMN_NAME_COUNT, wordCounter.getCount());
        values.put(WordCounterTable.COLUMN_NAME_GROUP_ID, wordCounter.getGroupID());
        values.put(WordCounterTable.COLUMN_NAME_IS_TRACKED, wordCounter.isTracked() ? 1 : 0);
        return values;
    }

    ContentValues from(WordsGroupToTrack group) {
        ContentValues values = new ContentValues();
        values.put(WordsToTrackGroupTable.COLUMN_NAME_NAME, group.getGroupName());
        values.put(WordsToTrackGroupTable.COLUMN_NAME_LIST_POSITION, group.getRecyclerPosition());
        return values;
    }

    ContentValues from(WordToTrack wordToTrack) {
        ContentValues values = new ContentValues();
        values.put(WordsToTrackTable.COLUMN_NAME_GROUP_NAME, wordToTrack.getGroupName());
        values.put(WordsToTrackTable.COLUMN_NAME_WORD, wordToTrack.getWord());
        values.put(WordsToTrackTable.COLUMN_NAME_IS_TRACKED, wordToTrack.isTracked() ? 1 : 0);
        values.put(WordsToTrackTable.COLUMN_NAME_LIST_POSITION, wordToTrack.getRecyclerPosition());
        return values;
    }

    ContentValues from(Limiter limiter) {
        ContentValues values = new ContentValues();
        values.put(LimiterNotificationsTable.COLUMN_NAME_WORD, limiter.getLimitedWord());
        values.put(LimiterNotificationsTable.COLUMN_NAME_LIMIT, limiter.getLimit());
        values.put(LimiterNotificationsTable.COLUMN_NAME_FOR_WORD, limiter.isForWord() ? 1 : 0);
        return values;
    }
}
