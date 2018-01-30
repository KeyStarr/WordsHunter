package com.keystarr.wordshunter.repository.sql;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

/**
 * Created by Cyril on 15.08.2017.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final String DTB_NAME = "ParasiteOffDtb.db";
    private static final int DTB_VERSION = 2;

    public SQLiteHelper(Context context) {
        super(context, DTB_NAME, null, DTB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLiteDtbContract.DaysTable.SQL_CREATE_DAY_DTB_TABLE);
        db.execSQL(SQLiteDtbContract.WordsCountersGroupsTable.SQL_CREATE_WORDS_COUNTERS_GROUP_TABLE);
        db.execSQL(SQLiteDtbContract.WordCounterTable.SQL_CREATE_WORD_COUNTER_TABLE);
        db.execSQL(SQLiteDtbContract.WordsToTrackGroupTable.SQL_CREATE_WORDS_TO_TRACK_GROUP_TABLE);
        db.execSQL(SQLiteDtbContract.WordsToTrackTable.SQL_CREATE_WORD_TO_TRACK_TABLE);
        db.execSQL(SQLiteDtbContract.LimiterNotificationsTable.SQL_CREATE_LIMITERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 2) {
            db.execSQL("ALTER TABLE days ADD COLUMN wordsTypedCounter INTEGER DEFAULT 0");
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }
}
