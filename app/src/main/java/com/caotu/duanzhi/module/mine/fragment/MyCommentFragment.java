package com.caotu.duanzhi.module.mine.fragment;

import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.mine.BaseBigTitleActivity;
import com.caotu.duanzhi.module.other.UserCommentFragment;
import com.caotu.duanzhi.utils.DevicesUtils;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe 我的评论页面
 */
public class MyCommentFragment extends UserCommentFragment {

    int mScrollY = 0;
    int headerHeight = 200;
    private TextView titleView;

    @Override
    protected int getLayoutRes() {
        return R.layout.layout_base_states_view;
    }

    @Override
    protected void initViewListener() {
        super.initViewListener();
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
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                mScrollY += dy;
                float scrollY = Math.min(headerHeight, mScrollY);
                float percent = scrollY / headerHeight;
                percent = Math.min(1, percent);
                if (titleView != null) {
                    titleView.setAlpha(percent);
                }
            }
        });
    }
}
