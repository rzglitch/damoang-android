package com.eekm.damoang.contents;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.eekm.damoang.models.article.GalleryListModel;
import com.eekm.damoang.util.ArticleParser;

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

            ArticleParser parser = new ArticleParser();

            parser.init(savedParseRules);
            parser.setDocument(document);
            parser.setViewType("parseGalleriesList");

            Elements list = parser.parseArticleViewParent();

            for (int i = 0; i < list.size(); i++) {
                parser.setParent_el_one(list.get(i));
                Elements parseLink = parser.parseArticleElements("link");

                if (!parseLink.isEmpty()) {
                    String link = parseLink.attr("abs:href");

                    String title = parseLink.text();
                    if (title.isEmpty()) {
                        parser.setParent_el_one(list.get(i));
                        title = parser.parseArticleString("link");
                    }
                    // String nick = list.get(i).select(".sv_member").text();

                    parser.setParent_el_one(list.get(i));
                    Elements meta_sel = parser.parseArticleElements("meta");

                    parser.setParent_el(meta_sel);
                    String datetime = parser.parseArticleString("datetime");

                    parser.setParent_el(meta_sel);
                    String recommend = parser.parseArticleString("recommend");

                    parser.setParent_el(meta_sel);
                    String views = parser.parseArticleString("views");

                    parser.setParent_el_one(list.get(i));
                    String imgSrc = parser.parseArticleString("img_src");

                    linkList.add(new GalleryListModel(link, title, "", recommend, views, datetime, imgSrc));
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "connection error");
        }

        this.links = linkList;
    }
}
