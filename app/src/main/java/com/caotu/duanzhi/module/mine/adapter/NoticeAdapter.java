package com.caotu.duanzhi.module.mine.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
import com.sunfusheng.GlideImageView;

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
                GlideImageView imageView = helper.getView(R.id.iv_notice_user);
                imageView.load(item.friendphoto, R.mipmap.touxiang_moren, 4);
                helper.addOnClickListener(R.id.iv_notice_user);
                break;
        }
        String friendname = item.friendname;
        if (!TextUtils.isEmpty(friendname) && friendname.length() >= 8) {
            friendname = friendname.substring(0, 8);
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
                    if (!TextUtils.isEmpty(name) && name.length() >= 8) {
                        name = name.substring(0, 8) + "...";
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

//        String contenturllist = item.content.getContenturllist();
//        GlideImageView contentIv = helper.getView(R.id.iv_content_list);
//        ArrayList<ImageData> imgList = VideoAndFileUtils.getImgList(contenturllist, null);
//        if (imgList == null || imgList.size() == 0) {
//            contentIv.setImageResource(R.mipmap.deletestyle2);
//        } else {
//            contentIv.load(imgList.get(0).url, R.mipmap.deletestyle2, 4);
//        }
    }
}
