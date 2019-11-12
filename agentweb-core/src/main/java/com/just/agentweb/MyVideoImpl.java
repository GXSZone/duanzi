/*
 * Copyright (C)  Justson(https://github.com/Justson/AgentWeb)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.just.agentweb;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * 自己判断是否有虚拟导航栏,有的话加个底部margin,还有种方式是重写AgentWeb(加个方法,把视频等全屏操作的父容器传进去)
 */
public class MyVideoImpl implements IVideo, EventInterceptor {

    private Activity mActivity;
    private WebView mWebView;

    private View mMoiveView = null;
    private ViewGroup mMoiveParentView = null;


    public MyVideoImpl(Activity mActivity, WebView webView) {
        this.mActivity = mActivity;
        this.mWebView = webView;

    }

    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        Activity mActivity;
        if ((mActivity = this.mActivity) == null || mActivity.isFinishing()) {
            return;
        }

        if (mMoiveView != null) {
            callback.onCustomViewHidden();
            return;
        }
        if (mWebView != null) {
            mWebView.setVisibility(View.GONE);
        }
        if (mMoiveParentView == null) {
            FrameLayout mDecorView = (FrameLayout) mActivity.getWindow().getDecorView();
            mMoiveParentView = new FrameLayout(mActivity);
            mMoiveParentView.setBackgroundColor(Color.BLACK);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT);
            params.bottomMargin = getNavigationBarHeight(mActivity);
            mDecorView.addView(mMoiveParentView, params);
        }
        mMoiveParentView.addView(this.mMoiveView = view);
        mMoiveParentView.setVisibility(View.VISIBLE);
    }


    /**
     * 获取NavigationBar的高度
     */
    public static int getNavigationBarHeight(Context context) {
        if (!hasNavigationBar(context)) {
            return 0;
        }
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height",
                "dimen", "android");
        //获取NavigationBar的高度
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 是否存在NavigationBar
     */
    public static boolean hasNavigationBar(Context context) {
        Display display = getWindowManager(context).getDefaultDisplay();
        Point size = new Point();
        Point realSize = new Point();
        display.getSize(size);
        display.getRealSize(realSize);
        return realSize.x != size.x || realSize.y != size.y;

    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     */
    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    @Override
    public void onHideCustomView() {
        if (mMoiveView == null) {
            return;
        }
        if (mActivity != null && mActivity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        mMoiveView.setVisibility(View.GONE);
        if (mMoiveParentView != null && mMoiveView != null) {
            mMoiveParentView.removeView(mMoiveView);

        }
        if (mMoiveParentView != null) {
            mMoiveParentView.setVisibility(View.GONE);
        }
        this.mMoiveView = null;
        if (mWebView != null) {
            mWebView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean isVideoState() {
        return mMoiveView != null;
    }

    @Override
    public boolean event() {

        if (isVideoState()) {
            onHideCustomView();
            return true;
        } else {
            return false;
        }
    }
}
