package com.keystarr.wordshunter.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.keystarr.wordshunter.receivers.NotifyWithDailyReportBroadcastReceiver;
import com.keystarr.wordshunter.receivers.TextAnalyzerServiceCheckBroadcastReceiver;

import org.threeten.bp.Instant;

/**
 * Created by Cyril on 11.10.2017.
 */

public final class BroadcastReceiversRegisteringUtils {

    public static final int BROADCAST_REQUEST_CODE_IS_SERVICE_DISABLED = 142;
    public static final int BROADCAST_REQUEST_CODE_DAILY_REPORT = 143;

    private BroadcastReceiversRegisteringUtils() {
    }

    public static void registerAlarmMngForServiceDisabledCheck(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent((context.getApplicationContext()), TextAnalyzerServiceCheckBroadcastReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                context, BROADCAST_REQUEST_CODE_IS_SERVICE_DISABLED, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, Instant.now().toEpochMilli(),
                AlarmManager.INTERVAL_HALF_DAY, alarmIntent);
    }

    public static void registerAlarmMngForDailyReportNotifications(Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent((context.getApplicationContext()), NotifyWithDailyReportBroadcastReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                context, BROADCAST_REQUEST_CODE_DAILY_REPORT, intent, 0);
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, DateUtils.getTomorrowDayDateInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }
}
