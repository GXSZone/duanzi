package com.caotu.duanzhi.module.mine.fragment;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.RedundantBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseVideoFragment;
import com.caotu.duanzhi.module.mine.BaseBigTitleActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * @author mac
 * @日期: 2018/11/13
 * @describe 需要重写adapter
 */
public class MyPostFragment extends BaseVideoFragment {

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
        mText.setText(titleView.getText());
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
    protected void getNetWorkDate(int load_more) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("pageno", position);
        params.put("pagesize", pageSize);
//        params.put("userid", "");
        OkGo.<BaseResponseBean<RedundantBean>>post(HttpApi.USER_WORKSHOW)
                .upJson(new JSONObject(params))
                .execute(new JsonCallback<BaseResponseBean<RedundantBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RedundantBean>> response) {
                        setDate(load_more, response.body().getData().getRows());
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<RedundantBean>> response) {
                        errorLoad();
                        super.onError(response);
                    }
                });
    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_tiezi;
    }

    @Override
    public String getEmptyText() {
        return "不会发段子的土豪不是好逗比";
    }

//    @Override
//    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
//        if (view.getId() == R.id.item_iv_more_bt) {
//            // TODO: 2018/11/13 可能需要添加提醒
//            MomentsDataBean bean = (MomentsDataBean) adapter.getData().get(position);
//            Jzvd.releaseAllVideos();
//            CommonHttpRequest.getInstance().deletePost(bean.getContentid());
//            adapter.remove(position);
//        } else {
//            super.onItemChildClick(adapter, view, position);
//        }
//    }
}
