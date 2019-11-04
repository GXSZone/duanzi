package com.caotu.duanzhi.module.detail_scroll;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.caotu.duanzhi.module.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 登录页面适配器,用FragmentStatePagerAdapter 省去editText 制空操作
 * FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT 控制生命周期的回调
 *
 * @author mac
 */
public class BaseFragmentAdapter extends FragmentStatePagerAdapter {

    private List<BaseFragment> mFragments = new ArrayList<>();

    @SuppressLint("WrongConstant")
    public BaseFragmentAdapter(FragmentManager fm, List<BaseFragment> fragmentList) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments == null ? 0 : mFragments.size();
    }

    /**
     * 复写该方法是为了解决 FragmentStatePagerAdapter fragment太多的话抛异常会
     * android.os.TransactionTooLargeException
     * data parcel size 571860 bytes
     *
     * @return
     */
    @Override
    public Parcelable saveState() {
        Bundle bundle = (Bundle) super.saveState();
        if (bundle != null) {
            bundle.putParcelableArray("states", null); // Never maintain any states from the base class, just null it out
        }
        return bundle;
    }
}

