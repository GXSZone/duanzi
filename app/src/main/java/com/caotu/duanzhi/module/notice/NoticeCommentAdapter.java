package com.caotu.duanzhi.module.notice;

import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DateUtils;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunfusheng.GlideImageView;
import com.sunfusheng.widget.ImageData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoticeCommentAdapter extends BaseQuickAdapter<MessageDataBean.RowsBean, BaseViewHolder> {

    public NoticeCommentAdapter() {
        super(R.layout.item_notice_comment);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageDataBean.RowsBean item) {

        GlideImageView userHeader = helper.getView(R.id.iv_notice_user);
        userHeader.load(item.friendphoto, R.mipmap.touxiang_moren, 4);
        helper.addOnClickListener(R.id.iv_notice_user);

        String timeText = "";
        try {
            Date start = DateUtils.getDate(item.createtime, DateUtils.YMDHMS);
            timeText = DateUtils.showTimeText(start);
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.setText(R.id.notice_time, timeText);

        String friendname = item.friendname;
        if (!TextUtils.isEmpty(friendname) && friendname.length() > 4) {
            friendname = friendname.substring(0, 4) + "...";
        }
        helper.setText(R.id.tv_item_user, friendname + " 评论了你");
        RelativeLayout contentRl = helper.getView(R.id.iv_glide_reply);
        contentRl.removeAllViews();
        //通知作用对象：1_作品 2_评论
        if (TextUtils.equals("1", item.noteobject) && item.content != null) {
            //1横 2竖 3图片 4文字
            if (TextUtils.equals("4", item.content.getContenttype())
                    && LikeAndUnlikeUtil.isLiked(item.content.getIsshowtitle())) {
                TextView textView = new TextView(contentRl.getContext());
                String text = item.content.getContenttitle();
                if (text.length() > 4) {
                    text = text.substring(0, 4) + "...";
                }
                textView.setText(text);
                textView.setTextSize(13);
                textView.setTextColor(DevicesUtils.getColor(R.color.color_828393));
                contentRl.addView(textView);
            } else {
                String contenturllist = item.content.getContenturllist();
                GlideImageView imageView = new GlideImageView(contentRl.getContext());
                imageView.setLayoutParams(new FrameLayout.LayoutParams(
                        DevicesUtils.dp2px(40), DevicesUtils.dp2px(40)));

                ArrayList<ImageData> imgList = VideoAndFileUtils.getImgList(contenturllist, null);
                if (imgList == null || imgList.size() == 0) {
                    imageView.setImageResource(R.mipmap.deletestyle2);
                } else {
                    imageView.load(imgList.get(0).url, R.mipmap.deletestyle2, 4);
                }
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                contentRl.addView(imageView);
            }

        } else if (TextUtils.equals("2", item.noteobject) && item.comment != null) {
            List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(item.comment.commenturl);
            if (commentUrlBean != null && commentUrlBean.size() > 0) {
                GlideImageView imageView = new GlideImageView(contentRl.getContext());
                imageView.setLayoutParams(new FrameLayout.LayoutParams(
                        DevicesUtils.dp2px(40), DevicesUtils.dp2px(40)));
                CommentUrlBean bean = commentUrlBean.get(0);
                if (LikeAndUnlikeUtil.isVideoType(bean.type)) {
                    imageView.load(bean.cover, R.mipmap.deletestyle2, 4);
                } else {
                    imageView.load(bean.info, R.mipmap.deletestyle2, 4);
                }
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                contentRl.addView(imageView);
            } else {
                TextView textView = new TextView(contentRl.getContext());
                String commenttext = item.comment.commenttext;
                if (commenttext.length() > 4) {
                    commenttext = commenttext.substring(0, 4) + "...";
                }
                textView.setTextSize(13);
                textView.setTextColor(DevicesUtils.getColor(R.color.color_828393));
                textView.setText(commenttext);
                contentRl.addView(textView);
            }
        }
//        else if (item.comment == null && item.content == null &&
//                (TextUtils.equals(item.notetype, "2") || TextUtils.equals(item.notetype, "5"))) {
//            GlideImageView imageView = new GlideImageView(contentRl.getContext());
//            imageView.setLayoutParams(new FrameLayout.LayoutParams(
//                    DevicesUtils.dp2px(40), DevicesUtils.dp2px(40)));
//            imageView.setImageResource(R.mipmap.deletestyle2);
//            contentRl.addView(imageView);
//        }
    }
}
