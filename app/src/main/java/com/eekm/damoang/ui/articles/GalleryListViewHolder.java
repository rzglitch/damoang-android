package com.eekm.damoang.ui.articles;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.eekm.damoang.R;

public class GalleryListViewHolder extends RecyclerView.ViewHolder {
    TextView doc_title;
    TextView doc_nickname;
    TextView doc_recommended;
    TextView doc_views;
    TextView doc_datetime;
    TextView doc_id;
    TextView doc_thumb;
    ImageView doc_thumb_iv;

    GalleryListViewHolder(View itemView) {
        super(itemView);
        doc_title = (TextView) itemView.findViewById(R.id.tv_gallery_title);
        doc_nickname = (TextView) itemView.findViewById(R.id.tv_gallery_nickname);
        doc_recommended = (TextView) itemView.findViewById(R.id.tv_gallery_recommended);
        doc_views = (TextView) itemView.findViewById(R.id.tv_gallery_views);
        doc_datetime = (TextView) itemView.findViewById(R.id.tv_gallery_datetime);
        doc_id = (TextView) itemView.findViewById(R.id.tv_gallery_id);
        doc_thumb = (TextView) itemView.findViewById(R.id.tv_gallery_thumb);
        doc_thumb_iv = (ImageView) itemView.findViewById(R.id.iv_gallery_thumb);
    }
}
