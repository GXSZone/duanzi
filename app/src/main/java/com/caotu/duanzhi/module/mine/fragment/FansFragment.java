package com.caotu.duanzhi.module.mine.fragment;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.Http.bean.UserFansBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.mine.adapter.FansAdapter;
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
public class FansFragment extends BaseStateFragment<ThemeBean> {

    private FansAdapter focusAdapter;
    String mUserId;
    boolean isMe;

    @Override
    protected BaseQuickAdapter getAdapter() {
        focusAdapter = new FansAdapter(null);
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
    protected void getNetWorkDate(int load_more) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", "20");
        map.put("userid", mUserId);
        OkGo.<BaseResponseBean<UserFansBean>>post(HttpApi.USER_MY_FANS)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<UserFansBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<UserFansBean>> response) {
                        List<UserFansBean.RowsBean> rows = response.body().getData().getRows();
                        List<ThemeBean> myFansDataBean = DataTransformUtils.getMyFansDataBean(rows, isMe);
                        setDate(load_more,myFansDataBean);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<UserFansBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_fans;
    }

    @Override
    public String getEmptyText() {
        if (isMe) {
            return "你的粉丝还在路上";
        } else {
            return "他的粉丝还在路上";
        }
    }
}