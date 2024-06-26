package com.eekm.damoang.contents;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.eekm.damoang.models.article.ArticleListModel;
import com.eekm.damoang.util.ArticleParser;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

            Log.d(TAG, "URL = "+URL);
            Log.d(TAG, "UA = "+savedUa);
            Log.d(TAG, "CF clearance = "+cfClearance);
            Connection.Response conn = Jsoup.connect(URL)
                    .userAgent(ua).header("Cookie",
                            "cf_clearance="+cfClearance+";PHPSESSID=a").execute();

            Document document = conn.parse();

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
