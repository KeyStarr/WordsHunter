package com.keystarr.wordshunter.models.events;

import com.keystarr.wordshunter.models.local.WordToTrack;

/**
 * Created by Cyril on 27.08.2017.
 */

public class WordBackToTrackingEvent {
    private WordToTrack wordToTrack;

    public WordBackToTrackingEvent(WordToTrack removedWord) {
        this.wordToTrack = removedWord;
    }

    public WordToTrack getWordToTrack() {
        return wordToTrack;
    }

    public void setWordToTrack(WordToTrack wordToTrack) {
        this.wordToTrack = wordToTrack;
    }
}
