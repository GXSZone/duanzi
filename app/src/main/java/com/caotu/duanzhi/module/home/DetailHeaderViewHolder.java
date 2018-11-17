package com.caotu.duanzhi.module.home;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.LogUtil;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.widget.MyRadioGroup;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.lzy.okgo.model.Response;
import com.ruffian.library.widget.RImageView;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe TODO
 */
public class DetailHeaderViewHolder {

    public  View parentView;
    public RImageView mBaseMomentAvatarIv;
    public TextView mBaseMomentNameTv;
    public ImageView mIvIsFollow;
    public TextView mTvContentText;
    public LinearLayout mBaseMomentImgsLl;
    public RadioButton mBaseMomentLike;
    public RadioButton mBaseMomentUnlike;
    public MyRadioGroup mLikeOrUnlikeGroup;
    public TextView mBaseMomentComment;
    public ImageView mBaseMomentShareIv;
    public NineImageView nineImageView;
    public MyVideoPlayerStandard videoView;
    ContentDetailFragment fragment;


    public DetailHeaderViewHolder(ContentDetailFragment fragment, View rootView) {
        this.parentView=rootView;
        this.fragment = fragment;
        this.mBaseMomentAvatarIv = (RImageView) rootView.findViewById(R.id.base_moment_avatar_iv);
        this.mBaseMomentNameTv = (TextView) rootView.findViewById(R.id.base_moment_name_tv);
        this.mIvIsFollow = (ImageView) rootView.findViewById(R.id.iv_is_follow);
        this.mTvContentText = (TextView) rootView.findViewById(R.id.tv_content_text);
        this.mBaseMomentImgsLl = (LinearLayout) rootView.findViewById(R.id.base_moment_imgs_ll);
        this.mBaseMomentLike = (RadioButton) rootView.findViewById(R.id.base_moment_like);
        this.mBaseMomentUnlike = (RadioButton) rootView.findViewById(R.id.base_moment_unlike);
        this.mLikeOrUnlikeGroup = (MyRadioGroup) rootView.findViewById(R.id.like_or_unlike_group);
        this.mBaseMomentComment = (TextView) rootView.findViewById(R.id.base_moment_comment);
        this.mBaseMomentShareIv = (ImageView) rootView.findViewById(R.id.base_moment_share_iv);
        this.nineImageView = rootView.findViewById(R.id.detail_image_type);
        this.videoView = rootView.findViewById(R.id.detail_video_type);
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

    public boolean isLandscape() {
        return landscape;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getCover() {
        return cover;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void bindDate(MomentsDataBean data) {
        GlideUtils.loadImage(data.getUserheadphoto(), mBaseMomentAvatarIv);
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
        mBaseMomentNameTv.setText(data.getUsername());
        //1关注 0未关注  已经关注状态的不能取消关注
        String isfollow = data.getIsfollow();
        if ("0".equals(isfollow) || TextUtils.isEmpty(isfollow)) {
            mIvIsFollow.setSelected(false);
        } else {
            mIvIsFollow.setEnabled(false);
        }
        mIvIsFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!LoginHelp.isLoginAndSkipLogin()) return;
                CommonHttpRequest.getInstance().<String>requestFocus(data.getContentuid(),
                        "2", true, new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                ToastUtil.showShort("关注成功");
                                mIvIsFollow.setEnabled(true);
                            }

                            @Override
                            public void onError(Response<BaseResponseBean<String>> response) {
                                ToastUtil.showShort("关注失败,请稍后重试");
                                super.onError(response);
                            }

                            @Override
                            public void needLogin() {
                                LoginHelp.goLogin();
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
        // TODO: 2018/11/14 现在接口有字段来初始化是否点赞或者已经踩了
//            mBaseMomentLike.setChecked(true);
//            mBaseMomentUnlike.setChecked(false);
        mLikeOrUnlikeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                dealLikeAndUnlike(checkedId == R.id.base_moment_like, data);
            }
        });
    }

    private void dealNineLayout(MomentsDataBean data) {
        ArrayList<ImageData> imgList = VideoAndFileUtils.getImgList(data.getContenturllist(), data.getContenttext());
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
                        (ImageView) nineImageView.getChildAt(position));
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
    }


    private void dealLikeAndUnlike(boolean islike, MomentsDataBean item) {
        String hasLikeOrUnlike = item.getGoodstatus();
        int likeCount = item.getContentgood();
        int unLikeCount = item.getContentbad();
        if (TextUtils.equals("0", hasLikeOrUnlike)) {
            if (islike) {
                mBaseMomentLike.setText(Int2TextUtils.toText(likeCount + 1, "w"));
                //不然Item的数据不变,在更改后的基础上加1
                item.setContentgood(likeCount + 1);
                item.setGoodstatus("1");
            } else {
                mBaseMomentUnlike.setText(Int2TextUtils.toText(unLikeCount + 1, "w"));
                item.setContentbad(unLikeCount + 1);
                item.setGoodstatus("2");
            }
        } else {
            if (islike) {
                mBaseMomentLike.setText(Int2TextUtils.toText(likeCount + 1, "w"));
                mBaseMomentUnlike.setText(Int2TextUtils.toText(unLikeCount - 1, "w"));
                item.setGoodstatus("1");
            } else {
                mBaseMomentLike.setText(Int2TextUtils.toText(likeCount - 1, "w"));
                mBaseMomentUnlike.setText(Int2TextUtils.toText(unLikeCount + 1, "w"));
                item.setGoodstatus("2");
            }
        }

        CommonHttpRequest.getInstance().<String>requestLikeOrUnlike(item.getContentuid(),
                item.getContentid(), islike, new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        String code = response.body().getCode();
                        if (!HttpCode.success_code.equals(code)) {
                            ToastUtil.showShort(islike ? "点赞失败!" : "踩失败");
                        }
                    }
                });
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
        if (ishowTag && !TextUtils.isEmpty(tagshow)) {
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

    public void setCallBack(ShareCallBack callBack) {
        this.callBack = callBack;
    }

    public interface ShareCallBack {
        void share(MomentsDataBean bean);
    }

}
