package com.eekm.damoang.models.board;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class BoardListModel implements Parcelable {
    private String board_name;
    private String board_url;

    public BoardListModel(String board_name, String board_url) {
        this.board_name = board_name;
        this.board_url = board_url;
    }

    protected BoardListModel(Parcel in) {
        board_name = in.readString();
        board_url = in.readString();
    }

    public static final Creator<BoardListModel> CREATOR = new Creator<BoardListModel>() {
        @Override
        public BoardListModel createFromParcel(Parcel in) {
            return new BoardListModel(in);
        }

        @Override
        public BoardListModel[] newArray(int size) {
            return new BoardListModel[size];
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
