package com.caotu.duanzhi.module;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.widget.MyExpandTextView;
import com.caotu.duanzhi.view.widget.MyRadioGroup;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.caotu.duanzhi.view.widget.VideoImageView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.lzy.okgo.model.Response;
import com.sunfusheng.util.Utils;
import com.sunfusheng.widget.GridLayoutHelper;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/6/21 14:06
 */

public class MomentsNewAdapter extends BaseQuickAdapter<MomentsDataBean, BaseViewHolder> {
    //1横 2竖 3图片 4文字  5web
    public static final int ITEM_VIDEO_TYPE = 1;
    public static final int ITEM_IMAGE_TYPE = 2;

    private int maxImgWidth;
    private int maxImgHeight;
    private int cellWidth;
    private int cellHeight;
    private int minImgWidth;
    private int minImgHeight;
    private int margin;

    public MomentsNewAdapter() {
        super(R.layout.item_base_content);
        setMultiTypeDelegate(new MultiTypeDelegate<MomentsDataBean>() {
            @Override
            protected int getItemType(MomentsDataBean entity) {
                //根据你的实体类来判断布局类型
                String contenttype = entity.getContenttype();
                int type;
                switch (contenttype) {
                    //使用视频布局
                    case "1"://横视频
                    case "2"://竖视频
                        type = ITEM_VIDEO_TYPE;
                        break;
                    default:
                        margin = DevicesUtils.dp2px(3);
                        maxImgHeight = maxImgWidth = (DevicesUtils.getSrecchWidth());
//                                - Utils.dp2px(mContext, 16) * 2);
                        cellHeight = cellWidth = (maxImgWidth - margin * 3) / 3;
                        minImgHeight = minImgWidth = cellWidth;
                        type = ITEM_IMAGE_TYPE;
                        break;
                }
                return type;
            }
        });
        //Step.2
        getMultiTypeDelegate()
                .registerItemType(ITEM_VIDEO_TYPE, R.layout.item_video_content)
                .registerItemType(ITEM_IMAGE_TYPE, R.layout.item_base_content);
    }

    @Override
    protected void convert(BaseViewHolder helper, MomentsDataBean item) {
        /*--------------------------点击事件,为了bean对象的获取-------------------------------*/
//        helper.addOnClickListener(R.id.base_moment_avatar_iv);
//        helper.addOnClickListener(R.id.item_iv_more_bt);
        helper.addOnClickListener(R.id.base_moment_like)
                .addOnClickListener(R.id.base_moment_unlike)
                .addOnClickListener(R.id.base_moment_share_iv)
                .addOnClickListener(R.id.base_moment_comment);
        /*----------------------------------------------------------------*/
        helper.setText(R.id.base_moment_like, Int2TextUtils.toText(item.getContentgood(), "w"))
                .setText(R.id.base_moment_unlike, Int2TextUtils.toText(item.getContentbad(), "w"))
                .setText(R.id.base_moment_comment, Int2TextUtils.toText(item.getContentcomment(), "w"));


        MyRadioGroup radioGroup = helper.getView(R.id.like_or_unlike_group);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                dealLikeAndUnlike(checkedId, helper, item);
            }
        });

        helper.setOnClickListener(R.id.base_moment_avatar_iv, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/11/8 如果是自己则不跳转
                if (!item.getContentuid().equals(MySpUtils.getString(MySpUtils.SP_MY_ID))) {
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_user,
                            item.getContentuid());
                }
            }
        });

        GlideUtils.loadImage(item.getUserheadphoto(), helper.getView(R.id.base_moment_avatar_iv));

        helper.setText(R.id.base_moment_name_tv, item.getUsername());

        MyExpandTextView contentView = helper.getView(R.id.expand_text_view);
        //判断是否显示话题 1可见，0不可见
        String tagshow = item.getTagshow();
        setContentText(contentView, tagshow, item.getContenttext(), "1".equals(item.getIsshowtitle()), item.getTagshowid());

        MomentsDataBean.BestmapBean bestmap = item.getBestmap();

        if (bestmap != null && !TextUtils.isEmpty(bestmap.getCommentid())) {
            helper.setGone(R.id.rl_best_parent, true);
            GlideUtils.loadImage(bestmap.getUserheadphoto(), helper.getView(R.id.iv_best_avatar));
            helper.setText(R.id.tv_spl_name, bestmap.getUsername());
            helper.setText(R.id.base_moment_spl_comment_tv, bestmap.getCommenttext());
            helper.setOnClickListener(R.id.iv_best_avatar, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO: 2018/11/8 如果是自己则不跳转
                    if (!bestmap.getUserid().equals(MySpUtils.getString(MySpUtils.SP_MY_ID))) {
                        HelperForStartActivity.openOther(HelperForStartActivity.type_other_user,
                                bestmap.getUserid());
                    }
                }
            });
            // TODO: 2018/11/12 判断类型后展示,九宫格和单视频显示隐藏判断
            NineImageView bestLayout = helper.getView(R.id.base_moment_spl_imgs_ll);
            VideoImageView oneVideo = helper.getView(R.id.video_best_one);
            //直接全屏
//            JzvdStd.startFullscreen(this, MyVideoPlayerStandard.class, VideoConstant.videoUrlList[6], "饺子辛苦了");
        } else {
            helper.setGone(R.id.rl_best_parent, false);
        }
        //Step.3
        switch (helper.getItemViewType()) {
            case ITEM_VIDEO_TYPE:
                dealVideo(helper, item);
                break;
            case ITEM_IMAGE_TYPE:
                //处理九宫格
                dealNineLayout(item, helper);
                break;
        }

    }

    private static final double CROSS_VIDEO_HIGH = 1.77d;
    private static final double VERTICAL_VIDEO_HIGH = 0.88d;

    /**
     * 控制视频的宽高
     *
     * @param player
     * @param isCross
     */
    public void setVideoWH(MyVideoPlayerStandard player, boolean isCross) {
        //横视频 1.77
        //竖视频 0.88
        double videoHigh = isCross ? CROSS_VIDEO_HIGH : VERTICAL_VIDEO_HIGH;
        LinearLayout.LayoutParams layoutParams =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) ((DevicesUtils.getSrecchWidth()
                        //40指的是控件和屏幕两边的间距加起来
                        - DevicesUtils.dp2px(40)) / videoHigh));

        player.setLayoutParams(layoutParams);
    }

    private void dealVideo(BaseViewHolder helper, MomentsDataBean item) {
        MyVideoPlayerStandard videoPlayerView = helper.getView(R.id.base_moment_video);
        List<ImageData> imgList = VideoAndFileUtils.getVideoList(item.getContenturllist(),
                item.getContenttext());
        videoPlayerView.setThumbImage(imgList.get(0).url);

        boolean landscape = "1".equals(item.getContenttype());
        setVideoWH(videoPlayerView, landscape);
        videoPlayerView.setOrientation(landscape);

        int playCount = Integer.parseInt(item.getPlaycount());
        videoPlayerView.setPlayCount(playCount);

        videoPlayerView.setOnShareBtListener(new MyVideoPlayerStandard.CompleteShareListener() {
            @Override
            public void share(SHARE_MEDIA share_media) {

            }

            @Override
            public void playStart() {
                CommonHttpRequest.getInstance().requestPlayCount(item.getContentid());
            }
        });
        videoPlayerView.setVideoUrl(imgList.get(1).url, "", true);

    }

    private void dealNineLayout(MomentsDataBean item, BaseViewHolder helper) {
        //神评区的显示隐藏在上面判断
        String type = item.getContenttype();
        String contenturllist = item.getContenturllist();
        switch (type) {
            case "3":
                helper.setGone(R.id.base_moment_imgs_ll, true);
                helper.setGone(R.id.bottom_parent, true);
                List<ImageData> imgList = VideoAndFileUtils.getImgList(contenturllist, item.getContenttext());
                //区分是单图还是多图
                NineImageView multiImageView = helper.getView(R.id.base_moment_imgs_ll);
                multiImageView.loadGif(false)
                        .enableRoundCorner(false)
                        .setData(imgList, getLayoutHelper(imgList));
                multiImageView.setOnItemClickListener(new NineImageView.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {

                    }
                });
                break;
            case "5":
                //web类型没有底部点赞等一些操作
                helper.setGone(R.id.base_moment_imgs_ll, true);
                helper.setGone(R.id.bottom_parent, false);

//                List<ImageData> img = VideoAndFileUtils.getImgList(contenturllist, item.getContenttext());
//                NineImageView oneImage = helper.getView(R.id.base_moment_imgs_ll);
//                oneImage.loadGif(false)
//                        .enableRoundCorner(false)
//                        .setData(img, new GridLayoutHelper(1,maxImgWidth,DevicesUtils.dp2px(140),0));
                break;
            case "4":
                helper.setGone(R.id.base_moment_imgs_ll, false);
                helper.setGone(R.id.bottom_parent, true);
                break;
        }

    }

    private GridLayoutHelper getLayoutHelper(List<ImageData> list) {
        int spanCount = Utils.getSize(list);
        if (spanCount == 1) {
            int width = list.get(0).realWidth;
            int height = list.get(0).realHeight;
            if (width > 0 && height > 0) {
                float whRatio = width * 1f / height;
                if (width > height) {
                    width = Math.max(minImgWidth, Math.min(width, maxImgWidth));
                    height = Math.max(minImgHeight, (int) (width / whRatio));
                } else {
                    height = Math.max(minImgHeight, Math.min(height, maxImgHeight));
                    width = Math.max(minImgWidth, (int) (height * whRatio));
                }
            } else {
                width = cellWidth;
                height = cellHeight;
            }
            return new GridLayoutHelper(spanCount, width, height, margin);
        }

        if (spanCount > 3) {
            spanCount = (int) Math.ceil(Math.sqrt(spanCount));
        }

        if (spanCount > 3) {
            spanCount = 3;
        }
        return new GridLayoutHelper(spanCount, cellWidth, cellHeight, margin);
    }

    private void dealLikeAndUnlike(int checkedId, BaseViewHolder helper, MomentsDataBean item) {
        int hasLikeOrUnlike = item.getHasLikeOrUnlike();
        RadioButton likeView = helper.getView(R.id.base_moment_like);
        int likeCount = item.getContentgood();
        RadioButton unlikeView = helper.getView(R.id.base_moment_unlike);
        int unLikeCount = item.getContentbad();
        if (hasLikeOrUnlike == 0) {
            if (R.id.base_moment_like == checkedId) {
                likeView.setText(Int2TextUtils.toText(likeCount + 1, "w"));
                //不然Item的数据不变,在更改后的基础上加1
                item.setContentgood(likeCount + 1);
                item.setHasLikeOrUnlike(1);
            } else {
                unlikeView.setText(Int2TextUtils.toText(unLikeCount + 1, "w"));
                item.setContentbad(unLikeCount + 1);
                item.setHasLikeOrUnlike(2);
            }
        } else {
            if (R.id.base_moment_like == checkedId) {
                likeView.setText(Int2TextUtils.toText(likeCount + 1, "w"));
                unlikeView.setText(Int2TextUtils.toText(unLikeCount - 1, "w"));
                item.setHasLikeOrUnlike(1);
            } else {
                likeView.setText(Int2TextUtils.toText(likeCount - 1, "w"));
                unlikeView.setText(Int2TextUtils.toText(unLikeCount + 1, "w"));
                item.setHasLikeOrUnlike(2);
            }
        }

        CommonHttpRequest.getInstance().<String>requestLikeOrUnlike(item.getContentuid(),
                item.getContentid(), R.id.base_moment_like == checkedId, new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        String code = response.body().getCode();
                        if (!HttpCode.success_code.equals(code)) {
                            ToastUtil.showShort(R.id.base_moment_like == checkedId ? "点赞失败!" : "踩失败");
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
    private void setContentText(MyExpandTextView contentView, String tagshow, String contenttext,
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

//        ViewHolder(View) {
//            this.view = view;
//            this.mBaseMomentAvatarIv = (RImageView) view.findViewById(R.id.base_moment_avatar_iv);
//            this.mBaseMomentNameTv = (TextView) view.findViewById(R.id.base_moment_name_tv);
//            this.mItemIvMoreBt = (ImageView) view.findViewById(R.id.item_iv_more_bt);
//            this.mExpandableText = (TextView) view.findViewById(R.id.expandable_text);
//            this.mExpandCollapse = (TextView) view.findViewById(R.id.expand_collapse);
//            this.mExpandTextView = (MyExpandTextView) view.findViewById(R.id.expand_text_view);
//            this.mBaseMomentImgsLl = (LinearLayout) view.findViewById(R.id.base_moment_imgs_ll);
//            this.mIvBestAvatar = (RImageView) view.findViewById(R.id.iv_best_avatar);
//            this.mTvSplName = (TextView) view.findViewById(R.id.tv_spl_name);
//            this.mBaseMomentSplLikeIv = (RImageView) view.findViewById(R.id.base_moment_spl_like_iv);
//            this.mBaseMomentSplCommentTv = (TextView) view.findViewById(R.id.base_moment_spl_comment_tv);
//            this.mBaseMomentSplImgsLl = (FrameLayout) view.findViewById(R.id.base_moment_spl_imgs_ll);
//            this.mRlBestParent = (RelativeLayout) view.findViewById(R.id.rl_best_parent);
//            this.mBaseMomentLikeIv = (RImageView) view.findViewById(R.id.base_moment_like_iv);
//            this.mBaseMomentLikeCountTv = (TextView) view.findViewById(R.id.base_moment_like_count_tv);
//            this.mBaseMomentUnlikeIv = (RImageView) view.findViewById(R.id.base_moment_unlike_iv);
//            this.mBaseMomentUnlikeCountTv = (TextView) view.findViewById(R.id.base_moment_unlike_count_tv);
//            this.mBaseMomentCommentIv = (ImageView) view.findViewById(R.id.base_moment_comment_iv);
//            this.mBaseMomentCommentCountTv = (TextView) view.findViewById(R.id.base_moment_comment_count_tv);
//            this.mBaseMomentShareIv = (ImageView) view.findViewById(R.id.base_moment_share_iv);
//        }
//    }

//    /**
//     * 点击分享
//     *
//     * @param platfrom
//     * @param position
//     * @param url
//     */
//    private void onclickShare(int platfrom, int position, String url) {
//
//        Activity activity = MyApplication.getInstance().getRunningActivity();
//        UMImage img = new UMImage(activity, getIcon(position));
//        String title = data.get(position).getContent();
//        String text = data.get(position).getTheme();
//        UMWeb web = new UMWeb(url);
//        web.setTitle(title);//标题
//        web.setThumb(img);  //缩略图
//        if (TextUtils.isEmpty(text)) {
//            text = "头图，最内涵的搞笑社区";
//        }
//        web.setDescription(text);//描述
//        switch (platfrom) {
//            case JiaoziVideoPlayerView.WEIXIN_CIRCLE:
//                doShare(web, SHARE_MEDIA.WEIXIN_CIRCLE);
//                break;
//            case JiaoziVideoPlayerView.QQ:
//                doShare(web, SHARE_MEDIA.QQ);
//                break;
//            case JiaoziVideoPlayerView.SINA:
//                new ShareAction(activity)
//                        .withText("[" + title + "] " + text + " " + url)
//                        .withMedia(img)
//                        .setPlatform(SHARE_MEDIA.SINA)//传入平台
//                        .setCallback(shareListener)//回调监听器
//                        .share();
//                break;
//            case JiaoziVideoPlayerView.WEIXIN:
//                doShare(web, SHARE_MEDIA.WEIXIN);
//                break;
//        }
//    }
//
//    /**
//     * 获取Logo图片
//     *
//     * @param position
//     * @return
//     */
//    private String getIcon(int position) {
//        String icon = null;
//        if (data.get(position).getImgs() == null || data.get(position).getImgs().size() == 0) {
//            icon = "http://shijun.image.alimmdn.com/app_logo.jpg";
//        } else {
//            icon = data.get(position).getImgs().get(0);
//        }
//        return icon;
//    }
//
//    /**
//     * 腾讯分享
//     *
//     * @param web
//     * @param media
//     */
//    public void doShare(UMWeb web, SHARE_MEDIA media) {
//        new ShareAction(MyApplication.getInstance().getRunningActivity())
//                .withMedia(web)
//                .setPlatform(media)//传入平台
//                .setCallback(shareListener)//回调监听器
//                .share();
//    }
//
//    /**
//     * 分享回调监听
//     */
//    private UMShareListener shareListener = new UMShareListener() {
//        /**
//         * @descrption 分享开始的回调
//         * @param platform 平台类型
//         */
//        @Override
//        public void onStart(SHARE_MEDIA platform) {
//        }
//
//        /**
//         * @descrption 分享成功的回调
//         * @param platform 平台类型
//         */
//        @Override
//        public void onResult(SHARE_MEDIA platform) {
//            ToastUtil.showShort("成功了");
//            CommonHttpRequest.getInstance().requestShare(data.get(isSharePosition));
//        }
//
//        /**
//         * @descrption 分享失败的回调
//         * @param platform 平台类型
//         * @param t 错误原因
//         */
//        @Override
//        public void onError(SHARE_MEDIA platform, Throwable t) {
//            ToastUtil.showShort("失败" + t.getMessage());
//        }
//
//        /**
//         * @descrption 分享取消的回调
//         * @param platform 平台类型
//         */
//        @Override
//        public void onCancel(SHARE_MEDIA platform) {
//            ToastUtil.showShort("取消了");
//        }
//    };
//


}
