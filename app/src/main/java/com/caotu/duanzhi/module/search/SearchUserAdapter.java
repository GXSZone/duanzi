package com.caotu.duanzhi.module.search;

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

public class SearchUserAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {
    public SearchUserAdapter() {
        super(R.layout.item_user_info);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBean item) {
        AvatarWithNameLayout nameLayout = helper.getView(R.id.group_user_avatar);
        //第二个参数待定
        nameLayout.setUserText(item.username, item.authname);
        // TODO: 2019-10-24 第三个用户标签待定
        nameLayout.load(item.userheadphoto, null, item.authpic);

        //关注按钮的模版代码
        TextView follow = helper.getView(R.id.iv_selector_is_follow);
        follow.setVisibility(MySpUtils.isMe(item.userid) ? View.GONE : View.VISIBLE);
        follow.setEnabled(!item.isFocus);
        follow.setTag(UmengStatisticsKeyIds.follow_user);
        follow.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestFocus(item.userid, "2", true,
                        new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                follow.setText("已关注");
                                follow.setEnabled(false);
                                ToastUtil.showShort("关注成功");
                            }
                        });
            }
        });

        helper.setText(R.id.tv_user_auth, "我是用户认证说明");
    }
}
