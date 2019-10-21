package com.caotu.duanzhi.advertisement;

import com.caotu.duanzhi.other.UmengHelper;
import com.qq.e.ads.splash.SplashADListener;
import com.qq.e.comm.util.AdError;

public class SplashADListenerAdapter implements SplashADListener {
    @Override
    public void onADDismissed() {

    }

    @Override
    public void onNoAD(AdError adError) {

    }

    @Override
    public void onADPresent() {
        UmengHelper.event(ADConfig.splash_show);

    }

    @Override
    public void onADClicked() {
        UmengHelper.event(ADConfig.splash_click);
    }

    @Override
    public void onADTick(long l) {
    }

    @Override
    public void onADExposure() {

    }
}
