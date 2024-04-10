package com.eekm.damoang.models.gallery;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class GalleryListModel implements Parcelable {
    private String doc_title;
    private String doc_nickname;
    private String doc_recommended;
    private String doc_views;
    private String doc_datetime;
    private String doc_id;
    private String doc_thumb;

    public GalleryListModel(String doc_id, String doc_title, String doc_nickname, String doc_recommended,
                            String doc_views, String doc_datetime, String doc_thumb) {
        this.doc_title = doc_title;
        this.doc_nickname = doc_nickname;
        this.doc_recommended = doc_recommended;
        this.doc_views = doc_views;
        this.doc_datetime = doc_datetime;
        this.doc_id = doc_id;
        this.doc_thumb = doc_thumb;
    }

    protected GalleryListModel(Parcel in) {
        doc_title = in.readString();
        doc_nickname = in.readString();
        doc_recommended = in.readString();
        doc_views = in.readString();
        doc_datetime = in.readString();
        doc_id = in.readString();
        doc_thumb = in.readString();
    }

    public static final Creator<GalleryListModel> CREATOR = new Creator<GalleryListModel>() {
        @Override
        public GalleryListModel createFromParcel(Parcel in) {
            return new GalleryListModel(in);
        }

        @Override
        public GalleryListModel[] newArray(int size) {
            return new GalleryListModel[size];
        }
    };

    public String getDoc_title() {
        return doc_title;
    }

    public void setDoc_title(String doc_title) {
        this.doc_title = doc_title;
    }

    public String getDoc_nickname() {
        return doc_nickname;
    }

    public void setDoc_nickname(String doc_nickname) {
        this.doc_nickname = doc_nickname;
    }

    public String getDoc_recommended() {
        return doc_recommended;
    }

    public void setDoc_recommended(String doc_recommended) {
        this.doc_recommended = doc_recommended;
    }

    public String getDoc_views() {
        return doc_views;
    }

    public void setDoc_views(String doc_views) {
        this.doc_views = doc_views;
    }

    public String getDoc_datetime() {
        return doc_datetime;
    }

    public void setDoc_datetime(String doc_datetime) {
        this.doc_datetime = doc_datetime;
    }

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
    }

    public String getDoc_thumb() {
        return doc_thumb;
    }

    public void setDoc_thumb(String doc_thumb) {
        this.doc_thumb = doc_thumb;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(doc_title);
        parcel.writeString(doc_nickname);
        parcel.writeString(doc_recommended);
        parcel.writeString(doc_views);
        parcel.writeString(doc_datetime);
        parcel.writeString(doc_id);
        parcel.writeString(doc_thumb);
    }
}
