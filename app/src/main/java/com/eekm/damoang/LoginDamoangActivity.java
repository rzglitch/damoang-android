package com.eekm.damoang;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class LoginDamoangActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cf_challenge);

        WebView webView = (WebView) findViewById(R.id.wv_cf_test);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);

        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setBuiltInZoomControls(false);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);

        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                Log.d("WebView", "your current url when webpage loading.." + url);
            }

            /* @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WebView", "your current url when webpage loading.. finish" + url);
                super.onPageFinished(view, url);

                String cfClearance = getCookie(url, "cf_clearance");
                String phpSessId = getCookie(url, "PHPSESSID");

                if (phpSessId != null) {
                    SharedPreferences preferences = getSharedPreferences("LocalPref", MODE_PRIVATE);

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("cfClearance", cfClearance);
                    editor.putString("currentUserAgent", view.getSettings().getUserAgentString());
                    editor.commit();

                    Intent intent = new Intent();
                    intent.putExtra("cf_clearance", cfClearance);
                    setResult(0, intent);

                    webView.clearCache(true);
                    webView.clearHistory();
                    webView.clearFormData();

                    CookieManager cookieManager = CookieManager.getInstance();
                    cookieManager.removeAllCookies(new ValueCallback() {
                        @Override
                        public void onReceiveValue(Object o) {
                            Log.d("onReceiveValue", o.toString());
                        }
                    });
                    cookieManager.getInstance().flush();

                    finish();
                }
            }

             */

            @Override
            public void onLoadResource(WebView view, String url) {
                // TODO Auto-generated method stub
                super.onLoadResource(view, url);
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Log.d(TAG, url);
                /*
                if (url.startsWith("https://damoang.net")) {
                    Log.d(TAG, "OK!!");
                    finish();
                }

                 */
                return false;
            }
        });

        webView.loadUrl("https://damoang.net/bbs/login.php");
    }

    public String getCookie(String siteName, String cookieName){
        String CookieValue = null;

        CookieManager cookieManager = CookieManager.getInstance();
        String cookies = cookieManager.getCookie(siteName);

        if (cookies != null) {
            String[] temp = cookies.split(";");
            for (String ar1 : temp) {
                if (ar1.contains(cookieName)) {
                    String[] temp1 = ar1.split("=");
                    CookieValue = temp1[1];
                    break;
                }
            }
        }
        return CookieValue;
    }
}
