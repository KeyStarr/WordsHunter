package com.keystarr.wordshunter.ui;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.app.App;
import com.keystarr.wordshunter.repository.PreferencesRepository;
import com.keystarr.wordshunter.ui.home.HomeFragment;
import com.keystarr.wordshunter.ui.home.SettingsFragment;
import com.keystarr.wordshunter.ui.set_up.SetUpActivity;
import com.keystarr.wordshunter.ui.stats.StatsMainFragment;
import com.keystarr.wordshunter.ui.words.WordsFragment;
import com.keystarr.wordshunter.utils.BroadcastReceiversRegisteringUtils;
import com.keystarr.wordshunter.utils.DateUtils;
import com.keystarr.wordshunter.utils.StatusBarUtils;

import org.threeten.bp.Instant;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.keystarr.wordshunter.receivers.NotifyWithDailyReportBroadcastReceiver.INTENT_IS_OPEN_YESTERDAY_STATS;
import static com.keystarr.wordshunter.receivers.TextAnalyzerServiceCheckBroadcastReceiver.INTENT_IS_OPEN_SETTINGS;
import static com.keystarr.wordshunter.ui.set_up.SetUpActivity.RESULT_CODE_SERVICE_LAUNCHED;

public class MainActivity extends AppCompatActivity implements
        FragmentManager.OnBackStackChangedListener {

    public static final String INTENT_DATA_FIRST_LAUNCH = "intent_first_launch";
    public static final String BACKSTACK_SETTINGS = "settings";
    private static final int REQUEST_CODE_SET_UP_ACTIVITY = 41;

    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNav;

    @Inject
    PreferencesRepository prefsRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        App.getApp(this).getAppComponent().inject(this);
        ButterKnife.bind(this);
        registerBroadcastReceivers();
        if (prefsRepo.isKeyServiceFirstLaunch()) {
            //the protection mechanism of re-registering receivers
            //registering won't be done every time app opens except the first time
            prefsRepo.setSendDaysReceiverCalledLast(DateUtils.getCurrentDayDateInMillis());

            PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
            Intent intent = new Intent(this, SetUpActivity.class);
            intent.putExtra(INTENT_DATA_FIRST_LAUNCH, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivityForResult(intent, REQUEST_CODE_SET_UP_ACTIVITY);
        } else {
            setUIForMainApp();
            if (getIntent().getBooleanExtra(INTENT_IS_OPEN_SETTINGS, false)) {
                replaceFragment(R.id.main_container, new SettingsFragment(),
                        false, BACKSTACK_SETTINGS);
            } else if (getIntent().getBooleanExtra(INTENT_IS_OPEN_YESTERDAY_STATS, false)) {
                prefsRepo.setOpenYesterdayStats(true);
                replaceFragment(R.id.main_container, new StatsMainFragment(),
                        true, null);
            } else {
                replaceFragment(R.id.main_container, new HomeFragment(),
                        true, null);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        cancelAllPendingNotifications();
    }

    private void registerBroadcastReceivers() {
        boolean sendDaysHighFrequencyCall = prefsRepo.isSendDaysReceiverCallFrequencyHigh();
        long nowTime = Instant.now().toEpochMilli();
        if (!sendDaysHighFrequencyCall
                && nowTime - prefsRepo.getSendDaysReceiverCalledLast() > 104400000) {
            //if didnt work for past 29 hours - re-register
            BroadcastReceiversRegisteringUtils
                    .registerAlarmMngForSendDaysStats(this, false);
        } else if (nowTime - prefsRepo.getSendDaysReceiverCalledLast() > 14827049) {
            //if didnt work for 4.11 hours - re-register
            BroadcastReceiversRegisteringUtils
                    .registerAlarmMngForSendDaysStats(this, true);
        }
        if (prefsRepo.isCheckServiceDisabled())
            BroadcastReceiversRegisteringUtils.registerAlarmMngForServiceDisabledCheck(this);
        if (prefsRepo.isSendDailyReports())
            BroadcastReceiversRegisteringUtils.registerAlarmMngForDailyReportNotifications(this);
    }

    private void cancelAllPendingNotifications() {
        NotificationManager mng = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (mng != null)
            mng.cancelAll();
    }

    public void showSettings() {
        replaceFragment(R.id.main_container, new SettingsFragment(),
                false, BACKSTACK_SETTINGS);
    }

    @Override
    public void onBackPressed() {
        FragmentManager mng = getSupportFragmentManager();
        if (mng.getBackStackEntryCount() != 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_SET_UP_ACTIVITY:
                switch (resultCode) {
                    case RESULT_CODE_SERVICE_LAUNCHED:
                        setUIForMainApp();
                        break;
                }
        }
    }

    private void setUIForMainApp() {
        StatusBarUtils.setStatusBarColor(getWindow(), R.color.colorPrimary);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return onBottomBarNavigationItemSelected(item);
            }
        });
    }

    public boolean onBottomBarNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_home:
                replaceFragment(R.id.main_container, new HomeFragment(),
                        true, null);
                break;
            case R.id.item_statistics:
                replaceFragment(R.id.main_container, new StatsMainFragment(),
                        true, null);
                break;
            case R.id.item_words:
                replaceFragment(R.id.main_container, new WordsFragment(),
                        true, null);
                break;
        }
        return true;
    }


    //base stuff
    private void replaceFragment(int container, Fragment fragment,
                                 boolean popBackStack, String backstackName) {
        FragmentManager mng = getSupportFragmentManager();
        if (popBackStack)
            mng.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction trans = mng.beginTransaction();
        trans.replace(container, fragment);
        if (backstackName != null)
            trans.addToBackStack(backstackName);
        trans.commit();
    }

    public void goToWordsTab() {
        bottomNav.setSelectedItemId(R.id.item_words);
    }

    //for back arrow to appear when needed
    @Override
    public void onBackStackChanged() {
        if (getSupportActionBar() != null) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
