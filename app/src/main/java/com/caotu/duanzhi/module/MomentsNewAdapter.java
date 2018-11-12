package com.caotu.duanzhi.module;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
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
import com.caotu.duanzhi.view.widget.MyExpandTextView;
import com.caotu.duanzhi.view.widget.MyRadioGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/6/21 14:06
 */

public class MomentsNewAdapter extends BaseQuickAdapter<MomentsDataBean, BaseViewHolder> {
    //1横 2竖 3图片 4文字  5web
    public static final int ITEM_VIDEO_TYPE = 1;
    public static final int ITEM_ONE_IMAGE_TYPE = 2;
    public static final int ITEM_MANY_IMAGE_TYPE = 3;
    public static final int ITEM_TEXT_TYPE = 4;
    public static final int ITEM_WEB_TYPE = 5;

    public MomentsNewAdapter() {
        super(R.layout.item_base_content);
        //Step.1
//        setMultiTypeDelegate(new MultiTypeDelegate<MomentsDataBean>() {
//            @Override
//            protected int getItemType(MomentsDataBean entity) {
//                //根据你的实体类来判断布局类型
//                String contenttype = entity.getContenttype();
//                int type = 0;
//                if (TextUtils.isEmpty(contenttype)) return ITEM_ONE_IMAGE_TYPE;
//                switch (contenttype) {
//                    //使用视频布局
//                    case "1"://横视频
//                    case "2"://竖视频
//                        type = ITEM_VIDEO_TYPE;
//                        break;
//                    case "3"://图片
//                        //区分是单图还是多图
//                        if (entity.getContenturllist().size() == 1) {
//                            type = ITEM_ONE_IMAGE_TYPE;
//                        } else {
//                            type = ITEM_MANY_IMAGE_TYPE;
//                        }
//                        break;
//                    //纯文字和web类型都用这个布局,只是web类型图片满屏,纯文字隐藏
//                    case "4":
//                    case "5":
//                        type = ITEM_ONE_IMAGE_TYPE;
//                        break;
//                }
//                return type;
//            }
//        });
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
        } else {
            helper.setGone(R.id.rl_best_parent, false);
        }
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
        if (ishowTag) {
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


//    static class ViewHolder {
//        View view;
//        RImageView mBaseMomentAvatarIv;
//        TextView mBaseMomentNameTv;
//        ImageView mItemIvMoreBt;
//        TextView mExpandableText;
//        TextView mExpandCollapse;
//        MyExpandTextView mExpandTextView;
//        LinearLayout mBaseMomentImgsLl;
//        RImageView mIvBestAvatar;
//        TextView mTvSplName;
//        RImageView mBaseMomentSplLikeIv;
//        TextView mBaseMomentSplCommentTv;
//        FrameLayout mBaseMomentSplImgsLl;
//        RelativeLayout mRlBestParent;
//        RImageView mBaseMomentLikeIv;
//        TextView mBaseMomentLikeCountTv;
//        RImageView mBaseMomentUnlikeIv;
//        TextView mBaseMomentUnlikeCountTv;
//        ImageView mBaseMomentCommentIv;
//        TextView mBaseMomentCommentCountTv;
//        ImageView mBaseMomentShareIv;
//
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
