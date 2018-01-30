package com.keystarr.wordshunter.models.events;

import com.keystarr.wordshunter.models.local.WordToTrack;

/**
 * Created by Bizarre on 25.09.2017.
 */

public class WordRemovedEvent {
    private WordToTrack removedWord;

    public WordRemovedEvent(WordToTrack removedWord) {
        this.removedWord = removedWord;
    }

    public WordToTrack getRemovedWord() {
        return removedWord;
    }

    public void setRemovedWord(WordToTrack removedWord) {
        this.removedWord = removedWord;
    }
}
