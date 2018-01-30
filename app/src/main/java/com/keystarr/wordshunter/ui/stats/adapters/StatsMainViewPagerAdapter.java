package com.keystarr.wordshunter.ui.stats.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.ui.stats.StatsChartListFragment;

/**
 * Created by Cyril on 05.07.2017.
 */

public class StatsMainViewPagerAdapter extends FragmentPagerAdapter {

    private Context ctx;

    public StatsMainViewPagerAdapter(Context ctx, FragmentManager fm) {
        super(fm);
        this.ctx = ctx;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return StatsChartListFragment.newInstance(
                        StatsChartListFragment.MODE_BY_GROUPS);
            default:
                return StatsChartListFragment.newInstance(
                        StatsChartListFragment.MODE_BY_WORDS);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public String getPageTitle(int position) {
        switch (position) {
            case 1:
                return ctx.getString(R.string.stats_by_groups);
            default:
                return ctx.getString(R.string.stats_by_words);
        }
    }
}
