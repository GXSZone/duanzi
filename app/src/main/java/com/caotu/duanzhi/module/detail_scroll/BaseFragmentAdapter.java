package com.caotu.duanzhi.module.detail_scroll;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.caotu.duanzhi.module.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录页面适配器,用FragmentStatePagerAdapter 省去editText 制空操作
 */
public class BaseFragmentAdapter extends FragmentStatePagerAdapter {

    private List<BaseFragment> mFragments = new ArrayList<>();

    public BaseFragmentAdapter(FragmentManager fm, List<BaseFragment> fragmentList) {
        super(fm);
        if (fragmentList != null) {
            mFragments = fragmentList;
        }
    }

    public void changeFragment(List<BaseFragment> list) {
        if (list != null) {
            mFragments = list;
            notifyDataSetChanged();
        }
    }

//    @Override
//    public int getItemPosition(@NonNull Object object) {
//        return POSITION_NONE;
//    }

    @Override
    public Fragment getItem(int position) {
        return mFragments == null ? null : mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }

}

