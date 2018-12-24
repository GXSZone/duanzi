package com.caotu.duanzhi.module.home;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

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
import com.caotu.duanzhi.module.discover.DiscoverFragment;
import com.caotu.duanzhi.module.login.LoginAndRegisterActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.mine.MineFragment;
import com.caotu.duanzhi.module.notice.NoticeFragment;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NotificationUtil;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.dialog.HomeProgressDialog;
import com.caotu.duanzhi.view.dialog.NotifyEnableDialog;
import com.caotu.duanzhi.view.dialog.VersionDialog;
import com.caotu.duanzhi.view.widget.MainBottomLayout;
import com.caotu.duanzhi.view.widget.SlipViewPager;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

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

    private MainBottomLayout bottomLayout;

    @Override
    protected void initView() {
        //借用极光的权限请求,省事
        JPushManager.getInstance().requestPermission(this);
        bottomLayout = findViewById(R.id.my_tab_bottom);
        slipViewPager = findViewById(R.id.home_viewpager);

        slipViewPager.setSlipping(false);
        bottomLayout.setListener(this);
        bottomLayout.bindViewPager(slipViewPager);
        initFragment();

        requestVersion();
        checkNotifyIsOpen();
    }

    /**
     * 检查通知的开关是否打开
     */
    private void checkNotifyIsOpen() {
        //获取通知栏开关状态,关闭状态就不推了
        boolean notificationEnable = NotificationUtil.notificationEnable(this);
        boolean hasShowed = MySpUtils.getBoolean(MySpUtils.KEY_HAS_SHOWED_NOTIFY_DIALOG, false);
        if (!notificationEnable && !hasShowed) {
            NotifyEnableDialog dialog = new NotifyEnableDialog(this);
            dialog.show();
            MySpUtils.putBoolean(MySpUtils.KEY_HAS_SHOWED_NOTIFY_DIALOG, true);
        }
    }


    private void initFragment() {
        mFragments = new ArrayList<>();
        homeFragment = new MainHomeNewFragment();
        mFragments.add(homeFragment);
        mFragments.add(new DiscoverFragment());
        mFragments.add(new NoticeFragment());
        mineFragment = new MineFragment();
        mFragments.add(mineFragment);
        slipViewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), mFragments));
        slipViewPager.setOffscreenPageLimit(3);
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
            }, 100, 20 * 1000);
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

            @Override
            public void onError(Response<BaseResponseBean<NoticeBean>> response) {
//                super.onError(response);
            }
        });
    }

    public void clearRed() {
        if (bottomLayout != null) {
            bottomLayout.hideSettingTipRed();
        }
    }

    int defaultTab = 0;

    @Override
    public void tabSelector(int index) {
        switch (index) {
            //发现页面
            case 1:
                defaultTab = 1;
                slipViewPager.setCurrentItem(1, false);

                break;
            //通知页面
            case 2:
                if (LoginHelp.isLogin()) {
                    bottomLayout.showRed(false);
                    slipViewPager.setCurrentItem(2, false);
                } else {
                    defaultTab = 2;
                    LoginHelp.goLogin();
                }
                break;
            //我的页面
            case 3:
                if (LoginHelp.isLogin()) {
                    slipViewPager.setCurrentItem(3, false);
                } else {
                    defaultTab = 3;
                    LoginHelp.goLogin();
                }
                break;
            default:
                defaultTab = 0;
                slipViewPager.setCurrentItem(0, false);
                break;
        }
        Jzvd.releaseAllVideos();
    }

    @Override
    public void tabPublish() {
//        Intent intent = new Intent(this, TestActivity.class);
//        startActivity(intent);
        if (isPublish) {
            ToastUtil.showShort("正在发布中,请稍等后再试");
            return;
        }
        if (LoginHelp.isLogin()) {
            HelperForStartActivity.openPublish();
        } else {
            defaultTab = -1;
            LoginHelp.goLogin();
        }
    }

    HomeProgressDialog dialog;
    boolean isPublish = false;

    public boolean isPublishing() {
        return isPublish;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusObject eventBusObject) {
        super.getEventBus(eventBusObject);
        int code = eventBusObject.getCode();
        switch (code) {
            case EventBusCode.LOGIN_OUT:
                defaultTab = 0;
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
                        if (!this.isFinishing() && !this.isDestroyed()) {
                            try {
                                dialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        ToastUtil.showShort("发布失败");
                        break;
                    case EventBusCode.pb_cant_talk:
                        ToastUtil.showShort(eventBusObject.getTag());
                        if (!this.isFinishing() && !this.isDestroyed()) {
                            try {
                                dialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        isPublish = false;
                        break;
                    default:
                        if (!this.isFinishing() && !this.isDestroyed()) {
                            try {
                                dialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        isPublish = false;
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

    /**
     * 处理登陆成功之后的页面跳转
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == LoginAndRegisterActivity.LOGIN_RESULT_CODE &&
                requestCode == LoginAndRegisterActivity.LOGIN_REQUEST_CODE) {
            if (defaultTab == -1) {
                defaultTab = 0;
                // TODO: 2018/11/29 直接跳转绑定手机页面
                if (!MySpUtils.getBoolean(MySpUtils.SP_HAS_BIND_PHONE, false)) {
                    HelperForStartActivity.openBindPhone();
                    return;
                }
                HelperForStartActivity.openPublish();
            } else if (defaultTab == 3) {
                slipViewPager.setCurrentItem(3, false);
                defaultTab = 0;
            } else if (defaultTab == 2) {
                defaultTab = 0;
                bottomLayout.showRed(false);
                slipViewPager.setCurrentItem(2, false);
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

    private long firstTime;

    /**
     * 双击退出
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (Jzvd.backPress()) {
                return true;
            }
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                ToastUtil.showShort("再按一次退出程序");
                firstTime = secondTime;
            } else {
                if (timer != null) {
                    timer.cancel();
                }
//                moveTaskToBack(true);
                finish();
                System.exit(0);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 这两个方法用于跳转详情统计用
     *
     * @return
     */
    public int getCurrentTab() {
        return slipViewPager.getCurrentItem();
    }

    public int getHomeFragment() {
        return homeFragment.getViewpagerCurrentIndex();
    }

    public void getLoadMoreDate(ILoadMore callBack) {
        if (homeFragment != null) {
            homeFragment.getLoadMoreDate(callBack);
        }
    }
}
