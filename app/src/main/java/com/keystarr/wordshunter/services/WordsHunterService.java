package com.keystarr.wordshunter.services;

import android.accessibilityservice.AccessibilityService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.accessibility.AccessibilityEvent;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.app.App;
import com.keystarr.wordshunter.models.events.GroupRemovedEvent;
import com.keystarr.wordshunter.models.events.WordAddedEvent;
import com.keystarr.wordshunter.models.events.WordBackToTrackingEvent;
import com.keystarr.wordshunter.models.events.WordRemovedEvent;
import com.keystarr.wordshunter.models.events.WordStoppedFromTrackingEvent;
import com.keystarr.wordshunter.models.events.WordToTrackGroupChangedEvent;
import com.keystarr.wordshunter.models.local.DayDtb;
import com.keystarr.wordshunter.models.local.Letter;
import com.keystarr.wordshunter.models.local.Limiter;
import com.keystarr.wordshunter.models.local.WordCounter;
import com.keystarr.wordshunter.models.local.WordToTrack;
import com.keystarr.wordshunter.models.local.WordsCountersGroup;
import com.keystarr.wordshunter.models.local.WordsGroupToTrack;
import com.keystarr.wordshunter.repository.DatabaseRepository;
import com.keystarr.wordshunter.repository.PreferencesRepository;
import com.keystarr.wordshunter.ui.MainActivity;
import com.keystarr.wordshunter.utils.InitialDataUtils;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import static android.support.v4.app.NotificationCompat.DEFAULT_ALL;
import static com.keystarr.wordshunter.utils.DateUtils.getCurrentDayDateInMillis;

public class WordsHunterService extends AccessibilityService {
    //TODO: potential bug: if day was reverted to day that was in stats
    //and there were some wordCounters on track that are not present or disabled in current list
    //they are still gonna be counted


    public static final int NOTIFICATION_ID_LIMIT_REACHED = 111;

    @Inject
    DatabaseRepository dtbRepo;
    @Inject
    Bus bus;
    @Inject
    PreferencesRepository prefsRepo;

    private DayDtb dayDtb;
    private List<WordsCountersGroup> bufferedWordsCountersGroups = new ArrayList<>();
    private long currentDate;
    private String bufferedText;

    @Override
    public void onServiceConnected() {
        App.getApp(this).getAppComponent().inject(this);
        try {
            bus.register(this);
        } catch (IllegalArgumentException ex) {
            //means bus is already registered which is fine
        }
        loadData();
        currentDate = getCurrentDayDateInMillis();
        initializeWordsGroupCountersList(bufferedWordsCountersGroups);
    }

    private void loadData() {
        loadInitialDataIfFirstLaunch();
        loadCurrentDay();
    }

    private void loadInitialDataIfFirstLaunch() {
        if (prefsRepo.isKeyServiceFirstLaunch()) {
            List<WordsGroupToTrack> wordsGroupsToTrackList =
                    InitialDataUtils.initializeWordsGroupsToTrackListFromDefaults(getApplication());
            dtbRepo.insert(wordsGroupsToTrackList);
            List<Limiter> limitersList = InitialDataUtils.initializeLimitersFromDefaults();
            dtbRepo.insertLimiters(limitersList);
            prefsRepo.setKeyServiceIsFirstLaunch(false);
        }
    }

    private void loadCurrentDay() {
        dayDtb = dtbRepo.getCurrentDay();
        if (dayDtb == null) {
            dayDtb = DayDtb.createForCurrentDate(
                    dtbRepo.getWordsGroupsToTrack(false, false, false));
            dtbRepo.insert(dayDtb);
        }
    }

    @Subscribe
    public void wordRemovedEvent(WordRemovedEvent event) {
        List<WordsCountersGroup> countersGroupsList =
                dayDtb.getWordsCountersGroupsList();
        for (int i = 0; i < countersGroupsList.size(); i++) {
            if (event.getRemovedWord().getGroupName().equals(countersGroupsList.get(i).getName())) {
                List<WordCounter> countersList = countersGroupsList.get(i).getWordsCountersList();
                for (int j = 0; j < countersList.size(); j++)
                    if (event.getRemovedWord().getWord().equals(countersList.get(j).getWord())) {
                        countersList.remove(j);
                        bufferedWordsCountersGroups.get(i).getWordsCountersList().remove(j);
                    }
            }
        }
    }

    @Subscribe
    public void groupRemovedEvent(GroupRemovedEvent event) {
        List<WordsCountersGroup> countersGroupsList =
                dayDtb.getWordsCountersGroupsList();
        for (int i = 0; i < countersGroupsList.size(); i++) {
            if (event.getRemovedGroup().getGroupName().equals(countersGroupsList.get(i).getName())) {
                countersGroupsList.remove(i);
                bufferedWordsCountersGroups.remove(i);
            }
        }
    }

    @Subscribe
    public void wordRemovedFromTrackingEvent(WordStoppedFromTrackingEvent event) {
        for (WordsCountersGroup group : dayDtb.getWordsCountersGroupsList()) {
            for (WordCounter wordCounter : group.getWordsCountersList())
                if (wordCounter.getWord().equals(event.getWord().getWord())) {
                    wordCounter.setTracked(false);
                    return;
                }
        }
    }

    @Subscribe
    public void wordBackToTrackingEvent(WordBackToTrackingEvent event) {
        for (WordsCountersGroup countersGroup : dayDtb.getWordsCountersGroupsList())
            for (WordCounter counter : countersGroup.getWordsCountersList())
                if (event.getWordToTrack().getWord().equals(counter.getWord())) {
                    counter.setTracked(true);
                    return;
                }
    }

    @Subscribe
    public void onWordAdded(WordAddedEvent event) {
        WordToTrack wordToTrack = event.getAddedWordToTrack();
        WordCounter newCounter = WordCounter.create(wordToTrack);
        List<WordsCountersGroup> countersGroupsList = dayDtb.getWordsCountersGroupsList();
        for (int i = 0; i < countersGroupsList.size(); i++) {
            WordsCountersGroup group = countersGroupsList.get(i);
            if (group.getName().equals(wordToTrack.getGroupName())) {
                dtbRepo.insertAndUpdateID(group.get_id(), newCounter);
                group.getWordsCountersList().add(0, newCounter);
                bufferedWordsCountersGroups.get(i).getWordsCountersList().add(0,
                        WordCounter.create(wordToTrack));
                return;
            }
        }
        //group was empty at day's start and not present in current counters
        //so need also to createForRetrieve word's group
        WordsCountersGroup wordsCountersGroup =
                WordsCountersGroup.create(wordToTrack.getGroupName());
        wordsCountersGroup.getWordsCountersList().add(newCounter);
        dtbRepo.insertAndUpdateID(dayDtb.getDate(), wordsCountersGroup);
        dayDtb.getWordsCountersGroupsList().add(0, wordsCountersGroup);
        bufferedWordsCountersGroups.add(0,
                WordsCountersGroup.create(wordsCountersGroup));
    }

    @Subscribe
    public void onWordRecyclerPosChanged(WordToTrackGroupChangedEvent event) {
        changeWordCounterGroupOnEvent(event, dayDtb.getWordsCountersGroupsList());
        changeWordCounterGroupOnEvent(event, bufferedWordsCountersGroups);
    }

    private void changeWordCounterGroupOnEvent(WordToTrackGroupChangedEvent event,
                                               List<WordsCountersGroup> countersGroups) {
        long oldGroupId = -10;
        WordCounter buffer = null;
        //remove word from an old group
        for (WordsCountersGroup countersGroup : countersGroups) {
            if (event.getOldGroupName().equals(countersGroup.getName())) {
                for (WordCounter counter : countersGroup.getWordsCountersList()) {
                    if (event.getChangedWordToTrack().getWord().equals(counter.getWord())) {
                        buffer = counter;
                        countersGroup.getWordsCountersList().remove(counter);
                        oldGroupId = countersGroup.get_id();
                        break;
                    }
                }
                break;
            }
        }
        //insert word to a new group
        if (buffer != null) {
            boolean groupFound = false;
            long newGroupId = -10;
            for (WordsCountersGroup countersGroup : countersGroups)
                if (event.getChangedWordToTrack().getGroupName().equals(countersGroup.getName())) {
                    newGroupId = countersGroup.get_id();
                    countersGroup.getWordsCountersList().add(buffer);
                    groupFound = true;
                    break;
                }
            if (!groupFound) {
                //means group was empty at day's start and not present in counters
                WordsCountersGroup wordsCountersGroup =
                        WordsCountersGroup.create(event.getChangedWordToTrack().getGroupName());
                wordsCountersGroup.getWordsCountersList().add(buffer);
                countersGroups.add(wordsCountersGroup);
                dtbRepo.insertAndUpdateIDOnlyGroup(dayDtb.getDate(), wordsCountersGroup);
                newGroupId = wordsCountersGroup.get_id();
            }
            buffer.setGroupID(newGroupId);
            dtbRepo.update(buffer, oldGroupId);
        }
    }

    private void initializeWordsGroupCountersList(List<WordsCountersGroup> countersGroupList) {
        for (WordsCountersGroup group : dayDtb.getWordsCountersGroupsList()) {
            countersGroupList.add(WordsCountersGroup.create(group));
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!event.getText().isEmpty()) {
            String bText = null;
            if (event.getBeforeText() != null) //can be null
                bText = event.getBeforeText().toString();
            String mText = event.getText().get(0).toString();
            initializeDayIfDateChangedInRuntime();
            analyzeTextForOccurrences(mText);

            //approximately catching the sending of the previous message
            //and the starting of the new one to count the words
            if ("".equals(bText) && bufferedText != null) {
                bufferedText += ' ';//for getting the last word
                countAmountOfWordsInMessage(bufferedText);
                countTheLastWord(bufferedText);//regular scanning does not count the last word
                //because it can be a part of a not finished word
                //so only when the message was sent we can safely check it
                bufferedText = "";
                dtbRepo.update(dayDtb);
            }

            if (!"".equals(mText)) {
                bufferedText = mText;
            }
        }
    }

    private void countAmountOfWordsInMessage(String message) {
        String word = "";
        for (int i = 0; i < message.length(); i++) {
            char c = message.charAt(i);
            if (c != ' ' && c != '\n') {
                word += c;
            } else if (!"".equals(word))
                dayDtb.incrementWordsTypedCounter();
        }
    }

    private void countTheLastWord(String message){
        StringBuilder word = new StringBuilder();
        char c = 0;
        for (int i = message.length() - 2; c != ' '; i--){
            c = message.charAt(i);
            word.append(c);
        }
        for (WordsCountersGroup countersGroup : dayDtb.getWordsCountersGroupsList())
            if (findAndCountOccurrenceOfWordInGroup(countersGroup, word.toString()))
                return;
    }

    private void initializeDayIfDateChangedInRuntime() {
        if (currentDate != getCurrentDayDateInMillis()) {
            if (dtbRepo.getCurrentDay() == null) {
                dayDtb = DayDtb.createForCurrentDate(
                        dtbRepo.getWordsGroupsToTrack(false, false, false));
                dtbRepo.insert(dayDtb);
            } else
                dayDtb = dtbRepo.getCurrentDay();
            currentDate = getCurrentDayDateInMillis();
        }
    }

    //Edit consciously and carefully as
    //bufferedWordsCountersGroups, currentWordsCountersGroup,
    //dayDtb.getWordsCountersGroupsList() do depend on their child positions critically
    //It means that the counters with equal names must stand on the same positions across these lists
    //This was made in value of performance increasing

    private void analyzeTextForOccurrences(String text) {
        List<WordsCountersGroup> currentWordsCountersGroups = countOccurrences(text);

        //this situation is really rare to happen (in fact it has been registered only once)
        //but if it happens, then the whole system is compromised and we are obliged to fix it
        if (currentWordsCountersGroups.size() != bufferedWordsCountersGroups.size()) {
            bufferedWordsCountersGroups = currentWordsCountersGroups;
            Answers.getInstance().logCustom(
                    new CustomEvent("WordsHunterService => analyze text => " +
                            "buffered and current groups list size mismatch")
                            .putCustomAttribute("BufferedWordCountersList size", currentWordsCountersGroups.size())
                            .putCustomAttribute("CurrentWordCountersList size", bufferedWordsCountersGroups.size())
                            .putCustomAttribute("Day wordGroupsList size", dayDtb.getWordsCountersGroupsList().size()));
            return;
        }

        for (int i = 0; i < bufferedWordsCountersGroups.size(); i++) {
            List<WordCounter> bufferedWordsCountersList =
                    bufferedWordsCountersGroups.get(i).getWordsCountersList();
            List<WordCounter> currentWordsCountersList =
                    currentWordsCountersGroups.get(i).getWordsCountersList();

            //this happens more often than the previous one (and for the reasons unknown)
            //the meaning and the aids to fix it are the same as described above
            if (currentWordsCountersList.size() != bufferedWordsCountersList.size()) {
                bufferedWordsCountersGroups = currentWordsCountersGroups;
                Answers.getInstance().logCustom(
                        new CustomEvent("WordsHunterService => analyze text => " +
                                "buffered and current counters list size mismatch")
                                .putCustomAttribute("BufferedWordCountersList size", bufferedWordsCountersList.size())
                                .putCustomAttribute("CurrentWordCountersList size", currentWordsCountersList.size())
                                .putCustomAttribute("Day wordCounters size",
                                        dayDtb.getWordsCountersGroupsList().get(i).getWordsCountersList().size()));
                return;
            }

            boolean atLeastOneCounterInGroupIncreased = false;
            for (int j = 0; j < bufferedWordsCountersList.size(); j++) {
                int bufferedCount = bufferedWordsCountersList.get(j).getCount();
                int currentCount = currentWordsCountersList.get(j).getCount();
                int increase = currentCount - bufferedCount;
                if (increase > 0) {
                    WordCounter changedCounter =
                            dayDtb.getWordsCountersGroupsList().get(i)
                                    .getWordsCountersList().get(j);
                    changedCounter.increaseCount(increase);
                    atLeastOneCounterInGroupIncreased = true;
                    checkIfLimitForWordIsExceededAndSendNotificationIfTrue(changedCounter);
                }
            }
            if (atLeastOneCounterInGroupIncreased) {
                checkIfLimitForGroupIsExceededAndSendNotificationIfTrue(
                        dayDtb.getWordsCountersGroupsList().get(i));
            }
        }
        dtbRepo.update(dayDtb);
        bufferedWordsCountersGroups = new ArrayList<>(currentWordsCountersGroups);
    }

    private List<WordsCountersGroup> countOccurrences(String text) {
        List<WordsCountersGroup> currentWordsCountersGroup = new ArrayList<>();
        initializeWordsGroupCountersList(currentWordsCountersGroup);
        String temporary = "";
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c != ' ' && c != '\n') {
                temporary += c;
            } else {
                if (!"".equals(temporary)) {
                    for (WordsCountersGroup countersGroup : currentWordsCountersGroup) {
                        boolean counted = findAndCountOccurrenceOfWordInGroup(countersGroup, temporary);
                        if (counted)
                            break;
                    }
                    temporary = "";
                }
            }
        }
        return currentWordsCountersGroup;
    }

    /**
     * Counts wordCounter of the passed @word if it is on the track list.
     * HashSet thing used to count words if they were written with
     * some characters repeated multi-times, ex. lol, looool, llllol, lollll would be counted as lol.
     *
     * @param group in which to search for words that being tracked
     * @param word  to search for
     * @return true, if word found in a group
     */
    private boolean findAndCountOccurrenceOfWordInGroup(WordsCountersGroup group, String word) {
        HashSet<Letter> wordCharSet = stringToLettersHashSet(word.toLowerCase());
        for (WordCounter wordCounter : group.getWordsCountersList()) {
            HashSet<Letter> r = stringToLettersHashSet(wordCounter.getWord().toLowerCase());
            if (wordCounter.isTracked()
                    && wordCharSet.equals(r)) {
                wordCounter.incrementCount();
                return true;
            }
        }
        return false;
    }

    private HashSet<Letter> stringToLettersHashSet(String word) {
        char[] wordChrs = word.toCharArray();
        HashSet<Letter> letters = new HashSet<>();
        int pos = 0;
        letters.add(new Letter(wordChrs[0], pos));
        for (int i = 1; i < wordChrs.length; i++) {
            if (wordChrs[i] != wordChrs[i - 1])
                pos++;
            letters.add(new Letter(wordChrs[i], pos));
        }
        return letters;
    }

    private void checkIfLimitForWordIsExceededAndSendNotificationIfTrue(WordCounter wordCounter) {
        Limiter limiter = dtbRepo.getLimiterForName(wordCounter.getWord(), true);
        if (limiter != null && wordCounter.getCount() >= limiter.getLimit()) {
            sendLimiterReachedNotification(wordCounter.getWord(), wordCounter.getCount(), false);
        }
    }

    private void checkIfLimitForGroupIsExceededAndSendNotificationIfTrue(WordsCountersGroup countersGroup) {
        Limiter limiter = dtbRepo.getLimiterForName(countersGroup.getName(), false);
        int totalGroupCount = countersGroup.getOverallGroupCounter();
        if (limiter != null && totalGroupCount >= limiter.getLimit()) {
            sendLimiterReachedNotification(countersGroup.getName(), totalGroupCount, true);
        }
    }

    private void sendLimiterReachedNotification(String name, int count, boolean forGroup) {
        Context context = getApplicationContext();
        Intent enableIntent = new Intent(context, MainActivity.class);
        PendingIntent descriptionPendingIntent = PendingIntent.getActivity(
                context, 0, enableIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_footprints_large_light_gray)
                .setContentTitle(context.getString(R.string.limit_exceeded_notif_title, name))
                .setContentText(context.getString(forGroup ? R.string.limit_exceeded_notif_description_group
                        : R.string.limit_exceeded_notif_description_word, count))
                .setContentIntent(descriptionPendingIntent)
                .setDefaults(DEFAULT_ALL)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setWhen(0);
        NotificationManagerCompat ntfMng = NotificationManagerCompat.from(context);
        ntfMng.notify(NOTIFICATION_ID_LIMIT_REACHED, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    public void onInterrupt() {
    }
}
