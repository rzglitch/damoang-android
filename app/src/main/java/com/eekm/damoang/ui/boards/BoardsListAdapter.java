package com.eekm.damoang.ui.boards;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eekm.damoang.R;

import java.util.List;

public class BoardsListAdapter extends RecyclerView.Adapter<BoardsListViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(int pos);
    }

    private OnItemClickListener clickListener;
    private List<BoardsListModel> dataList;

    public BoardsListAdapter(List<BoardsListModel> dataList) {
        this.dataList = dataList;
    }

    public void setOnItemClickEventListener (BoardsListAdapter.OnItemClickListener listener) {
        clickListener = listener;
    }

    @NonNull
    @Override
    public BoardsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.boards_list_item_main, parent, false);
        BoardsListViewHolder holder = new BoardsListViewHolder(v);

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
    public void onBindViewHolder(@NonNull BoardsListViewHolder holder, int pos) {
        BoardsListModel listData = dataList.get(pos);
        holder.board_name.setText(listData.getBoard_name());
        holder.board_url.setText(listData.getBoard_url());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
