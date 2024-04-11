package com.eekm.damoang;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.eekm.damoang.util.ArticleImage;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ViewImageActivity extends AppCompatActivity {
    SubsamplingScaleImageView imageView;
    ArticleImage mArticleImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.BLACK);

        imageView = findViewById(R.id.iv_view_content_image);

        subscribeObservable();
    }

    @SuppressLint("CheckResult")
    public void subscribeObservable() {
        getImageData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    imageView.setImage(ImageSource.bitmap(result.bmp));
                });
    }

    public Observable<ArticleImage> getImageData(){
        return Observable.fromCallable(() -> {
            Intent intent = getIntent();
            String url = intent.getStringExtra("imageSrc");
            mArticleImage = new ArticleImage();
            mArticleImage.getImage(url);

            return mArticleImage;
        });
    }
}