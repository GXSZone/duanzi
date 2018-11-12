package com.caotu.duanzhi.module.home;

import android.support.v4.app.Fragment;

import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.base.MyFragmentAdapter;
import com.caotu.duanzhi.module.mine.MineFragment;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.view.widget.MainBottomLayout;
import com.caotu.duanzhi.view.widget.SlipViewPager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements MainBottomLayout.BottomClickListener, IMainView {
    SlipViewPager slipViewPager;
    private MainHomeFragment homeFragment;
    private MineFragment mineFragment;
    private List<Fragment> mFragments;
    private MainPresenter presenter;

    @Override
    protected void initView() {
        //借用极光的权限请求,省事
        JPushManager.getInstance().requestPermission(this);
        MainBottomLayout bottomLayout = findViewById(R.id.my_tab_bottom);
        slipViewPager = findViewById(R.id.home_viewpager);
        slipViewPager.setSlipping(false);
        bottomLayout.setListener(this);
        bottomLayout.bindViewPager(slipViewPager);
        initFragment();
        presenter = new MainPresenter();
        presenter.create(this);
        EventBus.getDefault().register(this);
        presenter.requestVersion();
    }

    @Override
    protected void onDestroy() {
        presenter.destroy();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    private void initFragment() {
        mFragments = new ArrayList<>();
        homeFragment = new MainHomeFragment();
        mFragments.add(homeFragment);
        mineFragment = new MineFragment();
        mFragments.add(mineFragment);
        slipViewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), mFragments));
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    public void tabSelector(int index) {
        switch (index) {
            case 0:
                slipViewPager.setCurrentItem(0, false);
                break;
            case 1:
//                LoginHelp.isLoginAndSkipLogin();
                HelperForStartActivity.openPublish();
                break;
            case 2:
//                if (LoginHelp.isLoginAndSkipLogin()){
                    slipViewPager.setCurrentItem(1, false);
//                }
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusObject eventBusObject) {
        int code = eventBusObject.getCode();
        switch (code) {
            case EventBusCode.LOGIN_OUT:
                slipViewPager.setCurrentItem(0, false);
                break;
            case EventBusCode.PUBLISH:
                switch (eventBusObject.getMsg()) {
                    case EventBusCode.pb_start:
                        break;
                    case EventBusCode.pb_success:
                        break;
                    case EventBusCode.pb_error:
                        break;
                    default:
                        break;
                }

                if (slipViewPager.getCurrentItem() != 0) {
                    slipViewPager.setCurrentItem(0, false);
                }
                break;

            default:
                break;
        }
    }


}
