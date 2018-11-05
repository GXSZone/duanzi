package com.caotu.duanzhi.module.mine.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpCode;
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

    public void initFollowState(boolean isMe, boolean isFocus, ImageView follow) {
        if (isMe) {
            follow.setImageResource(isFocus ? R.drawable.follow_eachother : R.drawable.follow);
        } else {
            follow.setImageResource(isFocus ? R.drawable.unfollow : R.drawable.follow);
        }
    }

    public void initFollowClick(BaseViewHolder helper, ThemeBean item, boolean isMe) {
        helper.setOnClickListener(R.id.iv_selector_is_follow, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isFocus()) return;
                requestFocus(v, helper.getAdapterPosition(), "2", item, isMe);

            }
        });
    }

    public void requestFocus(View v, int adapterPosition, String s, ThemeBean item, boolean isMe) {
        boolean focus = item.isFocus();
        CommonHttpRequest.getInstance().<String>requestFocus(item.getUserId(), s, !focus, new JsonCallback<BaseResponseBean<String>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<String>> response) {
                boolean isSuccess = response.body().getCode().equals(HttpCode.success_code);
                ImageView isFocusView = (ImageView) v;
                if (isSuccess) {
                    FansAdapter.this.getData().get(adapterPosition).setFocus(!focus);
                    clickSuccess(isFocusView, isMe, focus, adapterPosition);
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

    public void clickSuccess(ImageView isFocusView, boolean isMe, boolean b, int adapterPosition) {
// TODO: 2018/11/5 目前需求取消关注只在我的关注页面才能取消
        if (isMe) {
            isFocusView.setImageResource(R.drawable.follow_eachother);
            ToastUtil.showShort("关注成功！");
        } else {
            isFocusView.setImageResource(R.drawable.unfollow);
            ToastUtil.showShort("关注成功！");
        }
        isFocusView.setEnabled(false);
    }
}
