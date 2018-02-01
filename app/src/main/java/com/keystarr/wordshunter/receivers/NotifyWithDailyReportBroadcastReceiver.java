package com.keystarr.wordshunter.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.app.App;
import com.keystarr.wordshunter.models.local.WordCounter;
import com.keystarr.wordshunter.repository.DatabaseRepository;
import com.keystarr.wordshunter.repository.PreferencesRepository;
import com.keystarr.wordshunter.ui.MainActivity;
import com.keystarr.wordshunter.utils.DateUtils;

import javax.inject.Inject;

import static android.support.v4.app.NotificationCompat.DEFAULT_LIGHTS;

/**
 * Created by Cyril on 08.10.2017.
 */

public class NotifyWithDailyReportBroadcastReceiver extends BroadcastReceiver {

    public static int NOTIFICATION_ID_DAILY_REPORT = 413;
    public static final String INTENT_IS_OPEN_YESTERDAY_STATS = "intent_open_yesterday_stats";

    @Inject
    DatabaseRepository dtbRepo;
    @Inject
    PreferencesRepository prefsRepo;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((App) context.getApplicationContext()).getAppComponent().inject(this);
        sendDailyReport(context);
    }

    private void sendDailyReport(Context context) {
        String title = context.getString(R.string.daily_report_title_notification);
        SpannedString description = getDescription(context);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(INTENT_IS_OPEN_YESTERDAY_STATS, true);
        PendingIntent openReportPendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_footprints_large_light_gray)
                .setContentTitle(title)
                .setContentText(description)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                .setContentIntent(openReportPendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(DEFAULT_LIGHTS)
                .setWhen(0);
        NotificationManagerCompat ntfMng = NotificationManagerCompat.from(context);
        ntfMng.notify(NOTIFICATION_ID_DAILY_REPORT, builder.build());
    }

    private SpannedString getDescription(Context context) {
        long yesterday = DateUtils.getYesterdayDayDateInMillis();
        String wordCount = String.valueOf(dtbRepo.getTotalDayWordsCount(yesterday));
        String limitsReached = String.valueOf(dtbRepo.getAmountOfYesterdayReachedLimits());
        WordCounter maxCountWord = dtbRepo.getYesterdayMostUsedTrackedWord();

        SpannedString description;
        if (maxCountWord == null || maxCountWord.getCount() == 0) {
            description = new SpannedString(context.getString(R.string.nothing_caught));
        } else {
            Spannable descWordsCaught = new SpannableString(
                    context.getString(R.string.words_caught, wordCount));
            //makePartOfSpannableWhite(descWordsCaught, wordCount);
            Spannable descLimitsReached = new SpannableString(
                    context.getString(R.string.limits_reached, limitsReached));
            //makePartOfSpannableWhite(descLimitsReached, limitsReached);
            Spannable descMostUsedWord = new SpannableString(
                    context.getString(R.string.most_used_word, maxCountWord.getWord())
                            + context.getString(R.string.time, maxCountWord.getCount()) + ".");
            //makePartOfSpannableWhite(descMostUsedWord, maxCountWord.getWord());
            //makePartOfSpannableWhite(descMostUsedWord, String.valueOf(maxCountWord.getCount()));
            description =
                    (SpannedString) TextUtils.concat(descWordsCaught, descLimitsReached, descMostUsedWord);
        }
        return description;
    }

    private void makePartOfSpannableWhite(Spannable text, String part) {
        //TODO: set black when background is white and otherwise the opposite
        int start = text.toString().indexOf(part);
        text.setSpan(new ForegroundColorSpan(Color.WHITE), start, start + part.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
