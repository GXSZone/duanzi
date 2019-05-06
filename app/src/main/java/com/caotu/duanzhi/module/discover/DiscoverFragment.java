package com.caotu.duanzhi.module.discover;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.GridLayoutManager;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.DiscoverBannerBean;
import com.caotu.duanzhi.Http.bean.DiscoverListBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class DiscoverFragment extends BaseStateFragment<DiscoverListBean.RowsBean> implements BaseQuickAdapter.OnItemClickListener {

    private MZBannerView<DiscoverBannerBean.BannerListBean> bannerView;
    private DiscoverItemAdapter discoverItemAdapter;

    @Override
    protected BaseQuickAdapter getAdapter() {
        discoverItemAdapter = new DiscoverItemAdapter();
        discoverItemAdapter.setOnItemClickListener(this);
        mStatesView.setBackgroundColor(DevicesUtils.getColor(R.color.white));
        return discoverItemAdapter;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        if (DateState.init_state == load_more) {
            OkGo.<BaseResponseBean<DiscoverBannerBean>>post(HttpApi.DISCOVER_BANNER)
                    .execute(new JsonCallback<BaseResponseBean<DiscoverBannerBean>>() {
                        @Override
                        public void onSuccess(Response<BaseResponseBean<DiscoverBannerBean>> response) {
                            List<DiscoverBannerBean.BannerListBean> bannerList = response.body().getData().getBannerList();
                            bindBanner(bannerList);
                        }
                    });
        }
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("pageno", position + "");
        hashMapParams.put("pagesize", "12");
        OkGo.<BaseResponseBean<DiscoverListBean>>post(HttpApi.DISCOVER_LIST)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<DiscoverListBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<DiscoverListBean>> response) {
                        List<DiscoverListBean.RowsBean> rows = response.body().getData().getRows();
                        setDate(load_more, rows);
                    }
                });
    }

    @Override
    public int getPageSize() {
        return 12;
    }


    private void bindBanner(List<DiscoverBannerBean.BannerListBean> bannerList) {
        if (bannerView != null && bannerList != null && bannerList.size() > 0) {
            bannerView.setBannerPageClickListener((view, i) -> {
                DiscoverBannerBean.BannerListBean bannerListBean = bannerList.get(i);
                skipByBanner(bannerListBean);
            });
            // 设置数据
            bannerView.setPages(bannerList, (MZHolderCreator<BannerViewHolder>) () -> new BannerViewHolder(bannerView));
            bannerView.start();
        }
    }

    private void skipByBanner(DiscoverBannerBean.BannerListBean bean) {
        //展示页类型 1_wap页 2_主题合集 3_主题 4_内容
        switch (bean.bannertype) {
            case "1":
                WebShareBean shareBean = new WebShareBean();
                shareBean.icon = bean.bannersharepic;
                HelperForStartActivity.checkUrlForSkipWeb(bean.bannertext, bean.bannerurl, AndroidInterface.type_banner, shareBean);
                //统计用
                CommonHttpRequest.getInstance().splashCount("BANNER" + bean.bannerid);
                break;
            case "3":
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, bean.bannerurl);
                break;
            case "4":
                HelperForStartActivity.openContentDetail(bean.bannerurl);
                break;
            default:
                // TODO: 2018/12/4 跳转H5页面固定
//                WebActivity.openWeb();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bannerView != null) {
            bannerView.pause();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (bannerView == null) return;
        if (!isVisibleToUser) {
            bannerView.pause();
        } else {
            bannerView.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerView != null) {
            bannerView.start();
        }
    }

    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }

    @Override
    protected void initViewListener() {
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.discover_header_banner, mRvContent, false);
        headerView.findViewById(R.id.tv_go_search).setOnClickListener(HelperForStartActivity::openSearch);
        bannerView = headerView.findViewById(R.id.mz_banner);
        GridLayoutManager layout = new GridLayoutManager(getContext(), 3);
        //设置列表的排布
        layout.setSpanSizeLookup(new HeaderGridLayoutManger(discoverItemAdapter));
        mRvContent.setLayoutManager(layout);
        adapter.setHeaderAndEmpty(true);
        adapter.setHeaderView(headerView);

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        DiscoverListBean.RowsBean bean = (DiscoverListBean.RowsBean) adapter.getData().get(position);
        HelperForStartActivity.openOther(bean.tagid);
    }

    public static class BannerViewHolder implements MZViewHolder<DiscoverBannerBean.BannerListBean> {
        private GlideImageView mImageView;
        private ViewGroup viewGroup;

        public BannerViewHolder(ViewGroup bannerView) {
            viewGroup = bannerView;
        }

        @Override
        public View createView(Context context) {
            // 返回页面布局
            View rootView = LayoutInflater.from(context).inflate(R.layout.item_banner_layout, viewGroup, false);
            mImageView = rootView.findViewById(R.id.image_banner);
            return rootView;
        }

        @Override
        public void onBind(Context context, int position, DiscoverBannerBean.BannerListBean data) {
            // 数据绑定
            if (!TextUtils.isEmpty(data.bannerpic) && data.bannerpic.startsWith("https")) {
                data.bannerpic = data.bannerpic.replace("https", "http");
            }
            mImageView.load(data.bannerpic, R.mipmap.shenlue_logo, 5);
        }
    }
}
