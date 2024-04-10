package com.eekm.damoang.util;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.eekm.damoang.models.gallery.GalleryListModel;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GalleriesList {
    public List<GalleryListModel> links;

    private String savedParseRules = null;

    public void setSavedParseRules(String savedParseRules) {
        this.savedParseRules = savedParseRules;
    }

    public void getList(String boardUrl, String savedUa, String cfClearance, int page) {
        List<GalleryListModel> linkList = new ArrayList<>();

        try {
            String ua = savedUa;
            String URL = boardUrl + "?page=" + page;

            Log.d(TAG, "CF clearance = "+cfClearance);
            Connection.Response conn = Jsoup.connect(URL)
                    .userAgent(ua).header("Cookie",
                            "cf_clearance="+cfClearance).execute();

            Document document = conn.parse();


            Elements list = document.select("#bo_list .row .col");

            ArticleParser parser = new ArticleParser();

            parser.setJsonResult(savedParseRules);
            parser.setDocument(document);
            parser.setViewType("parseArticleView");

            for (int i = 0; i < list.size(); i++) {
                Elements parseLink = list.get(i).select(".card-title a");

                if (!parseLink.isEmpty()) {
                    String link = parseLink.attr("abs:href");

                    String title = parseLink.text();
                    if (title.isEmpty()) {
                        title = list.get(i).select(".card-title a").text();
                    }
                    // String nick = list.get(i).select(".sv_member").text();
                    Elements meta_sel = list.get(i).select(".mt-auto div").select("div");
                    String datetime = meta_sel.get(meta_sel.size() - 1).text();
                    String recommend = meta_sel.get(2).text();
                    String views = meta_sel.get(1).text();

                    datetime = datetime.split(" ")[0];
                    recommend = recommend.split(" ")[0];
                    views = views.split(" ")[0];

                    String imgSrc = list.get(i).select("img.object-fit-cover").attr("src");

                    linkList.add(new GalleryListModel(link, title, "", recommend, views, datetime, imgSrc));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "connection error");
        }

        this.links = linkList;
    }
}
