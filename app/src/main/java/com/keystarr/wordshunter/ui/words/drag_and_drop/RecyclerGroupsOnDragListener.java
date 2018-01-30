package com.keystarr.wordshunter.ui.words.drag_and_drop;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.DragEvent;
import android.view.View;

import com.keystarr.wordshunter.models.local.WordToTrack;
import com.keystarr.wordshunter.ui.words.GroupsRecyclerAdapter;
import com.keystarr.wordshunter.ui.words.WordsRecyclerAdapter;

import java.util.HashMap;

/**
 * Created by Cyril on 09.09.2017.
 */

public class RecyclerGroupsOnDragListener implements View.OnDragListener {
    private RecyclerView groupsRecyclerView;
    private GroupsRecyclerOnDragOverEdgeScroller edgeScroller;
    private WordsRecyclerAdapter previousGroupAdapter;
    private CustomDragEvent groupEvent;

    public RecyclerGroupsOnDragListener(RecyclerView groupsRecyclerView) {
        this.groupsRecyclerView = groupsRecyclerView;
        edgeScroller = new GroupsRecyclerOnDragOverEdgeScroller(groupsRecyclerView);
    }

    private GroupsRecyclerAdapter.GroupsViewHolder getGroupHolderAt(float x, float y) {
        View foundView = groupsRecyclerView.findChildViewUnder(x, y);
        if (foundView != null)
            return (GroupsRecyclerAdapter.GroupsViewHolder)
                    groupsRecyclerView.getChildViewHolder(foundView);
        else
            return null;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                return onStarted(event);
            case DragEvent.ACTION_DRAG_LOCATION:
                onLocation(event);
                break;
            case DragEvent.ACTION_DROP:
                return onDropped(event);
            case DragEvent.ACTION_DRAG_ENDED:
                onEnded();
                return true;
        }
        return false;
    }


    public boolean onStarted(DragEvent event) {
        Bundle bundle = (Bundle) event.getLocalState();
        WordToTrack draggedWordToTrack = new WordToTrack(bundle.getString("word"),
                bundle.getString("group"), bundle.getBoolean("isTracked"), bundle.getInt("recyclerPos"));
        groupEvent = new CustomDragEvent(draggedWordToTrack, bundle.getInt("pos"),
                draggedWordToTrack.getGroupName(), -1, -1, false);

        GroupsRecyclerAdapter main =
                (GroupsRecyclerAdapter) groupsRecyclerView.getAdapter();
        HashMap<String, WordsRecyclerAdapter> groups =
                main.getWordsAdaptersMap();
        for (WordsRecyclerAdapter groupAdapter : groups.values()) {
            groupAdapter.getCustomDragListener().onStarted(groupEvent);
        }
        return true;
    }

    public void onLocation(DragEvent event) {
        GroupsRecyclerAdapter.GroupsViewHolder currentHolder =
                getGroupHolderAt(event.getX(), event.getY());
        if (currentHolder != null) {
            float offsetForGroupX = currentHolder.rootLayout.getLeft() + currentHolder.wordsRecyclerView.getLeft();
            float offsetForGroupY = currentHolder.rootLayout.getTop() + currentHolder.wordsRecyclerView.getTop();
            groupEvent.updateCoords(event.getX() - offsetForGroupX, event.getY() - offsetForGroupY);
            if (previousGroupAdapter == null) {
                currentHolder.getRecyclerAdapter().getCustomDragListener().onEntered(groupEvent);
                previousGroupAdapter = currentHolder.getRecyclerAdapter();
            } else if (!currentHolder.getGroupName().equals(previousGroupAdapter.getGroupName())) {
                groupEvent.setDraggedWordLeftStartGroup(true);
                previousGroupAdapter.getCustomDragListener().onExited();
                currentHolder.getRecyclerAdapter().getCustomDragListener().onEntered(groupEvent);
                previousGroupAdapter = currentHolder.getRecyclerAdapter();
            } else
                currentHolder.getRecyclerAdapter().getCustomDragListener().onLocation(groupEvent);
            edgeScroller.scrollIfNeed(event.getY());
        }
    }

    public boolean onDropped(DragEvent event) {
        GroupsRecyclerAdapter.GroupsViewHolder droppedAtHolder =
                getGroupHolderAt(event.getX(), event.getY());
        if (droppedAtHolder == null)
            return false;
        droppedAtHolder.getRecyclerAdapter().getCustomDragListener().onDropped(groupEvent);
        return true;
    }

    public void onEnded() {
        if (groupEvent.isDraggedWordLeftStartGroup() && !groupEvent.getResult()) {
            groupEvent.getDraggedWord().setGroupName(previousGroupAdapter.getGroupName());
        }
        GroupsRecyclerAdapter mainAdapter =
                (GroupsRecyclerAdapter) groupsRecyclerView.getAdapter();
        HashMap<String, WordsRecyclerAdapter> groupsAdapters =
                mainAdapter.getWordsAdaptersMap();
        for (WordsRecyclerAdapter groupAdapter : groupsAdapters.values()) {
            groupAdapter.getCustomDragListener().onEnded(groupEvent);
        }
        edgeScroller.removeScrollingCallbacksFromRecycler();
        previousGroupAdapter = null;
        groupEvent = null;
    }
}
