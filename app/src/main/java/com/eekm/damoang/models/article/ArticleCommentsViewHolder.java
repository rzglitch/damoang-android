package com.eekm.damoang.models.article;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.eekm.damoang.R;

public class ArticleCommentsViewHolder extends RecyclerView.ViewHolder {
    TextView doc_content;
    TextView doc_nickname;
    TextView doc_recommended;
    TextView doc_datetime;
    TextView doc_id;
    TextView doc_image;
    ImageView doc_image_iv;

    ArticleCommentsViewHolder(View itemView) {
        super(itemView);
        doc_nickname = (TextView) itemView.findViewById(R.id.doc_nickname2);
        doc_recommended = (TextView) itemView.findViewById(R.id.doc_recommended2);
        doc_datetime = (TextView) itemView.findViewById(R.id.doc_datetime2);
        doc_id = (TextView) itemView.findViewById(R.id.doc_id2);
        doc_content = (TextView) itemView.findViewById(R.id.doc_content_text);
    }
}
