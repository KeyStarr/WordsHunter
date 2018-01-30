package com.keystarr.wordshunter.ui.words.drag_and_drop;

import android.graphics.PointF;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.models.events.WordToTrackGroupChangedEvent;
import com.keystarr.wordshunter.models.events.WordsToTrackInGroupPositionsChangedEvent;
import com.keystarr.wordshunter.models.local.WordToTrack;
import com.keystarr.wordshunter.ui.words.WordsRecyclerAdapter;
import com.squareup.otto.Bus;

import java.util.Collections;

/**
 * Created by Cyril on 05.09.2017.
 */


public class RecyclerWordsOnCustomDragEventListener {
    private int from = -2;
    private int to = -2;
    private RecyclerView recyclerView;
    private boolean isEmpty = false;
    private Bus bus;
    private Handler updateInfoHandler;

    public RecyclerWordsOnCustomDragEventListener(RecyclerView recyclerView, Bus bus) {
        this.recyclerView = recyclerView;
        this.bus = bus;
        updateInfoHandler = new Handler();
    }

    private String getThisGroupName() {
        return ((WordsRecyclerAdapter) recyclerView.getAdapter()).getGroupName();
    }

    //TODO: if nothing else to do, try that method instead of findChildAt(int x,int y)
    private WordsRecyclerAdapter.WordsViewHolder getGroupHolderAt(float x, float y) {
        return (WordsRecyclerAdapter.WordsViewHolder)
                recyclerView.getChildViewHolder(recyclerView.findChildViewUnder(x, y));
    }

    public void onStarted(CustomDragEvent event) {
        to = event.getDraggedWordInitialPos();
        from = to;
        if (((WordsRecyclerAdapter) recyclerView.getAdapter()).getWordsToTrack().isEmpty())
            isEmpty = true;
    }

    public void onEntered(CustomDragEvent event) {
        if (!getThisGroupName().equals(event.getDraggedWord().getGroupName()))
            addWordItemToGroupOnEntered(event);
        else if (event.isDraggedWordLeftStartGroup())
            addWordItemToGroupOnEntered(event);
    }

    public void onLocation(CustomDragEvent event) {
        if (!isEmpty)
            updateCurrentDropZoneView(event);
    }

    public void onExited() {
        to = -2;
        from = -2;
        ((WordsRecyclerAdapter) recyclerView.getAdapter())
                .removeDraggingWord();
    }

    public void onDropped(CustomDragEvent event) {
        if (to == -2) {
            return;
        }
        WordsRecyclerAdapter wordsAdapter = (WordsRecyclerAdapter) recyclerView.getAdapter();
        wordsAdapter.onDragEnd();
        wordsAdapter.notifyItemChanged(to);

        WordToTrack changedWordToTrack = wordsAdapter.getWordsToTrack().get(to);

        String newGroupName = wordsAdapter.getGroupName();
        changedWordToTrack.setGroupName(newGroupName);
        changedWordToTrack.setRecyclerPosition(to);

        String oldGroupName = event.getDraggedWordInitialGroup();

        //update recycler positions on top of added word, if there was one
        if (!newGroupName.equals(oldGroupName)) {
            bus.post(new WordToTrackGroupChangedEvent(changedWordToTrack, oldGroupName));
            bus.post(new WordsToTrackInGroupPositionsChangedEvent(wordsAdapter.getWordsToTrack()));
        } else if (to != event.getDraggedWordInitialPos()) {
            bus.post(new WordsToTrackInGroupPositionsChangedEvent(wordsAdapter.getWordsToTrack()));
        }

        event.setResult(true);
        //TODO: make necessary changes
    }

    public void onEnded(CustomDragEvent event) {
        WordsRecyclerAdapter adapter = (WordsRecyclerAdapter) recyclerView.getAdapter();
        if (getThisGroupName().equals(event.getDraggedWordInitialGroup())) {
            if (!event.isDraggedWordLeftStartGroup()) {
                //in case drop happened so fast exited in origin group was not called
                if (event.getResult()) {
                    ((WordsRecyclerAdapter) recyclerView.getAdapter())
                            .removeDraggingWord();
                }
                adapter.onDragEnd();
                adapter.notifyDataSetChanged();//TODO: think to return the item changed anim
                //as now it's replaced to this due to some reasons
            }
            //update recycler positions of words on top of removed word, if there was one
            WordsRecyclerAdapter wordsAdapter = (WordsRecyclerAdapter) recyclerView.getAdapter();
            if (!event.getDraggedWord().getGroupName().equals(event.getDraggedWordInitialGroup()))
                bus.post(new WordsToTrackInGroupPositionsChangedEvent(wordsAdapter.getWordsToTrack()));
            else if (!event.getResult() && to != event.getDraggedWordInitialPos())
                bus.post(new WordsToTrackInGroupPositionsChangedEvent(wordsAdapter.getWordsToTrack()));
        } else if (getThisGroupName().equals(event.getDraggedWord().getGroupName())) {
            if (event.isDraggedWordLeftStartGroup() && !event.getResult()) {
                adapter.onDragEnd();
                adapter.notifyDataSetChanged();//TODO: think to return the item changed anim
                bus.post(new WordsToTrackInGroupPositionsChangedEvent(adapter.getWordsToTrack()));
            }
        } else
            adapter.onDragEnd();
        to = -2;
        from = -2;
    }

    private void addWordItemToGroupOnEntered(CustomDragEvent event) {
        if (isEmpty) {
            ((WordsRecyclerAdapter) recyclerView.getAdapter())
                    .insertDraggingWordToTrack(event.getDraggedWord(), 0);
            to = 0;
            isEmpty = false;
            return;
        }
        int childPos = findChildAt(false, event.getX(), event.getY());
        if (childPos != -2) {
            ((WordsRecyclerAdapter) recyclerView.getAdapter())
                    .insertDraggingWordToTrack(event.getDraggedWord(), childPos);
            to = childPos;
        } else {
            float centerRecyclerY = recyclerView.getY() + recyclerView.getHeight() / 2;
            if (event.getY() < centerRecyclerY) {
                ((WordsRecyclerAdapter) recyclerView.getAdapter())
                        .insertDraggingWordToTrack(event.getDraggedWord(), 0);
                to = 0;
            } else {
                ((WordsRecyclerAdapter) recyclerView.getAdapter())
                        .insertDraggingWordToTrack(event.getDraggedWord());
                to = recyclerView.getAdapter().getItemCount() - 1;
            }
        }
    }

    private void updateCurrentDropZoneView(CustomDragEvent event) {
        int tempTo = findChildAt(false, event.getX(), event.getY());
        if (tempTo != -2) {
            from = to;
            to = tempTo;
            int offset = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            to += offset;
        }
        WordsRecyclerAdapter adapter = (WordsRecyclerAdapter) recyclerView.getAdapter();
        int size = adapter.getWordsToTrack().size();
        if (from >= 0 && from < size && to >= 0 && to <= size && from != to) {
            if (from < to) {
                for (int i = from; i < to; i++) {
                    Collections.swap(adapter.getWordsToTrack(), i, i + 1);
                }
            } else {
                for (int i = from; i > to; i--) {
                    Collections.swap(adapter.getWordsToTrack(), i, i - 1);
                }
            }
            adapter.notifyItemMoved(from, to);
        }
    }

    /**
     * The next four methods are utility methods taken from Android Source Code. Most are package-private on View
     * or ViewGroup so I'm forced to replicate them here. Original source can be found:
     * http://grepcode.com/file/repository.grepcode.com/java/ext/com.google.android/android/5.1.0_r1/android/view/ViewGroup.java#ViewGroup.findFrontmostDroppableChildAt%28float%2Cfloat%2Candroid.graphics.PointF%29
     */

    private int findChildAt(boolean onlyX, float x, float y) {
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = recyclerView.getChildAt(i);
            if (isTransformedTouchPointInView(onlyX, x, y, child)) {
                return i;
            }
        }
        return -2;
    }

    private boolean isTransformedTouchPointInView(boolean onlyX, float x, float y, View child) {
        PointF point = new PointF(x, y);
        transformPointToViewLocal(point, child);
        return pointInView(onlyX, child, point.x, point.y);
    }

    private void transformPointToViewLocal(PointF pointToModify, View child) {
        pointToModify.x -= child.getLeft();
        pointToModify.y -= child.getTop();
    }

    private boolean pointInView(boolean onlyX, View v, float localX, float localY) {
        float yMargin = v.getContext().getResources().getDimension(R.dimen.words_recycler_item_card_margin);
        boolean isInViewByX = localX >= 0 && localX < (v.getRight() - v.getLeft());
        if (onlyX)
            return isInViewByX;
        else {
            return isInViewByX && localY >= -yMargin && localY < yMargin + (v.getBottom() - v.getTop());
        }
    }
}
