package com.caotu.duanzhi.module.search;

import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.view.widget.AvatarWithNameLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

public class AtUserAdapter extends BaseQuickAdapter<UserBean, BaseViewHolder> {
    public AtUserAdapter() {
        super(R.layout.item_user_info); //item_at_user_layout
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBean item) {
        helper.setGone(R.id.iv_selector_is_follow,false);
        //"我关注的人" : "我最近@的人"
        AvatarWithNameLayout nameLayout = helper.getView(R.id.group_user_avatar);
        //第二个参数待定
        nameLayout.setUserText(item.username, item.authname);
        nameLayout.load(item.userheadphoto, null, item.authpic);
    }
}
