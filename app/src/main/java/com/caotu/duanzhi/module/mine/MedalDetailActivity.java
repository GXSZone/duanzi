package com.caotu.duanzhi.module.mine;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.sunfusheng.GlideImageView;

public class MedalDetailActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTvUserLevel;
    private TextView mTvCheckNumber;
    private TextView mTvTimeValidity;
    private LinearLayout mLlParentMedal;
    private GlideImageView userLevelLogo;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_medal_detail;
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.web_share).setOnClickListener(this);
        userLevelLogo = findViewById(R.id.iv_user_medal);
        mTvUserLevel = findViewById(R.id.tv_user_level);
        mTvCheckNumber = findViewById(R.id.tv_check_number);
        mTvTimeValidity = findViewById(R.id.tv_time_validity);
        mLlParentMedal = findViewById(R.id.ll_parent_medal);
        String id = getIntent().getStringExtra(HelperForStartActivity.KEY_MEDAL_ID);
        getDateAndBindDate(id);
    }

    private void getDateAndBindDate(String id) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            default:

                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.web_share:
                WebShareBean webBean = ShareHelper.getInstance().createWebBean(false, false,
                        null, null, null);
                ShareDialog shareDialog = ShareDialog.newInstance(webBean);

                shareDialog.setListener(new ShareDialog.ShareMediaCallBack() {
                    @Override
                    public void callback(WebShareBean bean) {
                        Bitmap viewBitmap = VideoAndFileUtils.getViewBitmap(mLlParentMedal);
                        ShareHelper.getInstance().shareJustBitmap(bean,viewBitmap);
                    }

                    @Override
                    public void colloection(boolean isCollection) {

                    }
                });
                break;
        }

    }
}
