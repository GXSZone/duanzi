package com.caotu.duanzhi.module.mine.fragment;

import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.CommentBaseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.mine.CommentAdapter;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe TODO
 */
public class MyCommentFragment extends BaseStateFragment<CommentBaseBean.RowsBean> implements BaseQuickAdapter.OnItemClickListener {
    @Override
    protected BaseQuickAdapter getAdapter() {
        return new CommentAdapter();
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        //position已在父类处理
        map.put("pageno", "" + position);
        map.put("pagesize", pageSize);
        OkGo.<BaseResponseBean<CommentBaseBean>>
                post(HttpApi.USER_MY_COMMENT)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<CommentBaseBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<CommentBaseBean>> response) {
                        setDate(load_more, response.body().getData().getRows());
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<CommentBaseBean>> response) {
                        errorLoad();
                        super.onError(response);
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
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        CommentBaseBean.RowsBean bean = (CommentBaseBean.RowsBean) adapter.getData().get(position);
        //0_正常 1_已删除 2_审核中
        if ("1".equals(bean.getContentstatus())) {
            ToastUtil.showShort("该资源已被删除");
            return;
        }
        MomentsDataBean beanComment = DataTransformUtils.getBeanComment(bean.getContent());
        HelperForStartActivity.openContentDetail(beanComment, true);
    }
}
