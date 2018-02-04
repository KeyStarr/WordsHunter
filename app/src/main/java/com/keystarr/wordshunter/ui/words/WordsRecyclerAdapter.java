package com.keystarr.wordshunter.ui.words;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.models.local.WordToTrack;
import com.keystarr.wordshunter.repository.DatabaseRepository;
import com.keystarr.wordshunter.ui.words.drag_and_drop.RecyclerWordsOnCustomDragEventListener;
import com.keystarr.wordshunter.utils.InitialDataUtils;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by Cyril on 22.08.2017.
 */

public class WordsRecyclerAdapter extends RecyclerView.Adapter {

    private List<WordToTrack> wordsToTrack;
    private ButtonsPanelListener buttonsPanelListener;
    private String draggedWord;
    private String groupName;
    private RecyclerWordsOnCustomDragEventListener dragListener;
    private DatabaseRepository dtbRepo;

    public WordsRecyclerAdapter(ButtonsPanelListener buttonsPanelListener, String groupName,
                                List<WordToTrack> wordsToTrack, DatabaseRepository dtbRepo) {
        this.wordsToTrack = wordsToTrack;
        this.groupName = groupName;
        this.buttonsPanelListener = buttonsPanelListener;
        this.dtbRepo = dtbRepo;
    }

    public void setCustomDragListener(RecyclerWordsOnCustomDragEventListener dragListener) {
        this.dragListener = dragListener;
    }

    public RecyclerWordsOnCustomDragEventListener getCustomDragListener() {
        return dragListener;
    }

    public void insertWordToTrack(int pos, WordToTrack wordToTrack) {
        wordsToTrack.add(pos, wordToTrack);
        notifyItemInserted(pos);
    }

    public void insertWordToTrack(WordToTrack wordToTrack) {
        insertWordToTrack(wordsToTrack.size(), wordToTrack);
    }

    public void insertDraggingWordToTrack(WordToTrack wordToTrack,
                                          int pos) {
        insertWordToTrack(pos, wordToTrack);
        draggedWord = wordToTrack.getWord();
    }

    public void toggleNotificationsButton(WordsViewHolder holder, boolean turnGrey) {
        ImageButton notifyButton = holder.notificationsButton;
        Context context = notifyButton.getContext();
        int buttonColor = !turnGrey ? Color.TRANSPARENT
                : ContextCompat.getColor(context, R.color.greyed_button);
        holder.notificationsButton.setColorFilter(buttonColor, PorterDuff.Mode.SRC_ATOP);
    }

    public String getGroupName() {
        return groupName;
    }

    public List<WordToTrack> getWordsToTrack() {
        return wordsToTrack;
    }

    public void onDragEnd() {
        draggedWord = null;
    }

    public void insertDraggingWordToTrack(WordToTrack wordToTrack) {
        insertDraggingWordToTrack(wordToTrack, wordsToTrack.size());
    }

    public void removeDraggingWord() {
        for (int i = 0; i < wordsToTrack.size(); i++) {
            if (wordsToTrack.get(i).getWord().equals(draggedWord)) {
                wordsToTrack.remove(i);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.word_recycler_item, parent, false);
        return new WordsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        WordToTrack wordToTrack = wordsToTrack.get(position);
        WordsViewHolder vh = (WordsViewHolder) holder;
        vh.wordText.setText(wordToTrack.getWord());
        int trackedButtonColor = wordToTrack.isTracked()
                ? Color.TRANSPARENT
                : ContextCompat.getColor(vh.cardRoot.getContext(), R.color.greyed_button);
        vh.trackingButton.setColorFilter(trackedButtonColor, PorterDuff.Mode.SRC_ATOP);
        toggleNotificationsButton(vh, !wordHasLimiter(wordToTrack.getWord()));
        if (wordToTrack.getWord().equals(draggedWord))
            vh.cardRoot.setVisibility(View.INVISIBLE);
        else
            vh.cardRoot.setVisibility(View.VISIBLE);
    }


    private boolean wordHasLimiter(String word) {
        return dtbRepo.getLimiterForName(word, true) != null;
    }

    @Override
    public int getItemCount() {
        return wordsToTrack.size();
    }

    //TODO: make holder static
    public class WordsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.word_card)
        CardView cardRoot;
        @BindView(R.id.word_text)
        TextView wordText;
        @BindView(R.id.buttons_panel_expandable_layout)
        ExpandableLayout buttonsExpandable;
        @BindView(R.id.img_btn_tracking)
        ImageButton trackingButton;
        @BindView(R.id.img_btn_delete)
        ImageButton deleteButton;
        @BindView(R.id.img_btn_notify)
        ImageButton notificationsButton;
        @BindView(R.id.panel_buttons_container)
        LinearLayout panelButtonsContainer;
        @BindView(R.id.outside_buttons_container)
        LinearLayout outsideButtonsContainer;

        WordsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @OnClick(R.id.word_card)
        public void wordCardOnClick() {
            buttonsExpandable.toggle();
        }

        @OnLongClick(R.id.word_card)
        public boolean wordCardOnLongClick(View view) {
            cardRoot.setVisibility(View.INVISIBLE);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(cardRoot);
            WordToTrack wordToTrack = wordsToTrack.get(getAdapterPosition());
            Bundle bundle = new Bundle();
            bundle.putString("word", wordToTrack.getWord());
            bundle.putString("group", wordToTrack.getGroupName());
            bundle.putInt("pos", getAdapterPosition());
            bundle.putBoolean("isTracked", wordToTrack.isTracked());
            bundle.putInt("recyclerPos", wordToTrack.getRecyclerPosition());
            //putting data in the LocalState instead of ClipData
            //in cause of troubles with recyclerView caching out-of-screen-view
            //which leads to being not able to receive dragEnded here sometimes
            //plus inability to temporary add dragging word to hovered on group
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                cardRoot.startDragAndDrop(null, shadowBuilder, bundle, 0);
            } else {
                cardRoot.startDrag(null, shadowBuilder, bundle, 0);
            }
            cardRoot.setVisibility(View.INVISIBLE);
            draggedWord = wordText.getText().toString();
            return true;
        }

        @OnClick(R.id.img_btn_tracking)
        public void trackingButtonOnClick() {
            WordToTrack wordToTrack = wordsToTrack.get(getAdapterPosition());
            if (wordToTrack.isTracked()) {
                Context context = trackingButton.getContext();
                trackingButton.setColorFilter(
                        ContextCompat.getColor(context, R.color.greyed_button), PorterDuff.Mode.SRC_ATOP);
            } else
                trackingButton.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_ATOP);
            buttonsPanelListener.trackingButtonOnClick(wordToTrack);
        }

        @OnClick(R.id.img_btn_notify)
        public void notifyButtonOnClick() {
            int pos = getAdapterPosition();
            buttonsPanelListener.notificationsButtonOnClick(pos, wordsToTrack.get(pos));
        }

        @OnClick(R.id.img_btn_delete)
        public void deleteButtonOnClick() {
            int pos = getAdapterPosition();
            buttonsPanelListener.deleteButtonOnClick(
                    pos, wordsToTrack.get(getAdapterPosition()));
            wordsToTrack.remove(pos);
            notifyItemRemoved(pos);
        }
    }
}