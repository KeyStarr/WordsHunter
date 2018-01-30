package com.keystarr.wordshunter.models.local;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cyril on 09.08.2017.
 */

public class WordsCountersGroup {
    private long _id;
    private String name;
    private List<WordCounter> wordsCountersList;


    private WordsCountersGroup(String name, List<WordCounter> wordsCountersList) {
        this.name = name;
        this.wordsCountersList = wordsCountersList;
    }

    private WordsCountersGroup(long groupId, String name, List<WordCounter> wordsCountersList) {
        this(name, wordsCountersList);
        _id = groupId;
    }

    public static WordsCountersGroup create(WordsGroupToTrack groupToTrack) {
        WordsCountersGroup group = new WordsCountersGroup(
                groupToTrack.getGroupName(), new ArrayList<WordCounter>());
        for (WordToTrack wordToTrack : groupToTrack.getWordsToTrack()) {
            group.getWordsCountersList().add(WordCounter.create(wordToTrack));
        }
        return group;
    }

    public static WordsCountersGroup createForDebug(WordsGroupToTrack groupToTrack) {
        WordsCountersGroup group = new WordsCountersGroup(
                groupToTrack.getGroupName(), new ArrayList<WordCounter>());
        for (WordToTrack wordToTrack : groupToTrack.getWordsToTrack()) {
            group.getWordsCountersList().add(WordCounter.createForDebug(wordToTrack));
        }
        return group;
    }


    public static WordsCountersGroup create(String name) {
        return new WordsCountersGroup(name, new ArrayList<WordCounter>());
    }

    public static WordsCountersGroup create(long id, String name) {
        return new WordsCountersGroup(id, name, new ArrayList<WordCounter>());
    }

    public static WordsCountersGroup create(WordsCountersGroup wordsCountersGroup) {
        List<WordCounter> wordCountersList = new ArrayList<>();
        for (WordCounter counter : wordsCountersGroup.getWordsCountersList()) {
            wordCountersList.add(new WordCounter(
                    wordsCountersGroup.get_id(), counter.getWord(), 0, counter.isTracked()));
        }
        return new WordsCountersGroup(
                wordsCountersGroup.getName(),
                wordCountersList);
    }

    public int getOverallGroupCounter() {
        int count = 0;
        for (WordCounter counter : wordsCountersList)
            count += counter.getCount();
        return count;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WordCounter> getWordsCountersList() {
        return wordsCountersList;
    }

    public void setWordsCountersList(List<WordCounter> wordsCountersList) {
        this.wordsCountersList = wordsCountersList;
    }
}
