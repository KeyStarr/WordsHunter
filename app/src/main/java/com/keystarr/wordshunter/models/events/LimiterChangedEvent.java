package com.keystarr.wordshunter.models.events;

import com.keystarr.wordshunter.models.local.Limiter;

/**
 * Created by Bizarre on 26.09.2017.
 */

public class LimiterChangedEvent {
    private Limiter changedLimiter;

    public LimiterChangedEvent(Limiter changedLimiter) {
        this.changedLimiter = changedLimiter;
    }

    public Limiter getChangedLimiter() {
        return changedLimiter;
    }

    public void setChangedLimiter(Limiter changedLimiter) {
        this.changedLimiter = changedLimiter;
    }
}
