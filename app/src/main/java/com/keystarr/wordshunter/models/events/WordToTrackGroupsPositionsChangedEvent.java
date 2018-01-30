package com.keystarr.wordshunter.models.events;

import com.keystarr.wordshunter.models.local.WordsGroupToTrack;

import java.util.List;

/**
 * Created by Bizarre on 18.09.2017.
 */

public class WordToTrackGroupsPositionsChangedEvent {
    private List<WordsGroupToTrack> groupsToTrack;


    public WordToTrackGroupsPositionsChangedEvent(List<WordsGroupToTrack> groupsToTrack) {
        this.groupsToTrack = groupsToTrack;
    }

    public List<WordsGroupToTrack> getGroupsToTrack() {
        return groupsToTrack;
    }

    public void setGroupsToTrack(List<WordsGroupToTrack> groupsToTrack) {
        this.groupsToTrack = groupsToTrack;
    }
}
