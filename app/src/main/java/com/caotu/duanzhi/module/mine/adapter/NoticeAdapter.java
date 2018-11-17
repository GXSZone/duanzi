package com.caotu.duanzhi.module.mine.adapter;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.util.MultiTypeDelegate;
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
                GlideImageView imageView1 = helper.getView(R.id.iv_notice_user_one);
                imageView1.load(friendphotoArray.get(0), R.mipmap.touxiang_moren, 4);

                GlideImageView imageView2 = helper.getView(R.id.iv_notice_user_two);
                imageView2.load(friendphotoArray.get(1), R.mipmap.touxiang_moren, 4);

                helper.addOnClickListener(R.id.fl_more_users);
                break;
            default:
                GlideImageView imageView = helper.getView(R.id.iv_notice_user);
                imageView.load(item.friendphoto, R.mipmap.touxiang_moren, 4);
                helper.addOnClickListener(R.id.iv_notice_user);
                break;
        }
        String friendname = item.friendname;
        if (!TextUtils.isEmpty(friendname) && friendname.length() >= 6) {
            friendname = friendname.substring(0, 6) + "等";
        }

//        helper.setText(R.id.tv_item_user, friendname + " "
//                + (("1".equals(item.getNotetype()) ? "点赞" : "评论")
//                + (("1".equals(item.getNoteobject()) && item.getContent() != null) ? "了你的作品！" : "了你的评论！")));
        //通知类型 2评论3关注4通知5点赞折叠
        switch (item.notetype) {

            case "2":
                break;
            case "3":
                break;
            case "4":
                break;
            case "5":
                break;
            default:
                break;
        }
        String time = item.createtime;
        if (time.length() >= 8) {
            time = time.substring(0, 4) + "-" + time.substring(4, 6) + "-" + time.substring(6, 8);
        }
        helper.setText(R.id.notice_time, time);

        String contenturllist = item.content.contenturllist;
        GlideImageView contentIv = helper.getView(R.id.iv_content_list);
        ArrayList<ImageData> imgList = VideoAndFileUtils.getImgList(contenturllist, null);
        if (imgList == null || imgList.size() == 0) {
            contentIv.setImageResource(R.mipmap.deletestyle2);
        } else {
            contentIv.load(imgList.get(0).url, R.mipmap.deletestyle2, 4);
        }
    }
}
