package com.eekm.damoang;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eekm.damoang.databinding.ActivityGalleryListBinding;
import com.eekm.damoang.models.article.GalleryListAdapter;
import com.eekm.damoang.models.article.GalleryListModel;
import com.eekm.damoang.util.ArticleParser;
import com.eekm.damoang.contents.GalleriesList;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class GalleryListActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private @androidx.annotation.NonNull ActivityGalleryListBinding binding;

    private RecyclerView mPostRecyclerView;

    private GalleryListAdapter mAdapter;
    private ArrayList<GalleryListModel> mDatas = new ArrayList<>();
    private GalleriesList mArticlesList;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String cfClearance;
    private Boolean isLoadMore;
    private int currentPage = 1;
    private boolean isRefresh = false;

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

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGalleryListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        cfClearance = null;

        Intent intent = getIntent();
        String board_name = intent.getStringExtra("board_name");
        setTitle(board_name);

        mPostRecyclerView = findViewById(R.id.rv_article_list);

        mAdapter = new GalleryListAdapter(mDatas);
        mPostRecyclerView.setAdapter(mAdapter);
        mPostRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mPostRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, VERTICAL));

        if (savedInstanceState != null) {
            mDatas = savedInstanceState.getParcelableArrayList("mDatas");

            adaptRecyclerView();
        }

        if (mDatas.isEmpty()) {
            subscribeObservable();
        }

        mSwipeRefreshLayout = findViewById(R.id.sr_article_list);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRefresh = true;
                subscribeObservable();
            }
        });

        isLoadMore = false;

        initScrollListener();
    }

    public void adaptRecyclerView() {
        mAdapter = new GalleryListAdapter(mDatas);
        mAdapter.setOnItemClickEventListener(new GalleryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final GalleryListModel item = mDatas.get(pos);

                Intent intent = new Intent(GalleryListActivity.this, ViewArticleActivity.class);
                intent.putExtra("doc_id", item.getDoc_id());
                GalleryListActivity.this.startActivity(intent);
            }
        });
        mPostRecyclerView.setAdapter(mAdapter);
        mPostRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mDatas", mDatas);
        outState.putInt("currentPage", currentPage);
        Log.d("Saved", "mDatas: " + mDatas + "\ncurrentPage: " + currentPage);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mDatas = savedInstanceState.getParcelableArrayList("mDatas");
        currentPage = savedInstanceState.getInt("currentPage");
        Log.d("Restored", "mDatas: " + mDatas + "\ncurrentPage: " + currentPage);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pv_load_list);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void initScrollListener() {
        mPostRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @SuppressLint("CheckResult")
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager =
                        (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoadMore) {
                    if (
                            layoutManager != null &&
                                    layoutManager.findLastCompletelyVisibleItemPosition() ==
                                            mDatas.size() - 1) {
                        // Scrolled last item
                        loadMore();
                        isLoadMore = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        Log.d("loadMore", "load next list!");

        isLoadMore = true;
        subscribeObservable();
    }

    @SuppressLint("CheckResult")
    public void subscribeObservable() {
        getDamoangData().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    if (!result.links.isEmpty()) {
                        if (!isLoadMore) {
                            mDatas = new ArrayList<>();
                        }
                        for (int i = 1; i < result.links.size(); i++) {
                            GalleryListModel item = result.links.get(i);
                            String link = item.getDoc_id();
                            String title = item.getDoc_title();
                            String nick = item.getDoc_nickname();
                            String recommend = item.getDoc_recommended();
                            String views = item.getDoc_views();
                            String datetime = item.getDoc_datetime();
                            String thumb = item.getDoc_thumb();

                            mDatas.add(new GalleryListModel(link, title, nick,
                                    recommend, views, datetime, thumb));
                        }

                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pv_load_list);
                        progressBar.setVisibility(View.INVISIBLE);
                        mSwipeRefreshLayout.setRefreshing(false);
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "리스트를 불러오는 도중 오류가 발생했습니다.",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(
                                GalleryListActivity.this, CfChallengeActivity.class);
                        startActivityResult.launch(intent);
                    }
                    if (!isLoadMore) {
                        adaptRecyclerView();
                    }
                    if (isLoadMore) {
                        mAdapter.notifyDataSetChanged();
                        isLoadMore = false;
                    }
                    isRefresh = false;
                });
    }

    public Observable<GalleriesList> getDamoangData(){
        return Observable.fromCallable(() -> {
            SharedPreferences preferences = getSharedPreferences("LocalPref", MODE_PRIVATE);
            String savedClearance = preferences.getString("cfClearance", "");
            String savedUa = preferences.getString("currentUserAgent", "");
            String savedParseRules = preferences.getString("damoangParseRules", "");

            if (isLoadMore) {
                currentPage++;
            }

            if (isRefresh) {
                currentPage = 1;
            }

            Log.d("UserAgent", savedUa);

            if (savedUa == "") {
                savedUa = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, " +
                        "like Gecko) Damoang/1.0";
            }

            if (savedParseRules.isEmpty()) {
                ArticleParser parser = new ArticleParser();

                String parserData = parser.getParserData();

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("damoangParseRules", parserData);
                editor.apply();

                savedParseRules = parserData;
            }

            Intent intent = getIntent();
            String board_url = intent.getStringExtra("board_url");
            Log.d("BoardURL", board_url);

            mArticlesList = new GalleriesList();
            mArticlesList.setSavedParseRules(savedParseRules);
            mArticlesList.getList(board_url, savedUa, savedClearance, currentPage);

            return mArticlesList;
        });
    }
}