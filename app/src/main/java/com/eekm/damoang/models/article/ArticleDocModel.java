package com.eekm.damoang.models.article;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ArticleDocModel implements Parcelable {
    private String doc_title;
    private String doc_nickname;
    private String doc_recommended;
    private String doc_views;
    private String doc_datetime;
    private String doc_content;

    public ArticleDocModel(String doc_title, String doc_nickname, String doc_recommended,
                            String doc_views, String doc_datetime, String doc_content) {
        this.doc_title = doc_title;
        this.doc_nickname = doc_nickname;
        this.doc_recommended = doc_recommended;
        this.doc_views = doc_views;
        this.doc_datetime = doc_datetime;
        this.doc_content = doc_content;
    }

    protected ArticleDocModel(Parcel in) {
        doc_title = in.readString();
        doc_nickname = in.readString();
        doc_recommended = in.readString();
        doc_views = in.readString();
        doc_datetime = in.readString();
        doc_content = in.readString();
    }

    public static final Parcelable.Creator<ArticleDocModel> CREATOR = new Parcelable.Creator<ArticleDocModel>() {
        @Override
        public ArticleDocModel createFromParcel(Parcel in) {
            return new ArticleDocModel(in);
        }

        @Override
        public ArticleDocModel[] newArray(int size) {
            return new ArticleDocModel[size];
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

    public String getDoc_content() {
        return doc_content;
    }

    public void setDoc_content(String doc_content) {
        this.doc_content = doc_content;
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
        parcel.writeString(doc_content);
    }
}
