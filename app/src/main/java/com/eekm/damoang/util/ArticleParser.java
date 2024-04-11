package com.eekm.damoang.util;

import android.util.Log;

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

public class ArticleParser {
    public String jsonResult = null;
    private Document document = null;
    private String viewType = null;
    private Elements parent_el = null;
    private Element parent_el_one = null;

    private Elements processed_el = null;
    private Element processed_el_one = null;

    public void setDocument(Document document) {
        this.document = document;
    }
    public void setViewType(String viewType) {
        this.viewType = viewType;
    }
    public void setJsonResult(String jsonResult) {
        this.jsonResult = jsonResult;
    }
    public void setParent_el(Elements parent_el) {
        this.parent_el = parent_el;
    }
    public void setParent_el_one(Element parent_el_one) {
        this.parent_el_one = parent_el_one;
    }
    public void clearProcess() {
        this.parent_el = null;
        this.parent_el_one = null;
        this.processed_el = null;
        this.processed_el_one = null;
    }

    public String getJsonResult() {
        return jsonResult;
    }

    public String getParserData() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        Log.d("getParserData", "get a new parser rules data.");
        try {
            String url_text = "https://dkh1.mycafe24.com/damoangdroid/damoangParserTest.json";
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
                    String selCss = listObject.getString("css");

                    if (result == null) {
                        result = document.select(selCss);
                    } else {
                        result = result.select(selCss);
                    }
                }
            }

            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void select(String sel) {
        if (processed_el_one == null) {
            if (processed_el == null) {
                // 처음에는 parent_el에서 선택합니다.
                if (parent_el_one != null)
                    processed_el = parent_el_one.select(sel);
                else
                    processed_el = parent_el.select(sel);
            } else {
                // processed_el이 null이 아니면 processed_el에서 선택합니다.
                processed_el = processed_el.select(sel);
            }
        } else {
            // processed_el_one에 객체가 존재한다면
            // select 후  processed_el로 돌려준다.
            processed_el = processed_el_one.select(sel);
            processed_el_one = null;
        }
    }

    private void get(int idx) throws JSONException {
        if (parent_el_one != null && processed_el == null)
            throw new JSONException("Cannot use `get()` on Element.");

        Elements sel = null;

        if (processed_el == null) {
            if (idx < 0) {
                sel = parent_el;
                processed_el_one = parent_el.get(sel.size() - (idx * -1));
            } else {
                processed_el_one = parent_el.get(idx);
            }
        } else {
            if (idx < 0) {
                sel = processed_el;
                processed_el_one = processed_el.get(sel.size() - (idx * -1));
                processed_el = null;
            } else {
                processed_el_one = processed_el.get(idx);
                processed_el = null;
            }
        }
    }

    private String split(String regex, int idx) {
        if (processed_el_one == null) {
            return processed_el.text().split(regex)[idx];
        } else {
            return processed_el_one.text().split(regex)[idx];
        }
    }

    public Elements parseArticleElements(String itemName) {
        Elements result = null;

        if (parent_el == null && parent_el_one == null)
            parent_el = parseArticleViewParent();

        try {
            JSONObject avo = getArticleViewObject();
            JSONArray listArray = avo.getJSONArray(itemName);

            for (int i = 0; i < listArray.length(); i++) {
                JSONObject listObject = listArray.getJSONObject(i);
                String selFunc = listObject.getString("f");

                if (selFunc.equals("select")) {
                    String selCss = listObject.getString("css");
                    select(selCss);
                }

                if (selFunc.equals("get")) {
                    int getIndex = listObject.getInt("idx");
                    get(getIndex);
                }
            }

            result = processed_el;

            clearProcess();

            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Element parseArticleElement(String itemName) {
        Element result = null;

        if (parent_el == null && parent_el_one == null)
            parent_el = parseArticleViewParent();

        try {
            JSONObject avo = getArticleViewObject();
            JSONArray listArray = avo.getJSONArray(itemName);

            for (int i = 0; i < listArray.length(); i++) {
                JSONObject listObject = listArray.getJSONObject(i);
                String selFunc = listObject.getString("f");

                if (selFunc.equals("select")) {
                    String selCss = listObject.getString("css");
                    select(selCss);
                }

                if (selFunc.equals("get")) {
                    int getIndex = listObject.getInt("idx");
                    get(getIndex);
                }
            }

            result = processed_el_one;

            clearProcess();

            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String parseArticleString(String itemName) {
        String result = null;
        String processed_str = null;

        if (parent_el == null && parent_el_one == null)
            parent_el = parseArticleViewParent();

        try {
            JSONObject avo = getArticleViewObject();
            JSONArray listArray = avo.getJSONArray(itemName);

            for (int i = 0; i < listArray.length(); i++) {
                JSONObject listObject = listArray.getJSONObject(i);
                String selFunc = listObject.getString("f");

                if (selFunc.equals("attr")) {
                    String getAttrVal = listObject.getString("name");

                    if (processed_el_one == null) {
                        if (processed_el == null) {
                            // 처음에는 parent_el에서 선택합니다.
                            if (parent_el_one != null)
                                processed_str = parent_el_one.attr(getAttrVal);
                            else
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
                }

                if (selFunc.equals("select")) {
                    String selCss = listObject.getString("css");
                    select(selCss);
                }

                if (selFunc.equals("get")) {
                    int getIndex = listObject.getInt("idx");
                    get(getIndex);
                }

                if (selFunc.equals("split")) {
                    String getRegex = listObject.getString("r");
                    int getIndex = listObject.getInt("idx");
                    processed_str = split(getRegex, getIndex);
                }
            }

            if (processed_el == null)
                result = processed_el_one.text();
            else
                result = processed_el.text();

            if (processed_str != null) {
                clearProcess();
                return processed_str;
            } else {
                clearProcess();
                return result;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
}
