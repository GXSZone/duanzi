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
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.ruffian.library.widget.RImageView;
import com.sunfusheng.GlideImageView;
import com.sunfusheng.widget.ImageData;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NoticeLikeAdapter extends BaseQuickAdapter<MessageDataBean.RowsBean, BaseViewHolder> {
    public static final int TYPE_ONE = 100;
    public static final int TYPE_MORE = 101;

    public NoticeLikeAdapter() {
        super(R.layout.item_notice);
        //Step.1
        setMultiTypeDelegate(new MultiTypeDelegate<MessageDataBean.RowsBean>() {
            @Override
            protected int getItemType(MessageDataBean.RowsBean entity) {
//                通知类型 2评论3关注4通知5点赞折叠
                List<String> friendphotoArray = entity.friendphotoArray;
                String notetype = entity.notetype;
                if ("5".equals(notetype) && friendphotoArray != null && friendphotoArray.size() > 1) {
                    return TYPE_MORE;
                } else {
                    //根据你的实体类来判断布局类型
                    return TYPE_ONE;
                }
            }
        });
        //Step.2
        getMultiTypeDelegate()
                .registerItemType(TYPE_ONE, R.layout.item_notice)
                .registerItemType(TYPE_MORE, R.layout.item_notice_more);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageDataBean.RowsBean item) {
        // TODO: 2018/11/2 后面返回的因该是个集合
        switch (helper.getItemViewType()) {
            case TYPE_MORE:
                List<String> friendphotoArray = item.friendphotoArray;
                //异常处理
                RImageView imageView1 = helper.getView(R.id.iv_notice_user_one);
                RImageView imageView2 = helper.getView(R.id.iv_notice_user_two);

                if (friendphotoArray == null || friendphotoArray.size() < 2) {
                    imageView1.setImageResource(R.mipmap.touxiang_moren);
                    imageView2.setImageResource(R.mipmap.touxiang_moren);
                } else {
                    GlideUtils.loadImage(friendphotoArray.get(0), imageView1, true);
                    GlideUtils.loadImage(friendphotoArray.get(1), imageView2, false);
                }
                helper.setOnClickListener(R.id.fl_more_users, v -> {
                    String noteid = item.noteid;
                    HelperForStartActivity.openOther(HelperForStartActivity.type_other_praise, noteid,
                            item.friendcount);
                });
                break;
            default:
                String url = item.friendphoto;
                if (TextUtils.equals("5", item.notetype)) {
                    List<String> array = item.friendphotoArray;
                    url = array.get(0);
                }
                RImageView imageView = helper.getView(R.id.iv_notice_user);
                GlideUtils.loadImage(url, imageView,false);
                helper.addOnClickListener(R.id.iv_notice_user);
                break;
        }
        String friendname = item.friendname;
        if (!TextUtils.isEmpty(friendname) && friendname.length() > 4) {
            friendname = friendname.substring(0, 4) + "...";
        }
        //通知类型 2评论3关注4通知5点赞折叠
        String typeString = "赞了你";
        List<String> friendnameArray = item.friendnameArray;
        if (friendnameArray != null && friendnameArray.size() > 0) {
            String name = friendnameArray.get(0);
            if (!TextUtils.isEmpty(name) && name.length() > 4) {
                name = name.substring(0, 4) + "...";
            }
            friendname = name;
        }
        if (item.friendcount > 1) {
            friendname = friendname + "等" + item.friendcount + "人";
        }
        helper.setText(R.id.tv_item_user, friendname + " " + typeString);

        String timeText = "";
        try {
            Date start = DateUtils.getDate(item.createtime, DateUtils.YMDHMS);
            timeText = DateUtils.showTimeText(start);
        } catch (Exception e) {
            e.printStackTrace();
        }
        helper.setText(R.id.notice_time, timeText);

        RelativeLayout contentIv = helper.getView(R.id.fl_image_or_text);
        contentIv.removeAllViews();

        //通知作用对象：1_作品 2_评论
        if (TextUtils.equals("1", item.noteobject) && item.content != null) {
            //1横 2竖 3图片 4文字
            if (TextUtils.equals("4", item.content.getContenttype())
                    && LikeAndUnlikeUtil.isLiked(item.content.getIsshowtitle())) {
                TextView textView = new TextView(contentIv.getContext());
                String text = ParserUtils.htmlToJustAtText(item.content.getContenttitle());
                if (text.length() > 4) {
                    text = text.substring(0, 4) + "...";
                }
                textView.setText(text);
                textView.setTextSize(13);
                textView.setTextColor(DevicesUtils.getColor(R.color.color_828393));
                contentIv.addView(textView);
            } else {
                String contenturllist = item.content.getContenturllist();
                GlideImageView imageView = new GlideImageView(contentIv.getContext());
                imageView.setLayoutParams(new FrameLayout.LayoutParams(
                        DevicesUtils.dp2px(40), DevicesUtils.dp2px(40)));

                ArrayList<ImageData> imgList = VideoAndFileUtils.getImgList(contenturllist, null);
                if (imgList == null || imgList.size() == 0) {
                    imageView.setImageResource(R.mipmap.deletestyle2);
                } else {
                    imageView.load(imgList.get(0).url, R.mipmap.deletestyle2, 4);
                }
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                contentIv.addView(imageView);
            }

        } else if (TextUtils.equals("2", item.noteobject) && item.comment != null) {
            List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(item.comment.commenturl);
            if (commentUrlBean != null && commentUrlBean.size() > 0) {
                GlideImageView imageView = new GlideImageView(contentIv.getContext());
                imageView.setLayoutParams(new FrameLayout.LayoutParams(
                        DevicesUtils.dp2px(40), DevicesUtils.dp2px(40)));
                CommentUrlBean bean = commentUrlBean.get(0);
                if (LikeAndUnlikeUtil.isVideoType(bean.type)) {
                    imageView.load(bean.cover, R.mipmap.deletestyle2, 4);
                } else {
                    imageView.load(bean.info, R.mipmap.deletestyle2, 4);
                }
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                contentIv.addView(imageView);
            } else {
                TextView textView = new TextView(contentIv.getContext());
                String commenttext = ParserUtils.htmlToJustAtText(item.comment.commenttext);
                if (commenttext.length() > 4) {
                    commenttext = commenttext.substring(0, 4) + "...";
                }
                textView.setTextSize(13);
                textView.setTextColor(DevicesUtils.getColor(R.color.color_828393));
                textView.setText(commenttext);
                contentIv.addView(textView);
            }
        } else {
            GlideImageView imageView = new GlideImageView(contentIv.getContext());
            imageView.setLayoutParams(new FrameLayout.LayoutParams(
                    DevicesUtils.dp2px(40), DevicesUtils.dp2px(40)));
            imageView.setImageResource(R.mipmap.deletestyle2);
            contentIv.addView(imageView);
        }

    }
}
