package com.eekm.damoang.models.boards;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class BoardsListModel implements Parcelable {
    private String board_name;
    private String board_url;

    public BoardsListModel(String board_name, String board_url) {
        this.board_name = board_name;
        this.board_url = board_url;
    }

    protected BoardsListModel(Parcel in) {
        board_name = in.readString();
        board_url = in.readString();
    }

    public static final Creator<BoardsListModel> CREATOR = new Creator<BoardsListModel>() {
        @Override
        public BoardsListModel createFromParcel(Parcel in) {
            return new BoardsListModel(in);
        }

        @Override
        public BoardsListModel[] newArray(int size) {
            return new BoardsListModel[size];
        }
    };

    public String getBoard_name() {
        return board_name;
    }

    public void setBoard_name(String board_name) {
        this.board_name = board_name;
    }

    public String getBoard_url() {
        return board_url;
    }

    public void setBoard_url(String board_url) {
        this.board_url = board_url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(board_name);
        parcel.writeString(board_url);
    }
}
