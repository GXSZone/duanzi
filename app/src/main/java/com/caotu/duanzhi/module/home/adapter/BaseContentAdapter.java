package com.caotu.duanzhi.module.home.adapter;

import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.UmengHelper;
import com.caotu.duanzhi.UmengStatisticsKeyIds;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.other.VideoDownloadHelper;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.CustomMovementMethod;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.NineRvHelper;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;
import com.sunfusheng.widget.ImageCell;
import com.sunfusheng.widget.NineImageView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;

/**
 * 父类处理item的头部布局和脚部布局,只有中间的布局不一样的
 */
public abstract class BaseContentAdapter extends BaseQuickAdapter<MomentsDataBean, BaseViewHolder> {
    public static final int ITEM_VIDEO_TYPE = 1;
    public static final int ITEM_IMAGE_TYPE = 2;
    public static final int ITEM_WEB_TYPE = 3;
    public static final int ITEM_ONLY_ONE_IMAGE = 4;

    public BaseContentAdapter(int layoutResId) {
        super(layoutResId);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads != null && !payloads.isEmpty()) {
            if (holder.getItemViewType() != ITEM_WEB_TYPE) {
                MomentsDataBean o = (MomentsDataBean) payloads.get(0);
                dealLikeAndUnlike(holder, o);
            }
        } else {
            onBindViewHolder(holder, position);
        }
    }

    @Override
    protected void convert(BaseViewHolder helper, MomentsDataBean item) {
        ImageView moreAction = helper.getView(R.id.item_iv_more_bt);
        //web 类型没有这按钮
        if (moreAction != null) {
            moreAction.setImageResource(item.isMySelf ? R.mipmap.my_tiezi_delete : R.mipmap.home_more);
//            GlideUtils.loadImage(item.isMySelf ? R.mipmap.my_tiezi_delete : R.mipmap.home_more, moreAction);
        }
        // TODO: 2019/4/11 R.id.base_moment_comment 由于目前未设置跳转详情滑动评论页,所以不设置点击事件
        helper.addOnClickListener(R.id.item_iv_more_bt,
                R.id.base_moment_share_iv,
                R.id.txt_content);

        /*-------------------------------点赞和踩的处理---------------------------------*/

        GlideImageView guanjian = helper.getView(R.id.iv_user_headgear);
        guanjian.load(item.getGuajianurl());

        ImageView avatar = helper.getView(R.id.base_moment_avatar_iv);
        ImageView auth = helper.getView(R.id.user_auth);
        TextView userName = helper.getView(R.id.base_moment_name_tv);
        helper.addOnClickListener(R.id.base_moment_avatar_iv, R.id.base_moment_name_tv);
        bindItemHeader(avatar, auth, userName, item);

        //文本处理
        dealContentText(item, helper);

        //web 类型不需要处理神评和底部点赞踩操作
        if (helper.getItemViewType() != ITEM_WEB_TYPE) {
            dealLikeAndUnlike(helper, item);

            MomentsDataBean.BestmapBean bestmap = item.getBestmap();
            GlideImageView bestGunajian = helper.getView(R.id.iv_best_user_headgear);
            bestGunajian.load(item.getBestguajian());
            if (bestmap != null && bestmap.getCommentid() != null) {
                helper.setGone(R.id.rl_best_parent, true);
                NineRvHelper.dealBest(helper, bestmap, item.getBestauth(), item.getContentid());
            } else {
                helper.setGone(R.id.rl_best_parent, false);
            }
        }
        otherViewBind(helper, item);
    }

    /**
     * 绑定头部布局
     *
     * @param userPhoto
     * @param userAuth
     * @param userName
     * @param dataBean
     */
    public void bindItemHeader(ImageView userPhoto, ImageView userAuth, TextView userName,
                               MomentsDataBean dataBean) {
        GlideUtils.loadImage(dataBean.getUserheadphoto(), userPhoto, false);

        AuthBean authBean = dataBean.getAuth();
        if (authBean != null && !TextUtils.isEmpty(authBean.getAuthid())) {
            userAuth.setVisibility(View.VISIBLE);
            GlideUtils.loadImage(dataBean.authPic, userAuth);
        } else {
            userAuth.setVisibility(View.GONE);
        }
        userAuth.setOnClickListener(v -> {
            if (authBean != null && !TextUtils.isEmpty(authBean.getAuthurl())) {
                WebActivity.openWeb("用户勋章", authBean.getAuthurl(), true);
            }
        });
        userName.setText(dataBean.getUsername());
    }

    public abstract void otherViewBind(BaseViewHolder helper, MomentsDataBean item);

    private void dealContentText(MomentsDataBean item, BaseViewHolder helper) {
        TextView contentView = helper.getView(R.id.txt_content);
        TextView stateView = helper.getView(R.id.txt_state);
        boolean ishowTag = "1".equals(item.getIsshowtitle());
        String contenttext = item.getContenttitle();
        String tagshow = item.getTagshow();
        if (hasTag(item, contentView, stateView, ishowTag, contenttext, tagshow)) return;

        if (ishowTag) {
            contentView.setText(contenttext);
            dealTextHasMore(item, contentView, stateView);
            contentView.setVisibility(View.VISIBLE);
        } else {
            contentView.setVisibility(View.GONE);
            stateView.setVisibility(View.GONE);
        }
    }

    /**
     * https://github.com/binaryfork/Spanny 处理span的三方
     */

    public boolean hasTag(MomentsDataBean item, TextView contentView, TextView stateView, boolean ishowTag, String contenttext, String tagshow) {
        if (!TextUtils.isEmpty(tagshow)) {
            String source = "#" + item.getTagshow() + "#";
            if (ishowTag) {
                source += contenttext;
            }
            SpannableString ss = new SpannableString(source);
            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // TODO: 2018/11/8 话题详情
                    MyApplication.getInstance().putHistory(item.getContentid());
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, item.getTagshowid());
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            }, 0, item.getTagshow().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            ss.setSpan(new ForegroundColorSpan(DevicesUtils.getColor(R.color.color_FF698F)),
                    0, item.getTagshow().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            contentView.setText(ss);
            contentView.setMovementMethod(CustomMovementMethod.getInstance());
            contentView.setVisibility(View.VISIBLE);
            stateView.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    private void dealTextHasMore(MomentsDataBean item, TextView contentView, TextView stateView) {
        if (item.isShowCheckAll) {
            stateView.setVisibility(View.VISIBLE);
            if (item.isExpanded) {
                contentView.setMaxLines(Integer.MAX_VALUE);
                stateView.setText("收起");
            } else {
                contentView.setMaxLines(8);
                stateView.setText("全文");
            }
            stateView.setOnClickListener(v -> {
                item.isExpanded = !item.isExpanded;

                if (item.isExpanded) {
                    UmengHelper.event(UmengStatisticsKeyIds.content_view);
                    contentView.setMaxLines(Integer.MAX_VALUE);
                    stateView.setText("收起");
                } else {
                    contentView.setMaxLines(8);
                    stateView.setText("全文");
                }
            });
        } else {
            stateView.setVisibility(View.GONE);
            contentView.setMaxLines(Integer.MAX_VALUE);
        }
    }

    public void dealLikeAndUnlike(BaseViewHolder helper, MomentsDataBean item) {
        TextView likeView = helper.getView(R.id.base_moment_like);
        TextView unlikeView = helper.getView(R.id.base_moment_unlike);
        TextView commentView = helper.getView(R.id.base_moment_comment);
        ImageView shareWx = helper.getView(R.id.share_wx);
        //该控件的初始大小为0
        shareWx.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = shareWx.getLayoutParams();
                params.width = 0;
                params.height = 0;
                shareWx.setLayoutParams(params);
            }
        });
        shareWx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cover = VideoAndFileUtils.getCover(item.getContenturllist());
                WebShareBean webBean = new WebShareBean();
                webBean.medial = SHARE_MEDIA.WEIXIN;
                WebShareBean shareBeanByDetail = ShareHelper.getInstance().getShareBeanByDetail(webBean, item, cover, CommonHttpRequest.url);
                ShareHelper.getInstance().shareWeb(shareBeanByDetail);
            }
        });
        if (likeView == null || unlikeView == null || commentView == null) return;
        likeView.setText(Int2TextUtils.toText(item.getContentgood(), "w"));
        unlikeView.setText(Int2TextUtils.toText(item.getContentbad(), "w"));
        commentView.setText(Int2TextUtils.toText(item.getContentcomment(), "w"));
//        "0"_未赞未踩 "1"_已赞 "2"_已踩
        String goodstatus = item.getGoodstatus();

        if (TextUtils.equals("1", goodstatus)) {
            likeView.setSelected(true);
            unlikeView.setSelected(false);
        } else if (TextUtils.equals("2", goodstatus)) {
            unlikeView.setSelected(true);
            likeView.setSelected(false);
        } else {
            likeView.setSelected(false);
            unlikeView.setSelected(false);
        }
        likeView.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                ViewGroup.LayoutParams params = shareWx.getLayoutParams();
                if (!likeView.isSelected()) {
                    LikeAndUnlikeUtil.showLike(likeView, 20, 30);
                    showWxShareIcon(params, shareWx);
                } else {
                    hideShareWxIcon(params, shareWx);
                }

                CommonHttpRequest.getInstance().requestLikeOrUnlike(item.getContentuid(),
                        item.getContentid(), true, likeView.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                if (TextUtils.equals("2", item.getGoodstatus())) {
                                    unlikeView.setSelected(false);
                                    if (item.getContentbad() > 0) {
                                        item.setContentbad(item.getContentbad() - 1);
                                        unlikeView.setText(Int2TextUtils.toText(item.getContentbad(), "w"));
                                    }
                                }
                                int goodCount = item.getContentgood();
                                if (likeView.isSelected()) {
                                    goodCount--;
                                    likeView.setSelected(false);
                                } else {
                                    goodCount++;
                                    likeView.setSelected(true);
                                }
                                if (goodCount < 0) {
                                    goodCount = 0;
                                }
                                likeView.setText(Int2TextUtils.toText(goodCount, "w"));
                                item.setContentgood(goodCount);

                                //修改goodstatus状态 "0"_未赞未踩 "1"_已赞 "2"_已踩
                                item.setGoodstatus(likeView.isSelected() ? "1" : "0");

                            }
                        });
            }
        });

        unlikeView.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestLikeOrUnlike(item.getContentuid(),
                        item.getContentid(), false, unlikeView.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                if (TextUtils.equals("1", item.getGoodstatus())) {
                                    likeView.setSelected(false);
                                    if (item.getContentgood() > 0) {
                                        item.setContentgood(item.getContentgood() - 1);
                                        likeView.setText(Int2TextUtils.toText(item.getContentgood(), "w"));
                                    }
                                }
                                int badCount = item.getContentbad();
                                if (unlikeView.isSelected()) {
                                    badCount--;
                                    unlikeView.setSelected(false);
                                } else {
                                    badCount++;
                                    unlikeView.setSelected(true);
                                }
                                if (badCount < 0) {
                                    badCount = 0;
                                }
                                unlikeView.setText(Int2TextUtils.toText(badCount, "w"));
                                item.setContentbad(badCount);


                                //修改goodstatus状态 "0"_未赞未踩 "1"_已赞 "2"_已踩
                                item.setGoodstatus(unlikeView.isSelected() ? "2" : "0");
                            }
                        });
            }
        });
    }

    private void hideShareWxIcon(ViewGroup.LayoutParams params, ImageView shareWx) {
        if (params == null || shareWx == null) return;
        if (params.height <= 10 || params.width <= 10) return; //在方法里过滤
        ValueAnimator anim = ValueAnimator.ofInt(DevicesUtils.dp2px(30), 0);
        anim.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            params.width = value;
            params.height = value;
            shareWx.setLayoutParams(params);
        });
        anim.start();
    }

    private void showWxShareIcon(ViewGroup.LayoutParams params, ImageView shareWx) {
        if (params == null || shareWx == null) return;
        if (params.height > 10 || params.width > 10) return;  //在方法里过滤
        ValueAnimator anim = ValueAnimator.ofInt(0, DevicesUtils.dp2px(30));
        anim.setInterpolator(new OvershootInterpolator());
        anim.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            params.width = value;
            params.height = value;
            shareWx.setLayoutParams(params);
        });
        anim.start();
    }

    /**
     * 处理图片,包括单图和多图
     *
     * @param helper
     * @param item
     */
    public void dealImages(BaseViewHolder helper, MomentsDataBean item) {
        //神评区的显示隐藏在上面判断
        if (item.imgList == null || item.imgList.size() == 0) {
            ImageCell oneImage = helper.getView(R.id.only_one_image);
            if (oneImage != null) {
                oneImage.setVisibility(View.GONE);
            }
            NineImageView multiImageView = helper.getView(R.id.base_moment_imgs_ll);
            if (multiImageView != null) {
                multiImageView.setVisibility(View.GONE);
            }
            return;
        }
        //区分是单图还是多图
        if (item.imgList.size() == 1) {
            ImageCell oneImage = helper.getView(R.id.only_one_image);
            oneImage.setVisibility(View.VISIBLE);
            oneImage.setOnClickListener(v -> HelperForStartActivity.openImageWatcher(0, item.imgList, item.getContentid()));
            int max = DevicesUtils.getSrecchWidth() - DevicesUtils.dp2px(40);
            int min = max / 3;
            int width = item.imgList.get(0).realWidth;
            int height = item.imgList.get(0).realHeight;

            if (width > 0 && height > 0) {
                float whRatio = width * 1f / height;
                if (width > height) {
                    width = Math.max(min, Math.min(width, max));
                    height = Math.max(min, (int) (width / whRatio));
                } else {
                    height = Math.max(min, Math.min(height, max));
                    width = Math.max(min, (int) (height * whRatio));
                }
            } else {
                width = min;
                height = min;
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) oneImage.getLayoutParams();
            layoutParams.width = width;
            layoutParams.height = height;
            oneImage.setLayoutParams(layoutParams);
            oneImage.setData(item.imgList.get(0));
        } else {
            NineImageView multiImageView = helper.getView(R.id.base_moment_imgs_ll);
            multiImageView.setVisibility(View.VISIBLE);
            multiImageView.loadGif(false)
                    .enableRoundCorner(false)
                    .setData(item.imgList, NineLayoutHelper.getInstance().getLayoutHelper(item.imgList));

            multiImageView.setOnItemClickListener(position ->
                    HelperForStartActivity.openImageWatcher(position, item.imgList, item.getContentid()));

        }
    }

    /**
     * 处理视频
     *
     * @param helper
     * @param item
     */
    public void dealVideo(BaseViewHolder helper, MomentsDataBean item) {
        MyVideoPlayerStandard videoPlayerView = helper.getView(R.id.base_moment_video);

        if (item.imgList == null || item.imgList.size() < 2) {
//            ToastUtil.showShort("内容集合解析出问题了:" + item.getContenturllist() + "---------" + item.getContenttype());
            return;
        }
        videoPlayerView.setThumbImage(item.imgList.get(0).url);

        boolean landscape = "1".equals(item.getContenttype());
        VideoAndFileUtils.setVideoWH(videoPlayerView, landscape);

        try {
            int playCount = Integer.parseInt(item.getPlaycount());
            videoPlayerView.setPlayCount(playCount);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        videoPlayerView.setVideoTime(item.getShowtime());
        String videoUrl = item.imgList.get(1).url;

        ImageView shareIcon = helper.getView(R.id.share_wx);
        videoPlayerView.setOnShareBtListener(new MyVideoPlayerStandard.CompleteShareListener() {
            @Override
            public void share(SHARE_MEDIA share_media) {
                doShareFromVideo(item, share_media, item.imgList.get(0).url);
            }

            @Override
            public void downLoad() {
                VideoDownloadHelper.getInstance().startDownLoad(true, item.getContentid(), videoUrl);
            }

            @Override
            public void justPlay() {
                UmengHelper.event(UmengStatisticsKeyIds.content_view);
                videoPlayerView.setOrientation(landscape);
                videoPlayerView.dealPlayCount(item, videoPlayerView);
            }

            @Override
            public void timeToShowWxIcon() {
                if (shareIcon != null) {
                    showWxShareIcon(shareIcon.getLayoutParams(), shareIcon);
                }
            }
        });
        videoPlayerView.setVideoUrl(videoUrl, "", true);
    }

    /**
     * 处理视频播放完后的分享
     *
     * @param item
     * @param share_media
     */
    private void doShareFromVideo(MomentsDataBean item, SHARE_MEDIA share_media, String cover) {
        WebShareBean bean = ShareHelper.getInstance().changeContentBean(item, share_media, cover, CommonHttpRequest.url);
        ShareHelper.getInstance().shareWeb(bean);
    }
}
