package com.caotu.duanzhi.module.mine;

import android.view.View;
import android.webkit.WebView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.view.dialog.CustomHelpEditDialog;


public class HelpAndFeedbackActivity extends BaseActivity implements View.OnClickListener {
    private CustomHelpEditDialog customHelpEditDialog;
    private WebView mWebView;
    public static final String KEY_FEEDBACK = "file:///android_asset/html/HelpAndCallBack.html";


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }

    @Override
    public void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.show_dialog).setOnClickListener(this);
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.loadUrl(KEY_FEEDBACK);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_feedback_layout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.show_dialog:
                if (customHelpEditDialog == null) {
                    customHelpEditDialog = new CustomHelpEditDialog(this);
                    customHelpEditDialog.setOnClickListener(new CustomHelpEditDialog.OnClickListener() {
                        @Override
                        public void onClickHelpException() {
                            SubmitFeedBackActivity.start(SubmitFeedBackActivity.EXCEPTION);
                        }

                        @Override
                        public void onClickHelpOther() {
                            SubmitFeedBackActivity.start(SubmitFeedBackActivity.OTHER);
                        }
                    });
                }
                customHelpEditDialog.show();
                break;
        }
    }

}
