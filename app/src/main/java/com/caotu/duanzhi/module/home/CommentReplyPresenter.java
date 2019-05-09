package com.caotu.duanzhi.module.home;

import android.text.TextUtils;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentReplyBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.module.publish.PublishPresenter;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe 内容详情页面发布内容
 */
public class CommentReplyPresenter extends PublishPresenter {
    IVewPublishComment IView;
    MomentsDataBean parentBean;

    public CommentReplyPresenter(IVewPublishComment context, MomentsDataBean bean) {
        super(context);
        parentBean = bean;
        IView = context;
    }

    @Override
    public void uMengPublishError() {
        UmengHelper.event(UmengStatisticsKeyIds.comment_failure);
        if (IView == null) return;
        if (!isMainThread()) {
            MyApplication.getInstance().getHandler().post(new Runnable() {
                @Override
                public void run() {
                    IView.publishError();
                }
            });
        } else {
            IView.publishError();
        }
    }

    @Override
    public void uploadProgress(int barProgress) {
        if (IView != null) {
            IView.uploadProgress(barProgress);
        }
    }

    public void setByOnlyIdDate(MomentsDataBean date) {
        parentBean = date;
    }

    /**
     * 发表评论的接口
     */
    public void requestPublish() {
        if (isVideo && uploadTxFiles.size() < 2) {
            return;
        }
        /*
        id	作品id(不可为空)	string	@mock=aaa
        cmtuid	回复评论用户id（非一级评论时不可为空)	string
        commenturl	评论url	string
        replyfirst	一级评论id(非一级评论时不可为空	string
        replyid	上级评论id（非一级评论时不可为空	string
        text	评论内容(不可为空,Emoji表情需要URL编码)	string	@mock=哈哈哈哈
         */
        if (parentBean == null) {
            if (IView != null) {
                IView.publishError();
            }
            return;
        }
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("cid", parentBean.getContentid());//作品id(不可为空)
        params.put("cmtuid", parentBean.getContentuid());//回复评论用户id（非一级评论时不可为空)
        String commentList = VideoAndFileUtils.changeListToJsonArray(uploadTxFiles, publishType, mWidthAndHeight);
        if (!TextUtils.isEmpty(commentList)) {
            String replaceUrl = commentList.replace("\\", "");
            params.put("commenturl", replaceUrl);
        }
        params.put("text", content);// 	评论内容(不可为空,Emoji表情需要URL编码)
        OkGo.<BaseResponseBean<CommentReplyBean>>post(HttpApi.COMMENT_BACK)
                .headers("OPERATE", "COMMENT")
                .headers("VALUE", parentBean.getContentid())
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<BaseResponseBean<CommentReplyBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<CommentReplyBean>> response) {
                        if (!TextUtils.isEmpty(videoCover)) {
                            LanSongFileUtil.deleteFile(videoCover);
                        }
                        if (HttpCode.cant_talk.equals(response.body().getCode())) {
                            if (IView != null) {
                                IView.publishCantTalk(response.body().getMessage());
                            }
                            return;
                        }
                        CommentReplyBean data = response.body().getData();
                        if (data == null) {
                            if (IView != null) {
                                IView.publishError();
                            }
                            return;
                        }
                        //这个bean直接能用,就不用转一层了,直接扔给列表展示就行,需要判断头布局
                        CommendItemBean.RowsBean comment = data.comment;
                        if (IView != null) {
                            IView.endPublish(comment);
                        }
                        LanSongFileUtil.deleteDir(new File(LanSongFileUtil.TMP_DIR));
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<CommentReplyBean>> response) {
                        if (IView != null) {
                            IView.publishError();
                        }
                        if (!TextUtils.isEmpty(videoCover)) {
                            LanSongFileUtil.deleteFile(videoCover);
                        }
                        super.onError(response);
                    }
                });
    }

    @Override
    protected boolean shouldCheckLength() {
        return false;
    }
}
