package com.keystarr.wordshunter.models.remote;

import java.util.List;

/**
 * Created by Bizarre on 29.10.2017.
 */

public class DayStats {
    private long dayDate;
    private int wordsTypedCounter;
    private List<WordCounterRemote> wordCountersList;

    public DayStats(long dayDate, int wordsTypedCounter,
                    List<WordCounterRemote> wordCountersList) {
        this.dayDate = dayDate;
        this.wordsTypedCounter = wordsTypedCounter;
        this.wordCountersList = wordCountersList;
    }

    public long getDayDate() {
        return dayDate;
    }

    public void setDayDate(long dayDate) {
        this.dayDate = dayDate;
    }

    public List<WordCounterRemote> getWordCountersList() {
        return wordCountersList;
    }

    public void setWordCountersList(List<WordCounterRemote> wordCountersList) {
        this.wordCountersList = wordCountersList;
    }

    public int getWordsTypedCounter() {
        return wordsTypedCounter;
    }

    public void setWordsTypedCounter(int wordsTypedCounter) {
        this.wordsTypedCounter = wordsTypedCounter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DayStats dayStats = (DayStats) o;

        if (dayDate != dayStats.dayDate) return false;
        if (wordsTypedCounter != dayStats.wordsTypedCounter) return false;
        return wordCountersList != null ? wordCountersList.equals(dayStats.wordCountersList) : dayStats.wordCountersList == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (dayDate ^ (dayDate >>> 32));
        result = 31 * result + wordsTypedCounter;
        result = 31 * result + (wordCountersList != null ? wordCountersList.hashCode() : 0);
        return result;
    }
}
