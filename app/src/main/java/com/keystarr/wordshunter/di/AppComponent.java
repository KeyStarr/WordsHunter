package com.keystarr.wordshunter.di;

import com.keystarr.wordshunter.receivers.NotifyWithDailyReportBroadcastReceiver;
import com.keystarr.wordshunter.receivers.OnBootReceiversRiserBroadcastReceiver;
import com.keystarr.wordshunter.receivers.SendStatsBroadcastReceiver;
import com.keystarr.wordshunter.services.WordsHunterService;
import com.keystarr.wordshunter.ui.MainActivity;
import com.keystarr.wordshunter.ui.home.HomeFragment;
import com.keystarr.wordshunter.ui.home.PreferencesFragment;
import com.keystarr.wordshunter.ui.stats.StatsChartListFragment;
import com.keystarr.wordshunter.ui.stats.StatsMainFragment;
import com.keystarr.wordshunter.ui.words.WordsFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Cyril on 02.08.2017.
 */

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    void inject(WordsHunterService service);

    void inject(HomeFragment homeFragment);

    void inject(PreferencesFragment preferencesFragment);

    void inject(StatsChartListFragment fragment);

    void inject(StatsMainFragment fragment);

    void inject(WordsFragment fragment);

    void inject(MainActivity mainActivity);

    void inject(NotifyWithDailyReportBroadcastReceiver reportSendReceiver);

    void inject(SendStatsBroadcastReceiver statsSendReceiver);

    void inject(OnBootReceiversRiserBroadcastReceiver statsSendReceiver);
}
