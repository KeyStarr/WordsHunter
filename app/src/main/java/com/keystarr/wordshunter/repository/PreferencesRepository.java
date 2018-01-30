package com.keystarr.wordshunter.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


/**
 * Created by Cyril on 03.07.2017.
 */

public class PreferencesRepository {

    private static final String APP_PREFERENCES_KEY_SERVICE_IS_FIRST_LAUNCH = "key_service_first_launch";
    private static final String APP_PREFERENCES_USER_ID = "user_id";
    public static final String APP_PREFERENCES_USER_AGE = "user_age";
    public static final String APP_PREFERENCES_USER_GENDER = "user_gender";
    private static final String APP_PREFERENCES_OPEN_YESTERDAY_STATS = "open_stats";
    private static final String APP_PREFERENCES_WORDS_SCREEN_FIRST_LAUNCH = "prefs_words_screen_first_launch";
    public static final String APP_PREERENCES_IS_SEND_DAILY_REPORTS = "prefs_is_daily_reports";
    public static final String APP_PREFERENCES_LAST_TIME_SEND_DAYS_CALLED = "prefs_send_days_tweaked_last";
    public static final String APP_PREERENCES_IS_CHECK_SERVICE_DISABLED = "prefs_is_alarm_service_disabled";
    public static final String APP_PREFERENCES_IS_SEND_DAYS_RECEIVER_CALL_FREQUENCY_HIGH = "prefs_is_send_frequency_call_high";
    public static final String APP_PREFERENCES_IS_PERSONAL_DATA_GIVEN = "prefs_personal_data_given";

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

    public void setUserId(long id) {
        preferences.edit().putLong(APP_PREFERENCES_USER_ID, id).apply();
    }

    public void setUserAge(int age) {
        preferences.edit().putInt(APP_PREFERENCES_USER_AGE, age).apply();
    }

    public void setUserGender(boolean male) {
        preferences.edit().putBoolean(APP_PREFERENCES_USER_GENDER, male).apply();
    }


    public void setOpenYesterdayStats(boolean open) {
        preferences.edit().putBoolean(APP_PREFERENCES_OPEN_YESTERDAY_STATS, open).apply();
    }

    public void setPersonalDataGiven(boolean given) {
        preferences.edit().putBoolean(APP_PREFERENCES_IS_PERSONAL_DATA_GIVEN, given).apply();
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

    public boolean isPersonalDataGiven() {
        return preferences.getBoolean(APP_PREFERENCES_IS_PERSONAL_DATA_GIVEN, false);
    }

    public int getUserAge() {
        return preferences.getInt(APP_PREFERENCES_USER_AGE, 0);
    }

    public boolean getUserGender() {
        return preferences.getBoolean(APP_PREFERENCES_USER_GENDER, false);
    }

    public long getSendDaysReceiverCalledLast() {
        return preferences.getLong(APP_PREFERENCES_LAST_TIME_SEND_DAYS_CALLED, -1);
    }

    public long getUserId() {
        return preferences.getLong(APP_PREFERENCES_USER_ID, -1);
    }

    public boolean isWordsScreenFirstLaunch() {
        return preferences.getBoolean(APP_PREFERENCES_WORDS_SCREEN_FIRST_LAUNCH, true);
    }

    public void clearPrefs() {
        preferences.edit().clear().apply();
    }

    public void setSendDaysReceiverHighCallFrequency(boolean high) {
        preferences.edit()
                .putBoolean(APP_PREFERENCES_IS_SEND_DAYS_RECEIVER_CALL_FREQUENCY_HIGH, high).apply();
    }

    public boolean isSendDaysReceiverCallFrequencyHigh() {
        return preferences.getBoolean(
                APP_PREFERENCES_IS_SEND_DAYS_RECEIVER_CALL_FREQUENCY_HIGH, false);
    }

    public void setSendDaysReceiverCalledLast(long date) {
        preferences.edit().putLong(APP_PREFERENCES_LAST_TIME_SEND_DAYS_CALLED, date).apply();
    }
}
