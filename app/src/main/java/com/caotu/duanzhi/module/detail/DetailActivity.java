package com.caotu.duanzhi.module.detail;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommendItemBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.advertisement.ADConfig;
import com.caotu.duanzhi.advertisement.ADUtils;
import com.caotu.duanzhi.advertisement.IADView;
import com.caotu.duanzhi.advertisement.NativeAdListener;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseSwipeActivity;
import com.caotu.duanzhi.module.detail_scroll.BaseContentDetailFragment;
import com.caotu.duanzhi.module.detail_scroll.VideoDetailFragment;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * 现在内容详情和评论详情都用这个activity,只是里面的fragment不一样,所有逻辑都在fragment里
 * 这个是左右不能滑动的详情,跟ContentNewDetailActivity 从列表跳转过来可以左右滑动的详情区分,
 * 虽然里面套的fragment都一样.可以分开处理不同逻辑:比如没有跟列表的联动
 */

public class DetailActivity extends BaseSwipeActivity implements IADView {

    protected MomentsDataBean bean;
    private String contentId;
    private BaseContentDetailFragment detailFragment;
    private CommentNewFragment fragment;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_detail_empty;
    }

    @Override
    protected void initView() {
        getIntentDate();
    }


    public void getIntentDate() {
        contentId = getIntent().getStringExtra("contentId");
        bean = getIntent().getParcelableExtra(HelperForStartActivity.KEY_CONTENT);
        // TODO: 2019-07-30 一个详情activity包含内容详情,ugc详情,评论详情,只是里面套的fragment不一样
        CommendItemBean.RowsBean commentBean = getIntent().getParcelableExtra(HelperForStartActivity.KEY_DETAIL_COMMENT);
        if (commentBean != null) {
            List<CommentUrlBean> commentUrlBean = VideoAndFileUtils.getCommentUrlBean(commentBean.commenturl);

            boolean isVideo = commentUrlBean != null && commentUrlBean.size() > 0 &&
                    LikeAndUnlikeUtil.isVideoType(commentUrlBean.get(0).type);
            if (isVideo) {
                fragment = new CommentVideoFragment();
            } else {
                fragment = new CommentNewFragment();
            }
            Bundle bundle = new Bundle();
            bundle.putParcelable("commentBean", commentBean);
            turnToFragment(bundle, fragment, R.id.fl_fragment_content);
            return;
        }

        if (bean == null) {
            getDetailDate();
        } else {
            bindFragment();
        }
        if (TextUtils.isEmpty(contentId) && bean != null) {
            contentId = bean.getContentid();
        }
    }

    NativeExpressAD nativeAd;
    NativeExpressADView adView;

    NativeExpressAD nativeCommentAd;
    NativeExpressADView adCommentView;

    @Override
    public NativeExpressADView getAdView() {
        adView.render();
        return adView;
    }

    @Override
    public NativeExpressADView getCommentAdView() {
        adCommentView.render();
        return adCommentView;
    }

    public void bindFragment() {
        String contenttype = bean.getContenttype();
        if (TextUtils.equals(contenttype, "1") || TextUtils.equals(contenttype, "2")) {
            detailFragment = new VideoDetailFragment();
        } else {
            detailFragment = new BaseContentDetailFragment();
        }
        detailFragment.setDate(bean,0);
        turnToFragment(detailFragment, R.id.fl_fragment_content);
        //获取广告
        if (ADConfig.AdOpenConfig.contentAdIsOpen) {
            nativeAd = ADUtils.getNativeAd(this, ADConfig.datail_id, 1,
                    new NativeAdListener(2) {
                        @Override
                        public void onADLoaded(List<NativeExpressADView> list) {
                            super.onADLoaded(list);
                            adView = getNativeExpressADView();
//                            detailFragment.refreshAdView(adView);
                        }
                    });
        }
        if (ADConfig.AdOpenConfig.commentAdIsOpen) {
            nativeCommentAd = ADUtils.getNativeAd(this, ADConfig.comment_id, 1,
                    new NativeAdListener(3) {
                        @Override
                        public void onADLoaded(List<NativeExpressADView> list) {
                            super.onADLoaded(list);
                            adCommentView = getNativeExpressADView();
//                            detailFragment.refreshCommentListAd(adCommentView);
                        }
                    });
        }
    }

    private void getDetailDate() {
        if (TextUtils.isEmpty(contentId)) return;
        //用于通知跳转
        HashMap<String, String> hashMapParams = new HashMap<>();
        hashMapParams.put("contentid", contentId);
        OkGo.<BaseResponseBean<MomentsDataBean>>post(HttpApi.WORKSHOW_DETAILS)
                .upJson(new JSONObject(hashMapParams))
                .tag(this)
                .execute(new JsonCallback<BaseResponseBean<MomentsDataBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<MomentsDataBean>> response) {
                        MomentsDataBean data = DataTransformUtils.getContentNewBean(response.body().getData());
                        if (data == null) {
                            ToastUtil.showShort("该内容已不存在");
                            finish();
                            return;
                        }
                        bean = data;
                        bindFragment();
                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (detailFragment != null) {
            detailFragment.onActivityResult(requestCode, resultCode, data);
        }
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
