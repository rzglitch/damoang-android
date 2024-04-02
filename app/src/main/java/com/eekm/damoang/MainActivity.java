package com.eekm.damoang;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eekm.damoang.databinding.ActivityMainBinding;
import com.eekm.damoang.ui.articles.ArticleListAdapter;
import com.eekm.damoang.ui.articles.ArticleListModel;
import com.eekm.damoang.util.ArticlesList;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private RecyclerView mPostRecyclerView;

    private ArticleListAdapter mAdapter;
    private ArrayList<ArticleListModel> mDatas = new ArrayList<>();
    private ArticlesList mArticlesList;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String cfClearance;
    private Boolean isLoadMore;
    private int loadedPage = 1;

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

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController,
                mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        cfClearance = null;

        mPostRecyclerView = findViewById(R.id.rv_article_list);

        mAdapter = new ArticleListAdapter(mDatas);
        mAdapter.setOnItemClickEventListener(new ArticleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final ArticleListModel item = mDatas.get(pos);
                Toast.makeText(getApplicationContext(), "pos: " + pos +
                        ", data: " + item.getDoc_id(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, ViewArticleActivity.class);
                intent.putExtra("doc_id", item.getDoc_id());
                MainActivity.this.startActivity(intent);
            }
        });
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
                Toast.makeText(getApplicationContext(), "pos: " + pos +
                        ", data: " + item.getDoc_id(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, ViewArticleActivity.class);
                intent.putExtra("doc_id", item.getDoc_id());
                MainActivity.this.startActivity(intent);
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
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(
                this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("mDatas", mDatas);
        Log.d("Saved", mDatas.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mDatas = savedInstanceState.getParcelableArrayList("mDatas");
        Log.d("Restored", mDatas.toString());
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
                    Toast.makeText(getApplicationContext(), "서버에 오류가 발생했습니다.",
                            Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(
                            MainActivity.this, CfChallengeActivity.class);
                    startActivityResult.launch(intent);
                }
                if (!isLoadMore) {
                    adaptRecyclerView();
                }
                if (isLoadMore) {
                    mAdapter.notifyDataSetChanged();
                    isLoadMore = false;
                }
            });
    }

    public Observable<ArticlesList> getDamoangData(){
        return Observable.fromCallable(() -> {
            SharedPreferences preferences = getSharedPreferences("LocalPref", MODE_PRIVATE);
            String savedClearance = preferences.getString("cfClearance", "");
            String savedUa = preferences.getString("currentUserAgent", "");

            if (isLoadMore) {
                loadedPage++;
            }

            Log.d("UserAgent", savedUa);

            if (savedUa == "") {
                savedUa = "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, " +
                        "like Gecko) Damoang/1.0";
            }

            mArticlesList = new ArticlesList();
            mArticlesList.getList("free", savedUa, savedClearance, loadedPage);

            return mArticlesList;
        });
    }
}