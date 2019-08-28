package com.caotu.duanzhi.module.search;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.GlideUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ruffian.library.widget.RImageView;
import com.sunfusheng.GlideImageView;

public class AtUserAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {
    public AtUserAdapter() {
        super(R.layout.item_at_user_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBean item) {
        TextView groupView = helper.getView(R.id.item_group_tv);
        helper.setGone(R.id.item_group_tv, item.isHeader);
        if (item.isHeader && groupView.getVisibility() == View.VISIBLE) {
            groupView.setText(item.isFocus ? "我关注的人" : "我最近@的人");
        }

        RImageView imageView = helper.getView(R.id.iv_item_image);
        if (!TextUtils.isEmpty(item.userheadphoto)) {
            GlideUtils.loadImage(item.userheadphoto, imageView, false);
        }
        helper.setText(R.id.tv_item_user, item.username);

        GlideImageView mUserAuth = helper.getView(R.id.user_auth);

        if (!TextUtils.isEmpty(item.authpic)) {
            mUserAuth.setVisibility(View.VISIBLE);
            mUserAuth.load(item.authpic);
        } else {
            mUserAuth.setVisibility(View.GONE);
        }
    }
}
