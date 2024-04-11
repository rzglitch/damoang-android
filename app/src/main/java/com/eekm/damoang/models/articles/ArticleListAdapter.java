package com.eekm.damoang.models.articles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.eekm.damoang.R;

public class ArticleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_LIST = 0;
    public static final int VIEW_TYPE_PROGRESS = 1;

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == VIEW_TYPE_LIST) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_list_item_main, parent, false);
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
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_list_load_item_main, parent, false);
            ProgressBarViewHolder holder = new ProgressBarViewHolder(v);

            return holder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int pos) {
        if (holder instanceof ArticleListViewHolder) {
            ArticleListModel listData = dataList.get(pos);
            ((ArticleListViewHolder) holder).doc_title.setText(listData.getDoc_title());
            ((ArticleListViewHolder) holder).doc_nickname.setText(listData.getDoc_nickname());
            ((ArticleListViewHolder) holder).doc_recommended.setText(listData.getDoc_recommended());
            ((ArticleListViewHolder) holder).doc_views.setText(listData.getDoc_views());
            ((ArticleListViewHolder) holder).doc_datetime.setText(listData.getDoc_datetime());
            ((ArticleListViewHolder) holder).doc_id.setText(listData.getDoc_id());
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int pos) {
        return dataList.get(pos).getViewType();
    }
}
