package com.caotu.duanzhi.module.discover;

import android.content.Context;
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
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.home.ITabRefresh;
import com.caotu.duanzhi.other.AndroidInterface;
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

public class DiscoverFragment extends BaseStateFragment<DiscoverListBean.RowsBean>
        implements BaseQuickAdapter.OnItemClickListener,
        ITabRefresh {

    private MZBannerView<DiscoverBannerBean.BannerListBean> bannerView;
    private DiscoverItemAdapter discoverItemAdapter;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_discover_layout;
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        discoverItemAdapter = new DiscoverItemAdapter();
        discoverItemAdapter.setOnItemClickListener(this);
        return discoverItemAdapter;
    }

    private boolean bannerSuccess = false;

    @Override
    protected void getNetWorkDate(int load_more) {
        //请求失败刷新继续请求接口
        if (DateState.init_state == load_more ||
                (DateState.refresh_state == load_more && !bannerSuccess)) {
            getBannerDate();
        }
        getListDate(load_more);
    }

    private void getBannerDate() {
        OkGo.<BaseResponseBean<DiscoverBannerBean>>post(HttpApi.DISCOVER_BANNER)
                .execute(new JsonCallback<BaseResponseBean<DiscoverBannerBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<DiscoverBannerBean>> response) {
                        List<DiscoverBannerBean.BannerListBean> bannerList = response.body().getData().getBannerList();
                        bindBanner(bannerList);
                        bannerSuccess = true;
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<DiscoverBannerBean>> response) {
                        bannerSuccess = false;
                        super.onError(response);
                    }
                });
    }

    private void getListDate(int load_more) {
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
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.discover_header_layout, mRvContent, false);
        View searchView = rootView.findViewById(R.id.tv_go_search);
        if (searchView != null) {
            searchView.setOnClickListener(HelperForStartActivity::openSearch);
        }
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
        HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic,bean.tagid);
    }

    @Override
    public void refreshDateByTab() {
        if (mSwipeLayout != null) {
            mSwipeLayout.autoRefresh();
        }
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
            String url = MyApplication.buildFileUrl(data.bannerpic);
//                data.bannerpic = data.bannerpic.replace("https", "http");
            mImageView.load(url, R.mipmap.shenlue_logo, 5);
        }
    }
}
