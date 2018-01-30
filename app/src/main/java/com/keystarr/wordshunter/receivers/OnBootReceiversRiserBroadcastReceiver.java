package com.keystarr.wordshunter.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.keystarr.wordshunter.app.App;
import com.keystarr.wordshunter.repository.PreferencesRepository;
import com.keystarr.wordshunter.utils.BroadcastReceiversRegisteringUtils;

import javax.inject.Inject;

/**
 * Created by Cyril on 10.10.2017.
 */

public class OnBootReceiversRiserBroadcastReceiver extends BroadcastReceiver {

    @Inject
    PreferencesRepository prefsRepo;

    @Override
    public void onReceive(Context context, Intent intent) {
        ((App) context.getApplicationContext()).getAppComponent().inject(this);
        if (prefsRepo.isSendDailyReports())
            BroadcastReceiversRegisteringUtils.registerAlarmMngForDailyReportNotifications(context);
        if (prefsRepo.isCheckServiceDisabled())
            BroadcastReceiversRegisteringUtils.registerAlarmMngForServiceDisabledCheck(context);
    }


}
