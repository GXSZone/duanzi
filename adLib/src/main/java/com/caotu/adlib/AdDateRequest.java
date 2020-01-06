package com.caotu.adlib;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

/**
 * 因为每个SDK 都有自己的类不好做综合
 */
public interface AdDateRequest {

    void initAdOpenSwitch(String... isOpen);

    void loadAd(ADInfoWarp adMobGenInformation);

    View getAdView(ADInfoWarp adMobGenInformation);

    void destroy(ADInfoWarp information);

    void showAD(View adView, ViewGroup adContainer);

    ADInfoWarp initItemAd(Activity activity);

    ADInfoWarp initBannerAd(Activity activity, CommentDateCallBack callBack);

    ADInfoWarp initDetailHeaderAd(Activity activity, CommentDateCallBack callBack);

    ADInfoWarp initCommentItemAd(Activity activity, CommentDateCallBack callBack);
}
