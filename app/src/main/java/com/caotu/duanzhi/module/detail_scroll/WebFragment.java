package com.caotu.duanzhi.module.detail_scroll;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseFragment;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.just.agentweb.AgentWeb;

public class WebFragment extends BaseFragment implements View.OnClickListener {

    private AgentWeb mAgentWeb;
    /**
     * 这个分享bean对象还得判断是否为空
     */
    public String shareUrl;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_web;
    }

    @Override
    protected void initDate() {
        if (getActivity() != null && getActivity() instanceof ContentScrollDetailActivity) {

        }
    }

    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    protected void initView(View inflate) {
        ViewGroup webContent = inflate.findViewById(R.id.web_content);
        View errorView = LayoutInflater.from(getContext()).inflate(R.layout.layout_no_network, webContent, false);
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(webContent,
                        new FrameLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
//                .setWebChromeClient(mWebChromeClient)
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
    }


//    private WebChromeClient mWebChromeClient = new WebChromeClient() {
//        @Override
//        public void onReceivedTitle(WebView view, String title) {
//            super.onReceivedTitle(view, title);
//            webTitle.setText(title);
//        }
//    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.web_share) {
            WebShareBean webBean = ShareHelper.getInstance().createWebBean(false, false, null
                    , null, null);
            ShareDialog shareDialog = ShareDialog.newInstance(webBean);
            shareDialog.setListener(new ShareDialog.ShareMediaCallBack() {
                @Override
                public void callback(WebShareBean bean) {
                    if (bean != null) {
                        bean.url = shareUrl;

                        bean.title = "web";
                    }
                    ShareHelper.getInstance().shareFromWebView(bean);
                }

                @Override
                public void colloection(boolean isCollection) {

                }
            });
            shareDialog.show(getChildFragmentManager(), "share");
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
    }

    @Override
    public void onDestroyView() {
        if (mAgentWeb != null) {
            mAgentWeb.destroy();
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
        super.onDestroyView();
    }

    public void setDate(String info) {
        shareUrl = info;
    }
}
