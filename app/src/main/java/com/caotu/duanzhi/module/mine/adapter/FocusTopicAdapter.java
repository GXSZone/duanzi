package com.caotu.duanzhi.module.mine.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.ToastUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;

/**
 * @author mac
 * @日期: 2018/11/5
 * @describe TODO
 */
public class FocusTopicAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {
    public FocusTopicAdapter() {
        super(R.layout.item_focus_layout);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UserBean item) {
        helper.setText(R.id.tv_item_user, item.username);
        GlideImageView imageView = helper.getView(R.id.iv_item_image);
        imageView.load(item.userheadphoto, R.mipmap.touxiang_moren, 4);
        ImageView follow = helper.getView(R.id.iv_selector_is_follow);
        if (item.isMe) {
            follow.setSelected(true);
        } else {
            follow.setEnabled(!item.isFocus);
        }
        follow.setTag(UmengStatisticsKeyIds.follow_topic);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //只有关注操作,没有取消关注的操作,只有在自己主页才能取消,他人主页下关注完后不能取消关注了
                if (item.isMe) {
                    requestFocus(item, follow, helper.getLayoutPosition(), false, item.userid, true);
                } else {
                    if (item.isFocus) return;
                    requestFocus(item, follow, helper.getLayoutPosition(), true, item.userid, false);
                }
            }
        });
    }

    public void requestFocus(UserBean item, View v, int adapterPosition, boolean b, String userId, boolean isMe) {
        CommonHttpRequest.getInstance().requestFocus(userId, "1", b, new JsonCallback<BaseResponseBean<String>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<String>> response) {
                ImageView isFocusView = (ImageView) v;
                if (isMe) {
                    try {
                        //会有奇怪的角标越界
                        remove(adapterPosition);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ToastUtil.showShort("取消关注成功！");
                } else {
                    item.isFocus = true;
                    ToastUtil.showShort("关注成功！");
                    isFocusView.setEnabled(false);
                }
            }
        });
    }
}
