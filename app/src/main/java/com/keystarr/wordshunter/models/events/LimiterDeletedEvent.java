package com.keystarr.wordshunter.models.events;

import com.keystarr.wordshunter.models.local.Limiter;

/**
 * Created by Bizarre on 26.09.2017.
 */

public class LimiterDeletedEvent implements ILimiterEvent {
    private Limiter limiter;
    private int wordHolderPos;
    private int groupHolderPos;

    public LimiterDeletedEvent(Limiter limiter, int wordHolderPos, int groupHolderPos) {
        this.limiter = limiter;
        this.wordHolderPos = wordHolderPos;
        this.groupHolderPos = groupHolderPos;
    }

    public Limiter getLimiter() {
        return limiter;
    }

    public void setLimiter(Limiter deletedLimiter) {
        this.limiter = deletedLimiter;
    }

    public int getWordHolderPos() {
        return wordHolderPos;
    }

    public void setWordHolderPos(int wordHolderPos) {
        this.wordHolderPos = wordHolderPos;
    }

    public int getGroupHolderPos() {
        return groupHolderPos;
    }

    public void setGroupHolderPos(int groupHolderPos) {
        this.groupHolderPos = groupHolderPos;
    }
}
