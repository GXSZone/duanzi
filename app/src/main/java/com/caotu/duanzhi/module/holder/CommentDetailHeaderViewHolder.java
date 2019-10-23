package com.caotu.duanzhi.module.holder;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.fixTextClick.CustomMovementMethod;
import com.lzy.okgo.model.Response;
import com.sunfusheng.widget.ImageData;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe TODO
 */
public class CommentDetailHeaderViewHolder extends BaseHeaderHolder<CommendItemBean.RowsBean> {

    public TextView tvGoDetail;

    public CommentDetailHeaderViewHolder(View parentView) {
        super(parentView);
        tvGoDetail = rootView.findViewById(R.id.tv_click_content_detail);
    }

    public void commentPlus() {
        int contentcomment = headerBean.replyCount;
        contentcomment++;
        setComment(contentcomment);
        headerBean.replyCount = contentcomment;
    }

    public void commentMinus() {
        int contentcomment = headerBean.replyCount;
        contentcomment--;
        if (contentcomment < 0) {
            contentcomment = 0;
        }
        setComment(contentcomment);
        headerBean.replyCount = contentcomment;
    }


    @Override
    protected void dealOther(CommendItemBean.RowsBean dataBean) {
        if (dataBean.isShowContentFrom()) {
            tvGoDetail.setVisibility(View.VISIBLE);
        } else {
            tvGoDetail.setVisibility(View.GONE);
        }
        tvGoDetail.setOnClickListener(v -> HelperForStartActivity.openContentDetail(dataBean.contentid));
        setHeaderText(dataBean);
        setComment(dataBean.replyCount);
    }

    public void setHeaderText(CommendItemBean.RowsBean data) {
        mTvContentText.setVisibility(TextUtils.isEmpty(data.commenttext) ? View.GONE : View.VISIBLE);
        mTvContentText.setText(ParserUtils.htmlToSpanText(data.commenttext, true));
        mTvContentText.setMovementMethod(CustomMovementMethod.getInstance());
        mTvContentText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, MySpUtils.getFloat(MySpUtils.SP_TEXT_SIZE));
    }

    @Override
    protected void dealLikeBt(CommendItemBean.RowsBean data, View likeView) {
        UmengHelper.event(UmengStatisticsKeyIds.comment_like);
        if (!LoginHelp.isLogin()) {
            LoginHelp.goLogin();
            return;
        }
        CommonHttpRequest.getInstance().requestCommentsLike(data.userid,
                data.contentid, data.commentid, likeView.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        if (!likeView.isSelected()) {
                            LikeAndUnlikeUtil.showLike(likeView, 0, 20);
                        }
                        int likeCount = data.commentgood;
                        if (likeView.isSelected()) {
                            likeCount--;
                            if (likeCount < 0) {
                                likeCount = 0;
                            }
                        } else {
                            likeCount++;
                        }
                        mBaseMomentLike.setText(Int2TextUtils.toText(likeCount, "w"));
                        mBaseMomentLike.setSelected(!mBaseMomentLike.isSelected());

                        bottomLikeView.setText(Int2TextUtils.toText(likeCount, "w"));
                        bottomLikeView.setSelected(!bottomLikeView.isSelected());


                        //"0"_未赞未踩 "1"_已赞 "2"_已踩
                        data.goodstatus = mBaseMomentLike.isSelected() ? "1" : "0";
                        data.commentgood = likeCount;
                        EventBusHelp.sendCommendLikeAndUnlike(data);
                    }
                });
    }


    @Override
    protected void dealType(CommendItemBean.RowsBean data) {
        // TODO: 2018/11/17 如果集合是空的代表是纯文字类型
        List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(data.commenturl);
        if (commentUrlBean != null && commentUrlBean.size() > 0) {
            nineImageView.setVisibility(View.VISIBLE);
            dealNineImage(commentUrlBean, data.contentid);
        } else {
            nineImageView.setVisibility(View.GONE);
        }
    }


    private void dealNineImage(List<CommentUrlBean> commentUrlBean, String contentid) {
        if (commentUrlBean == null || commentUrlBean.size() == 0) return;
        cover = commentUrlBean.get(0).cover;
        ArrayList<ImageData> imgList = new ArrayList<>();
        for (int i = 0; i < commentUrlBean.size(); i++) {
            CommentUrlBean urlBean = commentUrlBean.get(i);
            String url = MyApplication.buildFileUrl(urlBean.info);
            ImageData data = new ImageData(url);
            try {
                if (!TextUtils.isEmpty(urlBean.getSize()) && urlBean.getSize().contains(",")) {
                    String[] split = urlBean.getSize().split(",");
                    data.realWidth = Integer.parseInt(split[0]);
                    data.realHeight = Integer.parseInt(split[1]);
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            imgList.add(data);
        }
        dealNineLayout(imgList, contentid, null);
    }
}
