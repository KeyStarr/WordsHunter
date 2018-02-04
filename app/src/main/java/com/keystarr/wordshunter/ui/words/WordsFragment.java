package com.keystarr.wordshunter.ui.words;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.app.App;
import com.keystarr.wordshunter.models.events.GroupAddedEvent;
import com.keystarr.wordshunter.models.events.GroupRemovedEvent;
import com.keystarr.wordshunter.models.events.ILimiterEvent;
import com.keystarr.wordshunter.models.events.LimiterAddedEvent;
import com.keystarr.wordshunter.models.events.LimiterChangedEvent;
import com.keystarr.wordshunter.models.events.LimiterDeletedEvent;
import com.keystarr.wordshunter.models.events.WordAddedEvent;
import com.keystarr.wordshunter.models.events.WordBackToTrackingEvent;
import com.keystarr.wordshunter.models.events.WordRemovedEvent;
import com.keystarr.wordshunter.models.events.WordStoppedFromTrackingEvent;
import com.keystarr.wordshunter.models.events.WordToTrackGroupsPositionsChangedEvent;
import com.keystarr.wordshunter.models.events.WordsToTrackInGroupPositionsChangedEvent;
import com.keystarr.wordshunter.models.local.DayDtb;
import com.keystarr.wordshunter.models.local.Limiter;
import com.keystarr.wordshunter.models.local.WordCounter;
import com.keystarr.wordshunter.models.local.WordToTrack;
import com.keystarr.wordshunter.models.local.WordsCountersGroup;
import com.keystarr.wordshunter.models.local.WordsGroupToTrack;
import com.keystarr.wordshunter.repository.DatabaseRepository;
import com.keystarr.wordshunter.repository.PreferencesRepository;
import com.keystarr.wordshunter.ui.set_up.SetUpActivity;
import com.keystarr.wordshunter.ui.words.drag_and_drop.Draggable;
import com.keystarr.wordshunter.ui.words.drag_and_drop.GroupRecyclerItemDragCallback;
import com.keystarr.wordshunter.ui.words.drag_and_drop.RecyclerGroupsOnDragListener;
import com.keystarr.wordshunter.utils.AccessibilityUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.yarolegovich.lovelydialog.LovelyChoiceDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

/**
 * A simple {@link Fragment} subclass.
 */
public class WordsFragment extends Fragment implements ButtonsPanelListener, GroupCardExpansionListener {

    private Unbinder unbinder;
    @BindView(R.id.groups_recycler_view)
    RecyclerView groupsRecyclerView;
    @BindView(R.id.error_message_text_view)
    TextView errorMessage;
    @BindView(R.id.error_layout)
    RelativeLayout errorLayout;
    @BindView(R.id.enable_service_button)
    Button enableServiceButton;
    @BindView(R.id.add_fab)
    FloatingActionButton addFAB;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    DatabaseRepository dtbRepo;
    @Inject
    PreferencesRepository prefsRepo;
    @Inject
    Bus bus;

    public WordsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        App.getApp(this).getAppComponent().inject(this);
        View view = inflater.inflate(R.layout.fragment_words, container, false);
        unbinder = ButterKnife.bind(this, view);
        setToolbar();
        setAddFABScrollingBehaviour();
        setRecycler();//as user almost surely will add smth
        if (prefsRepo.isWordsScreenFirstLaunch()
                && AccessibilityUtils.isAccessibilityServiceEnabled(getContext())) {
            showTutorial(true);
            prefsRepo.setWordsScreenFirstLaunch(false);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (AccessibilityUtils.isAccessibilityServiceEnabled(getContext())) {
            if (isWordsToTrackListEmpty())
                showEmptyErrorMessage();
            else
                hideAccessibilityServiceNotEnabledMessage();
        } else
            showAccessibilityServiceNotEnabledMessage();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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

    private void setToolbar() {
        toolbar.findViewById(R.id.ic_words_tutorial).setVisibility(View.VISIBLE);
        toolbar.setTitle(R.string.words);
    }

    private void setRecycler() {
        LinearLayoutManager manager = new LinearLayoutManager(
                getContext(), LinearLayoutManager.VERTICAL, false);
        groupsRecyclerView.setLayoutManager(manager);
        List<WordsGroupToTrack> groupToTracks = dtbRepo.getWordsGroupsToTrack(true, true, true);
        groupsRecyclerView.setAdapter(
                new GroupsRecyclerAdapter(App.getApp(this), groupToTracks,
                        this, dtbRepo, this, bus));
        groupsRecyclerView.setOnDragListener(new RecyclerGroupsOnDragListener(groupsRecyclerView));
        new ItemTouchHelper(new GroupRecyclerItemDragCallback(bus))
                .attachToRecyclerView(groupsRecyclerView);
    }

    private void setAddFABScrollingBehaviour() {
        groupsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && addFAB.isShown())
                    addFAB.hide();
                else if (dy < 0 && !addFAB.isShown())
                    addFAB.show();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        !recyclerView.canScrollVertically(1) && !recyclerView.canScrollVertically(-1)) {
                    if (addFAB.isShown())
                        addFAB.hide();
                    else
                        addFAB.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    private void showEmptyErrorMessage() {
        if (groupsRecyclerView != null) {
            groupsRecyclerView.setVisibility(View.INVISIBLE);
            toolbar.findViewById(R.id.ic_words_tutorial).setVisibility(View.GONE);
            errorLayout.setVisibility(View.VISIBLE);
            errorMessage.setText(getString(R.string.empty_message_words_to_track));
            CoordinatorLayout.LayoutParams params =
                    ((CoordinatorLayout.LayoutParams) addFAB.getLayoutParams());
            addFAB.show();
            params.setAnchorId(R.id.enable_service_button);
            params.anchorGravity = Gravity.BOTTOM | Gravity.CENTER;
        }
    }

    private void hideEmptyErrorMessage() {
        groupsRecyclerView.setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.ic_words_tutorial).setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        CoordinatorLayout.LayoutParams params =
                ((CoordinatorLayout.LayoutParams) addFAB.getLayoutParams());
        params.setAnchorId(R.id.groups_recycler_view);
        params.anchorGravity = Gravity.BOTTOM | Gravity.END;
    }

    private void showAccessibilityServiceNotEnabledMessage() {
        addFAB.setVisibility(View.GONE);
        groupsRecyclerView.setVisibility(View.GONE);
        toolbar.findViewById(R.id.ic_words_tutorial).setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        errorMessage.setText(getString(R.string.accessibility_service_not_enabled_message));
        enableServiceButton.setVisibility(View.VISIBLE);
        enableServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SetUpActivity.class));
            }
        });
    }

    private void hideAccessibilityServiceNotEnabledMessage() {
        addFAB.setVisibility(View.VISIBLE);
        toolbar.findViewById(R.id.ic_words_tutorial).setVisibility(View.VISIBLE);
        groupsRecyclerView.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
    }

    private boolean isWordsToTrackListEmpty() {
        List<WordsGroupToTrack> groupsToTrackList =
                dtbRepo.getWordsGroupsToTrack(true, true, false);
        return groupsToTrackList.size() == 1 && groupsToTrackList.get(0).getWordsToTrack().isEmpty();
    }


    @OnClick(R.id.add_fab)
    public void onFABClick() {
        final List<WordsGroupToTrack> groupToTracks =
                dtbRepo.getWordsGroupsToTrack(true, true, false);
        LovelyDialogFactory.createAddChoiceDialog(
                getContext(),
                new LovelyChoiceDialog.OnItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(int position, String item) {
                        if (position == LovelyDialogFactory.LIST_WORD_POSITION) {
                            LovelyDialogFactory.createAddWordDialog(
                                    getContext(), bus, getGroupsNamesList(groupToTracks, false),
                                    getLowCaseWordsList(groupToTracks))
                                    .show();
                        } else
                            LovelyDialogFactory.createAddGroupDialog(
                                    getContext(), bus, getGroupsNamesList(groupToTracks, true))
                                    .show();
                    }
                })
                .show();
    }

    @Subscribe
    public void wordsInGroupPositionsChanged(WordsToTrackInGroupPositionsChangedEvent event) {
        normalizeRecyclerPositionsOfWords(event.getWordsToTrack());
        dtbRepo.update(event.getWordsToTrack());
    }

    @Subscribe
    public void wordsGroupsToTrackPositionsChanged(WordToTrackGroupsPositionsChangedEvent event) {
        normalizeRecyclerPositionsOfWords(event.getGroupsToTrack());
        dtbRepo.updateGroupsToTrack(event.getGroupsToTrack());
    }

    private <T extends Draggable> void normalizeRecyclerPositionsOfWords(List<T> wordsToTrack) {
        //to be sure that positions after drag n drop
        //don't mess up
        if (wordsToTrack != null && wordsToTrack.size() != 0) {
            Collections.sort(wordsToTrack, new Comparator<T>() {
                @Override
                public int compare(T o1, T o2) {
                    return o1.comparePositions(o2);
                }
            });

            if (wordsToTrack.get(0).getRecyclerPosition() != 0)
                wordsToTrack.get(0).setRecyclerPosition(0);

            for (int i = 1; i < wordsToTrack.size(); i++) {
                int distinction = wordsToTrack.get(i).getRecyclerPosition()
                        - wordsToTrack.get(i - 1).getRecyclerPosition();
                if (distinction != 1)
                    wordsToTrack.get(i).setRecyclerPosition(wordsToTrack.get(i - 1).getRecyclerPosition() + 1);
            }
        }
    }

    @Subscribe
    public void onWordAdded(WordAddedEvent event) {
        WordToTrack wordToTrack = event.getAddedWordToTrack();
        dtbRepo.insert(wordToTrack);
        GroupsRecyclerAdapter groupsAdapter =
                ((GroupsRecyclerAdapter) groupsRecyclerView.getAdapter());
        WordsRecyclerAdapter wordsAdapter = groupsAdapter
                .getWordsRecyclerAdapterForGroup(wordToTrack.getGroupName());
        wordsAdapter.insertWordToTrack(wordToTrack);
        groupsRecyclerView.smoothScrollToPosition(
                groupsAdapter.getGroupPosition(wordToTrack.getGroupName()));
        if (errorLayout.isShown())
            hideEmptyErrorMessage();
    }

    @Subscribe
    public void onGroupAdded(GroupAddedEvent event) {
        dtbRepo.insert(event.getAddedGroupToTrack());

        //insert word into the adapter to display it
        GroupsRecyclerAdapter groupsAdapter =
                ((GroupsRecyclerAdapter) groupsRecyclerView.getAdapter());
        groupsAdapter.insertGroupToTrack(1, event.getAddedGroupToTrack());

        //update positions of the groups above inserted one
        List<WordsGroupToTrack> groupsToTrack = groupsAdapter.getGroupsToTrack();
        for (int i = 2; i < groupsToTrack.size(); i++) {
            WordsGroupToTrack groupToTrack = groupsToTrack.get(i);
            groupToTrack.setRecyclerPosition(groupToTrack.getRecyclerPosition() + 1);
        }
        dtbRepo.updateGroupsToTrack(groupsToTrack);

        groupsRecyclerView.smoothScrollToPosition(1);
        if (errorLayout.isShown())
            hideEmptyErrorMessage();
    }

    @Subscribe
    public void onLimiterNotificationAdded(LimiterAddedEvent event) {
        dtbRepo.insert(event.getLimiter());
        showLimiterStateChangedSnackbar(event.getLimiter().getLimitedWord(), 0);
        toggleNotificationsButtonAppearence(event);
    }

    @Subscribe
    public void onLimiterNotificationChanged(LimiterChangedEvent event) {
        dtbRepo.update(event.getChangedLimiter());
        showLimiterStateChangedSnackbar(event.getChangedLimiter().getLimitedWord(), 1);
    }

    @Subscribe
    public void onLimiterNotificationDeleted(LimiterDeletedEvent event) {
        dtbRepo.delete(event.getLimiter());
        showLimiterStateChangedSnackbar(event.getLimiter().getLimitedWord(), 2);
        toggleNotificationsButtonAppearence(event);
    }

    private void toggleNotificationsButtonAppearence(ILimiterEvent event) {
        GroupsRecyclerAdapter groupsAdapter = (GroupsRecyclerAdapter) groupsRecyclerView.getAdapter();
        if (event.getWordHolderPos() == -1) {
            groupsAdapter.notifyDataSetChanged();
        } else {
            WordsRecyclerAdapter wordsAdapter =
                    groupsAdapter.getWordsRecyclerAdapterForWord(event.getLimiter().getLimitedWord());
            wordsAdapter.notifyDataSetChanged();
        }
    }


    private void showLimiterStateChangedSnackbar(String name, int state) {
        String message = getString(R.string.limit_toast_base,name);
        switch (state) {
            case 0:
                message += getString(R.string.limit_toast_created);
                break;
            case 1:
                message += getString(R.string.limit_toast_changed);
                break;
            case 2:
                message += getString(R.string.limit_toast_removed);
                break;
        }
        Snackbar.make(groupsRecyclerView, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void trackingButtonOnClick(WordToTrack wordToTrack) {
        wordToTrack.setTracked(!wordToTrack.isTracked());
        dtbRepo.update(wordToTrack);
        if (wordToTrack.isTracked())
            bus.post(new WordBackToTrackingEvent(wordToTrack));
        else
            bus.post(new WordStoppedFromTrackingEvent(wordToTrack));
        showTrackingChangeSnackbar(wordToTrack);
    }

    private void showTrackingChangeSnackbar(WordToTrack wordToTrack) {
        Snackbar snackbar = Snackbar.make(groupsRecyclerView,
                getString(wordToTrack.isTracked() ? R.string.word_will_be_tracked_words_fragment :
                        R.string.word_wont_be_tracked_words_fragment, wordToTrack.getWord()),
                Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    @Override
    public void deleteButtonOnClick(final int pos, final WordsGroupToTrack groupToRemove) {
        makeUndoDeleteSnackbar(
                getString(R.string.group_was_removed, groupToRemove.getGroupName()),
                new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, @DismissEvent int event) {
                        if (event == DISMISS_EVENT_ACTION) {
                            ((GroupsRecyclerAdapter) groupsRecyclerView.getAdapter())
                                    .insertGroupToTrack(pos, groupToRemove);
                        } else {
                            //update recycler positions of groups that were above deleted one
                            List<WordsGroupToTrack> groupsToTrack =
                                    dtbRepo.getWordsGroupsToTrack(true, true, true);
                            for (int i = groupToRemove.getRecyclerPosition() + 1; i < groupsToTrack.size(); i++) {
                                WordsGroupToTrack groupToTrack = groupsToTrack.get(i);
                                groupToTrack.setRecyclerPosition(groupToTrack.getRecyclerPosition() - 1);
                            }
                            dtbRepo.updateGroupsToTrack(groupsToTrack);


                            dtbRepo.delete(groupToRemove);
                            DayDtb currentDay = dtbRepo.getCurrentDay();
                            for (WordsCountersGroup counterGroup : currentDay.getWordsCountersGroupsList()) {
                                if (groupToRemove.getGroupName().equals(counterGroup.getName())) {
                                    dtbRepo.delete(counterGroup);
                                    break;
                                }
                            }
                            dtbRepo.deleteLimiterByName(groupToRemove.getGroupName());

                            bus.post(new GroupRemovedEvent(groupToRemove));
                            if (isWordsToTrackListEmpty())
                                showEmptyErrorMessage();
                        }
                    }
                }).show();
    }

    @Override
    public void deleteButtonOnClick(final int pos, final WordToTrack wordToTrack) {
        makeUndoDeleteSnackbar(
                getString(R.string.word_was_removed, wordToTrack.getWord()),
                new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, @DismissEvent int event) {
                        if (event == DISMISS_EVENT_ACTION) {
                            ((GroupsRecyclerAdapter) groupsRecyclerView.getAdapter())
                                    .getWordsRecyclerAdapterForGroup(wordToTrack.getGroupName())
                                    .insertWordToTrack(pos, wordToTrack);
                        } else {
                            dtbRepo.delete(wordToTrack);
                            DayDtb currentDay = dtbRepo.getCurrentDay();
                            for (WordsCountersGroup group : currentDay.getWordsCountersGroupsList()) {
                                for (WordCounter counter : group.getWordsCountersList())
                                    if (counter.getWord().equals(wordToTrack.getWord())) {
                                        dtbRepo.delete(counter);
                                    }
                            }
                            dtbRepo.deleteLimiterByName(wordToTrack.getWord());

                            bus.post(new WordRemovedEvent(wordToTrack));
                            if (isWordsToTrackListEmpty())
                                showEmptyErrorMessage();
                        }
                    }
                }).show();
    }

    @Override
    public void notificationsButtonOnClick(int groupHolderPos, WordsGroupToTrack groupToTrack) {
        createLimiterDialog(-1, groupHolderPos, groupToTrack.getGroupName());
    }

    @Override
    public void notificationsButtonOnClick(int wordHolderPos, WordToTrack wordToTrack) {
        createLimiterDialog(wordHolderPos, -1, wordToTrack.getWord());
    }

    private void createLimiterDialog(int wordHolderPos, int groupHolderPos, String name) {
        Limiter limiter =
                dtbRepo.getLimiterForName(name, wordHolderPos != -1);
        if (limiter == null)
            LovelyDialogFactory.createNotificationsAddDialog(getContext(), bus, name, wordHolderPos, groupHolderPos)
                    .show();
        else {
            LovelyDialogFactory.createNotificationsEditDialog(getContext(), bus, limiter, groupHolderPos, wordHolderPos)
                    .show();
        }
    }

    private Snackbar makeUndoDeleteSnackbar(String message, Snackbar.Callback callback) {
        final Snackbar snackbar = Snackbar.make(groupsRecyclerView, message, Snackbar.LENGTH_LONG);
        snackbar.setAction(android.R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        snackbar.addCallback(callback);
        return snackbar;
    }

    private List<String> getGroupsNamesList(List<WordsGroupToTrack> groupsList, boolean toLowerCase) {
        List<String> groupsNamesList = new ArrayList<>();
        for (WordsGroupToTrack group : groupsList)
            if (!groupsNamesList.contains(group.getGroupName())) {
                String groupName = group.getGroupName();
                if (toLowerCase)
                    groupName = groupName.toLowerCase();
                groupsNamesList.add(groupName);
            }
        return groupsNamesList;
    }

    private List<String> getLowCaseWordsList(List<WordsGroupToTrack> groupsList) {
        List<String> wordsList = new ArrayList<>();
        for (WordsGroupToTrack group : groupsList)
            for (WordToTrack word : group.getWordsToTrack())
                if (!wordsList.contains(word.getWord()))
                    wordsList.add(word.getWord().toLowerCase());
        return wordsList;
    }

    @Override
    public void onGroupCardExpansion(int position) {
        groupsRecyclerView.smoothScrollToPosition(position);
    }

    @OnClick(R.id.ic_words_tutorial)
    public void onHelpClick() {
        showTutorial(false);
    }

    private void showTutorial(boolean withDelay) {
        //need a slightest delay because recycler view needs time to createForRetrieve items
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (addFAB != null)
                    addFAB.show();
                MaterialShowcaseSequence tutSequence =
                        new MaterialShowcaseSequence(getActivity());
                tutSequence.addSequenceItem(
                        addFAB,
                        getString(R.string.words_tutorial_title1),
                        getString(R.string.words_tutorial_content1),
                        getString(R.string.words_tutorial_dismiss_next));

                WordsRecyclerAdapter.WordsViewHolder wordsHolder =
                        findVisibleWordViewHolderForShowCase();
                if (wordsHolder == null) {
                    Toast.makeText(getContext(),
                            R.string.words_tutorial_toast_cant_see_word, Toast.LENGTH_LONG).show();
                } else {
                    tutSequence.addSequenceItem(
                            wordsHolder.notificationsButton,
                            getString(R.string.words_tutorial_title_2),
                            getString(R.string.words_tutorial_content2),
                            getString(R.string.words_tutorial_dismiss_next));

                    tutSequence.addSequenceItem(new MaterialShowcaseView.Builder(getActivity())
                            .setTarget(wordsHolder.cardRoot)
                            .setTitleText(getString(R.string.words_tutorial_title3))
                            .setContentText(getString(R.string.words_tutorial_content3))
                            .setDismissText(getString(R.string.words_tutorial_dismiss_finish))
                            .withRectangleShape()
                            .setDismissOnTouch(true)
                            .build());
                    tutSequence.start();
                }
            }
        }, withDelay ? 75 : 0);
    }

    //finding word for it's card and icon usage in showcase
    private WordsRecyclerAdapter.WordsViewHolder findVisibleWordViewHolderForShowCase() {
        LinearLayoutManager recyclerLayoutMng =
                (LinearLayoutManager) groupsRecyclerView.getLayoutManager();
        int firstVisiblePos = recyclerLayoutMng
                .findFirstVisibleItemPosition();
        int lastVisiblePos = recyclerLayoutMng
                .findLastVisibleItemPosition();
        if (lastVisiblePos - firstVisiblePos > 1)
            firstVisiblePos++;
        for (int i = firstVisiblePos; i <= lastVisiblePos; i++) {
            GroupsRecyclerAdapter.GroupsViewHolder groupHolder =
                    (GroupsRecyclerAdapter.GroupsViewHolder) groupsRecyclerView.findViewHolderForAdapterPosition(i);
            if (groupHolder == null)
                continue;
            int startPoint = groupHolder.wordsRecyclerView.getChildCount() > 1 ? 1 : 0;
            for (int j = startPoint; j < groupHolder.wordsRecyclerView.getChildCount(); j++) {
                WordsRecyclerAdapter.WordsViewHolder wordsHolder = (WordsRecyclerAdapter.WordsViewHolder)
                        groupHolder.wordsRecyclerView.findViewHolderForAdapterPosition(j);
                if (wordsHolder != null)
                    return wordsHolder;
            }
        }
        return null;
    }
}