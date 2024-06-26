package com.eekm.damoang.contents;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.eekm.damoang.models.article.ArticleCommentsModel;
import com.eekm.damoang.models.article.ArticleDocModel;
import com.eekm.damoang.util.ArticleParser;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ArticleView {
    public List<ArticleDocModel> links;
    public List<ArticleCommentsModel> comments;
    private String savedParseRules = null;

    public Boolean isOnlyAngers = null;

    public void setSavedParseRules(String savedParseRules) {
        this.savedParseRules = savedParseRules;
    }

    public void getView(String doc_id, String savedUa, String cfClearance) {
        List<ArticleDocModel> linkList = new ArrayList<>();
        List<ArticleCommentsModel> linkCommentList = new ArrayList<>();

        Connection.Response conn;
        Document document = null;

        try {
            String ua = savedUa;
            String URL = doc_id;

            Log.d(TAG, "URL = "+URL);
            Log.d(TAG, "UA = "+savedUa);
            Log.d(TAG, "CF clearance = "+cfClearance);
            conn = Jsoup.connect(URL)
                    .userAgent(ua).header("Cookie",
                            "cf_clearance="+cfClearance+";PHPSESSID=a").execute();

            document = conn.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            ArticleParser parser = new ArticleParser();

            parser.init(savedParseRules);
            parser.setDocument(document);
            parser.setViewType("parseArticleView");

            Elements list = parser.parseArticleViewParent();
            Elements meta_tags = document.getElementsByTag("meta");

            String title = parser.parseArticleString("title");
            String nick = "";

            for (Element meta : meta_tags) {
                String meta_name = meta.attr("name");

                if (Objects.equals(meta_name, "author")) {
                    nick = meta.attr("content");
                }
            }

            String datetime = parser.parseArticleString("datetime");

            parser.setParent_el_one(list.get(0));
            Elements doc_metadata = parser.parseArticleElements("doc_metadata");

            parser.setParent_el(doc_metadata);
            String recommend = parser.parseArticleString("recommend");

            parser.setParent_el(doc_metadata);
            String views = parser.parseArticleString("views");

            parser.setParent_el_one(list.get(0));
            String content_img = parser.parseArticleElement("content_img").toString();

            parser.setParent_el_one(list.get(0));
            Elements content_video = parser.parseArticleElements("content_video");
            String videowrap = "";
            if (!content_video.isEmpty()) {
                videowrap = content_video.get(0).toString();
            }

            parser.setParent_el_one(list.get(0));
            String content = parser.parseArticleElement("content").html();
            String merged_content = videowrap + content_img + "\n" + content;

            linkList.add(new ArticleDocModel(title, nick, recommend, views, datetime,
                    merged_content));

            // Comments
            parser.setViewType("parseArticleComment");
            Elements cmt = parser.parseArticleViewParent();
            for (int i = 0; i < cmt.size(); i++) {
                String cmt_link = cmt.get(i).id();
                parser.setParent_el_one(cmt.get(i));
                String cmt_content = parser.parseArticleElements("cmt_content").html();

                parser.setParent_el_one(cmt.get(i));
                String cmt_nick = parser.parseArticleString("cmt_nick");

                parser.setParent_el_one(cmt.get(i));
                String cmt_datetime = parser.parseArticleString("cmt_datetime");

                parser.setParent_el_one(cmt.get(i));
                Elements first_img = parser.parseArticleElements("cmt_image");

                String cmt_image = "";

                if (!first_img.isEmpty()) {
                    cmt_image = first_img.get(0).attr("data-cfsrc");

                    if (cmt_image.isEmpty()) {
                        cmt_image = first_img.get(0).attr("src");
                    }

                    if (cmt_image.startsWith("https://damoang.net/plugin/nariya")) {
                        cmt_image = "";
                    }
                }

                Log.d(TAG, cmt_content);

                parser.setParent_el_one(cmt.get(i));
                Elements btn_group_sel = parser.parseArticleElements("btn_group_sel");
                String cmt_recommend = "0";

                if (btn_group_sel.size() == 2) {
                    parser.setParent_el(btn_group_sel);
                    cmt_recommend = parser.parseArticleString("cmt_recommend");
                }

                String content_txt = Jsoup.parse(cmt_content).wholeText().trim();
                content_txt = content_txt.replaceAll("\n\n", "\n").replaceAll("\n\n", "\n");

                linkCommentList.add(new ArticleCommentsModel(cmt_link, content_txt, cmt_image,
                        cmt_nick, cmt_recommend, cmt_datetime));
            }
        } catch (Exception e) {
            Log.e(TAG, "This article is only visible to damoang's members.");
            if (document.text().
                    contains("우리 \"앙\"님만 열람할 수 있어요!"))
                this.isOnlyAngers = true;
        }

        this.links = linkList;
        this.comments = linkCommentList;
    }
}
