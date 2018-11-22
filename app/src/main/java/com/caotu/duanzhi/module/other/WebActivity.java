package com.caotu.duanzhi.module.other;

import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.just.agentweb.AgentWeb;

public class WebActivity extends BaseActivity implements View.OnClickListener {

    private AgentWeb mAgentWeb;
    public static final String KEY_BEAN = "BEAN";
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_URL = "URL";
    public static final String KEY_IS_SHOW_SHARE_ICON = "icon";
    public static final String KEY_USER_AGREEMENT = "file:///android_asset/html/UserAgreement.html";

    /**
     * 这个分享bean对象还得判断是否为空
     */
    private WebShareBean bean;
    public String shareUrl;
    private TextView webTitle;

    public static void openWeb(String title, String url, boolean isShowShareIcon, WebShareBean bean) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        Intent intent = new Intent(runningActivity,
                WebActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_IS_SHOW_SHARE_ICON, isShowShareIcon);
        intent.putExtra(KEY_BEAN, bean);
        runningActivity.startActivity(intent);
    }

    @Override
    protected void initView() {
        bean = getIntent().getParcelableExtra(KEY_BEAN);
        String url = getIntent().getStringExtra(KEY_URL);
        findViewById(R.id.iv_back).setOnClickListener(this);
        webTitle = findViewById(R.id.web_title);
        String title = getIntent().getStringExtra(KEY_TITLE);
        webTitle.setText(title);
        ViewGroup webContent = findViewById(R.id.web_content);
        View errorView = LayoutInflater.from(this).inflate(R.layout.layout_no_network, webContent,false);
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(webContent,
                        new FrameLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebChromeClient(mWebChromeClient)
                .setMainFrameErrorView(errorView)
                .createAgentWeb()
                .ready()
                .go(url);

        mAgentWeb.getAgentWebSettings().getWebSettings().setLoadWithOverviewMode(true);
        mAgentWeb.getAgentWebSettings().getWebSettings().setUseWideViewPort(true);
        //支持缩放
        mAgentWeb.getAgentWebSettings().getWebSettings().setSupportZoom(true);
        mAgentWeb.getAgentWebSettings().getWebSettings().setBuiltInZoomControls(true);
        mAgentWeb.getAgentWebSettings().getWebSettings().setDisplayZoomControls(false);
        //用于js调APP方法
//            androidInterface = new AndroidInterface();
//            mAgentWeb.getJsInterfaceHolder().addJavaObject("android", androidInterface);

        boolean isshow = getIntent().getBooleanExtra(KEY_IS_SHOW_SHARE_ICON, false);

    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_web;
    }


    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            webTitle.setText(title);
        }
    };

    @Override
    public void onClick(View v) {
        if (!mAgentWeb.back()) {
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAgentWeb != null) {
            mAgentWeb.destroy();
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
