package com.keystarr.wordshunter.ui.home;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.receivers.NotifyWithDailyReportBroadcastReceiver;
import com.keystarr.wordshunter.receivers.TextAnalyzerServiceCheckBroadcastReceiver;
import com.keystarr.wordshunter.utils.BroadcastReceiversRegisteringUtils;

import static com.keystarr.wordshunter.repository.PreferencesRepository.APP_PREERENCES_IS_CHECK_SERVICE_DISABLED;
import static com.keystarr.wordshunter.repository.PreferencesRepository.APP_PREERENCES_IS_SEND_DAILY_REPORTS;
import static com.keystarr.wordshunter.utils.BroadcastReceiversRegisteringUtils.BROADCAST_REQUEST_CODE_DAILY_REPORT;
import static com.keystarr.wordshunter.utils.BroadcastReceiversRegisteringUtils.BROADCAST_REQUEST_CODE_IS_SERVICE_DISABLED;

/**
 * A simple {@link Fragment} subclass.
 */
public class PreferencesFragment extends PreferenceFragmentCompat
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        AlarmManager alarmMgr = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent;
        PendingIntent penIntent;
        switch (key) {
            case APP_PREERENCES_IS_SEND_DAILY_REPORTS:
                if (sharedPreferences.getBoolean(key, true)) {
                    BroadcastReceiversRegisteringUtils.registerAlarmMngForDailyReportNotifications(getContext());
                } else {
                    intent = new Intent((getContext().getApplicationContext()),
                            NotifyWithDailyReportBroadcastReceiver.class);
                    penIntent = PendingIntent.getBroadcast(
                            getContext(), BROADCAST_REQUEST_CODE_DAILY_REPORT, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmMgr.cancel(penIntent);
                }
                break;
            case APP_PREERENCES_IS_CHECK_SERVICE_DISABLED:
                if (sharedPreferences.getBoolean(key, true)) {
                    BroadcastReceiversRegisteringUtils.registerAlarmMngForServiceDisabledCheck(getContext());
                } else {
                    intent = new Intent((getContext().getApplicationContext()),
                            TextAnalyzerServiceCheckBroadcastReceiver.class);
                    penIntent = PendingIntent.getBroadcast(
                            getContext(), BROADCAST_REQUEST_CODE_IS_SERVICE_DISABLED, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmMgr.cancel(penIntent);
                }
                break;
        }
    }
}
