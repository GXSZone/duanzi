package com.caotu.duanzhi.module.home.fragment;

import android.view.View;
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommentUrlBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.ShareUrlBean;
import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.ShareHelper;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.utils.VideoAndFileUtils;
import com.caotu.duanzhi.view.dialog.ActionDialog;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.model.Response;

public abstract class BaseNoVideoFragment extends BaseStateFragment<MomentsDataBean> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener, IHomeRefresh {

//    @Override
//    protected void getNetWorkDate(int load_more) {
//        if (!isVisibleToUser || !isViewInitiated) return;
//        //如果已经初始化过数据也直接返回,但是不能影响下拉刷新和加载更多
//        if (DateState.init_state == load_more && isDataInitiated) return;
//        // TODO: 2018/11/27 真正请求网络
//        realNetWorkRequest(load_more);
//        isDataInitiated = true;
//    }

    @Override
    protected void initViewListener() {
        adapter.setOnItemChildClickListener(this);
        adapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        MomentsDataBean bean = (MomentsDataBean) adapter.getData().get(position);
        switch (view.getId()) {
            //更多的操作的弹窗
            case R.id.item_iv_more_bt:
                if (MySpUtils.isMe(bean.getContentuid())) {
                    CommonHttpRequest.getInstance().deletePost(bean.getContentid());
                    adapter.remove(position);
                } else {
                    ActionDialog dialog = new ActionDialog();
                    dialog.setContentIdAndCallBack(bean.getContentid(), new ActionDialog.DialogListener() {
                        @Override
                        public void deleteItem() {
                            adapter.remove(position);
                        }
                    }, false);
                    dialog.show(getChildFragmentManager(), "ActionDialog");
                }
                break;
            //分享的弹窗
            case R.id.base_moment_share_iv:
                CommonHttpRequest.getInstance().getShareUrl(bean.getContentid(), new JsonCallback<BaseResponseBean<ShareUrlBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<ShareUrlBean>> response) {
                        String shareUrl = response.body().getData().getAz_url();
                        boolean videoType = LikeAndUnlikeUtil.isVideoType(bean.getContenttype());
                        WebShareBean webBean = ShareHelper.getInstance().createWebBean(videoType, true, bean.getIscollection()
                                , VideoAndFileUtils.getVideoUrl(bean.getContenturllist()), bean.getContentid());
                        ShareDialog shareDialog = ShareDialog.newInstance(webBean);
                        shareDialog.setListener(new ShareDialog.ShareMediaCallBack() {
                            @Override
                            public void callback(WebShareBean webBean) {
                                //该对象已经含有平台参数
                                String cover = VideoAndFileUtils.getCover(bean.getContenturllist());
                                WebShareBean shareBeanByDetail = ShareHelper.getInstance().getShareBeanByDetail(webBean, bean, cover, shareUrl);
                                ShareHelper.getInstance().shareWeb(shareBeanByDetail);
                            }

                            @Override
                            public void colloection(boolean isCollection) {
                                // TODO: 2018/11/16 可能还需要回调给列表
                                bean.setIscollection(isCollection ? "1" : "0");
                                ToastUtil.showShort(isCollection ? "收藏成功" : "取消收藏成功");
                            }
                        });
                        shareDialog.show(getChildFragmentManager(), getTag());
                    }
                });
                break;
            case R.id.expand_text_view:
                if (BaseConfig.MOMENTS_TYPE_WEB.equals(bean.getContenttype())) {
                    CommentUrlBean webList = VideoAndFileUtils.getWebList(bean.getContenturllist());
                    WebActivity.openWeb("web", webList.info, false, null);
                } else {
                    HelperForStartActivity.openContentDetail(bean, false);
                }
                break;
            case R.id.base_moment_comment:
                HelperForStartActivity.openContentDetail(bean, true);
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        // TODO: 2018/11/13 web 类型没有详情,直接跳web页面
        MomentsDataBean bean = (MomentsDataBean) adapter.getData().get(position);
        if (BaseConfig.MOMENTS_TYPE_WEB.equals(bean.getContenttype())) {
            CommentUrlBean webList = VideoAndFileUtils.getWebList(bean.getContenturllist());
            WebActivity.openWeb("web", webList.info, false, null);
        } else {
            HelperForStartActivity.openContentDetail(bean, false);
        }
    }

    /**
     * 因为推荐内容不一定是满20条的,所以全局放开
     *
     * @return
     */
    @Override
    public int getPageSize() {
        return 1;
    }

    /**
     * 用于给首页的刷新按钮刷新调用
     */
    @Override
    public void refreshDate() {
        if (mRvContent != null) {
            mRvContent.smoothScrollToPosition(0);
        }
        MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getNetWorkDate(DateState.refresh_state);
            }
        }, 200);
    }

    @Override
    public boolean isNeedLazyLoadDate() {
        return true;
    }
}
