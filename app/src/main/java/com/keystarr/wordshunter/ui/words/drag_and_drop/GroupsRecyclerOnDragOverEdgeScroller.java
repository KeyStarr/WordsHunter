package com.keystarr.wordshunter.ui.words.drag_and_drop;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Cyril on 09.09.2017.
 */

//Handles groups recycler scroll when top and bottom edge are reached
public class GroupsRecyclerOnDragOverEdgeScroller {
    private RecyclerView recyclerView;
    private float dragTouchY;

    GroupsRecyclerOnDragOverEdgeScroller(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                recyclerView.removeCallbacks(scrollBehaviourRunnable);
            }
        });
    }

    public void scrollIfNeed(float y) {
        dragTouchY = y;
        scrollBehaviourRunnable.run();
    }

    public void removeScrollingCallbacksFromRecycler() {
        recyclerView.removeCallbacks(scrollBehaviourRunnable);
    }

    //Runs when user moves finger on recycler
    //removes all previous same callback
    //checks if gesture happened on very top or very bottom
    //if happened - scrolls and posts itself again
    //else ends
    private Runnable scrollBehaviourRunnable = new Runnable() {
        @Override
        public void run() {
            int dir = 0;
            if (dragTouchY > recyclerView.getBottom() - 99) {
                dir = 1;
            } else if (dragTouchY < recyclerView.getTop() + 99)
                dir = -1;
            if (dir != 0) {
                recyclerView.scrollBy(0, dir * 8);
                recyclerView.removeCallbacks(scrollBehaviourRunnable);
                ViewCompat.postOnAnimation(recyclerView, this);
            }
        }
    };
}
