package com.keystarr.wordshunter.ui.stats;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.BarLineChartTouchListener;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.app.App;
import com.keystarr.wordshunter.models.events.DateRangeChangedEvent;
import com.keystarr.wordshunter.models.local.DayDtb;
import com.keystarr.wordshunter.models.local.StatsRecyclerEntry;
import com.keystarr.wordshunter.models.local.Week;
import com.keystarr.wordshunter.repository.DatabaseRepository;
import com.keystarr.wordshunter.repository.PreferencesRepository;
import com.keystarr.wordshunter.ui.stats.adapters.StatsRecyclerAdapter;
import com.keystarr.wordshunter.utils.DateUtils;
import com.keystarr.wordshunter.utils.WeekUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatsChartListFragment extends Fragment {

    private static final String BUNDLE_KEY_MODE = "statsDisplayMode";
    public final static int MODE_BY_WORDS = 0;
    public static final int MODE_BY_GROUPS = 1;
    public static final int MODE_BY_DAYS = 10;
    public static final int MODE_BY_WEEKS = 11;

    @BindView(R.id.days_line_chart)
    LineChart lineChart;
    @BindView(R.id.stats_recycler_root)
    RelativeLayout statsRecyclerRootLayout;
    @BindView(R.id.stats_recycler_view)
    RecyclerView statsRecyclerView;
    @BindView(R.id.stats_count_label)
    TextView statsWordsCountLabel;
    @BindView(R.id.stats_count)
    TextView statsWordsCount;
    @BindView(R.id.data_load_progress_bar)
    ProgressBar dataLoadProgressBar;

    @Inject
    Bus bus;
    @Inject
    DatabaseRepository dtbRepo;
    @Inject
    PreferencesRepository prefsRepo;

    private Unbinder unbinder;

    private ChartListEntriesFactory entriesFactory;
    private List<DayDtb> daysList;
    private List<Week> weeksList;
    private int statsDisplayMode;
    private int statsDateRangeMode;

    private SetUIAsyncTask setUITask;
    private SetChartAsyncTask setChartTask;
    private SetListAsyncTask setListTask;
    private UpdateUIAsyncTask updateUITask;

    private boolean primaryLoadingFinished = false;

    public StatsChartListFragment() {
        // Required empty public constructor
    }

    public static StatsChartListFragment newInstance(int mode) {
        StatsChartListFragment fragment = new StatsChartListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_KEY_MODE, mode);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        App.getApp(this).getAppComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_stats_chart_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        entriesFactory = new ChartListEntriesFactory();
        statsDisplayMode = getArguments().getInt(BUNDLE_KEY_MODE);
        statsDateRangeMode = MODE_BY_DAYS;
        setUITask = new SetUIAsyncTask();
        setUITask.execute();
        return view;
    }

    @Override
    public void onDestroy() {
        //to prevent memory leaks
        unbinder.unbind();
        if (setUITask != null)
            setUITask.cancel(true);
        if (setChartTask != null)
            setChartTask.cancel(true);
        if (setListTask != null)
            setListTask.cancel(true);
        if (updateUITask != null)
            updateUITask.cancel(true);
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (primaryLoadingFinished)
            updateUI(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

    private void toggleUIMode(boolean loading) {
        if (loading) {
            statsRecyclerRootLayout.setVisibility(View.GONE);
            lineChart.setVisibility(View.GONE);
            dataLoadProgressBar.setVisibility(View.VISIBLE);
        } else {
            dataLoadProgressBar.setVisibility(View.GONE);
            statsRecyclerRootLayout.setVisibility(View.VISIBLE);
            lineChart.setVisibility(View.VISIBLE);
        }
    }

    @Subscribe
    public void dateRangeChanged(DateRangeChangedEvent event) {
        Answers.getInstance().logCustom(new CustomEvent("Date range changed")
                .putCustomAttribute("date mode", event.getMode()));
        switch (event.getMode()) {
            case MODE_BY_DAYS:
                if (statsDateRangeMode != MODE_BY_DAYS) {
                    statsDateRangeMode = MODE_BY_DAYS;
                    updateUI(false);
                }
                break;
            case MODE_BY_WEEKS:
                if (statsDateRangeMode != MODE_BY_WEEKS) {
                    statsDateRangeMode = MODE_BY_WEEKS;
                    updateUI(false);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown date range");
        }
    }

    private void updateUI(boolean reloadDaysList) {
        updateUITask = new UpdateUIAsyncTask();
        updateUITask.execute(reloadDaysList);
    }

    private void setList() {
        setListTask = new SetListAsyncTask();
        setListTask.execute();
    }

    private void setListCountLabels(DayDtb dayDtb) {
        statsWordsCountLabel.setText(getString(R.string.stats_count_label_words_days,
                DateUtils.getDateFromLongToStringInDayMonthYearWords(dayDtb.getDate()) + ":"));
        statsWordsCount.setText(String.valueOf(dayDtb.getDayTotalOccurrencesCount()));
    }

    private void setListCountLabels(Week week) {
        statsWordsCountLabel.setText(getString(R.string.stats_count_label_words_weeks,
                week.getFormattedMonday() + ":"));
        statsWordsCount.setText(String.valueOf(week.getOccurrencesCount()));
    }

    private void setListRecycler() {
        LinearLayoutManager mng = new LinearLayoutManager(this.getContext());
        mng.setOrientation(LinearLayoutManager.VERTICAL);
        statsRecyclerView.setLayoutManager(mng);
    }

    private StatsRecyclerAdapter createAdapterForList(List<StatsRecyclerEntry> entryList) {
        return new StatsRecyclerAdapter(getContext(), entryList);
    }

    private void updateListForLastChartElement() {
        if (statsDateRangeMode == MODE_BY_DAYS)
            updateListByDay(daysList.get(daysList.size() - 1));
        else
            updateListByWeek(weeksList.get(weeksList.size() - 1));
    }

    private void updateListByDay(DayDtb day) {
        setListCountLabels(day);
        List<StatsRecyclerEntry> entries =
                entriesFactory.createListEntries(day, statsDisplayMode);
        statsRecyclerView.swapAdapter(createAdapterForList(entries), true);
    }

    private void updateListByWeek(Week week) {
        setListCountLabels(week);
        List<DayDtb> daysListForWeek = dtbRepo.getWeek(week.getWeekInYear());
        List<StatsRecyclerEntry> entries =
                entriesFactory.createListEntries(daysListForWeek, statsDisplayMode);
        statsRecyclerView.swapAdapter(createAdapterForList(entries), true);
    }

    private void setChart() {
        setChartTask = new SetChartAsyncTask();
        setChartTask.execute();
    }

    private void updateChart(List<Entry> entries) {
        setChartData(entries);
        setChartValueForAxisFormatters();
        modifyChartXViewport();
        lineChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);
    }

    private void setChartData(List<Entry> entries) {
        if (statsDateRangeMode == MODE_BY_WEEKS)
            weeksList = WeekUtils.fillWeekList(daysList);

        final LineDataSet lineDataSet = new LineDataSet(entries, "");
        styleChartDataSet(lineDataSet);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        final LineData lineData = new LineData(dataSets);
        lineData.setDrawValues(false);
        lineChart.setData(lineData);
    }

    private void setChartAppearance() {
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setHighlightPerDragEnabled(false);
        lineChart.setHighlightPerTapEnabled(false);
        lineChart.getXAxis().setDrawGridLines(true);
        lineChart.getXAxis().setGranularity(1f);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.setScaleEnabled(false);
        lineChart.setExtraBottomOffset(5f);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setDragDecelerationFrictionCoef(0.85f);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawLabels(false);
        lineChart.animateY(1400, Easing.EasingOption.EaseInOutQuart);
    }

    private void modifyChartXViewport() {
        int entriesSize = statsDateRangeMode == MODE_BY_DAYS ?
                daysList.size() : weeksList.size();
        int range = entriesSize < 3 ? 2 : 3;
        lineChart.setVisibleXRangeMinimum(range);
        lineChart.setVisibleXRangeMaximum(range);
        lineChart.moveViewToX(entriesSize - 1);
    }

    private void setChartValueForAxisFormatters() {
        if (statsDateRangeMode == MODE_BY_DAYS)
            setChartXValueFormatterByDays();
        else
            setChartXValueFormatterByWeeks();
    }

    private void setChartXValueFormatterByDays() {
        lineChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                value--;
                if (value >= 0 && value < daysList.size()) {
                    return DateUtils.getDateFromLongToStringInDayMonth(
                            daysList.get((int) value).getDate());
                } else
                    return "";
            }
        });
    }

    private void setChartXValueFormatterByWeeks() {
        lineChart.getXAxis().setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                value--;
                if (value >= 0 && value < weeksList.size()) {
                    return getString(R.string.week_from_monday,
                            weeksList.get((int) value).getFormattedMonday());
                } else
                    return "";
            }
        });
    }

    private void setChartAndListToYesterdayIfOpenedByNotification() {
        if (prefsRepo.isOpenYesterdayStats()) {
            int yesterdayPos = daysList.size();
            //ensure that the new day was created
            //otherwise yesterday would be the last day of the current days list
            if (daysList.get(daysList.size() - 1).getDate() == DateUtils.getCurrentDayDateInMillis()) {
                yesterdayPos -= 2;
                if (yesterdayPos < 0)
                    yesterdayPos = 0;
            } else
                yesterdayPos -= 1;
            lineChart.moveViewToX(yesterdayPos);
            updateListByDay(daysList.get(yesterdayPos));
            prefsRepo.setOpenYesterdayStats(false);
        }
    }

    private void styleChartDataSet(LineDataSet lineDataSet) {
        lineDataSet.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setLineWidth(1.8f);
        lineDataSet.setColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
        lineDataSet.setCircleColors(ContextCompat.getColor(getContext(), R.color.colorAccent));
    }

    private class SetUIAsyncTask extends AsyncTask<Void, Void, List<DayDtb>> {
        @Override
        protected List<DayDtb> doInBackground(Void... params) {
            return daysList = dtbRepo.getDaysSortByDateAscending();
        }

        @Override
        protected void onPostExecute(List<DayDtb> daysList) {
            setList();
            setChart();
        }
    }

    private class SetListAsyncTask extends AsyncTask<Void, Void, List<StatsRecyclerEntry>> {
        @Override
        protected void onPreExecute() {
            setListCountLabels(daysList.get(daysList.size() - 1));
            setListRecycler();
        }

        @Override
        protected List<StatsRecyclerEntry> doInBackground(Void... params) {
            return entriesFactory
                    .createListEntries(daysList.get(daysList.size() - 1), statsDisplayMode);
        }

        @Override
        protected void onPostExecute(List<StatsRecyclerEntry> entries) {
            statsRecyclerView.setAdapter(createAdapterForList(entries));
        }
    }

    private class SetChartAsyncTask extends AsyncTask<Void, Void, List<Entry>> {
        //Should be called last at set UI task as
        //it toggles UI mode for 'normal' from 'loading'
        @Override
        protected void onPreExecute() {
            setChartValueForAxisFormatters();
            setChartAppearance();
            setChartScrollingFeature();
        }

        @Override
        protected List<Entry> doInBackground(Void... params) {
            return entriesFactory
                    .createChartEntries(daysList, statsDateRangeMode);
        }

        @Override
        protected void onPostExecute(List<Entry> entries) {
            setChartData(entries);
            modifyChartXViewport();
            toggleUIMode(false);
            setChartAndListToYesterdayIfOpenedByNotification();
            primaryLoadingFinished = true;
        }
    }

    private class UpdateUIAsyncTask extends AsyncTask<Boolean, Void, List<Entry>> {
        @Override
        protected void onPreExecute() {
            toggleUIMode(true);
        }

        @Override
        protected List<Entry> doInBackground(Boolean... reloadDaysListFromDtb) {
            if (reloadDaysListFromDtb[0])
                daysList = dtbRepo.getDaysSortByDateAscending();
            return entriesFactory.createChartEntries(daysList, statsDateRangeMode);
        }

        @Override
        protected void onPostExecute(List<Entry> entries) {
            updateChart(entries);
            updateListForLastChartElement();
            toggleUIMode(false);
        }
    }

    private void setChartScrollingFeature() {
        lineChart.setOnChartGestureListener(new OnChartGestureListener() {
            //Unfortunately there is no way to set only ONE callback, only all
            private float dXPrevious = 9999999;
            private int times = 0;
            private int dXScrollSlowBorder = 10;

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {
                //TODO: check a bug for API 16-17
                //bug when before centerviewtoanimated lag spike appears
                if (Math.abs(dX - dXPrevious) < dXScrollSlowBorder) {
                    times++;
                }
                dXPrevious = dX;
                if (times > 2) {
                    //TODO: make a timer for cooldown on list refreshing
                    float xCenter = lineChart.getLowestVisibleX() + lineChart.getVisibleXRange() / 2;
                    int roundedXCenter = Math.round(xCenter);
                    times = 0;
                    if (roundedXCenter <= 0)
                        roundedXCenter = 1;
                    else if (statsDateRangeMode == MODE_BY_DAYS
                            && (roundedXCenter > daysList.size()))
                        roundedXCenter = daysList.size();
                    else if (statsDateRangeMode == MODE_BY_WEEKS
                            && roundedXCenter > weeksList.size())
                        roundedXCenter = weeksList.size();
                    ((BarLineChartTouchListener) lineChart.getOnTouchListener()).stopDeceleration();
                    lineChart.centerViewToAnimated((float) roundedXCenter, 0f,
                            YAxis.AxisDependency.LEFT, 1000);
                    DayDtb dayDtb = daysList.get(roundedXCenter - 1);
                    if (statsDateRangeMode == MODE_BY_DAYS)
                        updateListByDay(dayDtb);
                    else if (statsDateRangeMode == MODE_BY_WEEKS)
                        updateListByWeek(weeksList.get(roundedXCenter - 1));
                }
            }

            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {
            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {
            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
            }
        });
    }
}
