package com.caotu.duanzhi.module.home;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.base.MyFragmentAdapter;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.mine.MineFragment;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.dialog.HomeProgressDialog;
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
    private ImageView refreshBt;

    @Override
    protected void initView() {
        //借用极光的权限请求,省事
        JPushManager.getInstance().requestPermission(this);
        MainBottomLayout bottomLayout = findViewById(R.id.my_tab_bottom);
        slipViewPager = findViewById(R.id.home_viewpager);
        refreshBt = findViewById(R.id.iv_refresh);
        slipViewPager.setSlipping(false);
        bottomLayout.setListener(this);
        bottomLayout.bindViewPager(slipViewPager);
        initFragment();
        presenter = new MainPresenter();
        presenter.create(this);
        EventBus.getDefault().register(this);
        presenter.requestVersion();
        refreshBt.setOnClickListener(v -> {
            if (homeFragment != null) {
                refreshBt.animate().rotationBy(360 * 3).setDuration(1000)
                        .setInterpolator(new AccelerateDecelerateInterpolator());
                homeFragment.refreshDate();
            }
        });
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
                refreshBt.setVisibility(View.VISIBLE);
                break;
            case 1:
                if (LoginHelp.isLoginAndSkipLogin()) {
                    HelperForStartActivity.openPublish();
                }
                break;
            case 2:
                if (LoginHelp.isLoginAndSkipLogin()) {
                    slipViewPager.setCurrentItem(1, false);
                    refreshBt.setVisibility(View.GONE);
                }
                break;
            default:
                break;
        }
    }

    HomeProgressDialog dialog;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusObject eventBusObject) {
        int code = eventBusObject.getCode();
        switch (code) {
            case EventBusCode.LOGIN_OUT:
                slipViewPager.setCurrentItem(0, false);
                break;
            case EventBusCode.LOGIN:
                if (mineFragment != null) {
                    mineFragment.fetchData();
                }
                break;
            case EventBusCode.PUBLISH:
                switch (eventBusObject.getMsg()) {
                    case EventBusCode.pb_start:
                        if (dialog == null) {
                            dialog = new HomeProgressDialog(this);
                            dialog.show();
                        }
                        break;
                    case EventBusCode.pb_success:
                        // TODO: 2018/11/17 插入列表第一条
                        if (homeFragment != null) {
                            MomentsDataBean dataBean = (MomentsDataBean) eventBusObject.getObj();
                            homeFragment.addPublishDate(dataBean);
                        }
                        if (!this.isFinishing() && !this.isDestroyed()) {
                            try {
                                dialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case EventBusCode.pb_error:
                        ToastUtil.showShort("发布失败");
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
