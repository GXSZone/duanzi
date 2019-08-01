package com.caotu.duanzhi.module.detail;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.download.VideoDownloadHelper;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.view.fixTextClick.SimpeClickSpan;
import com.dueeeke.videoplayer.listener.VideoListenerAdapter;
import com.dueeeke.videoplayer.playerui.StandardVideoController;
import com.lzy.okgo.model.Response;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe 内容详情页的头布局
 */
public class DetailHeaderViewHolder extends BaseHeaderHolder<MomentsDataBean> {

    @Override
    public void doOtherByChild(StandardVideoController controller, String contentId) {
        controller.setMyVideoOtherListener(new VideoListenerAdapter() {
            @Override
            public void share(byte type) {
                WebShareBean bean = ShareHelper.getInstance().changeContentBean(headerBean,
                        ShareHelper.translationShareType(type), cover, CommonHttpRequest.url);
                ShareHelper.getInstance().shareWeb(bean);
            }

            @Override
            public void download() {
                VideoDownloadHelper.getInstance().startDownLoad(true, contentId, videoUrl);
            }
            @Override
            public void mute() {
                UmengHelper.event(UmengStatisticsKeyIds.volume);
            }
        });
    }


    public DetailHeaderViewHolder(View parentView) {
        super(parentView);
    }

    /**
     * 评论成功数值加一
     */
    @Override
    public void commentPlus() {
        if (headerBean == null) return;
        int contentcomment = headerBean.getContentcomment();
        contentcomment++;
        setComment(contentcomment);
        headerBean.setContentcomment(contentcomment);
        if (getIsNeedSync()) {
            EventBusHelp.sendLikeAndUnlike(headerBean);
        }
    }

    public boolean getIsNeedSync() {
        return true;
    }

    @Override
    public void commentMinus() {
        if (headerBean == null) return;
        int contentcomment = headerBean.getContentcomment();
        contentcomment--;
        setComment(contentcomment);
        headerBean.setContentcomment(contentcomment);
        if (getIsNeedSync()) {
            EventBusHelp.sendLikeAndUnlike(headerBean);
        }
    }


    /**
     * 为了同步数据用
     *
     * @param data
     */
    @Override
    public void justBindCountAndState(MomentsDataBean data) {
//        headerBean = data;
        //1关注 0未关注  已经关注状态的不能取消关注
        String isfollow = data.getIsfollow();
        if (LikeAndUnlikeUtil.isLiked(isfollow)) {
            mIvIsFollow.setEnabled(false);
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

    @Override
    public void bindDate(MomentsDataBean data) {
        super.bindDate(data);
        GlideUtils.loadImage(data.getUserheadphoto(), mBaseMomentAvatarIv, false);
        if (userAvatar != null) {
            GlideUtils.loadImage(data.getUserheadphoto(), userAvatar, false);
        }
        guanjian.load(data.getGuajianurl());
        mBaseMomentNameTv.setText(data.getUsername());
        if (mUserName != null) {
            mUserName.setText(data.getUsername());
        }
        dealTextContent(data);
        setComment(data.getContentcomment());
    }

    @Override
    protected void dealType(MomentsDataBean data) {
        String contenttype = data.getContenttype();
        boolean isVideo = LikeAndUnlikeUtil.isVideoType(contenttype);
        if (isVideo) {
            videoView.setVisibility(View.VISIBLE);
            nineImageView.setVisibility(View.GONE);

            dealVideo(data.imgList.get(1).url, data.imgList.get(0).url,
                    data.getContentid(), "1".equals(data.getContenttype()),
                    data.getShowtime(), data.getPlaycount());
        } else {
            videoView.setVisibility(View.GONE);
            nineImageView.setVisibility(View.VISIBLE);
            dealNineLayout(data.imgList, data.getContentid(), data.getContenttag());
        }
    }

    @Override
    protected void dealLikeBt(MomentsDataBean data, View likeView) {
        UmengHelper.event(UmengStatisticsKeyIds.content_like);
        if (!LoginHelp.isLogin()) {
            LoginHelp.goLogin();
            return;
        }
        CommonHttpRequest.getInstance().requestLikeOrUnlike(data.getContentuid(),
                data.getContentid(), true, likeView.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        if (!likeView.isSelected()) {
                            LikeAndUnlikeUtil.showLike(likeView, 20, 30);
                        }
                        int likeCount = data.getContentgood();
                        if (likeView.isSelected()) {
                            likeCount--;
                            if (likeCount < 0) {
                                likeCount = 0;
                            }
                        } else {
                            likeCount++;
                        }

                        mBaseMomentLike.setSelected(!mBaseMomentLike.isSelected());
                        mBaseMomentLike.setText(Int2TextUtils.toText(likeCount, "w"));

                        bottomLikeView.setText(Int2TextUtils.toText(likeCount, "w"));
                        bottomLikeView.setSelected(!bottomLikeView.isSelected());

                        data.setContentgood(likeCount);
                        //修改goodstatus状态 "0"_未赞未踩 "1"_已赞 "2"_已踩
                        data.setGoodstatus(mBaseMomentLike.isSelected() ? "1" : "0");
                        if (getIsNeedSync()) {
                            EventBusHelp.sendLikeAndUnlike(data);
                        }
                    }
                });
    }

    public void dealTextContent(MomentsDataBean data) {
        if (!TextUtils.isEmpty(data.getTagshow())) {
            String source = "#" + data.getTagshow() + "#";
            if (TextUtils.equals("1", data.getIsshowtitle())) {
                source = source + data.getContenttitle();
            }
            SpannableString ss = new SpannableString(source);
            ss.setSpan(new SimpeClickSpan() {
                @Override
                public void onSpanClick(View widget) {
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, data.getTagshowid());
                }
            }, 0, data.getTagshow().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mTvContentText.setText(ss);
            mTvContentText.setMovementMethod(LinkMovementMethod.getInstance());
            mTvContentText.setVisibility(View.VISIBLE);

        } else {
            if (TextUtils.equals("1", data.getIsshowtitle())) {
                mTvContentText.setVisibility(View.VISIBLE);
                mTvContentText.setText(data.getContenttitle());
            } else {
                mTvContentText.setVisibility(View.GONE);
            }
        }
    }
}
