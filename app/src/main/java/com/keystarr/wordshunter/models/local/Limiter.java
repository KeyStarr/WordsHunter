package com.keystarr.wordshunter.models.local;

/**
 * Created by Bizarre on 26.09.2017.
 */

public class Limiter {

    private String limitedWord;
    private int limit;
    private boolean forWord;

    public Limiter(String limitedWord, int limit, boolean forWord) {
        this.limitedWord = limitedWord;
        this.limit = limit;
        this.forWord = forWord;
    }

    public String getLimitedWord() {
        return limitedWord;
    }

    public void setLimitedWord(String limitedWord) {
        this.limitedWord = limitedWord;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isForWord() {
        return forWord;
    }

    public void setForWord(boolean forWord) {
        this.forWord = forWord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Limiter that = (Limiter) o;

        return limitedWord != null ? limitedWord.equals(that.limitedWord) : that.limitedWord == null;

    }

    @Override
    public int hashCode() {
        return limitedWord != null ? limitedWord.hashCode() : 0;
    }
}
