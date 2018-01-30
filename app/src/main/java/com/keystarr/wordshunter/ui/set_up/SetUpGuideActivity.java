package com.keystarr.wordshunter.ui.set_up;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.utils.StatusBarUtils;

/**
 * Created by Cyril on 05.11.2017.
 */

public class SetUpGuideActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarUtils.setStatusBarColor(getWindow(), R.color.colorPrimary);

        SetUpGuidePageFragment page2 =
                SetUpGuidePageFragment.newInstance(
                        getString(R.string.set_up_tutorial_step1),
                        getString(R.string.set_up_tutorial_page2),
                        R.drawable.tut1);
        SetUpGuidePageFragment page3 =
                SetUpGuidePageFragment.newInstance(
                        getString(R.string.set_up_tutorial_step2),
                        getString(R.string.set_up_tutorial_page3),
                        R.drawable.tut2c);
        SetUpGuidePageFragment page4 =
                SetUpGuidePageFragment.newInstance(
                        getString(R.string.set_up_tutorial_step3),
                        getString(R.string.set_up_tutorial_page4),
                        R.drawable.tut3c);
        SetUpGuidePageFragment page5 =
                SetUpGuidePageFragment.newInstance(
                        getString(R.string.set_up_tutorial_step4),
                        getString(R.string.set_up_tutorial_page5),
                        R.drawable.tut4c);

        addSlide(page2);
        addSlide(page3);
        addSlide(page4);
        addSlide(page5);

        setBarColor(Color.BLACK);
        setSeparatorColor(Color.parseColor("#585858"));

        showSkipButton(true);
        setProgressButtonEnabled(true);

        setVibrate(false);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
