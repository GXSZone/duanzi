package com.caotu.adlib;

import cn.admob.admobgensdk.ad.information.ADMobGenInformation;

/**
 * 信息流广告的包装类,不直接依赖SDK的类,方便后面替换
 */
public class ADInfoWarp {
    ADMobGenInformation adMobGenInformation;

    public static ADInfoWarp getInstance(ADMobGenInformation adMobGenInformation) {
        ADInfoWarp warp = new ADInfoWarp(adMobGenInformation);
        return warp;
    }

    private ADInfoWarp(ADMobGenInformation adMobGenInformation) {
        this.adMobGenInformation = adMobGenInformation;
    }

    private ADMobGenInformation getAdMobGenInformation() {
        return adMobGenInformation;
    }

    public void destory() {
        ADMobGenInformation information = getAdMobGenInformation();
        if (information != null) {
            information.destroy();
        }
    }

    public void loadAd() {
        ADMobGenInformation information = getAdMobGenInformation();
        if (information != null) {
            information.loadAd();
        }
    }
}
