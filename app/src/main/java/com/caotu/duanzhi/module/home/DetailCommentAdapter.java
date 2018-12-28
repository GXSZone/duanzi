package com.caotu.duanzhi.module.home;

import android.content.pm.ActivityInfo;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.FastClickListener;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.lzy.okgo.model.Response;
import com.sunfusheng.util.MediaFileUtils;
import com.sunfusheng.widget.ImageCell;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

/**
 * 详情里的评论列表
 */

public class DetailCommentAdapter extends BaseQuickAdapter<CommendItemBean.RowsBean, BaseViewHolder> {
    public static final int ITEM_IMAGE_TYPE = 1;
    public static final int ITEM_ONLY_ONE_IMAGE = 2;
    TextViewLongClick callBack;

    public DetailCommentAdapter(TextViewLongClick textViewLongClick) {
        super(R.layout.item_datail_comment_layout);
        callBack = textViewLongClick;
        setMultiTypeDelegate(new MultiTypeDelegate<CommendItemBean.RowsBean>() {
            @Override
            protected int getItemType(CommendItemBean.RowsBean entity) {
                ArrayList<ImageData> commentShowList = VideoAndFileUtils.getDetailCommentShowList(entity.commenturl);
                if (commentShowList == null || commentShowList.size() <= 1) {
                    return ITEM_ONLY_ONE_IMAGE;
                } else {
                    return ITEM_IMAGE_TYPE;
                }
            }
        });
        //Step.2
        getMultiTypeDelegate()
                .registerItemType(ITEM_IMAGE_TYPE, R.layout.item_datail_comment_layout)
                .registerItemType(ITEM_ONLY_ONE_IMAGE, R.layout.item_datail_comment_oneimage_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommendItemBean.RowsBean item) {
        //分组头的显示逻辑
        helper.setGone(R.id.list_header_group, item.showHeadr);
        if (item.showHeadr) {
            helper.setText(R.id.header_text, item.isBest ? "热门评论" : "新鲜评论");
        }

        ImageView bestAuth = helper.getView(R.id.user_auth);
        AuthBean authBean = item.getAuth();
        if (authBean != null && !TextUtils.isEmpty(authBean.getAuthid())) {
            bestAuth.setVisibility(View.VISIBLE);
            Log.i("authPic", "convert: " + authBean.getAuthpic());
            String cover = VideoAndFileUtils.getCover(authBean.getAuthpic());
            GlideUtils.loadImage(cover, bestAuth);
        } else {
            bestAuth.setVisibility(View.GONE);
        }
        bestAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authBean != null && !TextUtils.isEmpty(authBean.getAuthurl())) {
                    WebActivity.openWeb("用户勋章", authBean.getAuthurl(), true);
                }
            }
        });
        // TODO: 2018/11/29  神评的标志显示 因为有头布局
        helper.setGone(R.id.iv_god_bg, helper.getLayoutPosition() == 1 && item.isBest);
        helper.setGone(R.id.view_line_bottom, item.isShowFooterLine);
        ImageView avatar = helper.getView(R.id.comment_item_avatar);
        GlideUtils.loadImage(item.userheadphoto, avatar, false);
        helper.setText(R.id.comment_item_name_tx, item.username);

        helper.setOnClickListener(R.id.comment_item_name_tx, v -> HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, item.userid));
        avatar.setOnClickListener(v -> HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, item.userid));


        TextView mExpandTextView = helper.getView(R.id.expand_text_view);
        //changeUgcBean bean对象转换
        if (item.isUgc && item.isShowTitle) {
            mExpandTextView.setVisibility(View.GONE);
        } else {
            mExpandTextView.setVisibility(View.VISIBLE);
        }
        mExpandTextView.setText(item.commenttext);
        // TODO: 2018/12/18 设置了长按事件后单击事件又得另外添加
        helper.addOnClickListener(R.id.expand_text_view);
        mExpandTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (callBack != null) {
                    callBack.textLongClick(DetailCommentAdapter.this, v, getPositon(helper));
                }
                return false;
            }
        });
        TextView likeIv = helper.getView(R.id.base_moment_spl_like_iv);
        likeIv.setText(Int2TextUtils.toText(item.commentgood, "W"));
        likeIv.setSelected(LikeAndUnlikeUtil.isLiked(item.goodstatus));
        likeIv.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                if (item.isUgc) {
                    // TODO: 2018/11/20 如果是UGC则是对内容进行操作,点赞也是对这个内容操作
                    CommonHttpRequest.getInstance().requestLikeOrUnlike(item.userid,
                            item.contentid, true, likeIv.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                                @Override
                                public void onSuccess(Response<BaseResponseBean<String>> response) {
                                    commentLikeClick(item, likeIv);

                                }
                            });
                } else {
                    CommonHttpRequest.getInstance().requestCommentsLike(item.userid,
                            item.contentid, item.commentid, likeIv.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                                @Override
                                public void onSuccess(Response<BaseResponseBean<String>> response) {
                                    commentLikeClick(item, likeIv);
                                }
                            });
                }
            }
        });

        // TODO: 2018/11/14 分享的弹窗由fragment来实现具体内容
        helper.addOnClickListener(R.id.base_moment_share_iv);
        //这个是回复的显示内容
        dealReplyUI(item.childList, helper, item.replyCount, item);

        dealNinelayout(helper, item);
    }

    private int getPositon(BaseViewHolder helper) {
        if (helper.getLayoutPosition() >= getHeaderLayoutCount()) {
            return helper.getLayoutPosition() - getHeaderLayoutCount();
        }
        return 0;
    }

    public void dealNinelayout(BaseViewHolder helper, CommendItemBean.RowsBean item) {
        ArrayList<ImageData> commentShowList = VideoAndFileUtils.getDetailCommentShowList(item.commenturl);
        if (helper.getItemViewType() == ITEM_ONLY_ONE_IMAGE) {
            ImageCell oneImage = helper.getView(R.id.only_one_image);
            oneImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (commentShowList == null) return;
                    String url = commentShowList.get(0).url;
                    if (MediaFileUtils.getMimeFileIsVideo(url)) {
                        Jzvd.releaseAllVideos();
                        //直接全屏
                        Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                        JzvdStd.startFullscreen(oneImage.getContext()
                                , MyVideoPlayerStandard.class, url, "");
                    } else {
                        HelperForStartActivity.openImageWatcher(0, commentShowList,
                                item.contentid);
                    }
                }
            });
            if (commentShowList == null || commentShowList.size() == 0) {
                oneImage.setVisibility(View.GONE);
            } else {
                oneImage.setVisibility(View.VISIBLE);
                ImageData imageData = commentShowList.get(0);
                dealOneImageSize(oneImage, imageData);
                oneImage.setData(imageData);
            }
        } else {
            NineImageView multiImageView = helper.getView(R.id.detail_image);
            multiImageView.loadGif(false)
                    .setData(commentShowList, NineLayoutHelper.getInstance().getLayoutHelper(commentShowList));
            multiImageView.setClickable(true);
            multiImageView.setFocusable(true);
            multiImageView.setOnItemClickListener(new NineImageView.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    String url = commentShowList.get(position).url;
                    if (MediaFileUtils.getMimeFileIsVideo(url)) {
                        Jzvd.releaseAllVideos();
                        //直接全屏
                        Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                        JzvdStd.startFullscreen(multiImageView.getContext()
                                , MyVideoPlayerStandard.class, url, "");
                    } else {
                        HelperForStartActivity.openImageWatcher(position, commentShowList,
                                item.contentid);
                    }
                }
            });
        }
//        NineRvHelper.ShowNineImage(recyclerView, commentShowList, item.contentid);
    }

    /**
     * 新加入方法,调整单图显示
     *
     * @param oneImage
     * @param imageData
     */
    private void dealOneImageSize(ImageCell oneImage, ImageData imageData) {
        ViewGroup.LayoutParams layoutParams = oneImage.getLayoutParams();
        int fixedSize = DevicesUtils.dp2px(98);
        if (imageData.realHeight > 0 && imageData.realWidth > 0) {
            float whRatio = (imageData.realWidth + 0.0f) / imageData.realHeight;
            //修正宽高比
            if (whRatio < 0.5f) {
                whRatio = 0.5f;
            } else if (whRatio > 1.5f) {
                whRatio = 1.5f;
            }
            //宽大与高
            if (whRatio >= 1.0f) {
                layoutParams.height = fixedSize;
                layoutParams.width = (int) (fixedSize * whRatio);
            } else {
                layoutParams.width = fixedSize;
                layoutParams.height = (int) (fixedSize / whRatio);
            }
        } else {
            layoutParams.height = fixedSize;
            layoutParams.width = fixedSize;
        }
        oneImage.setLayoutParams(layoutParams);
    }

    /**
     * 处理评论列表的点赞数显示
     *
     * @param item
     * @param likeIv
     */
    public void commentLikeClick(CommendItemBean.RowsBean item, TextView likeIv) {
        int goodCount = item.commentgood;
        if (likeIv.isSelected()) {
            goodCount--;
            likeIv.setSelected(false);
        } else {
            goodCount++;
            likeIv.setSelected(true);
        }
        //修正
        if (goodCount < 0) {
            goodCount = 0;
        }
        likeIv.setText(Int2TextUtils.toText(goodCount, "w"));
        item.commentgood = goodCount;

        //"0"_未赞未踩 "1"_已赞 "2"_已踩
        item.goodstatus = likeIv.isSelected() ? "1" : "0";
    }

    protected void dealReplyUI(List<CommendItemBean.ChildListBean> childList, BaseViewHolder helper, int replyCount, CommendItemBean.RowsBean item) {
        helper.addOnClickListener(R.id.child_reply_layout);
        TextView more = helper.getView(R.id.comment_reply_more);
        TextView first = helper.getView(R.id.comment_item_first);
        TextView second = helper.getView(R.id.comment_item_second);
        //先判断是否是ugc,过滤没有神评的情况
        if (item.isUgc && (childList == null || childList.size() == 0)) {
            more.setVisibility(replyCount >= 2 ? View.VISIBLE : View.GONE);
            more.setText(String.format("共有%d条回复 \uD83D\uDC49", replyCount));
            first.setVisibility(View.GONE);
            second.setVisibility(View.GONE);
            if (replyCount < 2) {
                helper.setGone(R.id.child_reply_layout, false);
            }
            return;
        }
        if (childList == null || childList.size() == 0) {
            helper.setGone(R.id.child_reply_layout, false);
            return;
        }
        helper.setGone(R.id.child_reply_layout, true);

        int size = childList.size();
        if (item.isUgc) {
            more.setVisibility(replyCount >= 2 ? View.VISIBLE : View.GONE);
            more.setText(String.format("共有%d条回复 \uD83D\uDC49", replyCount));
        } else {
            more.setVisibility(size >= 2 ? View.VISIBLE : View.GONE);
            more.setText(String.format("共有%d条回复 \uD83D\uDC49", replyCount));
        }
        if (size == 1) {
            first.setVisibility(View.VISIBLE);
            second.setVisibility(View.GONE);
            setText(first, childList.get(0), item);
        } else if (size >= 2) {
            first.setVisibility(View.VISIBLE);
            second.setVisibility(View.VISIBLE);
            setText(first, childList.get(0), item);
            setText(second, childList.get(1), item);
        }
    }

    private void setText(TextView textView, CommendItemBean.ChildListBean childListBean, CommendItemBean.RowsBean item) {
        String username = childListBean.username;
        String commenturl = childListBean.commenturl;
        String commenttext = childListBean.commenttext;
        List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(commenturl);
        String type = "";
        if (commentUrlBean != null && commentUrlBean.size() > 0) {
            if (TextUtils.equals(commentUrlBean.get(0).type, "1")
                    || TextUtils.equals(commentUrlBean.get(0).type, "2")) {
                type = "[视频]";
            } else {
                type = "[图片]";
            }
        }
        String source = username + ":" + type + commenttext;
        SpannableString ss = new SpannableString(source);
        int length = TextUtils.isEmpty(username) ? 0 : username.length();
        if (length > 0) {
            ss.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    // TODO: 2018/11/8 话题详情
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, childListBean.userid);
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setUnderlineText(false);
                }
            }, 0, length + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            ss.setSpan(new ForegroundColorSpan(DevicesUtils.getColor(R.color.color_FF698F)),
                    0, length + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
