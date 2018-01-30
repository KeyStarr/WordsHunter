package com.keystarr.wordshunter.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.keystarr.wordshunter.ui.MainActivity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.keystarr.wordshunter.utils.AccessibilityUtils.isAccessibilityServiceEnabled;

public class WaiterService extends Service {

    public WaiterService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        final Context ctx = this;
        CountDownTimer timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (isAccessibilityServiceEnabled(ctx)) {
                    Intent intent = new Intent(ctx, MainActivity.class);
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    this.cancel();
                    stopSelf();
                }
            }

            @Override
            public void onFinish() {
                //открыть приложение и сказать пользователю мол ты шо хлопец тупишь
                //надо делать *так* и *эдак*
            }
        };
        timer.start();
        return START_NOT_STICKY;
    }
}
