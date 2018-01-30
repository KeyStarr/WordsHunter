package com.keystarr.wordshunter.models.remote;

import com.keystarr.wordshunter.models.local.WordCounter;

/**
 * Created by Cyril on 22.01.2018.
 */

public class WordCounterRemote {
    private String word;
    private int count;

    public WordCounterRemote(String word, int count) {
        this.word = word;
        this.count = count;
    }

    public WordCounterRemote(WordCounter wordCounter) {
        this.word = wordCounter.getWord();
        this.count = wordCounter.getCount();
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
