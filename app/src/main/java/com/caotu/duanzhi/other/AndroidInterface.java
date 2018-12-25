package com.caotu.duanzhi.other;

import android.webkit.JavascriptInterface;

import com.caotu.duanzhi.module.other.WebActivity;

public class AndroidInterface {

    @JavascriptInterface
    public String callAndroid() {
        return WebActivity.H5_KEY;
    }
}
