package com.keystarr.wordshunter.ui.set_up;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.utils.StatusBarUtils;

public class IntroActivity extends AppIntro {

    public static final int RESULT_CODE_INTRO_SHOWN = 1117;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBarUtils.setStatusBarColor(getWindow(), R.color.colorPrimary);

        SetUpGuidePageFragment page1 = SetUpGuidePageFragment.newInstance(null,
                getString(R.string.intro_page1_desc), R.drawable.intro1);

        SetUpGuidePageFragment page2 = SetUpGuidePageFragment.newInstance(
                getString(R.string.intro_page2_title), getString(R.string.intro_page2_desc),
                R.drawable.intro2);

        SetUpGuidePageFragment page3 = SetUpGuidePageFragment.newInstance(
                getString(R.string.intro_page3_title), getString(R.string.intro_page3_desc),
                R.drawable.intro3);

        SetUpGuidePageFragment page4 = SetUpGuidePageFragment.newInstance(
                getString(R.string.intro_page4_title), getString(R.string.intro_page4_desc),
                R.drawable.intro4);

        SetUpGuidePageFragment page5 = SetUpGuidePageFragment.newInstance(
                getString(R.string.intro_page5_title), getString(R.string.intro_page5_desc),
                R.drawable.intro5);

        addSlide(page1);
        addSlide(page2);
        addSlide(page3);
        addSlide(page4);
        addSlide(page5);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        setResult(RESULT_CODE_INTRO_SHOWN);
        finish();
    }


    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
