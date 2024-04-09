package com.eekm.damoang.util;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.eekm.damoang.ui.articles.ArticleCommentsModel;
import com.eekm.damoang.ui.articles.ArticleDocModel;

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

    public void setSavedParseRules(String savedParseRules) {
        this.savedParseRules = savedParseRules;
    }

    public void getView(String doc_id, String savedUa, String cfClearance) {
        List<ArticleDocModel> linkList = new ArrayList<>();
        List<ArticleCommentsModel> linkCommentList = new ArrayList<>();

        try {
            String ua = savedUa;
            String URL = doc_id;

            Log.d(TAG, "URL = "+URL);
            Log.d(TAG, "UA = "+savedUa);
            Log.d(TAG, "CF clearance = "+cfClearance);
            Connection.Response conn = Jsoup.connect(URL)
                    .userAgent(ua).header("Cookie",
                            "cf_clearance="+cfClearance+";PHPSESSID=a").execute();

            Document document = conn.parse();

            Elements list = document.select("#bo_v");
            Elements meta_tags = document.getElementsByTag("meta");

            ArticleParser parser = new ArticleParser();

            parser.setJsonResult(savedParseRules);
            parser.setDocument(document);
            parser.setViewType("parseArticleView");

            String title = parser.parseArticleString("title");
            String nick = "";

            for (Element meta : meta_tags) {
                String meta_name = meta.attr("name");

                if (Objects.equals(meta_name, "author")) {
                    nick = meta.attr("content");
                }
            }

            String datetime = parser.parseArticleString("datetime");
            Elements doc_metadata = list.get(0).select("#bo_v_info .pe-2");
            String recommend = doc_metadata.get(doc_metadata.size()-1).text();
            String views = doc_metadata.get(0).text();
            String content_img = list.get(0).select("#bo_v_img").get(0).toString();
            String content = list.get(0).select("#bo_v_con.na-convert").get(0).toString();
            String merged_content = content_img + "\n" + content;

            recommend = recommend.split(" ")[0];
            views = views.split(" ")[0];
            datetime = datetime.split(" ")[1];

            linkList.add(new ArticleDocModel(title, nick, recommend, views,datetime,
                    merged_content));

            // Comments
            Elements cmt = document.select("#bo_vc article");
            Log.d("commentList", "size: " + cmt.size());
            for (int i = 0; i < cmt.size(); i++) {
                String cmt_link = cmt.get(i).id();
                String cmt_content = cmt.get(i).select(".comment-content .na-convert").html();
                String cmt_nick = cmt.get(i).select(".me-2 .sv_member")
                        .attr("title");
                String cmt_datetime = cmt.get(i).select(".ms-auto").text();

                Elements btn_group_sel = cmt.get(i).select(".comment-content .btn-group");
                String cmt_recommend = "0";

                if (btn_group_sel.size() == 2) {
                    cmt_recommend = btn_group_sel.get(1).text();
                    cmt_recommend = cmt_recommend.split(" ")[1];
                }

                String content_txt = Jsoup.parse(cmt_content).wholeText();

                cmt_datetime = cmt_datetime.split(" ")[1];

                cmt_nick = cmt_nick.split(" ")[0];

                linkCommentList.add(new ArticleCommentsModel(cmt_link, content_txt, cmt_nick, cmt_recommend, cmt_datetime));
            }

            /*
            Elements list = document.select("#bo_v");

            String title = list.get(0).select("#bo_v_title").text();
            String nick = list.get(0).select("#bo_v_info .sv_member").text();
            String datetime = list.get(0).select("#bo_v_info div").
                    get(0).select("div").get(1).text();
            //String recommend = list.get(0).select("#bo_v_info div").get(1).select("div").get(2).text();
            String recommend = "dgdgdfg";
            String views = list.get(0).select("#bo_v_info div").
                    get(1).select("div").get(0).text();
            String content = list.get(0).select("#bo_v_con").text();

            datetime = datetime.split(" ")[0];
            recommend = recommend.split(" ")[0];
            views = views.split(" ")[0];

            linkList.add(new ArticleDocModel(title, nick, recommend, views, datetime, content));

            // Comments
            Elements cmt = document.select("#bo_vc");
            for (int i = 0; i < cmt.size(); i++) {
                String cmt_link = cmt.get(i).id();
                String cmt_content = cmt.get(i).select(".comment-content .na-convert").text();
                String cmt_nick = cmt.get(i).select(".me-2 .member").text();
                String cmt_datetime = cmt.get(i).select(".ms-auto").text();
                String cmt_recommend = cmt.get(i).select(".comment-content div").get(1).text();

                linkCommentList.add(new ArticleCommentsModel(cmt_link, cmt_content, cmt_nick, cmt_recommend, cmt_datetime));
            }

             */
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.links = linkList;
        this.comments = linkCommentList;
    }
}
