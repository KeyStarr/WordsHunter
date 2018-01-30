package com.keystarr.wordshunter.models.remote;

import java.util.List;

/**
 * Created by Bizarre on 29.10.2017.
 */

public class DayStatsList {
    private long userId;
    private List<DayStats> statsList;

    public DayStatsList(long userId, List<DayStats> statsList) {
        this.userId = userId;
        this.statsList = statsList;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public List<DayStats> getStatsList() {
        return statsList;
    }

    public void setStatsList(List<DayStats> statsList) {
        this.statsList = statsList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayStatsList that = (DayStatsList) o;

        if (userId != that.userId) return false;
        return statsList != null ? statsList.equals(that.statsList) : that.statsList == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (userId ^ (userId >>> 32));
        result = 31 * result + (statsList != null ? statsList.hashCode() : 0);
        return result;
    }
}
