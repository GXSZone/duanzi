package com.caotu.duanzhi.module.holder;

import android.animation.ValueAnimator;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.Int2TextUtils;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ParserUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.other.FastClickListener;
import com.caotu.duanzhi.view.widget.EyeTopicTextView;
import com.lzy.okgo.model.Response;

/**
 * @author mac
 * @日期: 2018/11/15
 * @describe 内容详情页的头布局
 */
public class DetailHeaderViewHolder extends BaseHeaderHolder<MomentsDataBean> {
    protected View ivGoHot;  //这个是内容详情专有的
    EyeTopicTextView eyeTopicTextView;

    public DetailHeaderViewHolder(View parentView) {
        super(parentView);
        ivGoHot = rootView.findViewById(R.id.iv_go_hot);
        ivGoHot.setOnClickListener(this);
        ivGoHot.setOnClickListener(new FastClickListener() {
            @Override
            protected void onSingleClick() {
                if (MySpUtils.isMe(headerBean.getContentuid())) {
                    ToastUtil.showShort("不能推荐自己的内容上热门哦");
                    return;
                }
                UmengHelper.event(UmengStatisticsKeyIds.top_popular);
                CommonHttpRequest.getInstance().goHot(headerBean.getContentid());
            }
        });
        eyeTopicTextView = rootView.findViewById(R.id.tv_topic);
    }

    /**
     * 评论成功数值加一
     */
    @Override
    public void commentPlus() {
        if (headerBean == null) return;
        int contentcomment = headerBean.getContentcomment();
        contentcomment++;
        setComment(contentcomment);
        headerBean.setContentcomment(contentcomment);
        EventBusHelp.sendLikeAndUnlike(headerBean);
    }

    @Override
    public void commentMinus() {
        if (headerBean == null) return;
        int contentcomment = headerBean.getContentcomment();
        contentcomment--;
        setComment(contentcomment);
        headerBean.setContentcomment(contentcomment);
        EventBusHelp.sendLikeAndUnlike(headerBean);
    }


    @Override
    protected void dealOther(MomentsDataBean dataBean) {
        dealTextContent(dataBean);
        setComment(dataBean.getContentcomment());
        if (eyeTopicTextView != null) {
            eyeTopicTextView.setTopicText(dataBean.getTagshowid(), dataBean.getTagshow());
        }
    }

    @Override
    protected void dealType(MomentsDataBean data) {
        String contenttype = data.getContenttype();
        if (TextUtils.equals(contenttype, "3")) {
            nineImageView.setVisibility(View.VISIBLE);
            dealNineLayout(data.imgList, data.getContentid(), data.getContenttag());
        } else {
            nineImageView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void dealLikeBt(MomentsDataBean data, View likeView) {
        UmengHelper.event(UmengStatisticsKeyIds.content_like);
        if (!LoginHelp.isLogin()) {
            LoginHelp.goLogin();
            return;
        }
        //这里注意下用的是headerBean, 虽然跟参数的对象是同一个
        CommonHttpRequest.getInstance().requestLikeOrUnlike(data.getContentuid(),
                data.getContentid(), true, likeView.isSelected(), new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        if (!likeView.isSelected()) {
                            LikeAndUnlikeUtil.showLike(likeView);
                            showWxShareIcon(ivGoHot);
                        }
                        int likeCount = headerBean.getContentgood();
                        if (likeView.isSelected()) {
                            likeCount--;
                            if (likeCount < 0) {
                                likeCount = 0;
                            }
                        } else {
                            likeCount++;
                        }

                        mBaseMomentLike.setSelected(!mBaseMomentLike.isSelected());
                        mBaseMomentLike.setText(Int2TextUtils.toText(likeCount, "w"));

                        bottomLikeView.setText(Int2TextUtils.toText(likeCount, "w"));
                        bottomLikeView.setSelected(!bottomLikeView.isSelected());

                        headerBean.setContentgood(likeCount);
                        //修改goodstatus状态 "0"_未赞未踩 "1"_已赞 "2"_已踩
                        headerBean.setGoodstatus(mBaseMomentLike.isSelected() ? "1" : "0");

                        EventBusHelp.sendLikeAndUnlike(headerBean);
                    }
                });
    }

    public void dealTextContent(MomentsDataBean data) {
        boolean isTagShow = TextUtils.equals("1", data.getIsshowtitle());
        if (isTagShow) {
            mTvContentText.setVisibility(View.VISIBLE);
            SpannableStringBuilder spanText = ParserUtils.htmlToSpanText(data.getContenttitle(), true);
            mTvContentText.setText(spanText);
            mTvContentText.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            mTvContentText.setVisibility(View.GONE);
        }
        mTvContentText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, MySpUtils.getFloat(MySpUtils.SP_TEXT_SIZE));
    }

    public void showWxShareIcon(View shareWx) {
        // TODO: 2019-09-02 这里还需要判断,该用户是否有该资格,没资格也不展示
        if (headerBean == null) return;
        if (!CommonHttpRequest.canGoHot || MySpUtils.isMe(headerBean.getContentuid())) return;

        ViewGroup.LayoutParams params = shareWx.getLayoutParams();
        if (params == null) return;
        if (params.height > 10 || params.width > 10) return;  //在方法里过滤
        // TODO: 2019-08-28 这里宽高还要调整
        int px = DevicesUtils.dp2px(40);
        ValueAnimator anim = ValueAnimator.ofInt(0, px);
        anim.setInterpolator(new OvershootInterpolator());
        anim.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            params.width = value + px;
            params.height = value;
            shareWx.setLayoutParams(params);
        });
        anim.start();
    }
}
