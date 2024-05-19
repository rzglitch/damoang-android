package com.eekm.damoang.models.article;

import android.view.View;
import android.widget.ProgressBar;

import androidx.recyclerview.widget.RecyclerView;

import com.eekm.damoang.R;

public class ProgressBarViewHolder extends RecyclerView.ViewHolder {
    ProgressBar progress;

    ProgressBarViewHolder(View itemView) {
        super(itemView);
        progress = (ProgressBar) itemView.findViewById(R.id.pb_load_list_progress);
    }
}
