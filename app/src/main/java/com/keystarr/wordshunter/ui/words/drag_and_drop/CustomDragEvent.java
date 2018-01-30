package com.keystarr.wordshunter.ui.words.drag_and_drop;

import com.keystarr.wordshunter.models.local.WordToTrack;

/**
 * Created by Cyril on 09.09.2017.
 */

public class CustomDragEvent {
    private WordToTrack draggedWord;
    private int draggedWordInitialPos;
    private String draggedWordInitialGroup;
    private float x, y;
    private boolean draggedWordLeftStartGroup;
    private boolean result;

    public CustomDragEvent(WordToTrack draggedWord, int draggedWordInitialPos,
                           String draggedWordInitialGroup,
                           int x, int y, boolean draggedWordLeftStartGroup) {
        this.draggedWord = draggedWord;
        this.draggedWordInitialPos = draggedWordInitialPos;
        this.draggedWordInitialGroup = draggedWordInitialGroup;
        this.draggedWordLeftStartGroup = draggedWordLeftStartGroup;
        updateCoords(x, y);
    }

    public void updateCoords(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public String getDraggedWordInitialGroup() {
        return draggedWordInitialGroup;
    }

    public void setDraggedWordInitialGroup(String draggedWordInitialGroup) {
        this.draggedWordInitialGroup = draggedWordInitialGroup;
    }

    public boolean isDraggedWordLeftStartGroup() {
        return draggedWordLeftStartGroup;
    }

    public void setDraggedWordLeftStartGroup(boolean draggedWordLeftStartGroup) {
        this.draggedWordLeftStartGroup = draggedWordLeftStartGroup;
    }

    public int getDraggedWordInitialPos() {
        return draggedWordInitialPos;
    }

    public void setDraggedWordInitialPos(int draggedWordInitialPos) {
        this.draggedWordInitialPos = draggedWordInitialPos;
    }

    public WordToTrack getDraggedWord() {
        return draggedWord;
    }

    public void setDraggedWord(WordToTrack draggedWord) {
        this.draggedWord = draggedWord;
    }


    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }


    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
