package com.eekm.damoang;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class ViewImageInterface {
    Context mContext;
    ViewImageInterface(Context activity) {
        mContext = activity;
    }
    @JavascriptInterface
    public void getImageSrc(String src) {
        Activity activity = new Activity();
        Intent intent = new Intent(mContext, ViewImageActivity.class);
        intent.putExtra("imageSrc", src);
        mContext.startActivity(intent);
        Log.d("Test", "src: " + src);
    }
}