package com.caotu.duanzhi.module.mine;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentBaseBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunfusheng.GlideImageView;
import com.sunfusheng.widget.ImageData;

import java.util.ArrayList;
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
        helper.setOnClickListener(R.id.ll_reply, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //0_正常 1_已删除 2_审核中
                if ("1".equals(item.contentstatus)) {
                    ToastUtil.showShort("该资源已被删除");
                    return;
                }
                if (TextUtils.equals("1", item.commentreply)) {
                    //回复的是内容,跳转到内容详情
                    MomentsDataBean beanComment = item.content;
                    HelperForStartActivity.openContentDetail(beanComment, false);
                } else {
                    //回复的是评论,跳转到评论详情
                    CommendItemBean.RowsBean comment = item.parentComment;
                    HelperForStartActivity.openCommentDetail(comment);
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
        if (TextUtils.equals("0", item.commentreply)) {
            content.setMaxEms(28);
            helper.setGone(R.id.iv_comment_item_second, true);
            CommendItemBean.RowsBean parentComment = item.parentComment;
            List<CommentUrlBean> urlBean = VideoAndFileUtils.getCommentUrlBean(parentComment.commenturl);
            String contentType = "";
            if (urlBean != null && urlBean.size() > 0) {
                boolean isVideo = LikeAndUnlikeUtil.isVideoType(urlBean.get(0).type);
                contentType = isVideo ? "「视频」" : "「图片」";
            }
            String name = parentComment.username;
            String text = parentComment.commenttext;

            String source = name + ":" + contentType + text;
            SpannableString ss = new SpannableString(source);

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
            content.setText(ss);
            content.setMovementMethod(LinkMovementMethod.getInstance());

        } else {
            helper.setGone(R.id.iv_comment_item_second, false);
            GlideImageView image = helper.getView(R.id.iv_comment_item_second);
            String contenturllist = item.content.getContenturllist();
            ArrayList<ImageData> imgList = VideoAndFileUtils.getImgList(contenturllist, null);
            if (imgList == null || imgList.size() == 0) {
                image.load(item.content.getUserheadphoto(), R.mipmap.touxiang_moren, 4);
            } else {
                image.load(imgList.get(0).url, R.mipmap.touxiang_moren, 4);
            }
            content.setMaxEms(8);
            content.setText(item.content.getContenttitle());
        }
    }
}
