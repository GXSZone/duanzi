package com.caotu.duanzhi.module.search;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBaseInfoBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.FastClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RImageView;

public class SearchUserAdapter extends BaseQuickAdapter<UserBaseInfoBean.UserInfoBean, BaseViewHolder> {
    public SearchUserAdapter() {
        super(R.layout.item_search_user_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBaseInfoBean.UserInfoBean item) {
        RImageView imageView = helper.getView(R.id.iv_topic_image);
        GlideUtils.loadImage(item.getUserheadphoto(), imageView, true);
        helper.setText(R.id.tv_topic_name, item.getUsername());

        ImageView mUserAuth = helper.getView(R.id.user_auth);
        AuthBean authBean = item.getAuth();
        if (authBean != null && !TextUtils.isEmpty(authBean.getAuthid())) {
            mUserAuth.setVisibility(View.VISIBLE);
            String cover = VideoAndFileUtils.getCover(authBean.getAuthpic());
            GlideUtils.loadImage(cover, mUserAuth);
        } else {
            mUserAuth.setVisibility(View.GONE);
        }

        mUserAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authBean != null && !TextUtils.isEmpty(authBean.getAuthurl())) {
                    WebActivity.openWeb("用户勋章", authBean.getAuthurl(), true);
                }
            }
        });

        helper.setText(R.id.tv_user_number, "段友号: " + item.getUno());
        RImageView isFollow = helper.getView(R.id.iv_user_follow);
        if (TextUtils.equals("1", item.getIsfollow())) {
            isFollow.setEnabled(false);
        }
        isFollow.setVisibility(TextUtils.equals(item.getUserid(), MySpUtils.getMyId())
                ? View.GONE : View.VISIBLE);
        //需要判断是否登录
        isFollow.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestFocus(item.getUserid(), "2",
                        true, new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                isFollow.setEnabled(false);
                                item.setIsfollow("1");
                                ToastUtil.showShort("关注成功");
                            }
                        });
            }
        });

    }
}
