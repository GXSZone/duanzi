package com.caotu.duanzhi.other;

import android.webkit.JavascriptInterface;

import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.MySpUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class AndroidInterface {

    @JavascriptInterface
    public String callappforkey() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userid", MySpUtils.getMyId());
            jsonObject.put("key", WebActivity.H5_KEY);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
