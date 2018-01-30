package com.keystarr.wordshunter.models.local;

import android.support.annotation.NonNull;

import com.keystarr.wordshunter.ui.words.drag_and_drop.Draggable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cyril on 09.08.2017.
 */

public class WordsGroupToTrack implements Draggable<WordsGroupToTrack> {
    private String groupName;
    private List<WordToTrack> wordsToTrack;
    private int recyclerPosition;

    public WordsGroupToTrack(String groupName, int recyclerPosition) {
        this.groupName = groupName;
        wordsToTrack = new ArrayList<>();
        this.recyclerPosition = recyclerPosition;
    }

    public WordsGroupToTrack(String groupName, List<WordToTrack> wordsToTrack, int recyclerPosition) {
        this(groupName, recyclerPosition);
        this.wordsToTrack = wordsToTrack;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<WordToTrack> getWordsToTrack() {
        return wordsToTrack;
    }

    public void setWordsToTrack(List<WordToTrack> wordsToTrack) {
        this.wordsToTrack = wordsToTrack;
    }

    public int getRecyclerPosition() {
        return recyclerPosition;
    }

    public void setRecyclerPosition(int recyclerPosition) {
        this.recyclerPosition = recyclerPosition;
    }

    @Override
    public int comparePositions(@NonNull WordsGroupToTrack o) {
        return recyclerPosition > o.getRecyclerPosition() ? 1 :
                recyclerPosition < o.getRecyclerPosition() ? -1 : 0;
    }
}
