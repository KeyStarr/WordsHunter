package com.keystarr.wordshunter.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import com.keystarr.wordshunter.app.App;
import com.keystarr.wordshunter.models.local.DayDtb;
import com.keystarr.wordshunter.models.local.WordCounter;
import com.keystarr.wordshunter.models.remote.DayStats;
import com.keystarr.wordshunter.models.remote.DayStatsList;
import com.keystarr.wordshunter.models.remote.User;
import com.keystarr.wordshunter.models.remote.WordCounterRemote;
import com.keystarr.wordshunter.network.StatsReceiverAPI;
import com.keystarr.wordshunter.repository.DatabaseRepository;
import com.keystarr.wordshunter.repository.PreferencesRepository;
import com.keystarr.wordshunter.utils.BroadcastReceiversRegisteringUtils;

import org.threeten.bp.Instant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Bizarre on 23.10.2017.
 */

public class SendStatsBroadcastReceiver extends BroadcastReceiver {

    private Context context;
    private List<DayDtb> unsentDays;

    @Inject
    PreferencesRepository prefsRepo;
    @Inject
    DatabaseRepository dtbRepo;
    @Inject
    StatsReceiverAPI statsAPI;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        ((App) context.getApplicationContext()).getAppComponent().inject(this);
        unsentDays = dtbRepo.getAllUnsentDays();
        if (!unsentDays.isEmpty()) {
            SendDaysStatsAsyncTask sendDaysAsyncTask = new SendDaysStatsAsyncTask();
            sendDaysAsyncTask.execute();
        }
        prefsRepo.setSendDaysReceiverCalledLast(Instant.now().toEpochMilli());
    }

    private class SendDaysStatsAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            return pingGoogleDNS();
        }

        private boolean pingGoogleDNS() {
            try {
                int timeoutMs = 1500;
                Socket sock = new Socket();
                SocketAddress socketAddress =
                        new InetSocketAddress("8.8.8.8", 53);

                sock.connect(socketAddress, timeoutMs);
                sock.close();

                return true;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean connected) {
            boolean wasHighFrequency = prefsRepo.isSendDaysReceiverCallFrequencyHigh();
            boolean increaseReceiverCallsFrequency = false;
            if (connected) {
                if (prefsRepo.getUserId() != -1) {
                    if (unsentDays.size() > 10) {
                        WifiManager wifi = (WifiManager)
                                context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        if (wifi.isWifiEnabled())
                            sendDaysToServer(unsentDays);
                        else
                            increaseReceiverCallsFrequency = true;
                    } else
                        sendDaysToServer(unsentDays);
                } else {
                    requestAndSaveUserId();
                    increaseReceiverCallsFrequency = true;
                }
            } else {
                increaseReceiverCallsFrequency = true;
            }
            if (wasHighFrequency != increaseReceiverCallsFrequency) {
                prefsRepo.setSendDaysReceiverHighCallFrequency(increaseReceiverCallsFrequency);
                BroadcastReceiversRegisteringUtils
                        .registerAlarmMngForSendDaysStats(context, increaseReceiverCallsFrequency);
            }
        }

        private void requestAndSaveUserId() {
            User user = new User(prefsRepo.getUserAge(), prefsRepo.getUserGender());

            Call<Long> requestIdCall = statsAPI.requestUserdId(user);
            requestIdCall.enqueue(new Callback<Long>() {
                @Override
                public void onResponse(Call<Long> call, Response<Long> response) {
                    if (response.isSuccessful()) {
                        Long userID = response.body();
                        if (userID != null) {
                            prefsRepo.setUserGender(false);
                            prefsRepo.setUserAge(0);
                            prefsRepo.setUserId(userID);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Long> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }

        private void sendDaysToServer(final List<DayDtb> unsentDays) {
            Call<Void> sendStatsCall = statsAPI.sendDayStats(
                    transformDayDtbListToDayStatsList(unsentDays));
            sendStatsCall.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        for (DayDtb day : unsentDays)
                            day.setSent(true);
                        dtbRepo.updateDays(unsentDays);
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }

        private DayStatsList transformDayDtbListToDayStatsList(List<DayDtb> daysList) {
            List<DayStats> statsList = new ArrayList<>();
            for (DayDtb day : daysList)
                statsList.add(new DayStats(day.getDate(), day.getWordsTypedCounter(),
                        transformWordsCountersToRemote(day.getAllWordCountersList())));
            return new DayStatsList(prefsRepo.getUserId(), statsList);
        }

        private List<WordCounterRemote> transformWordsCountersToRemote(List<WordCounter> wordsCountersList) {
            List<WordCounterRemote> remoteList = new ArrayList<>();
            for (WordCounter counter : wordsCountersList)
                remoteList.add(new WordCounterRemote(counter));
            return remoteList;
        }
    }
}