package com.caotu.duanzhi.module.discover;

import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.DiscoverBannerBean;
import com.caotu.duanzhi.Http.bean.DiscoverListBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.home.ITabRefresh;
import com.caotu.duanzhi.module.other.BannerHelper;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.zhouwei.mzbanner.MZBannerView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class DiscoverFragment extends BaseStateFragment<DiscoverListBean.RowsBean>
        implements BaseQuickAdapter.OnItemClickListener, ITabRefresh {

    private MZBannerView<DiscoverBannerBean.BannerListBean> bannerView;

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_discover_layout;
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        DiscoverOldAdapter discoverItemAdapter = new DiscoverOldAdapter();
        discoverItemAdapter.setOnItemClickListener(this);
        return discoverItemAdapter;
    }

    boolean isBannerSuccess = false;

    @Override
    protected void getNetWorkDate(int load_more) {
        //请求失败刷新继续请求接口
        if (DateState.load_more != load_more && !isBannerSuccess) {
            BannerHelper.getInstance().getBannerDate(bannerView, HttpApi.DISCOVER_BANNER, 0, new BannerHelper.BannerCallBack() {
                @Override
                public void isSuccess(boolean yes) {
                    isBannerSuccess = yes;
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

                    @Override
                    public void onError(Response<BaseResponseBean<DiscoverListBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (bannerView != null) {
            bannerView.pause();
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
        layout.setSpanSizeLookup(new HeaderGridLayoutManger(adapter));
        mRvContent.setLayoutManager(layout);
        adapter.setHeaderAndEmpty(true);
        adapter.setHeaderView(headerView);

    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        DiscoverListBean.RowsBean bean = (DiscoverListBean.RowsBean) adapter.getData().get(position);
        CommonHttpRequest.getInstance().discoverStatistics("DISCOVER" + bean.tagid);
        UmengHelper.discoverTpicEvent(bean.tagid);
        HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, bean.tagid);
    }

    @Override
    public void refreshDateByTab() {
        if (mSwipeLayout != null) {
            mSwipeLayout.autoRefresh();
        }
    }
}

