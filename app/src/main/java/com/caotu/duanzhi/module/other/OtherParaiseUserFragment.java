package com.caotu.duanzhi.module.other;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.DataTransformUtils;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.Http.bean.UserFocusBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.mine.adapter.FocusAdapter;
import com.caotu.duanzhi.utils.DevicesUtils;
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
public class OtherParaiseUserFragment extends BaseStateFragment<UserBean> implements BaseQuickAdapter.OnItemClickListener {

    String noteId;
    boolean isMe;
    private int count;

    @Override
    protected int getLayoutRes() {
        return R.layout.layout_refresh_with_title;
    }

    @Override
    protected void initView(View inflate) {
        super.initView(inflate);
        View backView = inflate.findViewById(R.id.iv_back);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null) {
                    getActivity().finish();
                }
            }
        });
        ViewGroup viewGroup = (ViewGroup) backView.getParent();
        viewGroup.setPadding(0, DevicesUtils.getStatusBarHeight(getActivity()), 0, 0);
    }

    @Override
    protected BaseQuickAdapter getAdapter() {
        return new FocusAdapter();
    }

    /**
     * 设置数据,关键参数:用户id,和是否是本人(UI相关)
     */
    public void setDate(String Id, boolean isMine, int friendCount) {
        noteId = Id;
        isMe = isMine;
        count = friendCount;
    }

    @Override
    protected void initViewListener() {
        adapter.setOnItemClickListener(this);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.layout_like_footer_view, mRvContent, false);
        TextView text = rootView.findViewById(R.id.tv_footer);
        adapter.setLoadMoreView(new MyListMoreView());
        if (count > 10) {
            text.setText(String.format(Locale.CHINA, "等%d个人", count));
            adapter.setFooterView(rootView);
        }
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        Map<String, String> map = CommonHttpRequest.getInstance().getHashMapParams();
        map.put("pageno", "" + position);
        map.put("pagesize", "20");
        map.put("noteid", noteId);

        OkGo.<BaseResponseBean<UserFocusBean>>post(HttpApi.USERLIST)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<UserFocusBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<UserFocusBean>> response) {
                        List<UserFocusBean.RowsBean> rows = response.body().getData().getRows();
                        List<UserBean> beans = DataTransformUtils.getMyFansDataBean(rows, isMe, true);
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
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        UserBean content = (UserBean) adapter.getData().get(position);
        HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, content.userid);
    }
}