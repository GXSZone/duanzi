package com.caotu.duanzhi.module.other;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.DiscoverBannerBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.advertisement.RoundFrameLayout;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import java.util.List;

public class BannerHelper {
    private static final BannerHelper ourInstance = new BannerHelper();

    public static BannerHelper getInstance() {
        return ourInstance;
    }

    private BannerHelper() {
    }

    public void getBannerDate(MZBannerView bannerView, String httpapi, int type) {
        CommonHttpRequest.getInstance().httpPostRequest(httpapi,
                null, new JsonCallback<BaseResponseBean<DiscoverBannerBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<DiscoverBannerBean>> response) {
                        bannerView.setVisibility(View.VISIBLE);
                        List<DiscoverBannerBean.BannerListBean> bannerList = response.body().getData().getBannerList();
                        bindBanner(bannerView, bannerList, type);
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
            bannerView.setPages(bannerList, (MZHolderCreator<BannerViewHolder>) ()
                    -> new BannerViewHolder(bannerView));
            bannerView.start();
        }
    }

    private void skipByBanner(DiscoverBannerBean.BannerListBean bean, int type) {
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
                String fromType = AndroidInterface.type_banner;
                if (type == 1) {
                    UmengHelper.meBannerEvent(bean.bannerid);
                    fromType = AndroidInterface.type_mine_banner;
                } else {
                    UmengHelper.discoverTpicEvent(bean.bannerid);
                }
                HelperForStartActivity.checkUrlForSkipWeb(bean.bannertext, bean.bannerurl,
                        fromType, shareBean);
                break;
        }
    }

    public static class BannerViewHolder implements MZViewHolder<DiscoverBannerBean.BannerListBean> {
        private GlideImageView mImageView;
        private ViewGroup viewGroup; //这个是轮播图的总父控件
        ViewGroup rootView; //imageview 和adview 的父控件

        public BannerViewHolder(ViewGroup bannerView) {
            viewGroup = bannerView;
        }

        @Override
        public View createView(Context context, DiscoverBannerBean.BannerListBean data) {
            // 返回页面布局

            if (TextUtils.equals("0", data.bannertype)) {
                //这个可能需要inflate
                rootView = new RoundFrameLayout(context);
            } else {
                rootView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.item_banner_layout, viewGroup, false);
                mImageView = rootView.findViewById(R.id.image_banner);
            }
            return rootView;
        }

        @Override
        public void onBind(Context context, int position, DiscoverBannerBean.BannerListBean data) {
            if (TextUtils.equals("0", data.bannertype) && data.adView != null) {
                if (rootView instanceof RoundFrameLayout) {
                    rootView.removeAllViews();
                    data.adView.render();
                    rootView.addView(data.adView);
                }
            } else {
                // 数据绑定
                String url = MyApplication.buildFileUrl(data.bannerpic);
                mImageView.load(url, R.mipmap.shenlue_logo, 5);
            }
        }
    }
}
