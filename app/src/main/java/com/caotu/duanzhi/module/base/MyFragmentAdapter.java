package com.caotu.duanzhi.module.base;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录页面适配器,用FragmentStatePagerAdapter 省去editText 制空操作
 * adapter 的区别
 * link {https://www.cnblogs.com/lianghui66/p/3607091.html}
 */
public class MyFragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> mFragments = new ArrayList<>();

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

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }
}

