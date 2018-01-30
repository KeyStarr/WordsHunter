package com.keystarr.wordshunter.models.events;

import com.keystarr.wordshunter.models.local.WordsGroupToTrack;

/**
 * Created by Bizarre on 25.09.2017.
 */

public class GroupRemovedEvent {
    private WordsGroupToTrack removedGroup;

    public GroupRemovedEvent(WordsGroupToTrack removedGroup) {
        this.removedGroup = removedGroup;
    }

    public WordsGroupToTrack getRemovedGroup() {
        return removedGroup;
    }

    public void setRemovedGroup(WordsGroupToTrack removedGroup) {
        this.removedGroup = removedGroup;
    }
}
