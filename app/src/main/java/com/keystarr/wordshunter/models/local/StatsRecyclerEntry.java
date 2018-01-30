package com.keystarr.wordshunter.models.local;

import android.support.annotation.NonNull;

/**
 * Created by Cyril on 07.07.2017.
 */

public class StatsRecyclerEntry implements Comparable<StatsRecyclerEntry> {
    private int number;
    private String name;
    private int count;
    private float relativeCount;

    public StatsRecyclerEntry(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public float getRelativeCount() {
        return relativeCount;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setRelativeCount(float relativeCount) {
        this.relativeCount = relativeCount;
    }

    public void increaseCount(int dCount) {
        this.count += dCount;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Override
    public int compareTo(@NonNull StatsRecyclerEntry o) {
        if (getCount() > o.getCount())
            return -1;
        else if (getCount() < o.getCount())
            return 1;
        else
            return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatsRecyclerEntry that = (StatsRecyclerEntry) o;

        return name.equals(that.name);

    }
}
