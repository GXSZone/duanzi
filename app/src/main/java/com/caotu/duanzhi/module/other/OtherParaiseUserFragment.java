package com.caotu.duanzhi.module.other;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.Http.bean.UserFansBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.mine.adapter.FocusAdapter;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.view.MyListMoreView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/5
 * @describe TODO
 */
public class OtherParaiseUserFragment extends BaseStateFragment<ThemeBean> implements BaseQuickAdapter.OnItemClickListener {

    String noteId;
    boolean isMe;
    private View rootView;
    private TextView text;

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new FocusAdapter(null);
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
        adapter.setOnItemClickListener(this);
        rootView = LayoutInflater.from(getContext()).inflate(R.layout.layout_like_footer_view, mRvContent, false);
        text = rootView.findViewById(R.id.tv_footer);
        adapter.setLoadMoreView(new MyListMoreView());

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
                        if (beans != null && rows.size() > 10) {
                            text.setText(String.format(Locale.CHINA, "等%d个人", rows.size()));
                            adapter.setFooterView(rootView);
                        }
                        setDate(load_more, beans);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<UserFansBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ThemeBean content = (ThemeBean) adapter.getData().get(position);
        HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, content.getUserId());
    }
}