package com.keystarr.wordshunter.app;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.crashlytics.android.Crashlytics;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.keystarr.wordshunter.di.AppComponent;
import com.keystarr.wordshunter.di.AppModule;
import com.keystarr.wordshunter.di.DaggerAppComponent;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Cyril on 02.08.2017.
 */

public class App extends Application {
    private AppComponent appComponent;

    public static App getApp(Activity activity) {
        return (App) activity.getApplication();
    }

    public static App getApp(Fragment fragment) {
        final FragmentActivity activity = fragment.getActivity();
        if (activity != null)
            return (App) activity.getApplication();
        throw new IllegalStateException("Fragment must be attached to activity!");
    }

    public static App getApp(Service service) {
        return (App) service.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        appComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
        AndroidThreeTen.init(this);
    }


    public AppComponent getAppComponent() {
        return appComponent;
    }
}
