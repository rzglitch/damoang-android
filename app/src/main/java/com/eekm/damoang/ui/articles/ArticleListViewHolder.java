package com.eekm.damoang.ui.articles;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.eekm.damoang.R;

public class ArticleListViewHolder extends RecyclerView.ViewHolder {
    TextView doc_title;
    TextView doc_nickname;
    TextView doc_recommended;
    TextView doc_views;
    TextView doc_datetime;
    TextView doc_id;

    ArticleListViewHolder(View itemView) {
        super(itemView);
        doc_title = (TextView) itemView.findViewById(R.id.doc_title);
        doc_nickname = (TextView) itemView.findViewById(R.id.doc_nickname);
        doc_recommended = (TextView) itemView.findViewById(R.id.doc_recommended);
        doc_views = (TextView) itemView.findViewById(R.id.doc_views);
        doc_datetime = (TextView) itemView.findViewById(R.id.doc_datetime);
        doc_id = (TextView) itemView.findViewById(R.id.doc_id);
    }
}
