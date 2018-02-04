package com.keystarr.wordshunter.ui.words;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
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
import com.keystarr.wordshunter.models.local.WordsGroupToTrack;
import com.keystarr.wordshunter.repository.DatabaseRepository;
import com.keystarr.wordshunter.ui.words.drag_and_drop.RecyclerWordsOnCustomDragEventListener;
import com.keystarr.wordshunter.utils.InitialDataUtils;
import com.squareup.otto.Bus;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Cyril on 20.08.2017.
 */

public class GroupsRecyclerAdapter extends RecyclerView.Adapter {

    private List<WordsGroupToTrack> groupsToTrack;
    private ButtonsPanelListener buttonsPanelListener;
    private GroupCardExpansionListener cardExpansionListener;
    private Context context;
    private HashMap<String, WordsRecyclerAdapter> wordsAdaptersMap;
    private Bus bus;
    private DatabaseRepository dtbRepo;

    public GroupsRecyclerAdapter(Context context, List<WordsGroupToTrack> groupsToTrack,
                                 ButtonsPanelListener buttonsPanelListener,
                                 DatabaseRepository dtbRepo,
                                 GroupCardExpansionListener cardExpansionListener, Bus bus) {
        this.context = context;
        this.groupsToTrack = groupsToTrack;
        this.dtbRepo = dtbRepo;
        this.buttonsPanelListener = buttonsPanelListener;
        this.cardExpansionListener = cardExpansionListener;
        this.bus = bus;
        wordsAdaptersMap = new HashMap<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_recycler_item, parent, false);
        return new GroupsViewHolder(view);
    }

    public void toggleNotificationsButton(GroupsViewHolder holder, boolean turnGrey) {
        ImageButton notifyButton = holder.notificationsButton;
        Context context = notifyButton.getContext();
        int buttonColor = !turnGrey ? Color.TRANSPARENT
                : ContextCompat.getColor(context, R.color.greyed_button);
        holder.notificationsButton.setColorFilter(buttonColor, PorterDuff.Mode.SRC_ATOP);
    }

    public void insertGroupToTrack(int pos, WordsGroupToTrack groupToTrack) {
        groupsToTrack.add(pos, groupToTrack);
        notifyItemInserted(pos);
    }

    public WordsRecyclerAdapter getWordsRecyclerAdapterForGroup(String groupName) {
        return wordsAdaptersMap.get(groupName);
    }

    public WordsRecyclerAdapter getWordsRecyclerAdapterForWord(String wordToFind) {
        for (WordsRecyclerAdapter wordsAdapter : wordsAdaptersMap.values()) {
            for (WordToTrack word : wordsAdapter.getWordsToTrack()) {
                if (wordToFind.equals(word.getWord())) {
                    return wordsAdapter;
                }
            }
        }
        return null;
    }

    public int getGroupPosition(String groupName) {
        for (int i = 0; i < groupsToTrack.size(); i++)
            if (groupName.equals(groupsToTrack.get(i).getGroupName()))
                return i;
        return -1;
    }

    public List<WordsGroupToTrack> getGroupsToTrack() {
        return groupsToTrack;
    }

    public HashMap<String, WordsRecyclerAdapter> getWordsAdaptersMap() {
        return wordsAdaptersMap;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GroupsViewHolder vh = (GroupsViewHolder) holder;
        String groupName = groupsToTrack.get(position).getGroupName();
        vh.groupText.setText(groupName);
        toggleNotificationsButton(vh, !groupHasLimiter(groupName));
        LinearLayoutManager mng = new LinearLayoutManager(
                vh.wordsRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false);
        vh.wordsRecyclerView.setLayoutManager(mng);
        WordsRecyclerAdapter wordsRecyclerAdapter = new WordsRecyclerAdapter(
                buttonsPanelListener, groupName, groupsToTrack.get(position).getWordsToTrack(), dtbRepo);
        wordsRecyclerAdapter.setCustomDragListener(
                new RecyclerWordsOnCustomDragEventListener(vh.wordsRecyclerView, bus));
        wordsAdaptersMap.put(groupName, wordsRecyclerAdapter);
        vh.wordsRecyclerView.setAdapter(wordsRecyclerAdapter);
        vh.wordsRecyclerView.setTag(groupName);
        //TODO: think of a better way to achieve same effect
        if (groupName.equals(context.getString(R.string.no_group)))
            vh.panelButtonsContainer.removeView(vh.deleteButton);
    }

    private boolean groupHasLimiter(String groupName) {
        return dtbRepo.getLimiterForName(groupName, false) != null;
    }

    @Override
    public int getItemCount() {
        return groupsToTrack.size();
    }

    public class GroupsViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.root_layout)
        public CardView rootLayout;
        @BindView(R.id.words_recycler_view)
        public RecyclerView wordsRecyclerView;
        @BindView(R.id.group_text)
        TextView groupText;
        @BindView(R.id.buttons_panel_expandable_layout)
        ExpandableLayout expandableButtonsLayout;
        @BindView(R.id.overflow)
        ImageButton overflowButton;
        @BindView(R.id.card_content_expandable)
        ExpandableLayout cardContentExpandable;
        @BindView(R.id.badge_view)
        TextView badgeTextView;
        @BindView(R.id.img_btn_delete)
        ImageButton deleteButton;
        @BindView(R.id.panel_buttons_container)
        LinearLayout panelButtonsContainer;
        @BindView(R.id.img_btn_notify)
        ImageButton notificationsButton;
        @BindView(R.id.outside_buttons_container)
        LinearLayout outsideButtonsContainer;

        GroupsViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
            expandableButtonsLayout.setOnExpansionUpdateListener(
                    new ExpandableLayout.OnExpansionUpdateListener() {
                        @Override
                        public void onExpansionUpdate(float expansionFraction, int state) {
                            overflowButton.setRotation(expansionFraction * 90);
                        }
                    }
            );
            cardContentExpandable.setOnExpansionUpdateListener(new ExpandableLayout.OnExpansionUpdateListener() {
                @Override
                public void onExpansionUpdate(float expansionFraction, int state) {
                    //if card that is out of view on top is being expanded
                    //animation is very clumsy
                    //TODO: fix animation clumsiness
                    if (state == 2)
                        cardExpansionListener.onGroupCardExpansion(getAdapterPosition());
                }
            });
        }

        public String getGroupName() {
            return getRecyclerAdapter().getGroupName();
        }

        public WordsRecyclerAdapter getRecyclerAdapter() {
            return (WordsRecyclerAdapter) wordsRecyclerView.getAdapter();
        }

        @OnClick(R.id.overflow)
        public void overflowButtonOnClick() {
            expandableButtonsLayout.toggle();
        }

        @OnClick(R.id.root_layout)
        public void outerCardViewOnClick() {
            if (cardContentExpandable.isExpanded()) {
                badgeTextView.setVisibility(View.VISIBLE);
                badgeTextView.setText(String.valueOf(groupsToTrack.get(getAdapterPosition()).getWordsToTrack().size()));
            } else
                badgeTextView.setVisibility(View.GONE);
            cardContentExpandable.toggle();
        }

        @OnClick(R.id.img_btn_notify)
        public void notifyButtonOnClick() {
            int pos = getAdapterPosition();
            buttonsPanelListener.notificationsButtonOnClick(pos, groupsToTrack.get(pos));
        }

        @OnClick(R.id.img_btn_delete)
        public void deleteButtonOnClick() {
            int pos = getAdapterPosition();
            buttonsPanelListener.deleteButtonOnClick(pos, groupsToTrack.get(pos));
            groupsToTrack.remove(pos);
            notifyItemRemoved(getAdapterPosition());
        }
    }
}
