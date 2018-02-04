package com.keystarr.wordshunter.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by Cyril on 03.07.2017.
 */

public class PreferencesRepository {

    private static final String APP_PREFERENCES_KEY_SERVICE_IS_FIRST_LAUNCH = "key_service_first_launch";
    private static final String APP_PREFERENCES_OPEN_YESTERDAY_STATS = "open_stats";
    private static final String APP_PREFERENCES_WORDS_SCREEN_FIRST_LAUNCH = "prefs_words_screen_first_launch";
    public static final String APP_PREERENCES_IS_SEND_DAILY_REPORTS = "prefs_is_daily_reports";
    public static final String APP_PREERENCES_IS_CHECK_SERVICE_DISABLED = "prefs_is_alarm_service_disabled";

    private SharedPreferences preferences;

    public PreferencesRepository(Context ctx) {
        preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public void setKeyServiceIsFirstLaunch(boolean isFirst) {
        preferences.edit().putBoolean(APP_PREFERENCES_KEY_SERVICE_IS_FIRST_LAUNCH, isFirst).apply();
    }

    public void setWordsScreenFirstLaunch(boolean isOpen) {
        preferences.edit().putBoolean(APP_PREFERENCES_WORDS_SCREEN_FIRST_LAUNCH, isOpen).apply();
    }

    public boolean isKeyServiceFirstLaunch() {
        return preferences.getBoolean(APP_PREFERENCES_KEY_SERVICE_IS_FIRST_LAUNCH, true);
    }

    public void setOpenYesterdayStats(boolean open) {
        preferences.edit().putBoolean(APP_PREFERENCES_OPEN_YESTERDAY_STATS, open).apply();
    }

    public boolean isOpenYesterdayStats() {
        return preferences.getBoolean(APP_PREFERENCES_OPEN_YESTERDAY_STATS, false);
    }

    public boolean isSendDailyReports() {
        return preferences.getBoolean(APP_PREERENCES_IS_SEND_DAILY_REPORTS, true);
    }

    public boolean isCheckServiceDisabled() {
        return preferences.getBoolean(APP_PREERENCES_IS_CHECK_SERVICE_DISABLED, true);
    }

    public boolean isWordsScreenFirstLaunch() {
        return preferences.getBoolean(APP_PREFERENCES_WORDS_SCREEN_FIRST_LAUNCH, true);
    }

    public void clearPrefs() {
        preferences.edit().clear().apply();
    }
}
