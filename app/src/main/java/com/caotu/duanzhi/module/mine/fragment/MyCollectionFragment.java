package com.caotu.duanzhi.module.mine.fragment;

import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.MomentsNewAdapter;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe TODO
 */
public class MyCollectionFragment extends BaseStateFragment<MomentsDataBean> implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener {
    @Override
    protected BaseQuickAdapter getAdapter() {
        return new MomentsNewAdapter();
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", pageSize);
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.COLLECTION)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        List<MomentsDataBean> rows = response.body().getData().getRows();
                        setDate(load_more, rows);
                    }
                });

    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_pinlun;
    }

    @Override
    public String getEmptyText() {
        //直接用string形式可以少一步IO流从xml读写
        return "下个神评就是你，快去评论吧";
    }

    @Override
    protected void initViewListener() {
        if (adapter != null) {
            adapter.setOnItemClickListener(this);
            adapter.setOnItemChildClickListener(this);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//        MomentsDataBean.RowsBean bean = (MomentsDataBean.RowsBean) adapter.getData().get(position);
////        //0_正常 1_已删除 2_审核中
////        if ("1".equals(bean.getContentstatus())) {
////            ToastUtil.showShort("该资源已被删除");
////            return;
////        }
//        HelperForStartActivity.openContentDetail(bean.getContentid());
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        // TODO: 2018/11/8 抽取到一个类里处理
        MomentsDataBean o = (MomentsDataBean) adapter.getData().get(position);

    }
}
