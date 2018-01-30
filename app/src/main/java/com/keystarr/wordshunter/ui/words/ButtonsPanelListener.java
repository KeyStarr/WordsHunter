package com.keystarr.wordshunter.ui.words;

import com.keystarr.wordshunter.models.local.WordToTrack;
import com.keystarr.wordshunter.models.local.WordsGroupToTrack;

/**
 * Created by Cyril on 24.08.2017.
 */

interface ButtonsPanelListener {

    void trackingButtonOnClick(WordToTrack wordToTrack);

    void deleteButtonOnClick(int pos, WordsGroupToTrack groupToTrack);

    void deleteButtonOnClick(int pos, WordToTrack wordToTrack);

    void notificationsButtonOnClick(int groupHolderPos, WordsGroupToTrack groupToTrack);

    void notificationsButtonOnClick(int wordHolderPos, WordToTrack wordToTrack);
}
