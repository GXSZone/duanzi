package com.caotu.duanzhi.module.home;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.NoticeBean;
import com.caotu.duanzhi.Http.bean.VersionBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.base.MyFragmentAdapter;
import com.caotu.duanzhi.module.login.LoginAndRegisterActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.mine.MineFragment;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.dialog.HomeProgressDialog;
import com.caotu.duanzhi.view.dialog.VersionDialog;
import com.caotu.duanzhi.view.widget.MainBottomLayout;
import com.caotu.duanzhi.view.widget.SlipViewPager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.jzvd.Jzvd;

public class MainActivity extends BaseActivity implements MainBottomLayout.BottomClickListener {
    SlipViewPager slipViewPager;
    private MainHomeNewFragment homeFragment;
    private MineFragment mineFragment;
    private List<Fragment> mFragments;
    private ImageView refreshBt;
    private MainBottomLayout bottomLayout;

    @Override
    protected void initView() {
        //借用极光的权限请求,省事
        JPushManager.getInstance().requestPermission(this);
        bottomLayout = findViewById(R.id.my_tab_bottom);
        slipViewPager = findViewById(R.id.home_viewpager);
        refreshBt = findViewById(R.id.iv_refresh);
        slipViewPager.setSlipping(false);
        bottomLayout.setListener(this);
        bottomLayout.bindViewPager(slipViewPager);
        initFragment();
        EventBus.getDefault().register(this);
        refreshBt.setOnClickListener(v -> {
            if (homeFragment != null) {
                refreshBt.animate().rotationBy(360 * 3).setDuration(1000)
                        .setInterpolator(new AccelerateDecelerateInterpolator());
                homeFragment.refreshDate();
            }
        });
        requestVersion();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    private void initFragment() {
        mFragments = new ArrayList<>();
        homeFragment = new MainHomeNewFragment();
        mFragments.add(homeFragment);
        mineFragment = new MineFragment();
        mFragments.add(mineFragment);
        slipViewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), mFragments));
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_main;
    }

    Timer timer;

    @Override
    protected void onStart() {
        super.onStart();
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    //只有登录状态下才去请求该接口
                    if (LoginHelp.isLogin()) {
                        requestNotice();
                    }
                }
            }, 100, 15 * 1000);
        }
    }

    private void requestNotice() {
        CommonHttpRequest.getInstance().requestNoticeCount(new JsonCallback<BaseResponseBean<NoticeBean>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<NoticeBean>> response) {
                NoticeBean bean = response.body().getData();
                try {
                    int goodCount = Integer.parseInt(bean.good);
                    int commentCount = Integer.parseInt(bean.comment);
                    int followCount = Integer.parseInt(bean.follow);
                    int noteCount = Integer.parseInt(bean.note);
                    if (goodCount + commentCount + followCount + noteCount > 0) {
                        bottomLayout.showRed(true);
                    } else {
                        bottomLayout.showRed(false);
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void clearRed() {
        bottomLayout.showRed(false);
    }

    int defaultTab = 0;

    @Override
    public void tabSelector(int index) {
        switch (index) {
            case 0:
                defaultTab = 0;
                slipViewPager.setCurrentItem(0, false);
                refreshBt.setVisibility(View.VISIBLE);
                break;
            case 1:
//                Intent intent = new Intent(this, TestActivity.class);
//                startActivity(intent);
                if (isPublish) {
                    ToastUtil.showShort("正在发布中,请稍等后再试");
                    return;
                }

                if (LoginHelp.isLogin()) {
                    HelperForStartActivity.openPublish();
                } else {
                    defaultTab = 1;
                    LoginHelp.goLogin();
                }

                break;
            case 2:
                if (LoginHelp.isLogin()) {
                    // TODO: 2018/11/27  viewpager里的fragment回调有问题,暂时这么解决
                    Jzvd.releaseAllVideos();
                    slipViewPager.setCurrentItem(1, false);
                    refreshBt.setVisibility(View.GONE);
                } else {
                    defaultTab = 2;
                    LoginHelp.goLogin();
                }

                break;
            default:
                defaultTab = 0;
                break;
        }
    }

    HomeProgressDialog dialog;
    boolean isPublish = false;

    public boolean isPublishing() {
        return isPublish;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusObject eventBusObject) {
        int code = eventBusObject.getCode();
        switch (code) {
            case EventBusCode.LOGIN_OUT:
                defaultTab = 0;
                refreshBt.setVisibility(View.VISIBLE);
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
                        }
                        dialog.show();
                        isPublish = true;
                        break;
                    case EventBusCode.pb_success:
                        isPublish = false;
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
                        ToastUtil.showShort("发布成功");
                        break;
                    case EventBusCode.pb_error:
                        isPublish = false;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == LoginAndRegisterActivity.LOGIN_RESULT_CODE &&
                requestCode == LoginAndRegisterActivity.LOGIN_REQUEST_CODE) {
            if (defaultTab == 1) {
                HelperForStartActivity.openPublish();
                defaultTab = 0;
            } else if (defaultTab == 2) {
                Jzvd.releaseAllVideos();
                slipViewPager.setCurrentItem(1, false);
                refreshBt.setVisibility(View.GONE);
                defaultTab = 0;
            }
        }
    }

    public void requestVersion() {
        OkGo.<BaseResponseBean<VersionBean>>post(HttpApi.VERSION)
                .tag(this)
                .execute(new JsonCallback<BaseResponseBean<VersionBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<VersionBean>> response) {
                        VersionBean data = response.body().getData();
                        if (data.newestversionandroid.value.compareToIgnoreCase(
                                DevicesUtils.getVerName()) > 0) {
                            VersionDialog dialog = new VersionDialog(MyApplication.getInstance().getRunningActivity()
                                    , data);
                            dialog.show();
                        }
                    }
                });
    }
}
