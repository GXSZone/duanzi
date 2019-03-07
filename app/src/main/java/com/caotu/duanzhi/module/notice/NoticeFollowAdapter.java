package com.caotu.duanzhi.module.notice;

import android.view.View;
import android.widget.ImageView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DateUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;

import java.util.Date;

public class NoticeFollowAdapter extends BaseQuickAdapter<MessageDataBean.RowsBean, BaseViewHolder> {

    public NoticeFollowAdapter() {
        super(R.layout.item_notice_follow);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageDataBean.RowsBean item) {

        GlideImageView imageView = helper.getView(R.id.iv_notice_user);
        imageView.load(item.friendphoto, R.mipmap.touxiang_moren, 4);
        helper.addOnClickListener(R.id.iv_notice_user);

        String timeText = "";
        try {
            Date start = DateUtils.getDate(item.createtime, DateUtils.YMDHMS);
            timeText = DateUtils.showTimeText(start);
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.setText(R.id.notice_time, timeText);

        helper.setText(R.id.tv_item_user, item.friendname + " 关注了你");
        ImageView follow = helper.getView(R.id.iv_selector_is_follow);
        boolean isfollow = LikeAndUnlikeUtil.isLiked(item.isfollow);
        if (isfollow) {
            follow.setEnabled(false);
        } else {
            follow.setSelected(false);
        }
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHttpRequest.getInstance().<String>requestFocus(item.friendid, "2", true,
                        new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                follow.setEnabled(false);
                            }
                        });
            }
        });
    }
}
