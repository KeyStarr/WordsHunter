package com.keystarr.wordshunter.models.events;

import com.keystarr.wordshunter.models.local.WordsGroupToTrack;

/**
 * Created by Cyril on 30.08.2017.
 */

public class GroupAddedEvent {
    private WordsGroupToTrack addedGroupToTrack;

    public GroupAddedEvent(WordsGroupToTrack addedGroupToTrack) {
        this.addedGroupToTrack = addedGroupToTrack;
    }

    public WordsGroupToTrack getAddedGroupToTrack() {
        return addedGroupToTrack;
    }

    public void setAddedGroupToTrack(WordsGroupToTrack addedGroupToTrack) {
        this.addedGroupToTrack = addedGroupToTrack;
    }
}
