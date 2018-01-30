package com.keystarr.wordshunter.ui.words.drag_and_drop;

/**
 * Created by Bizarre on 18.09.2017.
 */

public interface Draggable<T> {
    int getRecyclerPosition();

    void setRecyclerPosition(int pos);

    int comparePositions(T o);
}
