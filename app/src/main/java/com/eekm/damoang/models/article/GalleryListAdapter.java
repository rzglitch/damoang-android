package com.eekm.damoang.models.article;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eekm.damoang.R;

import java.util.List;

public class GalleryListAdapter extends RecyclerView.Adapter<GalleryListViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private OnItemClickListener clickListener;
    private List<GalleryListModel> dataList;
    public GalleryListAdapter(List<GalleryListModel> dataList) {
        this.dataList = dataList;
    }

    public void setOnItemClickEventListener (OnItemClickListener listener) {
        clickListener = listener;
    }

    @NonNull
    @Override
    public GalleryListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_gallery_list_item_main, parent, false);
        GalleryListViewHolder holder = new GalleryListViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = "";
                int pos = holder.getAdapterPosition();
                clickListener.onItemClick(pos);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryListViewHolder holder, int pos) {
        GalleryListModel listData = dataList.get(pos);
        holder.doc_title.setText(listData.getDoc_title());
        holder.doc_nickname.setText(listData.getDoc_nickname());
        holder.doc_recommended.setText(listData.getDoc_recommended());
        holder.doc_views.setText(listData.getDoc_views());
        holder.doc_datetime.setText(listData.getDoc_datetime());
        holder.doc_id.setText(listData.getDoc_id());

        Glide.with(holder.itemView)
                .load(listData.getDoc_thumb())
                .override(1200, 1200)
                .skipMemoryCache(true)
                .dontAnimate()
                .into(holder.doc_thumb_iv);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
