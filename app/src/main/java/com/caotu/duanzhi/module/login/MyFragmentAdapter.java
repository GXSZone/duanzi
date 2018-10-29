package com.caotu.duanzhi.module.login;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录页面适配器,用FragmentStatePagerAdapter 省去editText 制空操作
 */
public class MyFragmentAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> mFragments = new ArrayList<>();

    public MyFragmentAdapter(FragmentManager fm) {
        super(fm);
        LoginNewFragment loginFragment = new LoginNewFragment();
        mFragments.add(loginFragment);
        RegistNewFragment registFragment = new RegistNewFragment();
        mFragments.add(registFragment);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

}

