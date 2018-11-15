package com.caotu.duanzhi.module.home;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.NineLayoutHelper;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.widget.MyVideoPlayerStandard;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.okgo.model.Response;
import com.sunfusheng.util.MediaFileUtils;
import com.sunfusheng.widget.GridLayoutHelper;
import com.sunfusheng.widget.ImageData;
import com.sunfusheng.widget.NineImageView;

import java.util.ArrayList;

import cn.jzvd.JzvdStd;

/**
 * 详情里的评论列表
 */

public class CommentReplayAdapter extends BaseQuickAdapter<CommendItemBean.RowsBean, BaseViewHolder> {

    public CommentReplayAdapter() {
        super(R.layout.item_replay_comment_layout);
    }

    @Override
    protected void convert(BaseViewHolder helper, CommendItemBean.RowsBean item) {

        ImageView avatar = helper.getView(R.id.comment_item_avatar);
        GlideUtils.loadImage(item.userheadphoto, avatar, false);
        helper.setText(R.id.comment_item_name_tx, item.username);

        helper.setOnClickListener(R.id.comment_item_name_tx, v -> HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, item.userid));
        avatar.setOnClickListener(v -> HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, item.userid));

        TextView mExpandTextView = helper.getView(R.id.expand_text_view);
        mExpandTextView.setText(item.commenttext);
        ImageView likeIv = helper.getView(R.id.base_moment_spl_like_iv);
        likeIv.setSelected(LikeAndUnlikeUtil.isLiked(item.goodstatus));
        likeIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonHttpRequest.getInstance().requestCommentsLike(item.userid,
                        item.commentid, likeIv.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                likeIv.setSelected(!likeIv.isSelected());
                            }

                            @Override
                            public void needLogin() {
                                LoginHelp.goLogin();
                            }
                        });
            }
        });
        // TODO: 2018/11/14 分享的弹窗由fragment来实现具体内容
        helper.addOnClickListener(R.id.base_moment_share_iv);


        NineImageView mDetailImage = helper.getView(R.id.detail_image);
        ArrayList<ImageData> commentShowList = VideoAndFileUtils.getDetailCommentShowList(item.commenturl);
        if (commentShowList == null || commentShowList.size() == 0) return;
        mDetailImage.loadGif(false)
                .enableRoundCorner(false)
                .setData(commentShowList, new GridLayoutHelper(3, NineLayoutHelper.getCellWidth(),
                        NineLayoutHelper.getCellHeight(), NineLayoutHelper.getMargin()));
        mDetailImage.setOnItemClickListener(new NineImageView.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                String url = commentShowList.get(position).url;
                if (MediaFileUtils.getMimeFileIsVideo(url)) {
                    //直接全屏
                    JzvdStd.startFullscreen(MyApplication.getInstance().getRunningActivity()
                            , MyVideoPlayerStandard.class, url, "");
                } else {
                    HelperForStartActivity.openImageWatcher(position,commentShowList,null);
                }
            }
        });

    }

}
