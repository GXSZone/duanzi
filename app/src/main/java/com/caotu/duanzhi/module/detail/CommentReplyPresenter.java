package com.caotu.duanzhi.module.detail;

import android.text.TextUtils;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentReplyBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.module.publish.PublishPresenter;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe 还有待改动, 评论的回复还需要换接口请求
 */
public class CommentReplyPresenter extends PublishPresenter {
    IVewPublishComment IView;
    CommendItemBean.RowsBean parentBean;
    //这里指的就是评论ID
    String replyid;
    //这里指的是  [被]回复人的ID
    String cmtuid;

    public CommentReplyPresenter(IVewPublishComment context, CommendItemBean.RowsBean bean) {
        super(context);
        parentBean = bean;
        IView = context;
        replyid = parentBean.commentid;
        cmtuid = parentBean.userid;
    }

    public void uMengPublishError() {
        UmengHelper.event(UmengStatisticsKeyIds.comment_failure);
        if (IView == null) return;
        MyApplication.getInstance().getHandler().post(() -> IView.publishError());
    }

    @Override
    public void uploadProgress(int barProgress) {
        if (IView != null) {
            IView.uploadProgress(barProgress);
        }
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
        HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
        params.put("cid", parentBean.contentid);//作品id(不可为空)
        params.put("replyfirst", parentBean.commentid);//一级评论id(非一级评论时不可为空
        // TODO: 2018/11/18 点击条目也就是更改这两个用户信息而已
        params.put("replyid", replyid);//上级评论id（非一级评论时不可为空
        params.put("cmtuid", cmtuid);//回复评论用户id（非一级评论时不可为空)
        String commentList = VideoAndFileUtils.changeListToJsonArray(uploadTxFiles, publishType, publishType);
        if (!TextUtils.isEmpty(commentList)) {
            String replaceUrl = commentList.replace("\\", "");
            params.put("commenturl", replaceUrl);
        }
        params.put("text", content);// 	评论内容(不可为空,Emoji表情需要URL编码)
        OkGo.<BaseResponseBean<CommentReplyBean>>post(HttpApi.COMMENT_BACK)
                .headers("OPERATE", "COMMENT")
                .headers("VALUE", parentBean.contentid)
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<BaseResponseBean<CommentReplyBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<CommentReplyBean>> response) {
                        if (!TextUtils.isEmpty(videoCover)) {
                            LanSongFileUtil.deleteFile(videoCover);
                        }
                        clearSelectList();
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

    public void setUserInfo(String commentid, String userId) {
        replyid = commentid;
        cmtuid = userId;
    }

    @Override
    protected boolean shouldCheckLength() {
        return false;
    }

    /**
     * presenter 来处理集合数据,分离代码
     * @param rows
     * @param load_more
     */
    public void dealList(List<CommendItemBean.RowsBean> rows, int load_more) {
        if (rows != null
                && rows.size() > 0
                && DateState.init_state == load_more
                && parentBean != null && !TextUtils.isEmpty(parentBean.fromCommentId)) {
            int position = -1;

            for (int i = 0; i < rows.size(); i++) {
                if (TextUtils.equals(rows.get(i).commentid, parentBean.fromCommentId)) {
                    position = i;
                    break;
                }
            }
            if (position != -1) {
                CommendItemBean.RowsBean remove = rows.remove(position);
                if (remove != null) {
                    rows.add(0, remove);
                    if (IView != null) {
                        IView.setListDate(rows, load_more);
                    }
                }
            } else {
                // TODO: 2019-04-24 需要请求接口获取置顶
                HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
                params.put("cmtid", parentBean.fromCommentId);
                OkGo.<BaseResponseBean<CommendItemBean.RowsBean>>post(HttpApi.COMMENT_DEATIL)
                        .upJson(new JSONObject(params))
                        .execute(new JsonCallback<BaseResponseBean<CommendItemBean.RowsBean>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<CommendItemBean.RowsBean>> response) {
                                CommendItemBean.RowsBean data = response.body().getData();
                                if (data == null) return;
                                rows.add(0, data);
                                if (IView != null) {
                                    IView.setListDate(rows, load_more);
                                }
                            }

                            @Override
                            public void onError(Response<BaseResponseBean<CommendItemBean.RowsBean>> response) {
                                if (IView != null) {
                                    IView.setListDate(rows, load_more);
                                }
                            }
                        });
            }
            return;
        }
        if (IView != null) {
            IView.setListDate(rows, load_more);
        }
    }
}
