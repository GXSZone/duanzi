package com.caotu.duanzhi.module.mine.adapter;

import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.widget.AvatarWithNameLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;

/**
 * 关注的用户页面,包括我的关注和他人主页下面的关注,有区别
 */
public class FocusAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {

    public FocusAdapter() {
        super(R.layout.item_user_info);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBean item) {
        AvatarWithNameLayout nameLayout = helper.getView(R.id.group_user_avatar);
        nameLayout.setUserText(item.username, item.authname);
        nameLayout.load(item.userheadphoto, item.guajianurl, item.authpic);

        //关注按钮的模版代码
        TextView follow = helper.getView(R.id.iv_selector_is_follow);
        follow.setVisibility(MySpUtils.isMe(item.userid) ? View.GONE : View.VISIBLE);
        follow.setTag(UmengStatisticsKeyIds.follow_user);

        if (item.isMe) {
            follow.setText(item.isFocus ? "互相关注" : "取消关注");
        } else {
            follow.setText(item.isFocus ? "已关注" : "关注");
            follow.setEnabled(!item.isFocus);
        }

        follow.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                // TODO: 2018/11/5 只有在个人关注页面才能取消关注
                if (item.isMe) {
                    requestFocus(item, follow, helper.getLayoutPosition(), "2", false, item.userid, true);
                } else {
                    if (!item.isFocus) {
                        requestFocus(item, follow, helper.getLayoutPosition(), "2", true, item.userid, false);
                    }
                }
            }
        });
    }

    public void requestFocus(UserBean item, TextView v, int adapterPosition, String s, boolean b, String userId, boolean isMe) {
        if (adapterPosition < 0) return;
        CommonHttpRequest.getInstance().requestFocus(userId, s, b, new JsonCallback<BaseResponseBean<String>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<String>> response) {

                if (isMe) {
                    if (adapterPosition >= getData().size()) {
                        setNewData(null);
                    } else {
                        remove(adapterPosition);
                    }
                    ToastUtil.showShort("取消关注成功");
                } else {
                    v.setText("已关注");
                    v.setEnabled(false);
                    item.isFocus = true;
                    ToastUtil.showShort("关注成功！");
                }
            }
        });
    }
}
