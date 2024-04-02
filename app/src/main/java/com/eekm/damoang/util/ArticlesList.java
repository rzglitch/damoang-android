package com.eekm.damoang.util;

import static android.content.ContentValues.TAG;

import android.content.SharedPreferences;
import android.util.Log;

import com.eekm.damoang.ui.articles.ArticleListModel;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ArticlesList {
    public List<ArticleListModel> links;

    public void getList(String boardName, String savedUa, String cfClearance, int page) {
        List<ArticleListModel> linkList = new ArrayList<>();

        try {
            String ua = savedUa;
            String URL = "https://damoang.net/" + boardName + "?page=" + page;

            Log.d(TAG, "CF clearance = "+cfClearance);
            Connection.Response conn = Jsoup.connect(URL)
                    .userAgent(ua).header("Cookie",
                            "cf_clearance="+cfClearance).execute();

            Document document = conn.parse();


            Elements list = document.select(".list-group-flush>.list-group-item");

            for (int i = 0; i < list.size(); i++) {
                Elements parseLink = list.get(i).select("a");

                if (!parseLink.isEmpty()) {
                    String link = parseLink.attr("abs:href");
                    Elements num = list.get(i).select(".orangered");

                    if (!num.isEmpty()) {
                        if (Objects.equals(num.get(0).text().trim(), "공지")) {
                            continue;
                        }
                    }

                    String title = parseLink.text();
                    if (title.isEmpty()) {
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
