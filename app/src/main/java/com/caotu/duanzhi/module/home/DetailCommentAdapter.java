package com.caotu.duanzhi.module.home;

import android.content.pm.ActivityInfo;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.FastClickListener;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.sunfusheng.util.MediaFileUtils;
import com.sunfusheng.widget.GridLayoutHelper;
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

    public DetailCommentAdapter() {
        super(R.layout.item_datail_comment_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommendItemBean.RowsBean item) {
        //分组头的显示逻辑
        helper.setGone(R.id.list_header_group, item.showHeadr);
        if (item.showHeadr) {
            helper.setText(R.id.header_text, item.isBest ? "热门评论" : "最新评论");
        }
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
            mExpandTextView.setVisibility(View.INVISIBLE);
        }
        mExpandTextView.setText(item.commenttext);
// TODO: 2018/11/26 替换成textview
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

        NineImageView mDetailImage = helper.getView(R.id.detail_image);
        ArrayList<ImageData> commentShowList = VideoAndFileUtils.getDetailCommentShowList(item.commenturl);
        if (commentShowList == null || commentShowList.size() == 0) {
            mDetailImage.setVisibility(View.GONE);
            return;
        }
        mDetailImage.setVisibility(View.VISIBLE);
        mDetailImage.loadGif(false)
                .enableRoundCorner(false)
                .setData(commentShowList, new GridLayoutHelper(3, NineLayoutHelper.getCellWidth(),
                        NineLayoutHelper.getCellHeight(), NineLayoutHelper.getMargin()));
        mDetailImage.setOnItemClickListener(new NineImageView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String url = commentShowList.get(position).url;
                if (MediaFileUtils.getMimeFileIsVideo(url)) {
                    Jzvd.releaseAllVideos();
                    //直接全屏
                    Jzvd.FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    JzvdStd.startFullscreen(MyApplication.getInstance().getRunningActivity()
                            , JzvdStd.class, url, "");
                } else {
                    HelperForStartActivity.openImageWatcher(position, commentShowList, item.contentid);
                }
            }
        });

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
        if (goodCount > 0) {
            likeIv.setText(Int2TextUtils.toText(goodCount, "w"));
            item.commentgood = goodCount;
        }

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
