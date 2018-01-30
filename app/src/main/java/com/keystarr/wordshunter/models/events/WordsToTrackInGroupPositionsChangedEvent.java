package com.keystarr.wordshunter.models.events;

import com.keystarr.wordshunter.models.local.WordToTrack;

import java.util.List;

/**
 * Created by Bizarre on 17.09.2017.
 */

public class WordsToTrackInGroupPositionsChangedEvent {
    private List<WordToTrack> wordsToTrack;

    public WordsToTrackInGroupPositionsChangedEvent(List<WordToTrack> wordsToTrack) {
        this.wordsToTrack = wordsToTrack;
    }

    public List<WordToTrack> getWordsToTrack() {
        return wordsToTrack;
    }

    public void setWordsToTrack(List<WordToTrack> wordsToTrack) {
        this.wordsToTrack = wordsToTrack;
    }
}
