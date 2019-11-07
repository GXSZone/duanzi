package com.caotu.duanzhi.module.mine.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.widget.AvatarWithNameLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;

/**
 * 粉丝页面,包括我的粉丝和他人主页下的粉丝
 */
public class FansAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {
    public FansAdapter() {
        super(R.layout.item_user_info);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, UserBean item) {
        AvatarWithNameLayout nameLayout = helper.getView(R.id.group_user_avatar);
        nameLayout.setUserText(item.username, item.authname);
        nameLayout.load(item.userheadphoto, item.guajianurl, item.authpic);
        nameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_user,
                        item.userid);
            }
        });

        //关注按钮的模版代码
        TextView follow = helper.getView(R.id.iv_selector_is_follow);
        follow.setVisibility(MySpUtils.isMe(item.userid) ? View.GONE : View.VISIBLE);
        follow.setTag(UmengStatisticsKeyIds.follow_user);

        if (item.isMe) {
            follow.setText(item.isFocus ? "互相关注" : "关注");
        } else {
            follow.setText(item.isFocus ? "已关注" : "关注");
        }
        follow.setEnabled(!item.isFocus);
        follow.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                if (item.isFocus) return;
                requestFocus(follow, "2", item, item.isMe);
            }
        });
    }


    public void requestFocus(TextView v, String s, UserBean item, boolean isMe) {
        boolean focus = item.isFocus;
        CommonHttpRequest.getInstance().requestFocus(item.userid, s, !focus, new JsonCallback<BaseResponseBean<String>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<String>> response) {
                boolean isSuccess = response.body().getCode().equals(HttpCode.success_code);

                if (isSuccess) {
                    item.isFocus = !focus;
                    if (isMe) {
                        v.setText("互相关注");
                    } else {
                        v.setText("已关注");
                    }
                    v.setEnabled(false);
                    ToastUtil.showShort("关注成功！");
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
}
