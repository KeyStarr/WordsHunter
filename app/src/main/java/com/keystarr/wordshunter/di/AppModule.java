package com.keystarr.wordshunter.di;

import com.keystarr.wordshunter.app.App;
import com.keystarr.wordshunter.network.StatsReceiverAPI;
import com.keystarr.wordshunter.repository.DatabaseRepository;
import com.keystarr.wordshunter.repository.PreferencesRepository;
import com.keystarr.wordshunter.repository.sql.DatabaseRepositorySQLite;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Cyril on 02.08.2017.
 */

@Module
public class AppModule {
    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public DatabaseRepository provideDtbRepo() {
        return new DatabaseRepositorySQLite(app);
    }

    @Provides
    @Singleton
    public Bus provideBus() {
        return new Bus();
    }

    @Provides
    @Singleton
    public PreferencesRepository providePrefsRepo() {
        return new PreferencesRepository(app);
    }

    @Provides
    @Singleton
    public StatsReceiverAPI provideStatsReceiverAPI() {
        return new Retrofit.Builder()
                .baseUrl("LINK_TO_SERVER")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(StatsReceiverAPI.class);
    }
}
