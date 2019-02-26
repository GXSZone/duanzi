package com.caotu.duanzhi.module.other;

import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.Http.bean.UserFansBean;
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
public class OtherParaiseUserFragment extends BaseStateFragment<ThemeBean> implements BaseQuickAdapter.OnItemClickListener {

    private FocusAdapter focusAdapter;
    String noteId;
    boolean isMe;

    @Override
    protected BaseQuickAdapter getAdapter() {
        focusAdapter = new FocusAdapter(null);
        return focusAdapter;
    }

    /**
     * 设置数据,关键参数:用户id,和是否是本人(UI相关)
     */
    public void setDate(String Id, boolean isMine) {
        noteId = Id;
        isMe = isMine;
    }

    @Override
    protected void initViewListener() {
        if (focusAdapter != null) {
            focusAdapter.setOnItemClickListener(this);
            focusAdapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        }
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", "20");
        map.put("noteid", noteId);

        OkGo.<BaseResponseBean<UserFansBean>>post(HttpApi.USERLIST)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<UserFansBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<UserFansBean>> response) {
                        List<UserFansBean.RowsBean> rows = response.body().getData().getRows();
                        List<ThemeBean> beans = DataTransformUtils.getMyFansDataBean(rows, isMe);
                        setDate(load_more, beans);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<UserFansBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }
//该页面不可能为空
//    @Override
//    public int getEmptyImage() {
//        return R.mipmap.no_guanzhu;
//    }
//
//    @Override
//    public String getEmptyText() {
//        return "还没有关注任何人哦";
//    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ThemeBean content = (ThemeBean) adapter.getData().get(position);
        HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, content.getUserId());
    }
}