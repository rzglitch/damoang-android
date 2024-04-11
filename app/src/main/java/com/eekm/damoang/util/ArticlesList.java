package com.eekm.damoang.util;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.eekm.damoang.models.articles.ArticleListModel;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ArticlesList {
    public List<ArticleListModel> links;

    private String savedParseRules = null;

    public void setSavedParseRules(String savedParseRules) {
        this.savedParseRules = savedParseRules;
    }

    public void getList(String boardUrl, String savedUa, String cfClearance, int page) {
        List<ArticleListModel> linkList = new ArrayList<>();

        try {
            String ua = savedUa;
            String URL = boardUrl + "?page=" + page;

            OkHttpClient okHttp = new OkHttpClient();
            Log.d(TAG, "CF clearance = "+cfClearance);
            Request request = new Request.Builder().url(URL).get().build();
            Document document = Jsoup.parse(okHttp.newCall(request).execute().body().string());

            ArticleParser parser = new ArticleParser();

            parser.init(savedParseRules);
            parser.setDocument(document);
            parser.setViewType("parseArticleList");

            Elements list = parser.parseArticleViewParent();

            for (int i = 0; i < list.size(); i++) {
                Elements parseLink = list.get(i).select("a");

                if (!parseLink.isEmpty()) {
                    String link = parseLink.attr("abs:href");

                    parser.setParent_el_one(list.get(i));
                    Elements num = parser.parseArticleElements("num");

                    if (!num.isEmpty()) {
                        if (Objects.equals(num.get(0).text().trim(), "공지")) {
                            continue;
                        }
                    }

                    String title = parseLink.get(0).text();
                    if (title.isEmpty()) {
                        // 공지글
                        parser.setParent_el_one(list.get(i));
                        title = parser.parseArticleString("title");
                    }

                    parser.setParent_el_one(list.get(i));
                    String nick = parser.parseArticleString("nick");

                    parser.setParent_el_one(list.get(i));
                    String datetime = parser.parseArticleString("datetime");

                    parser.setParent_el_one(list.get(i));
                    String recommend = parser.parseArticleString("recommend");

                    parser.setParent_el_one(list.get(i));
                    String views = parser.parseArticleString("views");

                    linkList.add(new ArticleListModel(link, title, nick, recommend, views, datetime, 0));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "connection error");
        }

        Log.d(TAG, "parse end");
        this.links = linkList;
    }
}
