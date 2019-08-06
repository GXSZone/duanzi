package com.caotu.duanzhi.module.holder;

import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.dueeeke.videoplayer.player.IjkVideoView;

public class VideoHeaderHolder extends DetailHeaderViewHolder {
    public VideoHeaderHolder(View parentView) {
        super(parentView);
    }

    @Override
    public void bindDate(MomentsDataBean data) {
        headerBean = data;
        if (userAvatar != null) {
            GlideUtils.loadImage(data.getUserheadphoto(), userAvatar, false);
        }
        if (mUserName != null) {
            mUserName.setText(data.getUsername());
        }
        dealFollow(data);
        dealLikeAndUnlike(data);
        dealTextContent(data);
        setComment(data.getContentcomment());

        dealVideo(data.imgList.get(1).url, data.imgList.get(0).url,
                data.getContentid(), "1".equals(data.getContenttype()),
                data.getShowtime(), data.getPlaycount());
    }

    public void setVideoView(IjkVideoView view) {
        videoView = view;
    }

    protected void dealFollow(MomentsDataBean dataBean) {
        String userId = dataBean.getContentuid();
        String isFollow = dataBean.getIsfollow();
        if (MySpUtils.isMe(userId)) {
            if (mUserIsFollow != null) {
                mUserIsFollow.setVisibility(View.GONE);
            }
        } else {
            if (mUserIsFollow != null) {
                mUserIsFollow.setVisibility(View.VISIBLE);
            }
        }
        //1关注 0未关注  已经关注状态的不能取消关注
        if (LikeAndUnlikeUtil.isLiked(isFollow)) {
            if (mUserIsFollow != null) {
                mUserIsFollow.setText("已关注");
                mUserIsFollow.setEnabled(false);
            }
        }
    }

    @Override
    public void justBindCountAndState(MomentsDataBean data) {
//        headerBean = data;
        //1关注 0未关注  已经关注状态的不能取消关注
        String isfollow = data.getIsfollow();
        if (LikeAndUnlikeUtil.isLiked(isfollow)) {
            mUserIsFollow.setEnabled(false);
        }
        setComment(data.getContentcomment());
        mBaseMomentLike.setText(Int2TextUtils.toText(data.getContentgood(), "w"));

        if (headerBean != null) {
            boolean hasChangeComment = headerBean.getContentcomment() != data.getContentcomment();
            boolean hasChangeLike = headerBean.getContentgood() != data.getContentgood();
            boolean hasChangeBad = headerBean.getContentbad() != data.getContentbad();
            if (hasChangeBad || hasChangeLike || hasChangeComment) {
                // TODO: 2019/4/11 对象还用同一个不然转换的数据就没了
                headerBean.setContentgood(data.getContentgood());
                headerBean.setContentbad(data.getContentbad());
                headerBean.setContentcomment(data.getContentcomment());
                EventBusHelp.sendLikeAndUnlike(headerBean);
            }
        }
//        "0"_未赞未踩 "1"_已赞 "2"_已踩
        String goodstatus = data.getGoodstatus();

        if (TextUtils.equals("1", goodstatus)) {
            mBaseMomentLike.setSelected(true);
        }
    }
}
