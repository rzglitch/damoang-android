package com.eekm.damoang.util;

import android.content.SharedPreferences;
import android.util.Log;

import com.eekm.damoang.ui.boards.BoardsListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ArticleParser {
    public String jsonResult = null;
    private Document document = null;
    private String viewType = null;

    public void setDocument(Document document) {
        this.document = document;
    }
    public void setViewType(String viewType) {
        this.viewType = viewType;
    }
    public void setJsonResult(String jsonResult) {
        this.jsonResult = jsonResult;
    }

    public String getJsonResult() {
        return jsonResult;
    }

    public String getParserData() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        Log.d("getParserData", "get a new parser rules data.");
        try {
            String url_text = "https://dkh1.mycafe24.com/damoangdroid/damoangParser.json";
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

            this.jsonResult = result;

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return null;
    }

    public Boolean isRulesOutdated(String oldRule) {
        try {
            JSONObject jsonObject_old = new JSONObject(oldRule);
            JSONObject jsonObject_new = new JSONObject(jsonResult);

            String remoteParser_old = jsonObject_old.getString("remoteParser");
            String remoteParser_new = jsonObject_new.getString("remoteParser");

            JSONObject remoteParserObject_old = new JSONObject(remoteParser_old);
            JSONObject remoteParserObject_new = new JSONObject(remoteParser_new);

            int version_old = remoteParserObject_old.getInt("version");
            int version_new = remoteParserObject_new.getInt("version");

            if (version_old == version_new)
                return false;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 저장된 룰이 오래됐을 때
        return true;
    }

    public JSONObject getArticleViewObject() {
        try {
            JSONObject jsonObject = new JSONObject(jsonResult);

            String remoteParser = jsonObject.getString("remoteParser");
            JSONObject remoteParserObject = new JSONObject(remoteParser);

            return remoteParserObject.getJSONObject(viewType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * parseArticleViewParent
     * 긁을 대상의 부분만 가져옵니다.
     * @return Elements
     */
    public Elements parseArticleViewParent() {
        Elements result = null;

        try {
            JSONObject avo = getArticleViewObject();
            JSONArray listArray = avo.getJSONArray("list");

            for (int i = 0; i < listArray.length(); i++) {
                JSONObject listObject = listArray.getJSONObject(i);
                String selFunc = listObject.getString("f");

                if (selFunc.equals("select")) {
                    JSONArray selParam = listObject.getJSONArray("param");

                    if (result == null) {
                        result = document.select(selParam.getString(0));
                    } else {
                        result = result.select(selParam.getString(0));
                    }
                }
            }

            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String parseArticleString(String itemName) {
        String result = null;
        Elements parent_el = parseArticleViewParent();
        Elements processed_el = null;
        String processed_str = null;
        Element processed_el_one = null;

        try {
            JSONObject avo = getArticleViewObject();
            JSONArray listArray = avo.getJSONArray(itemName);

            for (int i = 0; i < listArray.length(); i++) {
                JSONObject listObject = listArray.getJSONObject(i);
                String selFunc = listObject.getString("f");

                if (selFunc.equals("select")) {
                    JSONArray selParam = listObject.getJSONArray("param");

                    if (processed_el_one == null) {
                        if (processed_el == null) {
                            // 처음에는 parent_el에서 선택합니다.
                            processed_el = parent_el.select(selParam.getString(0));
                        } else {
                            // processed_el이 null이 아니면 processed_el에서 선택합니다.
                            processed_el = processed_el.select(selParam.getString(0));
                        }
                    } else {
                        // processed_el_one에 객체가 존재한다면
                        // select 후  processed_el로 돌려준다.
                        processed_el = processed_el_one.select(selParam.getString(0));
                        processed_el_one = null;
                    }
                }

                if (selFunc.equals("attr")) {
                    String getAttrVal = listObject.getString("name");

                    if (processed_el_one == null) {
                        if (processed_el == null) {
                            // 처음에는 parent_el에서 선택합니다.
                            processed_str = parent_el.attr(getAttrVal);
                        } else {
                            // processed_el이 null이 아니면 processed_el에서 선택합니다.
                            processed_str = processed_el.attr(getAttrVal);
                        }
                    } else {
                        // processed_el_one에 객체가 존재한다면
                        // processed_el_one에서 선택합니다.
                        processed_str = processed_el_one.attr(getAttrVal);
                    }
                    // return String
                    return processed_str;
                }

                if (selFunc.equals("get")) {
                    int getIndex = listObject.getInt("idx");

                    if (processed_el == null) {
                        // 처음에는 parent_el에서 선택합니다.
                        processed_el_one = parent_el.get(getIndex);
                    } else {
                        // processed_el에 객체가 존재한다면 processed_el에서 선택합니다.
                        processed_el_one = processed_el.get(getIndex);
                        processed_el = null;
                    }
                }
            }

            if (processed_el == null)
                result = processed_el_one.text();
            else
                result = processed_el.text();

            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
