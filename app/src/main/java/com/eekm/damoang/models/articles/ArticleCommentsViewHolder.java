package com.eekm.damoang.models.articles;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.eekm.damoang.R;

public class ArticleCommentsViewHolder extends RecyclerView.ViewHolder {
    TextView doc_content;
    TextView doc_nickname;
    TextView doc_recommended;
    TextView doc_datetime;
    TextView doc_id;

    ArticleCommentsViewHolder(View itemView) {
        super(itemView);
        doc_nickname = (TextView) itemView.findViewById(R.id.doc_nickname2);
        doc_recommended = (TextView) itemView.findViewById(R.id.doc_recommended2);
        doc_content = (TextView) itemView.findViewById(R.id.doc_content2);
        doc_datetime = (TextView) itemView.findViewById(R.id.doc_datetime2);
        doc_id = (TextView) itemView.findViewById(R.id.doc_id2);
    }
}
