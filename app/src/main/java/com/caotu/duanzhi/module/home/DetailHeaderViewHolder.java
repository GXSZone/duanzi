package com.caotu.duanzhi.module.home;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.LogUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RImageView;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.JZMediaManager;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe TODO
 */
public class DetailHeaderViewHolder implements IHolder {

    public View parentView;
    public RImageView mBaseMomentAvatarIv;
    public TextView mBaseMomentNameTv;
    public ImageView mIvIsFollow, mUserAuth;
    public TextView mTvContentText;

    public TextView mBaseMomentLike, mBaseMomentUnlike, mBaseMomentComment;
    public ImageView mBaseMomentShareIv;
    public NineImageView nineImageView;
    public MyVideoPlayerStandard videoView;
    ContentDetailFragment fragment;
    int mVideoProgress;

    public DetailHeaderViewHolder(ContentDetailFragment fragment, View rootView, int mVideoProgress) {
        this.parentView = rootView;
        this.fragment = fragment;
        this.mVideoProgress = mVideoProgress;
        this.mBaseMomentAvatarIv = (RImageView) rootView.findViewById(R.id.base_moment_avatar_iv);
        this.mBaseMomentNameTv = (TextView) rootView.findViewById(R.id.base_moment_name_tv);
        this.mIvIsFollow = (ImageView) rootView.findViewById(R.id.iv_is_follow);
        this.mTvContentText = (TextView) rootView.findViewById(R.id.tv_content_text);
        this.mBaseMomentLike = rootView.findViewById(R.id.base_moment_like);
        this.mBaseMomentUnlike = rootView.findViewById(R.id.base_moment_unlike);
        this.mBaseMomentComment = (TextView) rootView.findViewById(R.id.base_moment_comment);
        this.mBaseMomentShareIv = (ImageView) rootView.findViewById(R.id.base_moment_share_iv);
        this.nineImageView = rootView.findViewById(R.id.detail_image_type);
        this.videoView = rootView.findViewById(R.id.detail_video_type);
        mUserAuth = rootView.findViewById(R.id.user_auth);
    }

    public MyVideoPlayerStandard getVideoView() {
        return videoView;
    }

    private boolean isVideo;
    //分享需要的icon使用记录
    private String cover;
    //视频的下载URL
    private String videoUrl;
    //横视频是1,默认则为竖视频
    private boolean landscape;

    @Override
    public boolean isLandscape() {
        return landscape;
    }

    @Override
    public String getVideoUrl() {
        return videoUrl;
    }

    @Override
    public String getCover() {
        return cover;
    }

    @Override
    public boolean isVideo() {
        return isVideo;
    }

    /**
     * 评论成功数值加一
     */
    @Override
    public void commentPlus() {
        int contentcomment = headerBean.getContentcomment();
        contentcomment++;
        mBaseMomentComment.setText(Int2TextUtils.toText(contentcomment, "w"));
        headerBean.setContentcomment(contentcomment);
        EventBusHelp.sendLikeAndUnlike(headerBean);
    }

    @Override
    public int headerViewHeight() {
        return parentView == null ? 0 : parentView.getMeasuredHeight();
    }


    MomentsDataBean headerBean;

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
        headerBean = data;
        GlideUtils.loadImage(data.getUserheadphoto(), mBaseMomentAvatarIv);
        mBaseMomentNameTv.setText(data.getUsername());
        mBaseMomentAvatarIv.setOnClickListener(v -> HelperForStartActivity.
                openOther(HelperForStartActivity.type_other_user, data.getContentuid()));
        mBaseMomentNameTv.setOnClickListener(v -> HelperForStartActivity.
                openOther(HelperForStartActivity.type_other_user, data.getContentuid()));


        AuthBean authBean = data.getAuth();
        if (authBean != null && !TextUtils.isEmpty(authBean.getAuthid())) {
            mUserAuth.setVisibility(View.VISIBLE);
            Log.i("authPic", "convert: " + authBean.getAuthpic());
            String cover = VideoAndFileUtils.getCover(authBean.getAuthpic());
            GlideUtils.loadImage(cover, mUserAuth);
        } else {
            mUserAuth.setVisibility(View.GONE);
        }
        mUserAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authBean != null && !TextUtils.isEmpty(authBean.getAuthurl())) {
                    WebActivity.openWeb("用户勋章", authBean.getAuthurl(), true);
                }
            }
        });

        String contenttype = data.getContenttype();
        isVideo = LikeAndUnlikeUtil.isVideoType(contenttype);
        if (isVideo) {
            videoView.setVisibility(View.VISIBLE);
            nineImageView.setVisibility(View.GONE);
            dealVideo(data);
        } else {
            videoView.setVisibility(View.GONE);
            nineImageView.setVisibility(View.VISIBLE);
            dealNineLayout(data);
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
                CommonHttpRequest.getInstance().<String>requestFocus(data.getContentuid(),
                        "2", true, new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                ToastUtil.showShort("关注成功");
                                mIvIsFollow.setEnabled(false);
                                data.setIsfollow("1");
                                EventBusHelp.sendLikeAndUnlike(data);
                            }

                            @Override
                            public void onError(Response<BaseResponseBean<String>> response) {
                                ToastUtil.showShort("关注失败,请稍后重试");
                                super.onError(response);
                            }
                        });
            }
        });

        setContentText(mTvContentText, data.getTagshow(), data.getContenttitle(),
                TextUtils.equals("1", data.getIsshowtitle()), data.getTagshowid());
        mBaseMomentShareIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack != null) {
                    callBack.share(data);
                }
            }
        });

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

    private void dealNineLayout(MomentsDataBean data) {
        ArrayList<ImageData> imgList = VideoAndFileUtils.getImgList(data.getContenturllist(), data.getContenttext());
        if (imgList == null || imgList.size() == 0) return;
        //区分是单图还是多图
        cover = imgList.get(0).url;
        nineImageView.loadGif(false)
                .enableRoundCorner(false)
                .setData(imgList, NineLayoutHelper.getInstance().getLayoutHelper(imgList));
        nineImageView.setClickable(true);
        nineImageView.setFocusable(true);
        nineImageView.setOnItemClickListener(new NineImageView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                HelperForStartActivity.openImageWatcher(position, imgList,
                        data.getContentid());
            }
        });
    }

    private void dealVideo(MomentsDataBean data) {
        List<ImageData> imgList = VideoAndFileUtils.getImgList(data.getContenturllist(),
                data.getContenttext());
        if (imgList == null || imgList.size() < 2) {
            ToastUtil.showShort("内容集合解析出问题了:" + data.getContenturllist());
            return;
        }
        LogUtil.logObject(imgList);
        cover = imgList.get(0).url;
        videoUrl = imgList.get(1).url;

        videoView.setThumbImage(cover);
        landscape = "1".equals(data.getContenttype());
        VideoAndFileUtils.setVideoWH(videoView, landscape);
        videoView.setOrientation(landscape);
        int playCount = Integer.parseInt(data.getPlaycount());
        videoView.setPlayCount(playCount);
        videoView.setVideoTime(data.getShowtime());
        videoView.setOnShareBtListener(new MyVideoPlayerStandard.CompleteShareListener() {
            @Override
            public void share(SHARE_MEDIA share_media) {
                WebShareBean bean = ShareHelper.getInstance().changeContentBean(data, share_media, cover, fragment.getShareUrl());
                ShareHelper.getInstance().shareWeb(bean);
            }

            @Override
            public void playStart() {
                CommonHttpRequest.getInstance().requestPlayCount(data.getContentid());
            }
        });
        videoView.setVideoUrl(videoUrl, "", false);
        videoView.autoPlay();
        if (mVideoProgress != 0 && !TextUtils.isEmpty(data.getShowtime())) {
            //跳转制定位置播放
            MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        long duration = Integer.parseInt(data.getShowtime()) * 1000;
                        //这里只有开始播放时才生效
//                videoView.seekToInAdvance = duration * mVideoProgress / 100;
                        JZMediaManager.seekTo(duration * mVideoProgress / 100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },1000);
        }

    }


    /**
     * 处理显示内容
     *
     * @param contentView
     * @param tagshow
     * @param contenttext
     * @param ishowTag
     * @param tagshowid
     */
    private void setContentText(TextView contentView, String tagshow, String contenttext,
                                boolean ishowTag, String tagshowid) {
        if (!ishowTag) return;
        if (!TextUtils.isEmpty(tagshow)) {
            String source = "#" + tagshow + "#" + contenttext;
            SpannableString ss = new SpannableString(source);

            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // TODO: 2018/11/8 话题详情
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, tagshowid);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            }, 0, tagshow.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            ss.setSpan(new ForegroundColorSpan(DevicesUtils.getColor(R.color.color_FF698F)),
                    0, tagshow.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            contentView.setText(ss);
            contentView.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            contentView.setText(contenttext);
        }
    }

    public ShareCallBack callBack;

    @Override
    public void setCallBack(IHolder.ShareCallBack callBack) {
        this.callBack = callBack;
    }
}
