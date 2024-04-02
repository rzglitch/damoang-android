package com.eekm.damoang.util;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.eekm.damoang.ui.articles.ArticleListModel;
import com.eekm.damoang.ui.boards.BoardsListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BoardsList {
    public List<BoardsListModel> lists;

    public void getBoardsList() {
        List<BoardsListModel> linkList = new ArrayList<>();
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            String url_text = "https://dkh1.mycafe24.com/damoangdroid/damoangList.json";
            URL url = new URL(url_text);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");

            }

            String result = buffer.toString();

            reader.close();

            try {
                JSONObject jsonObject = new JSONObject(result);

                String communityList = jsonObject.getString("communityList");
                JSONArray communityListArray = new JSONArray(communityList);

                for (int i = 0; i < communityListArray.length(); i++) {
                    JSONObject communityListObject = communityListArray.getJSONObject(i);
                    String communityName = communityListObject.getString("name");
                    String communityUrl = communityListObject.getString("url");
                    linkList.add(new BoardsListModel(communityName, communityUrl));
                }

                String somoimList = jsonObject.getString("somoimList");
                JSONArray somoimListArray = new JSONArray(somoimList);

                for (int i = 0; i < somoimListArray.length(); i++) {
                    JSONObject somoimListObject = somoimListArray.getJSONObject(i);
                    String somoimName = somoimListObject.getString("name");
                    String somoimUrl = somoimListObject.getString("url");
                    linkList.add(new BoardsListModel(somoimName, somoimUrl));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        this.lists = linkList;
    }
}
