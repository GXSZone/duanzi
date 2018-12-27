package com.caotu.duanzhi.other;

import android.webkit.JavascriptInterface;

import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.MySpUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class AndroidInterface {

    @JavascriptInterface
    public String callappforkey() {
        JSONObject jsonObject = new JSONObject();
        try {
            String myId = MySpUtils.getMyId();
            jsonObject.put("userid", AESUtils.encode(myId));
            jsonObject.put("key", WebActivity.H5_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
