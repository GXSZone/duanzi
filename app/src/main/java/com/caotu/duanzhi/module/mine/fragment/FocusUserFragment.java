package com.caotu.duanzhi.module.mine.fragment;

import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.Http.bean.UserFocusBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.mine.adapter.FocusAdapter;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/5
 * @describe TODO
 */
public class FocusUserFragment extends BaseStateFragment<ThemeBean> implements BaseQuickAdapter.OnItemClickListener {

    private FocusAdapter focusAdapter;
    String mUserId;
    boolean isMe;

    @Override
    protected BaseQuickAdapter getAdapter() {
        focusAdapter = new FocusAdapter(null);
        return focusAdapter;
    }

    /**
     * 设置数据,关键参数:用户id,和是否是本人(UI相关)
     */
    public void setDate(String userId, boolean isMine) {
        mUserId = userId;
        isMe = isMine;
    }

    @Override
    protected void initViewListener() {
        if (focusAdapter != null) {
            focusAdapter.setOnItemClickListener(this);
        }
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", "20");
        map.put("followtype", "2");
        map.put("userid", mUserId);

        OkGo.<BaseResponseBean<UserFocusBean>>post(HttpApi.USER_MY_FOCUS)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<UserFocusBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<UserFocusBean>> response) {
                        List<UserFocusBean.RowsBean> rows = response.body().getData().getRows();
                        List<ThemeBean> beans = DataTransformUtils.getMyFocusDataBean(rows, isMe, false);
                        setDate(load_more, beans);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<UserFocusBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_guanzhu;
    }

    @Override
    public String getEmptyText() {
        return "还没有关注任何人哦";
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ThemeBean content = (ThemeBean) adapter.getData().get(position);
        HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, content.getUserId());
    }
}