package com.caotu.duanzhi.module.detail_scroll;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UrlCheckBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.just.agentweb.AgentWeb;
import com.lzy.okgo.model.Response;

public class WebFragment extends BaseFragment implements View.OnClickListener {

    private AgentWeb mAgentWeb;
    /**
     * 这个分享bean对象还得判断是否为空
     */
    public String shareUrl, mShareTitle;
    private ViewGroup webContent;
    private View errorView;

    private ImageView shareIcon;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_web;
    }

    @Override
    protected void initDate() {
        CommonHttpRequest.getInstance().checkUrl(shareUrl, new JsonCallback<BaseResponseBean<UrlCheckBean>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<UrlCheckBean>> response) {
                // TODO: 2018/12/25 保存接口给的key,H5认证使用
                UrlCheckBean data = response.body().getData();
                WebActivity.H5_KEY = data.getReturnkey();
                boolean isShowShareIcon = TextUtils.equals("1", data.getIsshare());
                shareIcon.setVisibility(isShowShareIcon ? View.VISIBLE : View.INVISIBLE);
                loadUrl();
            }
        });
    }

    private void loadUrl() {
        mAgentWeb = AgentWeb.with(MyApplication.getInstance().getRunningActivity())
                .setAgentWebParent(webContent,
                        new FrameLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
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
        mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface());
    }

    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    protected void initView(View inflate) {
        webContent = inflate.findViewById(R.id.web_content);
        errorView = LayoutInflater.from(getContext()).inflate(R.layout.layout_no_network, webContent, false);
        inflate.findViewById(R.id.iv_back).setOnClickListener(this);
        shareIcon = inflate.findViewById(R.id.web_share);
        shareIcon.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
    }

    @Override
    public void onReStart() {
        super.onReStart();
        if (LoginHelp.isLogin()) {
            initDate();
        }
    }

    /**
     * 为了判断去登录页登录
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
    }

    //这个会导致webview回来的时候加载不出来,白屏的问题
    @Override
    public void onDestroyView() {
        if (mAgentWeb != null) {
            mAgentWeb.destroy();
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        super.onDestroyView();
    }

    public void setDate(String info, String shareTitle) {
        shareUrl = info;
        mShareTitle = shareTitle;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.web_share:
                WebShareBean bean = new WebShareBean();
                bean.url = shareUrl;
                bean.title = mShareTitle;
                ShareDialog shareDialog = ShareDialog.newInstance(bean);
                shareDialog.setListener(new ShareDialog.SimperMediaCallBack() {
                    @Override
                    public void callback(WebShareBean bean) {
                        ShareHelper.getInstance().shareFromWebView(bean);
                    }
                });
                shareDialog.show(getChildFragmentManager(), "share");

                break;
            case R.id.iv_back:
                requireActivity().finish();
                break;
        }
    }
}
