package com.caotu.duanzhi.module.mine.fragment;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.mine.BaseBigTitleActivity;
import com.caotu.duanzhi.module.other.UserCommentFragment;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe 我的评论页面
 */
public class MyCommentFragment extends UserCommentFragment {

    @Override
    protected int getLayoutRes() {
        return R.layout.layout_base_states_view;
    }

    @Override
    protected void initViewListener() {
        super.initViewListener();
        if (getActivity() instanceof BaseBigTitleActivity) {
            ((BaseBigTitleActivity) getActivity()).alphaTitleView(mRvContent, adapter);
        }
    }
}
