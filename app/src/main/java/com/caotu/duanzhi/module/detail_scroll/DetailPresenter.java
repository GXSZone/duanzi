package com.caotu.duanzhi.module.detail_scroll;

import android.text.TextUtils;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentReplyBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.config.HttpCode;
import com.caotu.duanzhi.module.detail.IVewPublishComment;
import com.caotu.duanzhi.module.publish.PublishPresenter;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.lansosdk.videoeditor.LanSongFileUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe 内容详情页面发布内容
 */
public class DetailPresenter extends PublishPresenter {
    IVewPublishComment IView;
    MomentsDataBean parentBean;

    public DetailPresenter(IVewPublishComment context, MomentsDataBean bean) {
        super(context);
        parentBean = bean;
        IView = context;
    }

    public void dealDateList(List<CommendItemBean.RowsBean> bestlist, List<CommendItemBean.RowsBean> rows,
                             MomentsDataBean ugc, @DateState int load_more) {
        ArrayList<CommendItemBean.RowsBean> beanArrayList = new ArrayList<>(25);
        CommendItemBean.RowsBean ugcBean = null;
        if (ugc != null) {
            ugcBean = DataTransformUtils.changeUgcBean(ugc);
        }
        //注意顺序就好
        if (AppUtil.listHasDate(bestlist)) {
            bestlist.get(0).isBest = true;
            beanArrayList.addAll(bestlist);
        }
        if (DateState.init_state == load_more && !TextUtils.isEmpty(parentBean.fromCommentId)) {
            commentTop(rows, load_more, beanArrayList, ugcBean);
            return;
        }
        if (ugcBean != null) {
            beanArrayList.add(ugcBean);
        }
        if (AppUtil.listHasDate(rows)) {
            beanArrayList.addAll(rows);
        }
        if (IView != null) {
            IView.setListDate(beanArrayList,load_more);
        }
    }

    public void commentTop(List<CommendItemBean.RowsBean> rows, int load_more,
                           ArrayList<CommendItemBean.RowsBean> beanArrayList,
                           CommendItemBean.RowsBean ugcBean) {
        int position = -1;

        for (int i = 0; i < rows.size(); i++) {
            if (TextUtils.equals(rows.get(i).commentid, parentBean.fromCommentId)) {
                position = i;
                break;
            }
        }
        if (position != -1) {
            CommendItemBean.RowsBean remove = rows.remove(position);
            beanArrayList.add(0, remove);
            if (ugcBean != null) {
                beanArrayList.add(ugcBean);
            }
            if (AppUtil.listHasDate(rows)) {
                beanArrayList.addAll(rows);
            }
            if (IView != null) {
                IView.setListDate(beanArrayList,load_more);
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
                            beanArrayList.add(0, data);
                            if (ugcBean != null) {
                                beanArrayList.add(ugcBean);
                            }
                            if (AppUtil.listHasDate(rows)) {
                                beanArrayList.addAll(rows);
                            }
                            if (IView != null) {
                                IView.setListDate(beanArrayList,load_more);
                            }
                        }

                        @Override
                        public void onError(Response<BaseResponseBean<CommendItemBean.RowsBean>> response) {
                            if (ugcBean != null) {
                                beanArrayList.add(ugcBean);
                            }
                            if (AppUtil.listHasDate(rows)) {
                                beanArrayList.addAll(rows);
                            }
                            if (IView != null) {
                                IView.setListDate(beanArrayList,load_more);
                            }
                        }
                    });
        }
    }

    @Override
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
