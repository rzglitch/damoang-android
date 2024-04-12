package com.eekm.damoang.models.articles;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ArticleCommentsModel implements Parcelable {
    private String doc_content;
    private String doc_nickname;
    private String doc_recommended;
    private String doc_datetime;
    private String doc_id;
    private String doc_image;

    public ArticleCommentsModel(String doc_id, String doc_content, String doc_image, String doc_nickname,
                                String doc_recommended, String doc_datetime) {
        this.doc_content = doc_content;
        this.doc_nickname = doc_nickname;
        this.doc_recommended = doc_recommended;
        this.doc_datetime = doc_datetime;
        this.doc_id = doc_id;
        this.doc_image = doc_image;
    }

    protected ArticleCommentsModel(Parcel in) {
        doc_content = in.readString();
        doc_nickname = in.readString();
        doc_recommended = in.readString();
        doc_datetime = in.readString();
        doc_id = in.readString();
        doc_image = in.readString();
    }

    public static final Parcelable.Creator<ArticleCommentsModel> CREATOR = new Parcelable.Creator<ArticleCommentsModel>() {
        @Override
        public ArticleCommentsModel createFromParcel(Parcel in) {
            return new ArticleCommentsModel(in);
        }

        @Override
        public ArticleCommentsModel[] newArray(int size) {
            return new ArticleCommentsModel[size];
        }
    };

    public String getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(String doc_id) {
        this.doc_id = doc_id;
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

    public String getDoc_datetime() {
        return doc_datetime;
    }

    public void setDoc_datetime(String doc_datetime) {
        this.doc_datetime = doc_datetime;
    }

    public String getDoc_content() {
        return doc_content;
    }

    public void setDoc_content(String doc_content) {
        this.doc_content = doc_content;
    }

    public String getDoc_image() { return doc_image; }

    public void setDoc_image(String doc_image) {
        this.doc_image = doc_image;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(doc_content);
        parcel.writeString(doc_nickname);
        parcel.writeString(doc_recommended);
        parcel.writeString(doc_datetime);
        parcel.writeString(doc_id);
        parcel.writeString(doc_image);
    }
}
