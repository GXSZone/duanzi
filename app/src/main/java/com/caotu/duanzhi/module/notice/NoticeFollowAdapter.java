package com.caotu.duanzhi.module.notice;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.other.FastClickListener;
import com.caotu.duanzhi.view.widget.AvatarWithNameLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;

public class NoticeFollowAdapter extends BaseQuickAdapter<MessageDataBean.RowsBean, BaseViewHolder> {

    public NoticeFollowAdapter() {
        super(R.layout.item_user_info);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MessageDataBean.RowsBean item) {

        String friendname = item.friendname;
        if (!TextUtils.isEmpty(friendname) && friendname.length() > 4) {
            friendname = friendname.substring(0, 4) + "...";
        }

        AvatarWithNameLayout nameLayout = helper.getView(R.id.group_user_avatar);
        nameLayout.setUserText(friendname + " 关注了你", item.timeText);
        // TODO: 2019-10-24 第三个用户标签待定
        nameLayout.load(item.friendphoto, item.guajianurl, null);
        helper.addOnClickListener(R.id.group_user_avatar);

        TextView follow = helper.getView(R.id.iv_selector_is_follow);
        follow.setEnabled(!LikeAndUnlikeUtil.isLiked(item.isfollow));
        follow.setText(LikeAndUnlikeUtil.isLiked(item.isfollow) ? "已关注" : "关注");
        follow.setTag(UmengStatisticsKeyIds.follow_user);
        follow.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestFocus(item.friendid, "2", true,
                        new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                follow.setText("已关注");
                                follow.setEnabled(false);
                                item.isfollow = "1";
                                ToastUtil.showShort("关注成功");
                            }
                        });
            }
        });
    }
}
