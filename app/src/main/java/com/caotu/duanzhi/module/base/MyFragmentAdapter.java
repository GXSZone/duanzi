package com.caotu.duanzhi.module.base;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * 登录页面适配器,用FragmentStatePagerAdapter 省去editText 制空操作
 * adapter 的区别
 * link {https://www.cnblogs.com/lianghui66/p/3607091.html}
 */
public class MyFragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments;

    public MyFragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        if (fragmentList != null) {
            mFragments = fragmentList;
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments == null ? null : mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }

    /**
     * 这后面两个方法重写是为了APP在后台被系统杀死后起来的一些bug问题,全部重新构建,不保留任何东西
     * 之前出过点击刷新按钮无效的问题因为在后台rv对象报空了
     *
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}

