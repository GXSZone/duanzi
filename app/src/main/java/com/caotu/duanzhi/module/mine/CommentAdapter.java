package com.caotu.duanzhi.module.mine;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentBaseBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DateUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.other.CustomMovementMethod;
import com.caotu.duanzhi.other.SimpeClickSpan;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunfusheng.GlideImageView;

import java.util.Date;
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
    protected void convert(@NonNull BaseViewHolder helper, CommentBaseBean.RowsBean item) {
        /********************************头布局逻辑***************************/
        String timeText = "";
        try {
            Date start = DateUtils.getDate(item.createtime, DateUtils.YMDHMS);
            timeText = DateUtils.showTimeText(start);
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.setText(R.id.comment_time, timeText);
        GlideImageView avatar = helper.getView(R.id.comment_item_avatar);
        avatar.load(item.userheadphoto, R.mipmap.touxiang_moren, 4);
        GlideImageView guajian = helper.getView(R.id.iv_user_headgear);
        guajian.load(item.guajianurl);
        helper.addOnClickListener(R.id.iv_delete_my_post)
                .addOnClickListener(R.id.comment_item_content_tv)
                .addOnClickListener(R.id.ll_reply);

        helper.setGone(R.id.iv_delete_my_post, MySpUtils.isMe(item.userid));

        /***************************文本展示逻辑*******************************/
        helper.setText(R.id.comment_item_name_tx, item.username);
        List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(item.commenturl);
        TextView textView = helper.getView(R.id.comment_item_content_tv);
        SpannableStringBuilder type = new SpannableStringBuilder();
        if (commentUrlBean != null && commentUrlBean.size() > 0) {
            if (TextUtils.equals(commentUrlBean.get(0).type, "1")
                    || TextUtils.equals(commentUrlBean.get(0).type, "2")) {
                type.append("「视频」");
            } else {
                type.append("「图片」");
            }
        }
        type.append(ParserUtils.htmlToSpanText(item.commenttext, true));
        textView.setText(type);
        textView.setMovementMethod(CustomMovementMethod.getInstance());

        /************************** 下面回复框一块逻辑 **************************/

        TextView content = helper.getView(R.id.comment_item_second_comment_tv);
        GlideImageView image = helper.getView(R.id.iv_comment_item_second);
        if (TextUtils.equals("1", item.contentstatus)) {
            image.setVisibility(View.VISIBLE);
            image.setImageResource(R.mipmap.deletestyle2);
            content.setVisibility(View.VISIBLE);
            content.setText("该内容已被删除");
            return;
        }
        //1 代表是内容 , 0 代表是评论
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

            SpannableStringBuilder builder = new SpannableStringBuilder();
            if (!TextUtils.isEmpty(parentComment.username)){
                builder.append(parentComment.username + ":", new SimpeClickSpan() {
                    @Override
                    public void onSpanClick(View widget) {
                        HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, parentComment.userid);
                    }
                },Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            builder.append(contentType)
                    .append(ParserUtils.htmlToJustAtText(parentComment.commenttext));
            content.setText(builder);
            content.setMovementMethod(LinkMovementMethod.getInstance());

        } else if (TextUtils.equals("1", item.commentreply)
                && item.content != null
                && !TextUtils.isEmpty(item.content.getContentid())) {

            String cover = VideoAndFileUtils.getCover(item.content.getContenturllist());
            if (TextUtils.isEmpty(cover)) {
                image.setVisibility(View.GONE);
            } else {
                image.setVisibility(View.VISIBLE);
                image.load(cover, R.mipmap.shenlue_logo, 4);
            }
            if (!"1".equals(item.content.getIsshowtitle())) {
                content.setVisibility(View.INVISIBLE);
                return;
            }
            content.setVisibility(View.VISIBLE);
            content.setMaxEms(10);
            content.setText(ParserUtils.htmlToJustAtText(item.content.getContenttitle()));
        } else {
            image.setVisibility(View.VISIBLE);
            image.setImageResource(R.mipmap.deletestyle2);

            content.setVisibility(View.VISIBLE);
            content.setText("该内容已被删除");
        }
    }
}
