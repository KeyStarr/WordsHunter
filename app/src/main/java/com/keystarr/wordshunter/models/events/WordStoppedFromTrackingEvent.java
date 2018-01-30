package com.keystarr.wordshunter.models.events;

import com.keystarr.wordshunter.models.local.WordToTrack;

/**
 * Created by Cyril on 26.08.2017.
 */

public class WordStoppedFromTrackingEvent {
    private WordToTrack word;

    public WordStoppedFromTrackingEvent(WordToTrack word) {
        this.word = word;
    }

    public WordToTrack getWord() {
        return word;
    }

    public void setWord(WordToTrack word) {
        this.word = word;
    }
}
