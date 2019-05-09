package com.caotu.duanzhi.module.detail;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.FastClickListener;
import com.lzy.okgo.model.Response;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe 内容详情页的头布局
 */
public class DetailHeaderViewHolder extends BaseHeaderHolder<MomentsDataBean> {

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
        mBaseMomentComment.setText(Int2TextUtils.toText(contentcomment, "w"));
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
        mBaseMomentComment.setText(Int2TextUtils.toText(contentcomment, "w"));
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

        mBaseMomentLike.setText(Int2TextUtils.toText(data.getContentgood(), "w"));
        mBaseMomentUnlike.setText(Int2TextUtils.toText(data.getContentbad(), "w"));
        mBaseMomentComment.setText(Int2TextUtils.toText(data.getContentcomment(), "w"));

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
        } else if (TextUtils.equals("2", goodstatus)) {
            mBaseMomentUnlike.setSelected(true);
        }
    }

    @Override
    public void bindDate(MomentsDataBean data) {
        super.bindDate(data);
        GlideUtils.loadImage(data.getUserheadphoto(), mBaseMomentAvatarIv, false);
        guanjian.load(data.getGuajianurl());

        mBaseMomentNameTv.setText(data.getUsername());
        View.OnClickListener onClickListener = v -> HelperForStartActivity.
                openOther(HelperForStartActivity.type_other_user, data.getContentuid());
        mBaseMomentAvatarIv.setOnClickListener(onClickListener);
        mBaseMomentNameTv.setOnClickListener(onClickListener);


        AuthBean authBean = data.getAuth();
        if (authBean != null && !TextUtils.isEmpty(authBean.getAuthid())) {
            mUserAuth.setVisibility(View.VISIBLE);
            Log.i("authPic", "convert: " + authBean.getAuthpic());
            String cover = VideoAndFileUtils.getCover(authBean.getAuthpic());
            GlideUtils.loadImage(cover, mUserAuth);
        } else {
            mUserAuth.setVisibility(View.GONE);
        }
        mUserAuth.setOnClickListener(v -> {
            if (authBean != null && !TextUtils.isEmpty(authBean.getAuthurl())) {
                WebActivity.openWeb("用户勋章", authBean.getAuthurl(), true);
            }
        });

        String contenttype = data.getContenttype();
        boolean isVideo = LikeAndUnlikeUtil.isVideoType(contenttype);
        if (isVideo) {
            videoView.setVisibility(View.VISIBLE);
            nineImageView.setVisibility(View.GONE);

            dealVideo(data.imgList.get(1).url,data.imgList.get(0).url,
                    data.getContentid(), "1".equals(data.getContenttype()),
                    data.getShowtime(), data.getPlaycount());
        } else {
            videoView.setVisibility(View.GONE);
            nineImageView.setVisibility(View.VISIBLE);
            dealNineLayout(data.imgList, data.getContentid());
        }
        if (MySpUtils.isMe(data.getContentuid())) {
            mIvIsFollow.setVisibility(View.GONE);
        } else {
            mIvIsFollow.setVisibility(View.VISIBLE);
        }
        //1关注 0未关注  已经关注状态的不能取消关注
        String isfollow = data.getIsfollow();
        if (LikeAndUnlikeUtil.isLiked(isfollow)) {
            mIvIsFollow.setEnabled(false);
        }
        mIvIsFollow.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestFocus(data.getContentuid(),
                        "2", true, new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                ToastUtil.showShort("关注成功");
                                mIvIsFollow.setEnabled(false);
                                data.setIsfollow("1");
                            }

                            @Override
                            public void onError(Response<BaseResponseBean<String>> response) {
                                ToastUtil.showShort("关注失败,请稍后重试");
                                super.onError(response);
                            }
                        });
            }
        });
        mBaseMomentShareIv.setOnClickListener(v -> {
            if (callBack != null) {
                callBack.share(data);
            }
        });
        dealTextContent(data);
        dealLikeAndUnlike(data);
    }

    public void dealLikeAndUnlike(MomentsDataBean data) {
        /*-------------------------------点赞和踩的处理---------------------------------*/
        mBaseMomentLike.setText(Int2TextUtils.toText(data.getContentgood(), "w"));
        mBaseMomentUnlike.setText(Int2TextUtils.toText(data.getContentbad(), "w"));
        mBaseMomentComment.setText(Int2TextUtils.toText(data.getContentcomment(), "w"));
//        "0"_未赞未踩 "1"_已赞 "2"_已踩
        String goodstatus = data.getGoodstatus();

        if (TextUtils.equals("1", goodstatus)) {
            mBaseMomentLike.setSelected(true);
            mBaseMomentUnlike.setSelected(false);
        } else if (TextUtils.equals("2", goodstatus)) {
            mBaseMomentUnlike.setSelected(true);
            mBaseMomentLike.setSelected(false);
        } else {
            mBaseMomentUnlike.setSelected(false);
            mBaseMomentLike.setSelected(false);
        }
        mBaseMomentLike.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestLikeOrUnlike(data.getContentuid(),
                        data.getContentid(), true, mBaseMomentLike.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                if (!mBaseMomentLike.isSelected()) {
                                    LikeAndUnlikeUtil.showLike(mBaseMomentLike, 20, 30);
                                }
                                if (TextUtils.equals("2", data.getGoodstatus())) {
                                    mBaseMomentUnlike.setSelected(false);
                                    if (data.getContentbad() > 0) {
                                        data.setContentbad(data.getContentbad() - 1);
                                        mBaseMomentUnlike.setText(Int2TextUtils.toText(data.getContentbad(), "w"));
                                    }
                                }
                                int goodCount = data.getContentgood();
                                if (mBaseMomentLike.isSelected()) {
                                    goodCount--;
                                    mBaseMomentLike.setSelected(false);
                                } else {
                                    goodCount++;
                                    mBaseMomentLike.setSelected(true);
                                }
                                mBaseMomentLike.setText(Int2TextUtils.toText(goodCount, "w"));
                                data.setContentgood(goodCount);
                                //修改goodstatus状态 "0"_未赞未踩 "1"_已赞 "2"_已踩
                                data.setGoodstatus(mBaseMomentLike.isSelected() ? "1" : "0");
                                EventBusHelp.sendLikeAndUnlike(data);

                            }
                        });
            }
        });

        mBaseMomentUnlike.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestLikeOrUnlike(data.getContentuid(),
                        data.getContentid(), false, mBaseMomentUnlike.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                if (TextUtils.equals("1", data.getGoodstatus())) {
                                    mBaseMomentLike.setSelected(false);
                                    if (data.getContentgood() > 0) {
                                        data.setContentgood(data.getContentgood() - 1);
                                        mBaseMomentLike.setText(Int2TextUtils.toText(data.getContentgood(), "w"));
                                    }
                                }
                                int badCount = data.getContentbad();
                                if (mBaseMomentUnlike.isSelected()) {
                                    badCount--;
                                    mBaseMomentUnlike.setSelected(false);
                                } else {
                                    badCount++;
                                    mBaseMomentUnlike.setSelected(true);
                                }
                                mBaseMomentUnlike.setText(Int2TextUtils.toText(badCount, "w"));
                                data.setContentbad(badCount);
                                //修改goodstatus状态 "0"_未赞未踩 "1"_已赞 "2"_已踩
                                data.setGoodstatus(mBaseMomentUnlike.isSelected() ? "2" : "0");
                                EventBusHelp.sendLikeAndUnlike(data);
                            }
                        });
            }
        });
        /*-------------------------------点赞和踩的处理结束---------------------------------*/
    }

    public void dealTextContent(MomentsDataBean data) {
        if (!TextUtils.isEmpty(data.getTagshow())) {
            String source = "#" + data.getTagshow() + "#";
            if (TextUtils.equals("1", data.getIsshowtitle())) {
                source = source + data.getContenttitle();
            }
            SpannableString ss = new SpannableString(source);
            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    // TODO: 2018/11/8 话题详情
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, data.getTagshowid());
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            }, 0, data.getTagshow().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            ss.setSpan(new ForegroundColorSpan(DevicesUtils.getColor(R.color.color_FF698F)),
                    0, data.getTagshow().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
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
