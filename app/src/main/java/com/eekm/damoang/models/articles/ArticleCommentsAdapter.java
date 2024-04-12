package com.eekm.damoang.models.articles;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eekm.damoang.R;

import java.util.List;

public class ArticleCommentsAdapter extends RecyclerView.Adapter<ArticleCommentsViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private ArticleListAdapter.OnItemClickListener clickListener;
    private List<ArticleCommentsModel> dataList;
    public ArticleCommentsAdapter(List<ArticleCommentsModel> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public ArticleCommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_comment_item_main, parent, false);
        ArticleCommentsViewHolder holder = new ArticleCommentsViewHolder(v);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleCommentsViewHolder holder, int pos) {
        ArticleCommentsModel listData = dataList.get(pos);
        holder.doc_content.setText(listData.getDoc_content());
        holder.doc_nickname.setText(listData.getDoc_nickname());
        holder.doc_recommended.setText(listData.getDoc_recommended());
        holder.doc_datetime.setText(listData.getDoc_datetime());
        holder.doc_id.setText(listData.getDoc_id());

        String doc_image = listData.getDoc_image();
        if (!doc_image.isEmpty()) {
            Glide.with(holder.itemView)
                    .load(listData.getDoc_image())
                    .override(1200, 1200)
                    .skipMemoryCache(true)
                    .dontAnimate()
                    .into(holder.doc_image_iv);
            holder.doc_image_iv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
