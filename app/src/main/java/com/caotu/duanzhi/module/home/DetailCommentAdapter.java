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
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.sunfusheng.util.MediaFileUtils;
import com.sunfusheng.widget.GridLayoutHelper;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;

import java.util.ArrayList;
import java.util.List;

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
        ImageView avatar = helper.getView(R.id.comment_item_avatar);
        GlideUtils.loadImage(item.userheadphoto, avatar, false);
        helper.setText(R.id.comment_item_name_tx, item.username);

        helper.setOnClickListener(R.id.comment_item_name_tx, v -> HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, item.userid));
        avatar.setOnClickListener(v -> HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, item.userid));

        TextView mExpandTextView = helper.getView(R.id.expand_text_view);
        mExpandTextView.setText(item.commenttext);
        ImageView likeIv = helper.getView(R.id.base_moment_spl_like_iv);
        likeIv.setSelected(LikeAndUnlikeUtil.isLiked(item.goodstatus));
        likeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHttpRequest.getInstance().requestCommentsLike(item.userid,
                        item.commentid, likeIv.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                likeIv.setSelected(!likeIv.isSelected());
                            }

                            @Override
                            public void needLogin() {
                                LoginHelp.goLogin();
                            }
                        });
            }
        });
        // TODO: 2018/11/14 分享的弹窗由fragment来实现具体内容
        helper.addOnClickListener(R.id.base_moment_share_iv);
        //这个是回复的显示内容
        dealReplyUI(item.childList, helper, item.replyCount);


        NineImageView mDetailImage = helper.getView(R.id.detail_image);
        ArrayList<ImageData> commentShowList = VideoAndFileUtils.getDetailCommentShowList(item.commenturl);
        if (commentShowList == null || commentShowList.size() == 0) return;
        mDetailImage.loadGif(false)
                .enableRoundCorner(false)
                .setData(commentShowList, new GridLayoutHelper(3, NineLayoutHelper.getCellWidth(),
                        NineLayoutHelper.getCellHeight(), NineLayoutHelper.getMargin()));
        mDetailImage.setOnItemClickListener(new NineImageView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String url = commentShowList.get(position).url;
                if (MediaFileUtils.getMimeFileIsVideo(url)) {
                    //直接全屏
                    JzvdStd.startFullscreen(MyApplication.getInstance().getRunningActivity()
                            , MyVideoPlayerStandard.class, url, "");
                } else {
                    HelperForStartActivity.openImageWatcher(position,commentShowList,null);
                }
            }
        });

    }

    private void dealReplyUI(List<CommendItemBean.ChildListBean> childList, BaseViewHolder helper, int replyCount) {
        if (childList == null || childList.size() == 0) {
            helper.setGone(R.id.child_reply_layout, false);
            return;
        }
        helper.setGone(R.id.child_reply_layout, true);
        TextView more = helper.getView(R.id.comment_reply_more);
        int size = childList.size();
        more.setVisibility(size >= 3 ? View.VISIBLE : View.GONE);
        more.setText(String.format("共有%d条回复 \uD83D\uDC49", replyCount));
        TextView first = helper.getView(R.id.comment_item_first);
        TextView second = helper.getView(R.id.comment_item_second);
        if (size == 1) {
            first.setVisibility(View.VISIBLE);
            second.setVisibility(View.GONE);
            setText(first, childList.get(0));
        } else if (size >= 2) {
            first.setVisibility(View.VISIBLE);
            second.setVisibility(View.VISIBLE);
            setText(first, childList.get(0));
            setText(second, childList.get(1));
        }
    }

    private void setText(TextView textView, CommendItemBean.ChildListBean childListBean) {
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
        }, 0, username.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ss.setSpan(new ForegroundColorSpan(DevicesUtils.getColor(R.color.color_FF698F)),
                0, username.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(ss);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

}
