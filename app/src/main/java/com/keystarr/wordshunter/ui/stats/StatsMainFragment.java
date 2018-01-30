package com.keystarr.wordshunter.ui.stats;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.app.App;
import com.keystarr.wordshunter.models.events.DateRangeChangedEvent;
import com.keystarr.wordshunter.repository.DatabaseRepository;
import com.keystarr.wordshunter.ui.stats.adapters.StatsMainViewPagerAdapter;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.keystarr.wordshunter.ui.stats.StatsChartListFragment.MODE_BY_DAYS;
import static com.keystarr.wordshunter.ui.stats.StatsChartListFragment.MODE_BY_WEEKS;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatsMainFragment extends Fragment {

    @BindView(R.id.stats_viewpager)
    NonScrollHorizontalViewPager statsPager;
    @BindView(R.id.tabs)
    TabLayout pagerTabs;
    @BindView(R.id.stats_empty_message)
    TextView emptyMsg;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    Bus bus;
    @Inject
    DatabaseRepository dtbRepo;

    public StatsMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        App.getApp(this).getAppComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_stats_main, container, false);
        ButterKnife.bind(this, view);
        setToolbar();
        statsPager.setAdapter(new StatsMainViewPagerAdapter(getContext(),
                getChildFragmentManager()));
        pagerTabs.setupWithViewPager(statsPager);
        toogleUIMode();
        return view;
    }

    private void setToolbar() {
        toolbar.setTitle(R.string.statistics);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    }

    private void toogleUIMode() {
        if (!dtbRepo.isThereAtLeastOneDay()) {
            statsPager.setVisibility(View.GONE);
            pagerTabs.setVisibility(View.GONE);
            emptyMsg.setVisibility(View.VISIBLE);
            setHasOptionsMenu(false);
        } else {
            emptyMsg.setVisibility(View.GONE);
            statsPager.setVisibility(View.VISIBLE);
            pagerTabs.setVisibility(View.VISIBLE);
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_toolbar, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DateRangeChangedEvent event = new DateRangeChangedEvent();
        switch (item.getItemId()) {
            case R.id.by_days:
                event.setMode(MODE_BY_DAYS);
                break;
            case R.id.by_weeks:
                event.setMode(MODE_BY_WEEKS);
                break;
        }
        bus.post(event);
        return true;
    }

}
