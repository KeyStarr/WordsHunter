package com.keystarr.wordshunter.ui.set_up;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;

import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.services.WaiterService;
import com.keystarr.wordshunter.utils.StatusBarUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.keystarr.wordshunter.ui.MainActivity.INTENT_DATA_FIRST_LAUNCH;
import static com.keystarr.wordshunter.utils.AccessibilityUtils.isAccessibilityServiceEnabled;

public class SetUpActivity extends AppCompatActivity {

    public static final int RESULT_CODE_SERVICE_LAUNCHED = 43;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up);
        ButterKnife.bind(this);
        if (getIntent().getBooleanExtra(INTENT_DATA_FIRST_LAUNCH, false)) {
            openIntro();
        }
        StatusBarUtils.setStatusBarColor(getWindow(), R.color.colorPrimary);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isAccessibilityServiceEnabled(this)) {
            setResult(RESULT_CODE_SERVICE_LAUNCHED);
            finish();
        }
    }

    private void openIntro() {
        Intent intent = new Intent(this, IntroActivity.class);
        startActivity(intent);
    }

    private void openTutorial() {
        Intent intent = new Intent(this, SetUpGuideActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.open_button)
    public void openSettingsPageOnClick() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        startService(new Intent(this, WaiterService.class));
    }

    @OnClick(R.id.forgot_tv)
    public void openTutorialOnClick() {
        openTutorial();
    }
}
