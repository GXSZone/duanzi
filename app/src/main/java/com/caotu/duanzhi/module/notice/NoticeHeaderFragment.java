package com.caotu.duanzhi.module.notice;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MessageDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/5
 * @describe TODO
 */
public class NoticeHeaderFragment extends BaseStateFragment<MessageDataBean.RowsBean> {
    String mType;

    @Override
    protected BaseQuickAdapter getAdapter() {
        BaseQuickAdapter adapter;
        switch (mType) {
            case HelperForStartActivity.KEY_NOTICE_COMMENT:
                adapter = new NoticeFollowAdapter();
                break;
            case HelperForStartActivity.KEY_NOTICE_FOLLOW:
                adapter = "3";
                break;
            case HelperForStartActivity.KEY_NOTICE_LIKE:
                adapter = "5";
                break;
            default:
                adapter = "4";
                break;
        }
        return adapter;
    }


    /**
     * 设置数据,关键参数:用户id,和是否是本人(UI相关)
     */
    public void setDate(String type) {
        mType = type;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", "20");
        map.put("notetype", mType);
        OkGo.<BaseResponseBean<MessageDataBean>>post(HttpApi.NOTICE_OF_ME)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<MessageDataBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<MessageDataBean>> response) {
                        MessageDataBean data = response.body().getData();
                        setDate(load_more, data.rows);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<MessageDataBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_tongzhi;
    }

    @Override
    public String getEmptyText() {
        return "空空如也，快去和段友们互动吧";
    }
}