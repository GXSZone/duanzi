package com.caotu.duanzhi.module.mine;

import android.graphics.Bitmap;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.caotu.duanzhi.view.widget.TitleView;
import com.sunfusheng.GlideImageView;

public class MedalDetailActivity extends BaseActivity {

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
        TitleView titleView = findViewById(R.id.title_view);
        titleView.setTitleText("段友守护者");
        titleView.setMoreView(R.mipmap.home_share);
        titleView.setClickListener(() -> {
            ShareDialog shareDialog = ShareDialog.newInstance(new WebShareBean());
            shareDialog.setListener(new ShareDialog.SimperMediaCallBack() {
                @Override
                public void callback(WebShareBean bean) {
                    Bitmap viewBitmap = VideoAndFileUtils.getViewBitmap(mLlParentMedal);
                    ShareHelper.getInstance().shareJustBitmap(bean, viewBitmap);
                }
            });
            shareDialog.show(getSupportFragmentManager(), "share");
        });
        userLevelLogo = findViewById(R.id.iv_user_medal);
        mTvUserLevel = findViewById(R.id.tv_user_level);
        mTvCheckNumber = findViewById(R.id.tv_check_number);
        mTvTimeValidity = findViewById(R.id.tv_time_validity);
        mLlParentMedal = findViewById(R.id.ll_parent_medal);
        UserBaseInfoBean.UserInfoBean.HonorlistBean honorlistBean = getIntent().getParcelableExtra(HelperForStartActivity.KEY_MEDAL_ID);
        getDateAndBindDate(honorlistBean);
    }

    private void getDateAndBindDate(UserBaseInfoBean.UserInfoBean.HonorlistBean bean) {
        userLevelLogo.load(bean.levelinfo.pic3);
        mTvUserLevel.setText(bean.levelinfo.word);
        String detailinfo = bean.detailinfo;
        mTvCheckNumber.setText(String.format("累计审核%s条", detailinfo == null ? 0 : detailinfo));
        mTvTimeValidity.setText(String.format("%s获得\n有效期至：段友守护者任期结束", bean.gethonortime));
    }
}
