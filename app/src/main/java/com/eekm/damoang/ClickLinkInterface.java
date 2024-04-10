package com.eekm.damoang;

import static android.content.ContentValues.TAG;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class ClickLinkInterface {
    Context mContext;
    ClickLinkInterface(Context activity) {
        mContext = activity;
    }
    @JavascriptInterface
    public void clickArticleLink(String src) {
        final String url = src;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        mContext.startActivity(intent);
    }
}