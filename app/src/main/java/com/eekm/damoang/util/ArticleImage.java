package com.eekm.damoang.util;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ArticleImage {
    public Bitmap bmp;

    public void getImage(String img_url) {
        bmp = null;

        Log.d(TAG, "URL: " + img_url);

        try {
            URL url = new URL(img_url);
            bmp = BitmapFactory.decodeStream(
                    url.openConnection().getInputStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
