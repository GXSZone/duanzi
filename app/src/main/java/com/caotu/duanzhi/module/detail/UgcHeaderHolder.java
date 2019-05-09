package com.caotu.duanzhi.module.detail;

import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.view.FastClickListener;
import com.lzy.okgo.model.Response;

/**
 * @author mac
 * @日期: 2018/11/20
 * @describe 头布局的踩需要隐藏, 内容的bean对象, 评论详情头布局的样式
 */
public class UgcHeaderHolder extends DetailHeaderViewHolder {


    public UgcHeaderHolder(View parentView) {
        super(parentView);
    }

    @Override
    public boolean getIsNeedSync() {
        return false;
    }

    @Override
    public void dealTextContent(MomentsDataBean data) {
        //	1可见，0不可见
        mTvContentText.setText("1".equals(data.getIsshowtitle()) ? data.getContenttitle() : "");
        mTvContentText.setVisibility(TextUtils.isEmpty(mTvContentText.getText().toString())
                ? View.GONE : View.VISIBLE);
    }

    @Override
    public void justBindCountAndState(MomentsDataBean data) {

    }

    @Override
    public void dealLikeAndUnlike(MomentsDataBean data) {
        /*-------------------------------点赞和踩的处理---------------------------------*/
        mBaseMomentLike.setText(Int2TextUtils.toText(data.getContentgood(), "w"));

        mBaseMomentComment.setText(Int2TextUtils.toText(data.getContentcomment(), "w"));
//        "0"_未赞未踩 "1"_已赞 "2"_已踩

        if (TextUtils.equals("1", data.getGoodstatus())) {
            mBaseMomentLike.setSelected(true);
        }
        mBaseMomentLike.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                CommonHttpRequest.getInstance().requestLikeOrUnlike(data.getContentuid(),
                        data.getContentid(), true, mBaseMomentLike.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                            @Override
                            public void onSuccess(Response<BaseResponseBean<String>> response) {
                                if (!mBaseMomentLike.isSelected()) {
                                    LikeAndUnlikeUtil.showLike(mBaseMomentLike, 20, 30);
                                }
                                int goodCount = data.getContentgood();
                                if (mBaseMomentLike.isSelected()) {
                                    goodCount--;
                                    mBaseMomentLike.setSelected(false);
                                } else {
                                    goodCount++;
                                    mBaseMomentLike.setSelected(true);
                                }
                                mBaseMomentLike.setText(Int2TextUtils.toText(goodCount, "w"));
                                data.setContentgood(goodCount);
                                //修改goodstatus状态 "0"_未赞未踩 "1"_已赞 "2"_已踩
                                data.setGoodstatus(mBaseMomentLike.isSelected() ? "1" : "0");
                            }
                        });
            }
        });
        /*-------------------------------点赞和踩的处理结束---------------------------------*/
    }
}
