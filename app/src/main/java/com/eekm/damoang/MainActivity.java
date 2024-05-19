package com.eekm.damoang;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eekm.damoang.databinding.ActivityMainBinding;
import com.eekm.damoang.models.board.BoardListAdapter;
import com.eekm.damoang.models.board.BoardListModel;
import com.eekm.damoang.util.ArticleParser;
import com.eekm.damoang.contents.BoardsList;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private @androidx.annotation.NonNull ActivityMainBinding binding;
    private BoardsList mBoardsList;
    private BoardListAdapter mAdapter;
    private RecyclerView mBoardsRecyclerView;
    private ArrayList<BoardListModel> mDatas = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBoardListMain.toolbar);

        this.invalidateOptionsMenu();

        mBoardsRecyclerView = findViewById(R.id.rv_boards_list);

        mAdapter = new BoardListAdapter(mDatas);

        mBoardsRecyclerView.setAdapter(mAdapter);
        mBoardsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mBoardsRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, VERTICAL));

        if (savedInstanceState != null) {
            mDatas = savedInstanceState.getParcelableArrayList("boardsList");

            adaptRecyclerView();
        } else {
            subscribeParseRulesSingle();
        }

        if (mDatas.isEmpty()) {
            subscribeSingle();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("boardsList", mDatas);
        Log.d("Saved", mDatas.toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mDatas = savedInstanceState.getParcelableArrayList("boardsList");
        Log.d("Restored", mDatas.toString());

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pv_load_list);
        progressBar.setVisibility(View.INVISIBLE);
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

    @SuppressLint("CheckResult")
    public void subscribeSingle() {
        getDamoangBoardsList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    if (!result.lists.isEmpty()) {
                        mDatas = new ArrayList<>();

                        for (int i = 0; i < result.lists.size(); i++) {
                            BoardListModel item = result.lists.get(i);
                            String board_name = item.getBoard_name();
                            String board_url = item.getBoard_url();

                            mDatas.add(new BoardListModel(board_name, board_url));
                        }

                        adaptRecyclerView();
                    }
                });
    }

    @SuppressLint("CheckResult")
    public void subscribeParseRulesSingle() {
        getDamoangParserRules().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    SharedPreferences preferences = getSharedPreferences("LocalPref", MODE_PRIVATE);
                    String savedParseRules = preferences.getString("damoangParseRules", "");

                    if (savedParseRules.isEmpty()) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("damoangParseRules", result.getJsonResult());
                        editor.apply();
                    } else if (result.isRulesOutdated(savedParseRules)) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("damoangParseRules", result.getJsonResult());
                        editor.apply();

                        Log.d(TAG, "Updated outdated rules");
                    }
                });
    }

    public void adaptRecyclerView() {
        mAdapter = new BoardListAdapter(mDatas);
        mAdapter.setOnItemClickEventListener(new BoardListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                final BoardListModel item = mDatas.get(pos);
                /* Toast.makeText(getApplicationContext(), "pos: " + pos +
                        ", data: " + item.getBoard_url(), Toast.LENGTH_SHORT).show();
                 */

                Intent intent;

                if (Objects.equals(item.getBoard_url(), "https://damoang.net/gallery")) {
                    intent = new Intent(MainActivity.this, GalleryListActivity.class);
                } else {
                    intent = new Intent(MainActivity.this, DocListActivity.class);
                }
                intent.putExtra("board_url", item.getBoard_url());
                intent.putExtra("board_name", item.getBoard_name());
                MainActivity.this.startActivity(intent);
            }
        });
        mBoardsRecyclerView.setAdapter(mAdapter);
        mBoardsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.pv_load_list);
        progressBar.setVisibility(View.INVISIBLE);
    }

    public Single<BoardsList> getDamoangBoardsList() {
        return Single.fromCallable(() -> {
            mBoardsList = new BoardsList();
            mBoardsList.getBoardsList();

            return mBoardsList;
        });
    }

    public Single<ArticleParser> getDamoangParserRules() {
        return Single.fromCallable(() -> {
            // 파서 룰 정의 파일을 받아옵니다
            ArticleParser parser = new ArticleParser();
            parser.getParserData();

            return parser;
        });
    }
}