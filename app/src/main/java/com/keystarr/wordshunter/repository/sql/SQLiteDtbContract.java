package com.keystarr.wordshunter.repository.sql;

import android.provider.BaseColumns;

/**
 * Created by Cyril on 15.08.2017.
 */

public final class SQLiteDtbContract {

    private SQLiteDtbContract() {
    }

    public static class DaysTable implements BaseColumns {
        public static final String TABLE_NAME = "days";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_WEEK_IN_YEAR = "weekInYear";
        public static final String COLUMN_NAME_SENT = "sent";
        public static final String COLUMN_NAME_WORDS_TYPED_COUNTER = "wordsTypedCounter";

        public static final String SQL_CREATE_DAY_DTB_TABLE =
                "CREATE TABLE " + DaysTable.TABLE_NAME + " (" +
                        COLUMN_NAME_DATE + " INTEGER UNIQUE NOT NULL," +
                        COLUMN_NAME_WEEK_IN_YEAR + " INTEGER," +
                        COLUMN_NAME_WORDS_TYPED_COUNTER + " INTEGER," +
                        COLUMN_NAME_SENT + " INTEGER)";
    }

    public static class WordsCountersGroupsTable implements BaseColumns {
        public static final String TABLE_NAME = "wordsCountersGroups";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DAY_DATE = "dayDate";

        public static final String SQL_CREATE_WORDS_COUNTERS_GROUP_TABLE =
                "CREATE TABLE " + WordsCountersGroupsTable.TABLE_NAME + " (" +
                        COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_NAME + " TEXT NOT NULL," +
                        COLUMN_NAME_DAY_DATE + " INTEGER NOT NULL," +
                        "CONSTRAINT unq UNIQUE (" + COLUMN_NAME_NAME + "," + COLUMN_NAME_DAY_DATE + ")," +
                        "FOREIGN KEY(" + COLUMN_NAME_DAY_DATE + ") REFERENCES " + DaysTable.TABLE_NAME +
                        "(" + DaysTable.COLUMN_NAME_DATE + ")" + " ON DELETE CASCADE)";
    }

    public static class WordCounterTable implements BaseColumns {
        public static final String TABLE_NAME = "wordsCounters";
        public static final String COLUMN_NAME_GROUP_ID = "groupId";
        public static final String COLUMN_NAME_WORD = "word";
        public static final String COLUMN_NAME_COUNT = "count";
        public static final String COLUMN_NAME_IS_TRACKED = "isTracked";

        public static final String SQL_CREATE_WORD_COUNTER_TABLE =
                "CREATE TABLE " + WordCounterTable.TABLE_NAME + " (" +
                        COLUMN_NAME_GROUP_ID + " INTEGER," +
                        COLUMN_NAME_WORD + " TEXT NOT NULL," +
                        COLUMN_NAME_COUNT + " INTEGER," +
                        COLUMN_NAME_IS_TRACKED + " INTEGER," +
                        "CONSTRAINT unq UNIQUE (" + COLUMN_NAME_GROUP_ID + "," + COLUMN_NAME_WORD + ")," +
                        "FOREIGN KEY(" + COLUMN_NAME_GROUP_ID + ") REFERENCES "
                        + WordsCountersGroupsTable.TABLE_NAME +
                        "(" + WordsCountersGroupsTable.COLUMN_NAME_ID + ")" + " ON DELETE CASCADE)";

    }

    public static class WordsToTrackGroupTable implements BaseColumns {
        public static final String TABLE_NAME = "wordsToTrackGroup";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LIST_POSITION = "groupListPos";

        public static final String SQL_CREATE_WORDS_TO_TRACK_GROUP_TABLE =
                "CREATE TABLE " + WordsToTrackGroupTable.TABLE_NAME + " (" +
                        COLUMN_NAME_NAME + " TEXT UNIQUE NOT NULL," +
                        COLUMN_NAME_LIST_POSITION + " INTEGER NOT NULL)";
    }

    public static class WordsToTrackTable implements BaseColumns {
        public static final String TABLE_NAME = "wordsToTrack";
        public static final String COLUMN_NAME_GROUP_NAME = "groupName";
        public static final String COLUMN_NAME_WORD = "word";
        public static final String COLUMN_NAME_IS_TRACKED = "isTracked";
        public static final String COLUMN_NAME_LIST_POSITION = "wordListPos";

        public static final String SQL_CREATE_WORD_TO_TRACK_TABLE =
                "CREATE TABLE " + WordsToTrackTable.TABLE_NAME + " (" +
                        COLUMN_NAME_WORD + " TEXT UNIQUE NOT NULL," +
                        COLUMN_NAME_GROUP_NAME + " TEXT NOT NULL," +
                        COLUMN_NAME_IS_TRACKED + " INTEGER NOT NULL," +
                        COLUMN_NAME_LIST_POSITION + " INTEGER NOT NULL, " +
                        "FOREIGN KEY(" + COLUMN_NAME_GROUP_NAME + ") REFERENCES " +
                        WordsToTrackGroupTable.TABLE_NAME +
                        "(" + WordsToTrackGroupTable.COLUMN_NAME_NAME + ")" + " ON DELETE CASCADE)";
    }

    public static class LimiterNotificationsTable implements BaseColumns {
        public static final String TABLE_NAME = "limiters";
        public static final String COLUMN_NAME_WORD = "word";
        public static final String COLUMN_NAME_LIMIT = "limitCount";
        public static final String COLUMN_NAME_FOR_WORD = "forWord";

        public static final String SQL_CREATE_LIMITERS_TABLE =
                "CREATE TABLE " + LimiterNotificationsTable.TABLE_NAME + " (" +
                        COLUMN_NAME_WORD + " TEXT NOT NULL, " +
                        COLUMN_NAME_LIMIT + " INTEGER NOT NULL, " +
                        COLUMN_NAME_FOR_WORD + " INTEGER NOT NULL, " +
                        "CONSTRAINT uniqueLimiter UNIQUE (" + COLUMN_NAME_FOR_WORD + ", " + COLUMN_NAME_WORD + "))";
    }
}
