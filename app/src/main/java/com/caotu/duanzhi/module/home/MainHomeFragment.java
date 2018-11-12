package com.caotu.duanzhi.module.home;

import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.MomentsNewAdapter;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.view.dialog.ActionDialog;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class MainHomeFragment extends BaseStateFragment<MomentsDataBean> implements BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener {


    @Override
    protected BaseQuickAdapter getAdapter() {
        return new MomentsNewAdapter();
    }

    @Override
    protected void initViewListener() {
        adapter.setOnItemChildClickListener(this);
        adapter.setOnItemClickListener(this);

    }

    @Override
    public int getPageSize() {
        return 6;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        HashMap<String, String> hashMapParams = CommonHttpRequest.getInstance().getHashMapParams();
        hashMapParams.put("uuid", DevicesUtils.getDeviceId(MyApplication.getInstance()));
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.MAIN_RECOMMEND_CONTENT)
                .upJson(new JSONObject(hashMapParams))
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        List<MomentsDataBean> rows = response.body().getData().getContentList();
                        setDate(load_more, rows);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<RedundantBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        MomentsDataBean bean = (MomentsDataBean) adapter.getData().get(position);
        switch (view.getId()) {
            //更多的操作的弹窗
            case R.id.item_iv_more_bt:
                ActionDialog dialog = new ActionDialog();
                dialog.setContentIdAndCallBack(bean.getContentid(), new ActionDialog.DialogListener() {
                    @Override
                    public void deleteItem() {
                        adapter.remove(position);
                    }
                });
                dialog.show(getChildFragmentManager(), "ActionDialog");
                break;
            //分享的弹窗
            case R.id.base_moment_share_iv:
                ShareDialog shareDialog = new ShareDialog();
                shareDialog.show(getChildFragmentManager(), "shareDialog");
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MomentsDataBean bean = (MomentsDataBean) adapter.getData().get(position);
        HelperForStartActivity.openContentDetail(bean.getContentid());

    }
}
