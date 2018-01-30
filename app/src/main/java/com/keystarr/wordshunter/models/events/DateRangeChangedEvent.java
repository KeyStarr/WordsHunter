package com.keystarr.wordshunter.models.events;

/**
 * Created by Cyril on 12.08.2017.
 */

public class DateRangeChangedEvent {

    private int mode;

    public DateRangeChangedEvent() {
    }

    public DateRangeChangedEvent(int mode) {
        this.mode = mode;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }
}
