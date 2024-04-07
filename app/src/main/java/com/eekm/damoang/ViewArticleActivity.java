package com.eekm.damoang;

import static android.app.PendingIntent.getActivity;
import static android.content.ContentValues.TAG;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

import static java.security.AccessController.getContext;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eekm.damoang.ui.articles.ArticleCommentsAdapter;
import com.eekm.damoang.ui.articles.ArticleCommentsModel;
import com.eekm.damoang.ui.articles.ArticleDocModel;
import com.eekm.damoang.ui.articles.ArticleListAdapter;
import com.eekm.damoang.ui.articles.ArticleListModel;
import com.eekm.damoang.util.ArticleComments;
import com.eekm.damoang.util.ArticleView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ViewArticleActivity extends AppCompatActivity {
    private RecyclerView mCommentRecyclerView;
    private ConstraintLayout mTitleBar;
    private WebView webView;

    private ArticleCommentsAdapter mAdapter;
    private ArrayList<ArticleCommentsModel> mCommentDatas = new ArrayList<>();
    private ArticleView mArticleView;
    private ArticleComments mArticleComments;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String cfClearance;

    private String doc_title = "";
    private String doc_views = "";
    private String doc_nick = "";
    private String doc_recommend = "";
    private String doc_datetime = "";
    private String doc_content = "";

    private TextView tv_title;
    private TextView tv_views;
    private TextView tv_nick;
    private TextView tv_recommend;
    private TextView tv_datetime;

    private ActivityResultLauncher<Intent> startActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 0){
                        String clearance = result.getData().getStringExtra("cf_clearance");
                        cfClearance = clearance;

                        subscribeObservable();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_article);

        tv_title = (TextView)findViewById(R.id.va_doc_title);
        tv_views = (TextView)findViewById(R.id.va_doc_views);
        tv_nick = (TextView)findViewById(R.id.va_doc_nickname);
        tv_recommend = (TextView)findViewById(R.id.va_doc_recommended);
        tv_datetime = (TextView)findViewById(R.id.va_doc_datetime);

        mTitleBar = findViewById(R.id.c_view_title_bar);
        mCommentRecyclerView = findViewById(R.id.rv_comments);

        mAdapter = new ArticleCommentsAdapter(mCommentDatas);
        mCommentRecyclerView.setAdapter(mAdapter);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCommentRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, VERTICAL));

        mSwipeRefreshLayout = findViewById(R.id.sr_article_view);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                subscribeObservable();
            }
        });

        Intent intent = getIntent();
        String doc_id = intent.getStringExtra("doc_id");

        webView = (WebView) findViewById(R.id.wv_document);

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
        webView.setBackgroundColor(Color.TRANSPARENT);

        webView.addJavascriptInterface(new ViewImageInterface(this), "viewImage");

        String nightCss = "#000";

        int nightMode =
                getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        if (nightMode == Configuration.UI_MODE_NIGHT_YES) {
            nightCss = "#ccc";
        }

        String finalNightCss = nightCss;
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl(
                        "javascript:document.body.style.setProperty(\"color\", \"" + finalNightCss + "\");"
                );
            }
        });

        if (savedInstanceState != null) {
            mCommentDatas = savedInstanceState.getParcelableArrayList("mCommentDatas");
            doc_title = savedInstanceState.getString("doc_title");
            doc_views = savedInstanceState.getString("doc_views");
            doc_nick = savedInstanceState.getString("doc_nick");
            doc_recommend = savedInstanceState.getString("doc_recommend");
            doc_datetime = savedInstanceState.getString("doc_datetime");
            doc_content = savedInstanceState.getString("doc_content");

            initialDatas();
        }

        if (mCommentDatas.isEmpty()) {
            subscribeObservable();
        }

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.c_view_title_bar);
        constraintLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mCommentDatas", mCommentDatas);
        outState.putString("doc_title", doc_title);
        outState.putString("doc_views", doc_views);
        outState.putString("doc_nick", doc_nick);
        outState.putString("doc_recommend", doc_recommend);
        outState.putString("doc_datetime", doc_datetime);
        outState.putString("doc_content", doc_content);

        Log.d(TAG, "Saved");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCommentDatas = savedInstanceState.getParcelableArrayList("mCommentDatas");
        doc_title = savedInstanceState.getString("doc_title");
        doc_views = savedInstanceState.getString("doc_views");
        doc_nick = savedInstanceState.getString("doc_nick");
        doc_recommend = savedInstanceState.getString("doc_recommend");
        doc_datetime = savedInstanceState.getString("doc_datetime");
        doc_content = savedInstanceState.getString("doc_content");

        initialDatas();

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pb_view_article);
        progressBar.setVisibility(View.INVISIBLE);

        Log.d(TAG, "Restored");
    }

    @SuppressLint("CheckResult")
    public void subscribeObservable() {
        getDamoangViewData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    if (!result.links.isEmpty()) {
                        ArticleDocModel item = result.links.get(0);
                        doc_title = item.getDoc_title();
                        doc_views = item.getDoc_views();
                        doc_nick = item.getDoc_nickname();
                        doc_recommend = item.getDoc_recommended();
                        doc_datetime = item.getDoc_datetime();
                        doc_content = item.getDoc_content();

                        initialDatas();

                        for (int i = 0; i < result.comments.size(); i++) {
                            ArticleCommentsModel cmt_item = result.comments.get(i);
                            String cmt_link = cmt_item.getDoc_id();
                            String cmt_content = cmt_item.getDoc_content();
                            String cmt_nick = cmt_item.getDoc_nickname();
                            String cmt_recommend = cmt_item.getDoc_recommended();
                            String cmt_datetime = cmt_item.getDoc_datetime();

                            mCommentDatas.add(new ArticleCommentsModel(cmt_link, cmt_content, cmt_nick,
                                    cmt_recommend, cmt_datetime));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "리스트를 불러오는 도중 오류가 발생했습니다.",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(
                                ViewArticleActivity.this, CfChallengeActivity.class);
                        startActivityResult.launch(intent);
                    }
                });
    }

    public void initialDatas() {
        tv_title.setText(doc_title);
        tv_views.setText(doc_views);
        tv_nick.setText(doc_nick);
        tv_recommend.setText(doc_recommend);
        tv_datetime.setText(doc_datetime);

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.c_view_title_bar);
        constraintLayout.setVisibility(View.VISIBLE);

        String contentsHtml = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\">\n" +
                "<title>View document</title>\n" +
                "<style>\n" +
                "* {\n" +
                "    font-size: 16px !important;\n" +
                "}\n" +
                "img {\n" +
                "    max-width: 100%;\n" +
                "    height: auto;\n" +
                "}\n" +
                "video, audio {\n" +
                "    max-width: 100%;\n" +
                "}\n" +
                "iframe {\n" +
                "    max-width: 100%;\n" +
                "}" +
                "</style>" +
                "</head>\n" +
                "<body style=\"padding: 8px\">" + doc_content + "</body>\n" +
                "<script type=\"text/javascript\">\n" +
                "(function() {\n" +
                "    var imgs = document.querySelectorAll('img');\n" +
                "    for (var i = 0; i < imgs.length; i++) {\n" +
                "        imgs[i].setAttribute('onclick','dGetImage(this.getAttribute(\"src\"))');\n" +
                "    }\n" +
                "\n" +
                "})();\n" +
                "\n" +
                "function dGetImage(src) {\n" +
                "    viewImage.getImageSrc(src);\n" +
                "}\n" +
                "</script>" +
                "</html>";

        webView.loadData(contentsHtml, "text/html; charset=utf-8", "UTF-8");

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pb_view_article);
        progressBar.setVisibility(View.INVISIBLE);
        mSwipeRefreshLayout.setRefreshing(false);

        adaptRecyclerView();
    }

    public void adaptRecyclerView() {
        mAdapter = new ArticleCommentsAdapter(mCommentDatas);
        mCommentRecyclerView.setAdapter(mAdapter);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public Observable<ArticleView> getDamoangViewData(){
        return Observable.fromCallable(() -> {
            SharedPreferences preferences = getSharedPreferences("LocalPref", MODE_PRIVATE);
            String savedClearance = preferences.getString("cfClearance", "");
            String savedUa = preferences.getString("currentUserAgent", "");

            Log.d("UserAgent", savedUa);
            Log.d("Clearance", savedClearance);

            if (savedUa == "") {
                savedUa = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) Damoang/1.0";
            }

            Intent intent = getIntent();
            String doc_id = intent.getStringExtra("doc_id");

            Log.d("get", doc_id);

            mArticleView = new ArticleView();
            mArticleView.getView(doc_id, savedUa, savedClearance);

            return mArticleView;
        });
    }
}