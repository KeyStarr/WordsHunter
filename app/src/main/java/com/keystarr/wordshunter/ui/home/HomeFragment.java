package com.keystarr.wordshunter.ui.home;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.app.App;
import com.keystarr.wordshunter.models.local.DayDtb;
import com.keystarr.wordshunter.models.local.WordCounter;
import com.keystarr.wordshunter.models.local.WordToTrack;
import com.keystarr.wordshunter.models.local.WordsCountersGroup;
import com.keystarr.wordshunter.models.local.WordsGroupToTrack;
import com.keystarr.wordshunter.repository.DatabaseRepository;
import com.keystarr.wordshunter.repository.PreferencesRepository;
import com.keystarr.wordshunter.ui.MainActivity;
import com.keystarr.wordshunter.ui.set_up.SetUpActivity;
import com.keystarr.wordshunter.ui.words.LovelyDialogFactory;
import com.keystarr.wordshunter.utils.AccessibilityUtils;
import com.keystarr.wordshunter.utils.DateUtils;

import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    @BindView(R.id.parasitesWheel)
    PieChart mChart;
    @BindView(R.id.chart_label)
    TextView chartLabel;
    @BindView(R.id.word_count_for_week)
    TextView weekCountTV;
    @BindView(R.id.word_count_for_month)
    TextView monthCountTV;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @Inject
    DatabaseRepository dtbRepo;
    @Inject
    PreferencesRepository prefsRepo;

    private DayDtb currentDay;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        App.getApp(this).getAppComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        if (!prefsRepo.isPersonalDataGiven())
            LovelyDialogFactory.createPersonalDataDialog(getContext(), prefsRepo).show();
        setToolbar();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        currentDay = getOrCreateAndGetCurrentDay();
        setChartLabel();
        setPieChart();
        setCountLabels();
        if (!AccessibilityUtils.isAccessibilityServiceEnabled(getContext()))
            notifyAccessibilityServiceDisabled();
        else
            checkAndNotifyIfNothingToTrack();
    }

    @OnClick(R.id.ic_settings)
    public void onSettingsClick() {
        ((MainActivity) getActivity()).showSettings();
    }

    private void setToolbar() {
        toolbar.setTitle(R.string.home);
        toolbar.findViewById(R.id.ic_settings).setVisibility(View.VISIBLE);
    }

    private void setCountLabels() {
        int lastSevenDaysCount = dtbRepo.getTotalWordsCountForLastDays(7),
                lastThirtyDaysCount = dtbRepo.getTotalWordsCountForLastDays(30);
        weekCountTV.setText(getString(R.string.for_seven_days, lastSevenDaysCount));
        monthCountTV.setText(getString(R.string.for_thirty_days, lastThirtyDaysCount));
    }

    private void checkAndNotifyIfNothingToTrack() {
        List<WordsGroupToTrack> groupsToTrack = dtbRepo.getWordsGroupsToTrack(false, false, false);
        if (groupsToTrack == null || !isAnythingToTrack(groupsToTrack)) {
            if (!prefsRepo.isKeyServiceFirstLaunch()) {
                Snackbar snackbar =
                        Snackbar.make(mChart, R.string.no_words_to_track_message, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(getString(R.string.no_words_to_track_snackbar_action_home_fragment),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((MainActivity) getActivity()).goToWordsTab();
                            }
                        });
                //AccessibilityUtils.makeSnackbarAnimationAppearWithAccessibilityOn(snackbar);
                snackbar.show();
            } else
                throw new IllegalStateException("HomeFragment started without first launch of KeyGet service");
        }
    }

    private boolean isAnythingToTrack(List<WordsGroupToTrack> groupsToTrack) {
        for (WordsGroupToTrack group : groupsToTrack)
            for (WordToTrack word : group.getWordsToTrack())
                if (word.isTracked())
                    return true;
        return false;
    }

    private void notifyAccessibilityServiceDisabled() {
        Snackbar snackbar =
                Snackbar.make(mChart, R.string.service_not_enabled_home_fragment, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(getString(R.string.enable_service_home_fragment),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), SetUpActivity.class));
                    }
                });
        //AccessibilityUtils.makeSnackbarAnimationAppearWithAccessibilityOn(snackbar);
        snackbar.show();
    }

    private void populateDaysSqlDbForDebug(int minYear, int maxYear) {
        List<WordsGroupToTrack> groupsToTrack = dtbRepo.getWordsGroupsToTrack(false, false, false);
        for (int year = minYear; year < maxYear; year++)
            for (int month = 1; month <= 12; month++)
                for (int day = 1; day < 29; day++) {
                    LocalDate date = LocalDate.of(year, month, day);
                    ZonedDateTime zdt = date.atStartOfDay(ZoneId.systemDefault());
                    long millis = zdt.toInstant().toEpochMilli();
                    dtbRepo.insert(DayDtb.createForDebug(millis,
                            Integer.valueOf(DateUtils.getDateFromLongToStringWeekInYear(millis)),
                            groupsToTrack));
                }
    }

    private DayDtb getOrCreateAndGetCurrentDay() {
        DayDtb day = dtbRepo.getCurrentDay();
        if (day == null) {
            List<WordsGroupToTrack> wordsGroupsToTrack =
                    dtbRepo.getWordsGroupsToTrack(false, false, false);
            day = DayDtb.createForCurrentDate(wordsGroupsToTrack);
            dtbRepo.insert(day);
        }
        return day;
    }

    private void setChartLabel() {
        chartLabel.setText(getString(R.string.charts_label,
                DateUtils.getDateFromLongToStringInDayMonthYear(currentDay.getDate())));
    }

    private void updateChartCenterText(int total) {
        mChart.setCenterText(String.valueOf(total));
    }

    private void setPieChart() {
        setPieChartView();
        setPieChartData(currentDay.getWordsCountersGroupsList());
        updateChartCenterText(currentDay.getDayTotalOccurrencesCount());
    }

    private void setPieChartView() {
        mChart.setUsePercentValues(false);
        mChart.getDescription().setEnabled(false);
        mChart.getLegend().setEnabled(false);

        mChart.setExtraOffsets(5, 10, 5, 5);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setCenterTextSize(24f);
        mChart.setDrawCenterText(true);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(50f);
        mChart.setTransparentCircleRadius(50f);

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(false);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        mChart.setEntryLabelColor(Color.BLACK);
        mChart.setEntryLabelTextSize(12f);
    }

    private void setPieChartData(List<WordsCountersGroup> countersGroupsList) {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        for (int i = 0; i < countersGroupsList.size(); i++) {
            List<WordCounter> wordCountersList = countersGroupsList.get(i).getWordsCountersList();
            for (int j = 0; j < wordCountersList.size(); j++) {
                if (wordCountersList.get(j).getCount() != 0) {
                    entries.add(new PieEntry(wordCountersList.get(j).getCount(),
                            wordCountersList.get(j).getWord()));
                }
            }
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        // add a lot of colors
        //TODO: rework to generate colors by words count
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);
        data.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf((int) value);
            }
        });
        mChart.setData(data);
        mChart.invalidate();
    }
}
