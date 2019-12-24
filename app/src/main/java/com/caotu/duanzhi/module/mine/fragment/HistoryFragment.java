package com.caotu.duanzhi.module.mine.fragment;

import android.content.Context;

import androidx.annotation.NonNull;

import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.RealmBean;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseVideoFragment;
import com.caotu.duanzhi.module.mine.BaseBigTitleActivity;
import com.caotu.duanzhi.utils.AppUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.RealmHelper;
import com.caotu.duanzhi.view.dialog.BaseIOSDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe TODO
 */
public class HistoryFragment extends BaseVideoFragment {

    private List<RealmBean> sortedList;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        sortedList = RealmHelper.getSortedList();
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.layout_no_refresh;
    }

    /**
     * 删除浏览历史调用
     */
    public void clearHistory() {
        BaseIOSDialog dialog = new BaseIOSDialog(getActivity(), new BaseIOSDialog.SimpleClickAdapter() {
            @Override
            public void okAction() {
                RealmHelper.clearAll();
                MySpUtils.deleteKey(MySpUtils.SP_LOOK_HISTORY);
                setDate(DateState.init_state, null);
                sortedList = null;
            }
        });
        dialog.setTitleText("是否清空所有浏览历史记录？");
        dialog.show();
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        if (DateState.load_more != load_more) {
            if (!AppUtil.listHasDate(sortedList)) {
                setDate(load_more, null);
                return;
            }
        }
        int initIndex = (position - 1) * 10;
        int size = position * 10;
        ArrayList<String> request = new ArrayList<>(10);
        for (int i = initIndex; i < size; i++) {
            if (i <= sortedList.size() - 1) {
                request.add(sortedList.get(i).getContentId());
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("contentidlist", request);
//        String json = "{\"contentidlist\":" + request.toString() + "}";
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.HISTORY)
                .upJson(new JSONObject(map))
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
    protected void initViewListener() {
        super.initViewListener();
        if (getActivity() instanceof BaseBigTitleActivity) {
            ((BaseBigTitleActivity) getActivity()).getHistoryDelete().setOnClickListener(v -> clearHistory());
        }
    }

    @Override
    public String getEmptyText() {
        //直接用string形式可以少一步IO流从xml读写
        return "还没有浏览历史,快去逛逛吧";
    }
}
