package com.keystarr.wordshunter.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.ui.MainActivity;
import com.keystarr.wordshunter.ui.set_up.SetUpActivity;
import com.keystarr.wordshunter.utils.AccessibilityUtils;

import static android.support.v4.app.NotificationCompat.DEFAULT_LIGHTS;

/**
 * Created by Cyril on 28.08.2017.
 */

public class TextAnalyzerServiceCheckBroadcastReceiver extends BroadcastReceiver {

    public static final int NOTIFICATION_ID_SERVICE_DISABLED = 341;
    public static final String INTENT_IS_OPEN_SETTINGS = "intent_is_open_settings";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!AccessibilityUtils.isAccessibilityServiceEnabled(context))
            notifyServiceDisabled(context);
    }

    private void notifyServiceDisabled(Context context) {
        Intent enableIntent = new Intent(context, SetUpActivity.class);
        PendingIntent enablePendingIntent = PendingIntent.getActivity(
                context, 0, enableIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //TODO: SET NORMAL ICONS
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_footprints_large_light_gray)
                .setContentTitle(context.getString(R.string.notification_service_disabled_title))
                .setContentText(context.getString(R.string.notification_service_disabled_text))
                .setContentIntent(enablePendingIntent)
                .setDefaults(DEFAULT_LIGHTS)
                .setAutoCancel(true);
        Intent stopSendingIntent = new Intent(context, MainActivity.class);
        stopSendingIntent.putExtra(INTENT_IS_OPEN_SETTINGS, true);
        PendingIntent stopSendingPendingIntent = PendingIntent.getActivity(
                context, 1, stopSendingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Action actionEnable = new NotificationCompat.Action(
                R.drawable.ic_confirm_dark_gray,
                context.getString(R.string.notification_service_disabled_action_enable),
                enablePendingIntent);
        builder.addAction(new NotificationCompat.Action.Builder(actionEnable).build());
        NotificationCompat.Action actionStop = new NotificationCompat.Action(
                R.drawable.ic_delete_small_dark_gray,
                context.getString(R.string.notification_service_disabled_action_stop_notifying),
                stopSendingPendingIntent);
        builder.addAction(new NotificationCompat.Action.Builder(actionStop).build());
        builder.setPriority(NotificationCompat.PRIORITY_MAX);
        builder.setWhen(0);
        NotificationManagerCompat ntfMng = NotificationManagerCompat.from(context);
        ntfMng.notify(NOTIFICATION_ID_SERVICE_DISABLED, builder.build());
    }
}
