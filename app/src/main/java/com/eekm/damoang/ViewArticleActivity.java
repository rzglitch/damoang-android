package com.eekm.damoang;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
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

        mTitleBar = findViewById(R.id.c_view_title_bar);
        mCommentRecyclerView = findViewById(R.id.rv_comments);

        mAdapter = new ArticleCommentsAdapter(mCommentDatas);
        mCommentRecyclerView.setAdapter(mAdapter);
        mCommentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (savedInstanceState != null) {
            mCommentDatas = savedInstanceState.getParcelableArrayList("mCommentDatas");

            adaptRecyclerView();
        }

        if (mCommentDatas.isEmpty()) {
            subscribeObservable();
        }

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

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.c_view_title_bar);
        constraintLayout.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("CheckResult")
    public void subscribeObservable() {
        getDamoangViewData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    if (!result.links.isEmpty()) {
                        ArticleDocModel item = result.links.get(0);
                        String title = item.getDoc_title();
                        String views = item.getDoc_views();
                        String nick = item.getDoc_nickname();
                        String recommend = item.getDoc_recommended();
                        String datetime = item.getDoc_datetime();
                        String content = item.getDoc_content();

                        TextView tv_title = (TextView)findViewById(R.id.va_doc_title);
                        TextView tv_views = (TextView)findViewById(R.id.va_doc_views);
                        TextView tv_nick = (TextView)findViewById(R.id.va_doc_nickname);
                        TextView tv_recommend = (TextView)findViewById(R.id.va_doc_recommended);
                        TextView tv_datetime = (TextView)findViewById(R.id.va_doc_datetime);

                        tv_title.setText(title);
                        tv_views.setText(views);
                        tv_nick.setText(nick);
                        tv_recommend.setText(recommend);
                        tv_datetime.setText(datetime);

                        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.c_view_title_bar);
                        constraintLayout.setVisibility(View.VISIBLE);

                        String htmlData = "<!DOCTYPE html>\n" +
                                "<html>\n" +
                                "<head>\n" +
                                "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\">\n" +
                                "<title>View document</title>\n" +
                                "</head>\n" +
                                "<body style=\"padding: 8px\">" + content + "</body>\n" +
                                "</html>";

                        webView.loadData(htmlData, "text/html; charset=utf-8", "UTF-8");


                        mCommentDatas = new ArrayList<>();
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

                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pb_view_article);
                        progressBar.setVisibility(View.INVISIBLE);
                        mSwipeRefreshLayout.setRefreshing(false);
                    } else {
                        Toast.makeText(getApplicationContext(), "서버에 오류가 발생했습니다.",
                                Toast.LENGTH_SHORT).show();
                    }

                    adaptRecyclerView();
                });
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