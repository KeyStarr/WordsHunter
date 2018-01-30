package com.keystarr.wordshunter.ui.stats;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Cyril on 04.08.2017.
 */

public class NonScrollHorizontalViewPager extends ViewPager {
    public NonScrollHorizontalViewPager(Context context) {
        super(context);
    }

    public NonScrollHorizontalViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return false;
    }

}
