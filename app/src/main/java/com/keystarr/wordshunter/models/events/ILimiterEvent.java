package com.keystarr.wordshunter.models.events;

import com.keystarr.wordshunter.models.local.Limiter;

/**
 * Created by Bizarre on 05.10.2017.
 */

public interface ILimiterEvent {
    Limiter getLimiter();

    int getWordHolderPos();

    int getGroupHolderPos();
}
