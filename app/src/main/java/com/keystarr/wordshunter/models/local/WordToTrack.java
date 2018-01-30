package com.keystarr.wordshunter.models.local;

import android.support.annotation.NonNull;

import com.keystarr.wordshunter.ui.words.drag_and_drop.Draggable;

/**
 * Created by Cyril on 27.08.2017.
 */

public class WordToTrack implements Draggable<WordToTrack> {
    private String word;
    private String groupName;
    private boolean isTracked;
    private int recyclerPosition;

    public WordToTrack(String word, String groupName, boolean isTracked, int recyclerPosition) {
        this.word = word;
        this.groupName = groupName;
        this.isTracked = isTracked;
        this.recyclerPosition = recyclerPosition;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isTracked() {
        return isTracked;
    }

    public void setTracked(boolean tracked) {
        isTracked = tracked;
    }

    public int getRecyclerPosition() {
        return recyclerPosition;
    }

    public void setRecyclerPosition(int recyclerPosition) {
        this.recyclerPosition = recyclerPosition;
    }

    @Override
    public int comparePositions(@NonNull WordToTrack o) {
        return recyclerPosition > o.getRecyclerPosition() ? 1 :
                recyclerPosition < o.getRecyclerPosition() ? -1 : 0;
    }
}
