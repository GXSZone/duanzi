package com.caotu.duanzhi.module.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutView());
        initView();
    }

    protected abstract void initView();

    protected abstract @LayoutRes
    int getLayoutView();

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    /**
     * 用于保证字体大小不随系统改变
     *
     * @return
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        res.updateConfiguration(configuration, res.getDisplayMetrics());
        return res;
    }

    public void turnToFragment(Bundle bundle, Fragment fragment, @IdRes int fragmentLayout) {
        if (fragment == null) {
            return;
        }
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentLayout, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }


    public void AddStackFragment(Bundle bundle, Fragment fragment, @IdRes int fragmentLayout) {
        if (fragment == null) {
            return;
        }
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentLayout, fragment);
        // TODO: 2018/10/30 如果添加到返回栈会影响返回键的功能
        fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.commitAllowingStateLoss();
    }
}
