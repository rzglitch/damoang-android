package com.eekm.damoang.util;

import static android.content.ContentValues.TAG;

import android.util.Log;

import com.eekm.damoang.ui.articles.ArticleCommentsModel;
import com.eekm.damoang.ui.articles.ArticleDocModel;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArticleView {
    public List<ArticleDocModel> links;
    public List<ArticleCommentsModel> comments;

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

            String title = list.get(0).select("#bo_v_title").text();
            String nick = list.get(0).select("#bo_v_info .sv_member").text();
            String datetime = list.get(0).select("#bo_v_info>div").select("span").get(3).text();
            Elements doc_metadata = list.get(0).select("#bo_v_info .pe-2");
            String recommend = doc_metadata.get(doc_metadata.size()-1).text();
            String views = doc_metadata.get(0).text();
            String content_img = list.get(0).select("#bo_v_img").html();
            String content = list.get(0).select("#bo_v_con").html();
            String merged_content = content_img + content;

            recommend = recommend.split(" ")[0];
            views = views.split(" ")[0];

            linkList.add(new ArticleDocModel(title, nick, recommend, views, datetime, merged_content));

            // Comments
            Elements cmt = document.select("#bo_vc article");
            Log.d("commentList", "size: " + cmt.size());
            for (int i = 0; i < cmt.size(); i++) {
                String cmt_link = cmt.get(i).id();
                String cmt_content = cmt.get(i).select(".comment-content .na-convert").text();
                String cmt_nick = cmt.get(i).select(".me-2 .member").text();
                String cmt_datetime = cmt.get(i).select(".ms-auto").text();
                String cmt_recommend = cmt.get(i).select(".comment-content div").get(1).text();

                cmt_datetime = cmt_datetime.split(" ")[1];
                cmt_recommend = cmt_recommend.split(" ")[3];

                linkCommentList.add(new ArticleCommentsModel(cmt_link, cmt_content, cmt_nick, cmt_recommend, cmt_datetime));
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
