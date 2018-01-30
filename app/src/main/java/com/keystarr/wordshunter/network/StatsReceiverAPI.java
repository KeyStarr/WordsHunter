package com.keystarr.wordshunter.network;

import com.keystarr.wordshunter.models.remote.DayStatsList;
import com.keystarr.wordshunter.models.remote.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Bizarre on 29.10.2017.
 */

public interface StatsReceiverAPI {
    @POST("users")
    Call<Long> requestUserdId(@Body User user);

    @POST("daysStats")
    Call<Void> sendDayStats(@Body DayStatsList statsList);
}
