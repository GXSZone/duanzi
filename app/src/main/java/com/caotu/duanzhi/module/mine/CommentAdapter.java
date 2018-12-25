package com.caotu.duanzhi.module.mine;

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

import com.caotu.duanzhi.Http.bean.AuthBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentBaseBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunfusheng.GlideImageView;

import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/5
 * @describe 我的评论列表
 */
public class CommentAdapter extends BaseQuickAdapter<CommentBaseBean.RowsBean, BaseViewHolder> {

    public CommentAdapter() {
        super(R.layout.item_comment_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommentBaseBean.RowsBean item) {
        GlideImageView avatar = helper.getView(R.id.comment_item_avatar);
        avatar.load(item.userheadphoto, R.mipmap.touxiang_moren, 4);
        helper.addOnClickListener(R.id.iv_delete_my_post)
                .addOnClickListener(R.id.ll_reply);

        ImageView mUserAuth = helper.getView(R.id.user_auth);
        AuthBean authBean = item.auth;
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

        helper.setText(R.id.comment_item_name_tx, item.username);
        List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(item.commenturl);
        String type = "";
        if (commentUrlBean != null && commentUrlBean.size() > 0) {
            if (TextUtils.equals(commentUrlBean.get(0).type, "1")
                    || TextUtils.equals(commentUrlBean.get(0).type, "2")) {
                type = "「视频」";
            } else {
                type = "「图片」";
            }
        }
        helper.setText(R.id.comment_item_content_tv, type + item.commenttext);
        //1 代表是内容 , 0 代表是评论
        TextView content = helper.getView(R.id.comment_item_second_comment_tv);
        GlideImageView image = helper.getView(R.id.iv_comment_item_second);
        if (TextUtils.equals("1", item.contentstatus)) {
            image.setVisibility(View.VISIBLE);
            image.setImageResource(R.mipmap.deletestyle2);

            content.setVisibility(View.VISIBLE);
            content.setText("该内容已被删除");
            return;
        }

        if (TextUtils.equals("0", item.commentreply) &&
                item.parentComment != null &&
                !TextUtils.isEmpty(item.parentComment.commentid)) {
            content.setVisibility(View.VISIBLE);
            content.setMaxEms(28);
            image.setVisibility(View.GONE);
            CommendItemBean.RowsBean parentComment = item.parentComment;
            List<CommentUrlBean> urlBean = VideoAndFileUtils.getCommentUrlBean(parentComment.commenturl);
            String contentType = "";
            if (urlBean != null && urlBean.size() > 0) {
                boolean isVideo = LikeAndUnlikeUtil.isVideoType(urlBean.get(0).type);
                contentType = isVideo ? "「视频」" : "「图片」";
            }
            String name = parentComment.username;
            String text = parentComment.commenttext;
            String source;
            if (!TextUtils.isEmpty(name)) {
                source = name + ":" + contentType + text;
            } else {
                source = contentType + text;
            }

            SpannableString ss = new SpannableString(source);
            if (!TextUtils.isEmpty(name)) {
                ss.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        // TODO: 2018/11/8 话题详情
                        HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, parentComment.userid);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setUnderlineText(false);
                    }
                }, 0, name.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(new ForegroundColorSpan(DevicesUtils.getColor(R.color.color_FF698F)),
                        0, name.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            content.setText(ss);
            content.setMovementMethod(LinkMovementMethod.getInstance());

        } else if (TextUtils.equals("1", item.commentreply)
                && item.content != null
                && !TextUtils.isEmpty(item.content.getContentid())) {

            String cover = VideoAndFileUtils.getCover(item.content.getContenturllist());
            if (TextUtils.isEmpty(cover)) {
                image.setVisibility(View.GONE);
            } else {
                image.setVisibility(View.VISIBLE);
                image.load(cover, R.mipmap.deletestyle2, 4);
            }
            if (!"1".equals(item.content.getIsshowtitle())) {
                content.setVisibility(View.INVISIBLE);
                return;
            }
            content.setVisibility(View.VISIBLE);
            content.setMaxEms(10);
            content.setText(item.content.getContenttitle());
        } else {
            image.setVisibility(View.VISIBLE);
            image.setImageResource(R.mipmap.deletestyle2);

            content.setVisibility(View.VISIBLE);
            content.setText("该内容已被删除");
        }
    }
}
