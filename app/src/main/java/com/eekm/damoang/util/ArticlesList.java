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

            Log.d(TAG, "CF clearance = "+cfClearance);
            Connection.Response conn = Jsoup.connect(URL)
                    .userAgent(ua).header("Cookie",
                            "cf_clearance="+cfClearance).execute();

            Document document = conn.parse();

            ArticleParser parser = new ArticleParser();

            parser.setJsonResult(savedParseRules);
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
                        title = list.get(i).select("a strong").text();
                    }
                    String nick = list.get(i).select(".sv_member").text();
                    String datetime = list.get(i).select(".wr-date").text();
                    String recommend = list.get(i).select(".wr-num.order-3").text();
                    String views = list.get(i).select(".wr-num.order-4").text();

                    datetime = datetime.split(" ")[0];
                    recommend = recommend.split(" ")[0];
                    views = views.split(" ")[0];

                    linkList.add(new ArticleListModel(link, title, nick, recommend, views, datetime));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "connection error");
        }

        this.links = linkList;
    }
}
