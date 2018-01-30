package com.keystarr.wordshunter.ui.stats.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.keystarr.wordshunter.R;
import com.keystarr.wordshunter.models.local.StatsRecyclerEntry;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Cyril on 07.07.2017.
 */

public class StatsRecyclerAdapter extends RecyclerView.Adapter {

    private List<StatsRecyclerEntry> entries;
    private Context ctx;

    public StatsRecyclerAdapter(Context ctx, List<StatsRecyclerEntry> entries) {
        this.ctx = ctx;
        this.entries = entries;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.frequency_recycler_view_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        StatsRecyclerEntry entry = entries.get(position);
        if (entry != null) {
            //TODO: make раз и разА
            int count = entry.getCount();
            String times = ctx.getString(R.string.time, count);
            viewHolder.entryCount.setText(times);
            viewHolder.entryFrequencyBar.setProgress((int) entry.getRelativeCount());
            viewHolder.entryName.setText(entry.getName());
            viewHolder.entryNumber.setText(String.valueOf(entry.getNumber()));
            if (count > 0 && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //TODO: manage to get rid of gray part of bar becoming shade of colorAccent
                //on pre-Lollipop devices
                Context context = viewHolder.entryFrequencyBar.getContext();
                viewHolder.entryFrequencyBar.getProgressDrawable()
                        .setColorFilter(
                                ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.entry_count)
        TextView entryCount;
        @BindView(R.id.entry_number)
        TextView entryNumber;
        @BindView(R.id.frequency_bar)
        ProgressBar entryFrequencyBar;
        @BindView(R.id.entry_name)
        TextView entryName;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
