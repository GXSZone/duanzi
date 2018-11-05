package com.caotu.duanzhi.module.mine.fragment;

import com.caotu.duanzhi.Http.bean.ThemeBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.base.BaseStateFragment;
import com.caotu.duanzhi.module.mine.adapter.FocusAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe 通知, 粉丝, 关注都可以用这个页面, 只要处理好关注状态就好
 */
public class FocusTypeFragment extends BaseStateFragment<ThemeBean> {

    private FocusAdapter focusAdapter;
    String mUserId;
    boolean isMe;

    @Override
    protected BaseQuickAdapter getAdapter() {
        focusAdapter = new FocusAdapter(null);
        return focusAdapter;
    }

    /**
     * 设置数据,关键参数:用户id,和是否是本人(UI相关)
     */
    public void setDate(String userId,boolean isMine) {
        mUserId = userId;
        isMe = isMine;
    }

    @Override
    protected void getNetWorkDate(int load_more) {

    }

    @Override
    public int getEmptyImage() {
        return R.mipmap.no_fans;
    }

    @Override
    public String getEmptyText() {
        if (isMe){
            return "你的粉丝还在路上";
        }else {
            return "他的粉丝还在路上";
        }
    }
}
