package com.eekm.damoang;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.eekm.damoang.databinding.ActivityDocListBinding;
import com.eekm.damoang.databinding.ActivityMainBinding;
import com.eekm.damoang.ui.articles.ArticleListAdapter;
import com.eekm.damoang.ui.articles.ArticleListModel;
import com.eekm.damoang.util.ArticlesList;
import com.eekm.damoang.util.BoardsList;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DocListActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private @androidx.annotation.NonNull ActivityDocListBinding binding;

    private RecyclerView mPostRecyclerView;

    private ArticleListAdapter mAdapter;
    private ArrayList<ArticleListModel> mDatas = new ArrayList<>();
    private ArticlesList mArticlesList;

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

        binding = ActivityDocListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        this.invalidateOptionsMenu();
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

        mAdapter = new ArticleListAdapter(mDatas);
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
        mAdapter = new ArticleListAdapter(mDatas);
        mAdapter.setOnItemClickEventListener(new ArticleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final ArticleListModel item = mDatas.get(pos);
                /* Toast.makeText(getApplicationContext(), "pos: " + pos +
                        ", data: " + item.getDoc_id(), Toast.LENGTH_SHORT).show();
                 */

                Intent intent = new Intent(DocListActivity.this, ViewArticleActivity.class);
                intent.putExtra("doc_id", item.getDoc_id());
                DocListActivity.this.startActivity(intent);
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

    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem actionLogout = (MenuItem) menu.findItem(R.id.action_logout);
        MenuItem showUsername = (MenuItem) menu.findItem(R.id.menu_username);
        actionLogout.setVisible(false);
        showUsername.setVisible(false);

        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mDatas", mDatas);
        outState.putInt("currentPage", currentPage);
        Log.d("Saved", mDatas.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mDatas = savedInstanceState.getParcelableArrayList("mDatas");
        currentPage = savedInstanceState.getInt("currentPage");
        Log.d("Restored", mDatas.toString());

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pv_load_list);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void initScrollListener() {
        mPostRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

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
        Log.d("test", "load more!");

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
                        ArticleListModel item = result.links.get(i);
                        String link = item.getDoc_id();
                        String title = item.getDoc_title();
                        String nick = item.getDoc_nickname();
                        String recommend = item.getDoc_recommended();
                        String views = item.getDoc_views();
                        String datetime = item.getDoc_datetime();

                        mDatas.add(new ArticleListModel(link, title, nick,
                                recommend, views, datetime));
                    }

                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.pv_load_list);
                    progressBar.setVisibility(View.INVISIBLE);
                    mSwipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "리스트를 불러오는 도중 오류가 발생했습니다.",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(
                            DocListActivity.this, CfChallengeActivity.class);
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

    public Observable<ArticlesList> getDamoangData(){
        return Observable.fromCallable(() -> {
            SharedPreferences preferences = getSharedPreferences("LocalPref", MODE_PRIVATE);
            String savedClearance = preferences.getString("cfClearance", "");
            String savedUa = preferences.getString("currentUserAgent", "");

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

            Intent intent = getIntent();
            String board_url = intent.getStringExtra("board_url");
            Log.d("BoardURL", board_url);

            mArticlesList = new ArticlesList();
            mArticlesList.getList(board_url, savedUa, savedClearance, currentPage);

            return mArticlesList;
        });
    }
}