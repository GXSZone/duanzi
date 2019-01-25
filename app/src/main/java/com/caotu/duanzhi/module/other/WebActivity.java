package com.caotu.duanzhi.module.other;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UrlCheckBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.login.LoginAndRegisterActivity;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.just.agentweb.AgentWeb;
import com.lzy.okgo.model.Response;

public class WebActivity extends BaseActivity implements View.OnClickListener {

    private AgentWeb mAgentWeb;
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_URL = "URL";
    public static final String KEY_IS_SHOW_SHARE_ICON = "icon";
    public static final String KEY_SHARE_BEAN = "share_bean";
    public WebShareBean mShareBean;
    //H5认证使用的key
    public static String H5_KEY;
    public static String WEB_FROM_TYPE;
    public static String USER_ID;
    /**
     * 这个分享bean对象还得判断是否为空
     */
    public String shareUrl;
    private TextView webTitle;

    public static void openWeb(String title, String url, boolean isShowShareIcon) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        Intent intent = new Intent(runningActivity,
                WebActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_IS_SHOW_SHARE_ICON, isShowShareIcon);
        runningActivity.startActivity(intent);
    }

    /**
     * 分享内容由外部决定时使用
     *
     * @param title
     * @param url
     * @param isShowShareIcon
     * @param shareBean
     */
    public static void openWeb(String title, String url, boolean isShowShareIcon, WebShareBean shareBean) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        Intent intent = new Intent(runningActivity,
                WebActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_IS_SHOW_SHARE_ICON, isShowShareIcon);
        intent.putExtra(KEY_SHARE_BEAN, shareBean);
        runningActivity.startActivity(intent);
    }

    @Override
    protected void initView() {
        shareUrl = getIntent().getStringExtra(KEY_URL);
        findViewById(R.id.iv_back).setOnClickListener(this);
        ImageView shareIcon = findViewById(R.id.web_share);
        webTitle = findViewById(R.id.web_title);
        String title = getIntent().getStringExtra(KEY_TITLE);
        webTitle.setText(title);
        ViewGroup webContent = findViewById(R.id.web_content);
        View errorView = LayoutInflater.from(this).inflate(R.layout.layout_no_network, webContent, false);
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(webContent,
                        new FrameLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebChromeClient(mWebChromeClient)
                .setMainFrameErrorView(errorView)
                .createAgentWeb()
                .ready()
                .go(shareUrl);

        mAgentWeb.getAgentWebSettings().getWebSettings().setLoadWithOverviewMode(true);
        mAgentWeb.getAgentWebSettings().getWebSettings().setUseWideViewPort(true);
        //支持缩放
        mAgentWeb.getAgentWebSettings().getWebSettings().setSupportZoom(true);
        mAgentWeb.getAgentWebSettings().getWebSettings().setBuiltInZoomControls(true);
        mAgentWeb.getAgentWebSettings().getWebSettings().setDisplayZoomControls(false);
        //用于js调APP方法

        mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface());

        boolean showShareIcon = getIntent().getBooleanExtra(KEY_IS_SHOW_SHARE_ICON, true);

        shareIcon.setVisibility(showShareIcon ? View.VISIBLE : View.INVISIBLE);
        shareIcon.setOnClickListener(this);
        mShareBean = getIntent().getParcelableExtra(KEY_SHARE_BEAN);
    }

    public void setShareBean(WebShareBean shareBean) {
        this.mShareBean = shareBean;
        if (shareBean != null && !TextUtils.isEmpty(shareBean.title)) {
            webTitle.setText(shareBean.title);
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_web;
    }


    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (mShareBean != null && !TextUtils.isEmpty(mShareBean.title)) return;
            webTitle.setText(title);
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.web_share) {
            if (mShareBean == null) {
                mShareBean = new WebShareBean();
            }
            ShareDialog shareDialog = ShareDialog.newInstance(mShareBean);
            shareDialog.setListener(new ShareDialog.SimperMediaCallBack() {
                @Override
                public void callback(WebShareBean bean) {
                    if (TextUtils.isEmpty(bean.title)) {
                        bean.title = webTitle.getText().toString();
                    }
                    if (TextUtils.isEmpty(bean.url)) {
                        bean.url = shareUrl;
                    }
                    if (bean.webType == 1) {
                        ShareHelper.getInstance().shareWebPicture(bean, bean.url);
                    } else {
                        ShareHelper.getInstance().shareFromWebView(bean);
                    }
                }
            });
            shareDialog.show(getSupportFragmentManager(), "share");
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
        //防止新值没有被替换,仅限当前操作,退出的值需要重新赋
        H5_KEY = "";
        WEB_FROM_TYPE = "";
        USER_ID = "";
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mAgentWeb.handleKeyEvent(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == LoginAndRegisterActivity.LOGIN_RESULT_CODE &&
                requestCode == LoginAndRegisterActivity.LOGIN_REQUEST_CODE) {
            CommonHttpRequest.getInstance().checkUrl(shareUrl, new JsonCallback<BaseResponseBean<UrlCheckBean>>() {
                @Override
                public void onSuccess(Response<BaseResponseBean<UrlCheckBean>> response) {
                    // TODO: 2018/12/25 保存接口给的key,H5认证使用
                    UrlCheckBean data = response.body().getData();
                    WebActivity.H5_KEY = data.getReturnkey();
                    // 登陆成功重新刷新
                    if (mAgentWeb != null) {
                        mAgentWeb.getWebCreator().getWebView().reload();
                    }
                }
            });
        }
    }
}
