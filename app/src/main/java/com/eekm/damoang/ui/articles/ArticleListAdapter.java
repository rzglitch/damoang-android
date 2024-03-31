package com.eekm.damoang.ui.articles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.eekm.damoang.R;

public class ArticleListAdapter extends RecyclerView.Adapter<ArticleListViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private OnItemClickListener clickListener;
    private List<ArticleListModel> dataList;
    public ArticleListAdapter(List<ArticleListModel> dataList) {
        this.dataList = dataList;
    }

    public void setOnItemClickEventListener (OnItemClickListener listener) {
        clickListener = listener;
    }

    @NonNull
    @Override
    public ArticleListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_list_item_main, parent, false);
        ArticleListViewHolder holder = new ArticleListViewHolder(v);

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
    public void onBindViewHolder(@NonNull ArticleListViewHolder holder, int pos) {
        ArticleListModel listData = dataList.get(pos);
        holder.doc_title.setText(listData.getDoc_title());
        holder.doc_nickname.setText(listData.getDoc_nickname());
        holder.doc_recommended.setText(listData.getDoc_recommended());
        holder.doc_views.setText(listData.getDoc_views());
        holder.doc_datetime.setText(listData.getDoc_datetime());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
