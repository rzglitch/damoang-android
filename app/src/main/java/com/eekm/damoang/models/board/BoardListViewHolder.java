package com.eekm.damoang.models.board;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.eekm.damoang.R;

public class BoardListViewHolder extends RecyclerView.ViewHolder {
    TextView board_name;
    TextView board_url;
    public BoardListViewHolder(View itemView) {
        super(itemView);
        board_name = (TextView) itemView.findViewById(R.id.board_name);
        board_url = (TextView) itemView.findViewById(R.id.board_url);
    }
}
