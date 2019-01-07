package com.caotu.duanzhi.module.notice;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;
import com.sunfusheng.widget.ImageData;

import java.util.ArrayList;
import java.util.List;

public class NoticeAdapter extends BaseQuickAdapter<MessageDataBean.RowsBean, BaseViewHolder> {
    public static final int TYPE_ONE = 100;
    public static final int TYPE_MORE = 101;

    public NoticeAdapter(@Nullable List<MessageDataBean.RowsBean> data) {
        super(data);
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
                GlideImageView imageView1 = helper.getView(R.id.iv_notice_user_one);
                GlideImageView imageView2 = helper.getView(R.id.iv_notice_user_two);
                if (friendphotoArray == null || friendphotoArray.size() < 2) {
                    imageView1.loadDrawable(R.mipmap.touxiang_moren);
                    imageView2.loadDrawable(R.mipmap.touxiang_moren);
                } else {
                    imageView1.load(friendphotoArray.get(0), R.mipmap.touxiang_moren, 4);
                    imageView2.load(friendphotoArray.get(1), R.mipmap.touxiang_moren, 4);
                }
                helper.addOnClickListener(R.id.fl_more_users);
                break;
            default:
                String url = item.friendphoto;
                if (TextUtils.equals("5", item.notetype)) {
                    List<String> array = item.friendphotoArray;
                    url = array.get(0);
                }
                GlideImageView imageView = helper.getView(R.id.iv_notice_user);
                imageView.load(url, R.mipmap.touxiang_moren, 4);
                helper.addOnClickListener(R.id.iv_notice_user);
                GlideImageView guanjian = helper.getView(R.id.iv_user_headgear);
                guanjian.load(item.guajianurl);
                break;
        }
        String friendname = item.friendname;
        if (!TextUtils.isEmpty(friendname) && friendname.length() > 4) {
            friendname = friendname.substring(0, 4) + "...";
        }
        String typeString;
        String time = item.createtime;
        if (time.length() >= 8) {
            time = time.substring(0, 4) + "-" + time.substring(4, 6) + "-" + time.substring(6, 8);
        }
        //通知类型 2评论3关注4通知5点赞折叠
        switch (item.notetype) {
            case "2":
                typeString = "评论了你";
                break;
            case "3":
                typeString = "关注了你";
                break;
            case "5":
                typeString = "赞了你";
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

                break;
            //通知类型
            default:
                typeString = "";
                time = item.notetext;
                break;
        }
        helper.setText(R.id.tv_item_user, friendname + " " + typeString);
        helper.setText(R.id.notice_time, time);

        RelativeLayout contentIv = helper.getView(R.id.fl_image_or_text);
        contentIv.removeAllViews();
        if (TextUtils.equals("3", item.notetype)) {
            //是否已关注对方(userid是否已关注friendid) 1_是 0_否
            ImageView imageView = new ImageView(contentIv.getContext());
            imageView.setImageResource(R.drawable.image_follow_selector);
            boolean isfollow = LikeAndUnlikeUtil.isLiked(item.isfollow);
            if (isfollow) {
                imageView.setEnabled(false);
            } else {
                imageView.setSelected(false);
            }
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonHttpRequest.getInstance().<String>requestFocus(item.friendid, "2", true, new JsonCallback<BaseResponseBean<String>>() {
                        @Override
                        public void onSuccess(Response<BaseResponseBean<String>> response) {
                            imageView.setEnabled(false);
                        }
                    });
                }
            });
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            contentIv.addView(imageView);
            return;
        }
        //通知作用对象：1_作品 2_评论
        if (TextUtils.equals("1", item.noteobject) && item.content != null) {
            //1横 2竖 3图片 4文字
            if (TextUtils.equals("4", item.content.getContenttype())
                    && LikeAndUnlikeUtil.isLiked(item.content.getIsshowtitle())) {
                TextView textView = new TextView(contentIv.getContext());
                String text = item.content.getContenttitle();
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
                String commenttext = item.comment.commenttext;
                if (commenttext.length() > 4) {
                    commenttext = commenttext.substring(0, 4) + "...";
                }
                textView.setTextSize(13);
                textView.setTextColor(DevicesUtils.getColor(R.color.color_828393));
                textView.setText(commenttext);
                contentIv.addView(textView);
            }
        } else if (item.comment == null && item.content == null &&
                (TextUtils.equals(item.notetype, "2") || TextUtils.equals(item.notetype, "5"))) {
            GlideImageView imageView = new GlideImageView(contentIv.getContext());
            imageView.setLayoutParams(new FrameLayout.LayoutParams(
                    DevicesUtils.dp2px(40), DevicesUtils.dp2px(40)));
            imageView.setImageResource(R.mipmap.deletestyle2);
            contentIv.addView(imageView);
        }

    }
}
