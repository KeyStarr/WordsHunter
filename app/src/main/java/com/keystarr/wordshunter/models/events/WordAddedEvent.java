package com.keystarr.wordshunter.models.events;

import com.keystarr.wordshunter.models.local.WordToTrack;

/**
 * Created by Cyril on 30.08.2017.
 */

public class WordAddedEvent {
    private WordToTrack addedWordToTrack;

    public WordAddedEvent(WordToTrack addedWordToTrack) {
        this.addedWordToTrack = addedWordToTrack;
    }

    public WordToTrack getAddedWordToTrack() {
        return addedWordToTrack;
    }

    public void setAddedWordToTrack(WordToTrack addedWordToTrack) {
        this.addedWordToTrack = addedWordToTrack;
    }
}
