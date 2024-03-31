package com.eekm.damoang;

import android.util.Log;
import android.webkit.JavascriptInterface;

public class CheckSucceedInterface {
    @JavascriptInterface
    public void getHtml(String html) {
        Log.d("Test", "html: " + html);
    }
}