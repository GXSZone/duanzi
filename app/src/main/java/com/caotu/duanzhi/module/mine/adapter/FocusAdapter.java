package com.caotu.duanzhi.module.mine.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.ToastUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;

import java.util.List;

/**
 * 关注用于页面
 */
public class FocusAdapter extends BaseQuickAdapter<ThemeBean, BaseViewHolder> {

    public FocusAdapter(@Nullable List<ThemeBean> data) {
        super(R.layout.focus_item_layout, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ThemeBean item) {
//        iv_item_image   R.id.iv_selector_is_follow
        helper.setText(R.id.tv_item_user, item.getThemeName());
        GlideImageView imageView = helper.getView(R.id.iv_item_image);
        imageView.load(item.getThemeAvatar(), R.mipmap.touxiang_moren, 4);

        boolean isMe = item.isMe();
        boolean isFocus = item.isFocus();
        ImageView follow = helper.getView(R.id.iv_selector_is_follow);

        initFollowState(isMe, isFocus, follow);
        initFollowClick(helper, item, isMe);

    }

    public void initFollowState(boolean isMe, boolean isFocus, ImageView follow) {
        if (isMe) {
            follow.setImageResource(isFocus ? R.drawable.follow_eachother : R.drawable.unfollow);
        } else {
            follow.setEnabled(!isFocus);
        }
    }

    public void initFollowClick(BaseViewHolder helper, ThemeBean item, boolean isMe) {
        helper.setOnClickListener(R.id.iv_selector_is_follow, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/11/5 只有在个人关注页面才能取消关注
                if (item.isMe()) {
                    requestFocus(v, helper.getAdapterPosition(), "2", false, item.getUserId(), isMe);
                } else {
                    if (!item.isFocus()) {
                        requestFocus(v, helper.getAdapterPosition(), "2", !item.isFocus(), item.getUserId(), isMe);
                    }
                }
            }
        });
    }

    public void requestFocus(View v, int adapterPosition, String s, boolean b, String userId, boolean isMe) {
        CommonHttpRequest.getInstance().<String>requestFocus(userId, s, b, new JsonCallback<BaseResponseBean<String>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<String>> response) {

                ImageView isFocusView = (ImageView) v;
                if (isMe) {
                    isFocusView.setImageResource(R.drawable.follow);
                    FocusAdapter.this.remove(adapterPosition);
                }else {
                    isFocusView.setEnabled(false);
                    ToastUtil.showShort("关注成功！");
                }
            }
        });
    }
}
