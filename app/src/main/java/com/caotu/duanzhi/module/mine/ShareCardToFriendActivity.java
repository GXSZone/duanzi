package com.caotu.duanzhi.module.mine;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class ShareCardToFriendActivity extends BaseActivity implements View.OnClickListener {


    private ScrollView scrollView;

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        ImageView user_photo = findViewById(R.id.iv_user_avatar);
        TextView userName = findViewById(R.id.tv_user_name);
        GlideUtils.loadImage(MySpUtils.getString(MySpUtils.SP_MY_AVATAR), user_photo, true);
        userName.setText(MySpUtils.getMyName());
        findViewById(R.id.share_weixin).setOnClickListener(this);
        findViewById(R.id.share_friend).setOnClickListener(this);
        findViewById(R.id.share_qq).setOnClickListener(this);
        findViewById(R.id.share_qq_space).setOnClickListener(this);
        findViewById(R.id.share_weibo).setOnClickListener(this);
        scrollView = findViewById(R.id.scroll_view_share_card);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_share_card;
    }

    SHARE_MEDIA medial = null;

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            default:
                medial = null;
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.share_weixin:
                medial = SHARE_MEDIA.WEIXIN;
                break;
            case R.id.share_friend:
                medial = SHARE_MEDIA.WEIXIN_CIRCLE;
                break;
            case R.id.share_qq:
                medial = SHARE_MEDIA.QQ;
                break;
            case R.id.share_qq_space:
                medial = SHARE_MEDIA.QZONE;
                break;
            case R.id.share_weibo:
                medial = SHARE_MEDIA.SINA;
                break;

        }
        if (medial != null) {
            Bitmap viewBitmap = VideoAndFileUtils.getLongViewBitmap(scrollView);
            ShareHelper.getInstance().shareImage(medial, viewBitmap);
        }
    }
}
