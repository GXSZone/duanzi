package com.caotu.duanzhi.module.mine;

import android.text.TextUtils;

import com.caotu.duanzhi.Http.bean.CommentBaseBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.R;
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

        GlideImageView image = helper.getView(R.id.iv_comment_item_second);
        String contenturllist = item.content.getContenturllist();
        ArrayList<ImageData> imgList = VideoAndFileUtils.getImgList(contenturllist, null);
        if (imgList == null || imgList.size() == 0) {
            image.load(item.content.getUserheadphoto(), R.mipmap.touxiang_moren, 4);
        } else {
            image.load(imgList.get(0).url, R.mipmap.touxiang_moren, 4);
        }

        helper.setText(R.id.comment_item_name_tx, item.username);
        List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(item.commenturl);
        String type = "";
        if (commentUrlBean != null && commentUrlBean.size() > 0) {
            if (TextUtils.equals(commentUrlBean.get(0).type, "1")
                    || TextUtils.equals(commentUrlBean.get(0).type, "2")) {
                type = "[视频]";
            } else {
                type = "[图片]";
            }
        }

        helper.setText(R.id.comment_item_content_tv, type + item.commenttext);
        String contenttitle;
        if (TextUtils.isEmpty(item.parenttext)) {
            contenttitle = item.parentname;
        } else {
            contenttitle = item.parenttext;
        }
        helper.setText(R.id.comment_item_second_comment_tv, contenttitle);
    }
}
