package com.caotu.duanzhi.module.mine;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sunfusheng.GlideImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class NoticeAdapter extends BaseQuickAdapter<MessageDataBean.RowsBean, BaseViewHolder> {


    public NoticeAdapter(int layoutResId, @Nullable List<MessageDataBean.RowsBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MessageDataBean.RowsBean item) {
        GlideImageView imageView = helper.getView(R.id.iv_notice_user);
        imageView.load(item.getFriendphoto(), R.mipmap.touxiang_moren, 4);
        helper.addOnClickListener(R.id.iv_notice_user);

        String friendname = item.getFriendname();
        if (!TextUtils.isEmpty(friendname) && friendname.length() >= 6) {
            friendname = friendname.substring(0, 6) + "...";
        }
        helper.setText(R.id.tv_item_user, friendname + " "
                + (("1".equals(item.getNotetype()) ? "点赞" : "评论")
                + (("1".equals(item.getNoteobject()) && item.getContent() != null) ? "了你的作品！" : "了你的评论！")));
        String time = item.getCreatetime();
        if (time.length() >= 8) {
            time = time.substring(0, 4) + "-" + time.substring(4, 6) + "-" + time.substring(6, 8);
        }
        helper.setText(R.id.notice_time, time);

        String contenturllist = item.getContent().getContenturllist();
        GlideImageView contentIv = helper.getView(R.id.iv_content_list);
        try {
            JSONArray jsonArray = new JSONArray(contenturllist);
            String url = (String) jsonArray.get(0);
            if (jsonArray.length() == 0) {
                contentIv.setImageResource(R.mipmap.deletestyle2);
            } else {
                contentIv.load(url, R.mipmap.deletestyle2, 4);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
