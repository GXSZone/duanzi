package com.caotu.adlib;

import android.util.Log;

import cn.admob.admobgensdk.ad.listener.ADMobGenSplashAdListener;

/**
 * 为了统一回调埋点事件
 */
public class AdSplashListenerAdapter implements ADMobGenSplashAdListener {
    BuriedPointListener pointListener;

    public void setPointListener(BuriedPointListener Listener) {
       pointListener = Listener;
    }

    @Override
    public void onADExposure() {
        Log.i("splash", "广告展示曝光回调，但不一定是曝光成功了，比如一些网络问题导致上报失败 ::::: ");
        if (pointListener != null) {
            pointListener.adItemBuriedPoint(BuriedPointListener.splash_type, BuriedPointListener.show);
        }
    }

    @Override
    public void onADFailed(String s) {

    }

    @Override
    public void onADReceiv() {

    }

    @Override
    public void onADClick() {
        if (pointListener != null) {
            pointListener.adItemBuriedPoint(BuriedPointListener.splash_type, BuriedPointListener.click);
        }
    }

    @Override
    public void onAdClose() {

    }
}
