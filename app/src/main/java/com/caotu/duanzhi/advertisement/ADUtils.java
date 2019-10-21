package com.caotu.duanzhi.advertisement;

import android.app.Activity;
import android.content.Context;

import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.splash.SplashAD;

/**
 * 统一处理广告的获取
 */
public class ADUtils {
    public static SplashAD getSplashAD(Activity activity, SplashADListenerAdapter listenerAdapter) {
        SplashAD splashAD = new SplashAD(activity, ADConfig.AD_APPID, ADConfig.splash_id, listenerAdapter);
        splashAD.preLoad();
        return splashAD;
    }

    /**
     * 获取原生广告,包括列表和banner
     *
     * @param context
     * @param adId     广告位Id
     * @param count    获取的广告数
     * @param listener 广告的监听
     * @return
     */
    public static NativeExpressAD getNativeAd(Context context, String adId, int count, NativeAdListener listener) {
        ADSize adSize = new ADSize(ADSize.FULL_WIDTH, ADSize.AUTO_HEIGHT);
        NativeExpressAD expressAD = new NativeExpressAD(context, adSize, ADConfig.AD_APPID,
                adId, listener);
        expressAD.loadAD(count);
        return expressAD;
    }
}
