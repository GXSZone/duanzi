package com.caotu.duanzhi.module.other;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.DiscoverBannerBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.view.banner.MZBannerView;
import com.caotu.duanzhi.view.banner.MZHolderCreator;
import com.caotu.duanzhi.view.banner.MZViewHolder;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;

import java.util.List;

public class BannerHelper {
    private static final BannerHelper ourInstance = new BannerHelper();

    public static BannerHelper getInstance() {
        return ourInstance;
    }

    private BannerHelper() {
    }

    public interface BannerCallBack {
        void isSuccess(boolean yes);
    }

    public void getBannerDate(MZBannerView bannerView, String httpapi, int type, BannerCallBack callBack) {
        if (bannerView == null) return;
        CommonHttpRequest.getInstance().httpPostRequest(httpapi,
                null, new JsonCallback<BaseResponseBean<DiscoverBannerBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<DiscoverBannerBean>> response) {
                        List<DiscoverBannerBean.BannerListBean> bannerList = response.body().getData().getBannerList();
                        bindBanner(bannerView, bannerList, type);
                        if (callBack != null) {
                            callBack.isSuccess(true);
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<DiscoverBannerBean>> response) {
                        super.onError(response);
                        if (callBack != null) {
                            callBack.isSuccess(false);
                        }
                    }
                });
    }

    public void bindBanner(MZBannerView bannerView, List<DiscoverBannerBean.BannerListBean> bannerList, int type) {
        if (bannerView != null && bannerList != null && bannerList.size() > 0) {
            bannerView.setBannerPageClickListener((view, i) -> {
                DiscoverBannerBean.BannerListBean bannerListBean = bannerList.get(i);
                skipByBanner(bannerListBean, type);
            });
            // 设置数据
            bannerView.setPages(bannerList, (MZHolderCreator<BannerViewHolder>) () -> new BannerViewHolder());
            bannerView.start();
        }
    }

    private void skipByBanner(DiscoverBannerBean.BannerListBean bean, int type) {
        CommonHttpRequest.getInstance().splashCount("BANNER" + bean.bannerid);
        //展示页类型 1_wap页 2_主题合集 3_主题 4_内容
        switch (bean.bannertype) {
            case "3":
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, bean.bannerurl);
                break;
            case "4":
                HelperForStartActivity.openContentDetail(bean.bannerurl);
                break;
            default:
                WebShareBean shareBean = new WebShareBean();
                shareBean.icon = bean.bannersharepic;
                HelperForStartActivity.checkUrlForSkipWeb(bean.bannertext, bean.bannerurl,
                        type == 1 ? AndroidInterface.type_mine_banner : AndroidInterface.type_banner, shareBean);
                if (type == 1) {
                    UmengHelper.meBannerEvent(bean.bannerid);
                } else {
                    UmengHelper.discoverTpicEvent(bean.bannerid);
                }
                break;
        }
    }

    public static class BannerViewHolder implements MZViewHolder<DiscoverBannerBean.BannerListBean> {
        @Override
        public View createView(Context context, DiscoverBannerBean.BannerListBean data) {
            GlideImageView mImageView = new GlideImageView(context);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            int padding = DevicesUtils.dp2px(5);
            mImageView.setPadding(padding, 0, padding, 0);
            String url = MyApplication.buildFileUrl(data.bannerpic);
            mImageView.load(url, R.mipmap.shenlue_logo, 5);
            return mImageView;
        }
    }
}
