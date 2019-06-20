package com.caotu.duanzhi.module.notice;

import android.text.TextUtils;
import android.widget.ImageView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DateUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.FastClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RImageView;

import java.util.Date;

public class NoticeFollowAdapter extends BaseQuickAdapter<MessageDataBean.RowsBean, BaseViewHolder> {

    public NoticeFollowAdapter() {
        super(R.layout.item_notice_follow);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageDataBean.RowsBean item) {

        RImageView imageView = helper.getView(R.id.iv_notice_user);
        GlideUtils.loadImage(item.friendphoto,imageView,false);
        helper.addOnClickListener(R.id.iv_notice_user);

        String timeText = "";
        try {
            Date start = DateUtils.getDate(item.createtime, DateUtils.YMDHMS);
            timeText = DateUtils.showTimeText(start);
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.setText(R.id.notice_time, timeText);
        String friendname = item.friendname;
        if (!TextUtils.isEmpty(friendname) && friendname.length() > 4) {
            friendname = friendname.substring(0, 4) + "...";
        }
        helper.setText(R.id.tv_item_user, friendname + " 关注了你");
        ImageView follow = helper.getView(R.id.iv_selector_is_follow);
        boolean isfollow = LikeAndUnlikeUtil.isLiked(item.isfollow);
        if (isfollow) {
            follow.setEnabled(false);
        } else {
            follow.setSelected(false);
        }
        follow.setTag(UmengStatisticsKeyIds.follow_user);
        follow.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestFocus(item.friendid, "2", true,
                        new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                follow.setEnabled(false);
                                ToastUtil.showShort("关注成功");
                            }
                        });
            }
        });
    }
}
