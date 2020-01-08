package com.caotu.duanzhi.module.other;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.widget.FrameLayout;

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
import com.caotu.duanzhi.view.widget.TitleView;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.MiddlewareWebChromeBase;
import com.lzy.okgo.model.Response;

/**
 * webView 的文件选择有几步:复写 WebChromeClient 的 onShowFileChooser,可以用自带的图片选择器,必须返回true
 * 结果回调里注意路径,需要转一下getMediaUriFromPath
 * 另外如果取消的话还得置空,不然就不会再次调用到onShowFileChooser
 * <p>
 * https://github.com/yangchong211/YCWebView 基于X5封装
 */
public class WebActivity extends BaseActivity {

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
    private TitleView titleView;

    public static void openWeb(String title, String url, boolean isShowShareIcon) {
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        if (runningActivity == null) return;
        Intent intent = new Intent(runningActivity, WebActivity.class);
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
        if (runningActivity == null) return;
        Intent intent = new Intent(runningActivity, WebActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_URL, url);
        intent.putExtra(KEY_IS_SHOW_SHARE_ICON, isShowShareIcon);
        intent.putExtra(KEY_SHARE_BEAN, shareBean);
        runningActivity.startActivity(intent);
    }

    @Override
    protected void initView() {
//        shareUrl = "http://v3.toutushare.com/apph5_videoshare_xq/pages/indexs.html";
//        shareUrl = "https://testh5.itoutu.com:442/apph5_approve/pages/approve.html";
        titleView = findViewById(R.id.title_view);
        titleView.setClickListener(this::shareClick);
        titleView.setTitleText(getIntent().getStringExtra(KEY_TITLE));
        titleView.setMoreView(R.mipmap.home_share);
        titleView.setBackView(R.mipmap.web_close);
        shareUrl = getIntent().getStringExtra(KEY_URL);

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(findViewById(R.id.web_content), new FrameLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
//                .setWebChromeClient(client)
                .useMiddlewareWebChrome(getMiddlewareWebChrome())
                .setMainFrameErrorView(R.layout.layout_no_network, -1)
                .addJavascriptInterface("android", new AndroidInterface())
                .createAgentWeb()
                .ready()
                .go(shareUrl);

        boolean showShareIcon = getIntent().getBooleanExtra(KEY_IS_SHOW_SHARE_ICON, true);
        titleView.setRightGone(!showShareIcon);
        mShareBean = getIntent().getParcelableExtra(KEY_SHARE_BEAN);
        //兼容自动播放视频和音频
//        mAgentWeb.getAgentWebSettings().getWebSettings().setMediaPlaybackRequiresUserGesture(false);
    }

    private MiddlewareWebChromeBase getMiddlewareWebChrome() {
        return new MiddlewareWebChromeBase() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (titleView != null) {
                    titleView.setTitleText(title);
                }
            }
        };
    }


    public void setShareBean(WebShareBean shareBean) {
        this.mShareBean = shareBean;
        if (shareBean != null && !TextUtils.isEmpty(shareBean.title)) {
            titleView.setTitleText(shareBean.title);
        }
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_web;
    }

    public void shareClick() {
        if (mShareBean == null) {
            mShareBean = new WebShareBean();
        }
        // TODO: 2019-09-10 H5有个需要点APP里的分享,需要自己处理一些东西的操作
        mAgentWeb.getJsAccessEntrace().quickCallJs("setShareToAndroid");

        ShareDialog shareDialog = ShareDialog.newInstance(mShareBean);
        shareDialog.setListener(new ShareDialog.SimperMediaCallBack() {
            @Override
            public void callback(WebShareBean bean) {
                if (TextUtils.isEmpty(bean.title)) {
                    bean.title = titleView.getTitleTextView().getText().toString();
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

    /**
     * 为了优化前后台切换的声音播放问题
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mAgentWeb != null) {
            //closemusic   openmusic
            mAgentWeb.getJsAccessEntrace().quickCallJs("closemusic");
            mAgentWeb.getWebLifeCycle().onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAgentWeb != null) {
            mAgentWeb.getJsAccessEntrace().quickCallJs("openmusic");
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
