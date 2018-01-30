package com.keystarr.wordshunter.models.events;

import com.keystarr.wordshunter.models.local.WordToTrack;

/**
 * Created by Bizarre on 11.09.2017.
 */

public class WordToTrackGroupChangedEvent {
    private String oldGroupName;
    private WordToTrack changedWordToTrack;

    public WordToTrackGroupChangedEvent(WordToTrack changedWordToTrack, String oldroupName) {
        this.changedWordToTrack = changedWordToTrack;
        this.oldGroupName = oldroupName;
    }

    public WordToTrack getChangedWordToTrack() {
        return changedWordToTrack;
    }

    public void setChangedWordToTrack(WordToTrack changedWordToTrack) {
        this.changedWordToTrack = changedWordToTrack;
    }

    public String getOldGroupName() {
        return oldGroupName;
    }

    public void setOldGroupName(String oldGroupName) {
        this.oldGroupName = oldGroupName;
    }

    public boolean hasAlsoChangedGroup() {
        return oldGroupName != null;
    }
}
