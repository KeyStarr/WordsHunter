package com.keystarr.wordshunter.models.local;

import android.support.annotation.NonNull;

import java.util.Random;

/**
 * Created by Bizarre on 28.06.2017.
 */
public class WordCounter implements Comparable<WordCounter> {

    private long groupID;
    private String word;
    private int count;
    private boolean isTracked;

    public WordCounter(String word, int count, boolean isTracked) {
        //SHOULD ONLY BE USED WHEN LATER INSERT IN DTB IS GUARANTEED
        //WITH RETRIEVAL OF ROW'S ID AND SETTING IT TO FIELD GROUP_ID
        this.word = word;
        this.count = count;
        this.isTracked = isTracked;
    }

    public WordCounter(long groupID, String word, int count, boolean isTracked) {
        this(word, count, isTracked);
        this.groupID = groupID;
    }

    public static WordCounter create(WordToTrack wordToTrack) {
        //SHOULD ONLY BE USED WHEN LATER INSERT IN DTB IS GUARANTEED
        //WITH RETRIEVAL OF ROW'S ID AND SETTING IT TO FIELD GROUP_ID
        return new WordCounter(wordToTrack.getWord(), 0, wordToTrack.isTracked());
    }

    public static WordCounter createForDebug(WordToTrack wordToTrack) {
        //SHOULD ONLY BE USED WHEN LATER INSERT IN DTB IS GUARANTEED
        //WITH RETRIEVAL OF ROW'S ID AND SETTING IT TO FIELD GROUP_ID
        Random rand = new Random();
        return new WordCounter(wordToTrack.getWord(), Math.abs(rand.nextInt() % 10), wordToTrack.isTracked());
    }

    public static WordCounter create(long groupID, WordToTrack wordToTrack) {
        return new WordCounter(groupID, wordToTrack.getWord(), 0, wordToTrack.isTracked());
    }

    public long getGroupID() {
        return groupID;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public void increaseCount(int value) {
        count += value;
    }

    public void incrementCount() {
        increaseCount(1);
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public boolean isTracked() {
        return isTracked;
    }

    public void setTracked(boolean tracked) {
        isTracked = tracked;
    }

    @Override
    public int compareTo(@NonNull WordCounter o) {
        if (count > o.getCount())
            return -1;
        else if (count < o.getCount())
            return 1;
        return 0;
    }
}
