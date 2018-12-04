package com.caotu.duanzhi.module.discover;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.DiscoverBannerBean;
import com.caotu.duanzhi.Http.bean.DiscoverListBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.other.WebActivity;
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

    List<DiscoverBannerBean.BannerListBean> bannerListBeans;

    private void bindBanner(List<DiscoverBannerBean.BannerListBean> bannerList) {
        bannerListBeans = bannerList;
        if (bannerView != null) {

            bannerView.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
                @Override
                public void onPageClick(View view, int i) {
                    DiscoverBannerBean.BannerListBean bannerListBean = bannerListBeans.get(i);
                    skipByBanner(bannerListBean);
//                    Log.i("bannerPosition", "onPageClick: " + i);
                }
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
                WebActivity.openWeb(bean.bannertext, bean.bannerurl, false, null);
                break;

            case "3":
                HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, bean.bannerid);
                break;
            case "4":
                HelperForStartActivity.openContentDetail(bean.bannerid);
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
        // TODO: 2018/12/4 跳转话题详情
        HelperForStartActivity.openOther(HelperForStartActivity.type_other_topic, bean.tagid);

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
            mImageView.load(data.bannerpic, R.mipmap.image_default, 5);
        }
    }
}
