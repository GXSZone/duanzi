package com.caotu.duanzhi.module.mine.fragment;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.DateState;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseVideoFragment;
import com.caotu.duanzhi.module.mine.BaseBigTitleActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe TODO
 */
public class HistoryFragment extends BaseVideoFragment {
    List<Map.Entry<String, Long>> list;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //费时可能需要放子线程
        Set<Map.Entry<String, Long>> entries = MyApplication.getInstance().getMap().entrySet();
        list = new ArrayList<>(entries);
        Collections.sort(list, (o1, o2) -> {
            //降序排序
            return o2.getValue().compareTo(o1.getValue());
        });

    }

    @Override
    public int getPageSize() {
        return 5;
    }

    @Override
    protected void getNetWorkDate(int load_more) {
        if (DateState.init_state == load_more || DateState.refresh_state == load_more) {
            if (list == null || list.size() == 0) {
                setDate(load_more, null);
                return;
            }
        }
        Map<String, Object> map = new HashMap<>();
        int initIndex = (position - 1) * 10;
        int size = position * 10 - 1;
        List<String> request = new ArrayList<>(10);
        for (int i = initIndex; i < size; i++) {
            if (i <= list.size() - 1) {
                request.add(list.get(i).getKey());
            }
        }
        map.put("contentidlist", request);
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.HISTORY)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        List<MomentsDataBean> rows = response.body().getData().getContentList();
                        setDate(load_more, rows);

                        //回调给滑动详情页数据
                        if (DateState.load_more == load_more && dateCallBack != null) {
                            dateCallBack.loadMoreDate(rows);
                            dateCallBack = null;
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<RedundantBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });

    }

    int mScrollY = 0;
    int headerHeight = 200;
    private TextView titleView;

    @Override
    protected void initViewListener() {
        super.initViewListener();
        titleView = null;
        if (getActivity() != null && getActivity() instanceof BaseBigTitleActivity) {
            titleView = ((BaseBigTitleActivity) getActivity()).getmText();
        }
        View inflate = LayoutInflater.from(mRvContent.getContext()).inflate(R.layout.layout_header_title, mRvContent, false);
        TextView mText = inflate.findViewById(R.id.tv_base_title);
        mText.setText("浏览历史");
        mText.post(() -> {
            Shader shader_horizontal = new LinearGradient(0, 0,
                    mText.getWidth(), 0,
                    DevicesUtils.getColor(R.color.color_FF8787),
                    DevicesUtils.getColor(R.color.color_FF698F),
                    Shader.TileMode.CLAMP);
            mText.getPaint().setShader(shader_horizontal);
        });
        adapter.setHeaderView(inflate);
        adapter.setHeaderAndEmpty(true);
        mRvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mScrollY += dy;
//                if (dy == 0 || mScrollY > headerHeight) return;
                float scrollY = Math.min(headerHeight, mScrollY);
                float percent = scrollY / headerHeight;
                percent = Math.min(1, percent);
                if (titleView != null) {
                    titleView.setAlpha(percent);
                }
            }
        });
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_tiezi;
    }

    @Override
    public String getEmptyText() {
        //直接用string形式可以少一步IO流从xml读写
        return "还没有浏览历史,快去逛逛吧";
    }
}
