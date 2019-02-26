package com.caotu.duanzhi.module.mine.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;

import java.util.List;

/**
 * 关注用于页面
 */
public class FansAdapter extends FocusAdapter {
    public FansAdapter(@Nullable List<ThemeBean> data) {
        super(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ThemeBean item) {
        super.convert(helper, item);
        helper.setOnClickListener(R.id.iv_item_image, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_user,
                        item.getUserId());
            }
        });

        helper.setOnClickListener(R.id.tv_item_user, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_user,
                        item.getUserId());
            }
        });

    }

    public void initFollowState(boolean isMe, boolean isFocus, ImageView follow, ThemeBean item) {
        if (isMe) {
            follow.setImageResource(isFocus ? R.drawable.follow_eachother : R.drawable.follow);
        } else {
            follow.setImageResource(isFocus ? R.drawable.unfollow : R.drawable.follow);
        }
        follow.setVisibility(TextUtils.equals(item.getUserId(), MySpUtils.getMyId())
                ? View.GONE : View.VISIBLE);
    }

    public void initFollowClick(BaseViewHolder helper, ThemeBean item, boolean isMe) {
        helper.setOnClickListener(R.id.iv_selector_is_follow, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isFocus()) return;
                requestFocus(v, "2", item, isMe);

            }
        });
    }

    public void requestFocus(View v,  String s, ThemeBean item, boolean isMe) {
        boolean focus = item.isFocus();
        CommonHttpRequest.getInstance().<String>requestFocus(item.getUserId(), s, !focus, new JsonCallback<BaseResponseBean<String>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<String>> response) {
                boolean isSuccess = response.body().getCode().equals(HttpCode.success_code);
                ImageView isFocusView = (ImageView) v;
                if (isSuccess) {
                    item.setFocus(!focus);
                    clickSuccess(isFocusView, isMe);
                    return;
                }
                if (focus) {
                    ToastUtil.showShort("关注失败！");
                } else {
                    ToastUtil.showShort("取消关注失败！");
                }
            }
        });
    }

    public void clickSuccess(ImageView isFocusView, boolean isMe) {
// TODO: 2018/11/5 目前需求取消关注只在我的关注页面才能取消
        if (isMe) {
            isFocusView.setImageResource(R.drawable.follow_eachother);
            ToastUtil.showShort("关注成功！");
        } else {
            isFocusView.setImageResource(R.drawable.unfollow);
            ToastUtil.showShort("关注成功！");
        }
//        isFocusView.setEnabled(false);
    }
}
