package com.keystarr.wordshunter.ui.words.drag_and_drop;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.keystarr.wordshunter.models.events.WordToTrackGroupsPositionsChangedEvent;
import com.keystarr.wordshunter.ui.words.GroupsRecyclerAdapter;
import com.squareup.otto.Bus;

import java.util.Collections;

/**
 * Created by Cyril on 03.09.2017.
 */

public class GroupRecyclerItemDragCallback extends ItemTouchHelper.Callback {

    private Bus bus;

    public GroupRecyclerItemDragCallback(Bus bus) {
        this.bus = bus;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0);
    }

    @Override
    public int interpolateOutOfBoundsScroll(RecyclerView recyclerView,
                                            int viewSize, int viewSizeOutOfBounds,
                                            int totalSize, long msSinceStartScroll) {
        float sizeAcceleration = 26 * (((float) viewSizeOutOfBounds) / viewSize);
        return Math.round(sizeAcceleration);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition(),
                toPosition = target.getAdapterPosition();
        GroupsRecyclerAdapter adapter = (GroupsRecyclerAdapter) recyclerView.getAdapter();
        adapter.getGroupsToTrack().get(fromPosition).setRecyclerPosition(toPosition);
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(adapter.getGroupsToTrack(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(adapter.getGroupsToTrack(), i, i - 1);
            }
        }
        adapter.notifyItemMoved(fromPosition, toPosition);
        //TODO: SUBSTRACT UPDATED GROUPS FROM LIST AND UPDATE ONLY THEM
        bus.post(new WordToTrackGroupsPositionsChangedEvent(adapter.getGroupsToTrack()));
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    }
}
