package com.keystarr.wordshunter.models.local;

/**
 * Created by Cyril on 11.09.2017.
 */

public class Letter {
    private char chr;
    private int pos;

    public Letter(char chr, int pos) {
        this.chr = chr;
        this.pos = pos;
    }

    public char getChr() {
        return chr;
    }

    public void setChr(char chr) {
        this.chr = chr;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Letter letter = (Letter) o;

        return chr == letter.chr && pos == letter.pos;

    }

    @Override
    public int hashCode() {
        int result = (int) chr;
        result = 31 * result + pos;
        return result;
    }
}
