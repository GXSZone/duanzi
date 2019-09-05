package com.caotu.duanzhi.module.home;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.Http.bean.MomentsDataBean;
import com.caotu.duanzhi.Http.bean.NoticeBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.base.MyFragmentAdapter;
import com.caotu.duanzhi.module.detail.ILoadMore;
import com.caotu.duanzhi.module.detail_scroll.DetailGetLoadMoreDate;
import com.caotu.duanzhi.module.discover.DiscoverFragment;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.mine.MineFragment;
import com.caotu.duanzhi.module.notice.NoticeFragment;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.LikeAndUnlikeUtil;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NotificationUtil;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.dialog.BaseIOSDialog;
import com.caotu.duanzhi.view.dialog.HomeProgressDialog;
import com.caotu.duanzhi.view.dialog.NotifyEnableDialog;
import com.caotu.duanzhi.view.widget.MainBottomLayout;
import com.caotu.duanzhi.view.widget.SlipViewPager;
import com.dueeeke.videoplayer.player.VideoViewManager;
import com.lzy.okgo.model.Response;
import com.tencent.bugly.beta.Beta;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements MainBottomLayout.BottomClickListener, DetailGetLoadMoreDate {
    SlipViewPager slipViewPager;
    private MainHomeNewFragment homeFragment;
    private MainBottomLayout bottomLayout;
    private View statusBar;


    @Override
    protected void initView() {
        //借用极光的权限请求,省事

        bottomLayout = findViewById(R.id.my_tab_bottom);
        slipViewPager = findViewById(R.id.home_viewpager);
        fullScreen(this);
        statusBar = findViewById(R.id.view_dynamic_status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            statusBar.setBackgroundColor(DevicesUtils.getColor(R.color.white));
        } else {
            statusBar.setBackgroundColor(DevicesUtils.getColor(R.color.color_status_bar));
        }

        ViewGroup.LayoutParams layoutParams = statusBar.getLayoutParams();
        layoutParams.height = DevicesUtils.getStatusBarHeight(MainActivity.this);
        statusBar.setLayoutParams(layoutParams);

        slipViewPager.setSlipping(false);
        bottomLayout.setListener(this);
        bottomLayout.bindViewPager(slipViewPager);
        initFragment();

        requestVersion();
        checkNotifyIsOpen();
        getIntentDate();
        checkMyPermission();
    }

    private void checkMyPermission() {
        if (MySpUtils.getBoolean(MySpUtils.SP_PERMISSION_SHOW, false)) return;
        BaseIOSDialog dialog = new BaseIOSDialog(this, new BaseIOSDialog.OnClickListener() {
            @Override
            public void okAction() {
                JPushManager.getInstance().requestPermission(MainActivity.this);
            }

            @Override
            public void cancelAction() {
                JPushManager.getInstance().requestPermission(MainActivity.this);
            }
        });
        dialog.setOkText("同意并继续")
                .setTitleByTipText(true)
                .setTitleText(BaseConfig.permission_title)
                .show();
        MySpUtils.putBoolean(MySpUtils.SP_PERMISSION_SHOW, true);
    }

    /**
     * 这个是H5 打开APP的操作
     */
    private void getIntentDate() {
        Uri data = getIntent().getData();
        if (data != null) {
            String query = data.getQuery();
            ToastUtil.showShort(query);
            String type = data.getQueryParameter("type");
            String url = data.getQueryParameter("url");
        }
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

    @Override
    protected int getLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRunnable == null) {
            mRunnable = new MyRunnable(this);
            mNoticeHandler.postDelayed(mRunnable, 1000);
        }
    }

    public void stopHandler() {
        mNoticeHandler.removeCallbacks(mRunnable);
        mRunnable = null;
    }

    MyRunnable mRunnable;

    /**
     * 防止内存泄露的写法
     */
    private static class MyRunnable implements Runnable {
        WeakReference<MainActivity> weakReference;

        public MyRunnable(MainActivity activity) {
            if (weakReference == null) {
                weakReference = new WeakReference<>(activity);
            }
        }

        @Override
        public void run() {
            //只有登录状态下才去请求该接口
            if (LoginHelp.isLogin()) {
                if (weakReference.get() != null) {
                    weakReference.get().requestNotice();
                }
            }
            if (weakReference.get() != null) {
                weakReference.get().mNoticeHandler.postDelayed(this, 1000 * 15);
            }
        }
    }

    Handler mNoticeHandler = MyApplication.getInstance().getHandler();

    /**
     * 供消息页面调用修改小红点数量
     *
     * @param count
     */
    public void changeBottomRed(int count) {
        if (bottomLayout != null) {
            redCount = redCount - count;
            bottomLayout.showRed(redCount);
        }
    }

    public int redCount;

    public void requestNotice() {
        CommonHttpRequest.getInstance().requestNoticeCount(new JsonCallback<BaseResponseBean<NoticeBean>>() {
            @Override
            public void onSuccess(Response<BaseResponseBean<NoticeBean>> response) {
                NoticeBean bean = response.body().getData();
                try {
                    int callCount = Integer.parseInt(bean.call);
                    int goodCount = Integer.parseInt(bean.good);
                    int commentCount = Integer.parseInt(bean.comment);
                    int followCount = Integer.parseInt(bean.follow);
                    int noteCount = Integer.parseInt(bean.note);
                    redCount = callCount + goodCount + commentCount + followCount + noteCount;
                    if (redCount > 0) {
                        bottomLayout.showRed(redCount);
                        bottomTabTip();
                    } else {
                        bottomLayout.showRed(0);
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

    private void bottomTabTip() {
        if (!MyApplication.redNotice && bottomLayout != null) {
            //这种方式更解耦
            ViewGroup parent = (ViewGroup) bottomLayout.getChildAt(0);
            View noticeView = parent.getChildAt(3);
            LikeAndUnlikeUtil.showNoticeTip(noticeView);
            MyApplication.redNotice = true;
        }
    }


    @Override
    public void tabPublish() {
        if (isPublish) {
            ToastUtil.showShort("正在发布中,请稍等后再试");
            return;
        }
        HelperForStartActivity.openPublish(bottomLayout);
    }

    @Override
    public void isFullScreen(boolean yes) {
        if (statusBar != null) {
            statusBar.setVisibility(yes ? View.GONE : View.VISIBLE);
        }
    }

    HomeProgressDialog dialog;
    boolean isPublish = false;

    public boolean isPublishing() {
        return isPublish;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusObject eventBusObject) {
        if (mFragments == null) return;
        if (eventBusObject.getCode() == EventBusCode.LOGIN) {
            for (Fragment mFragment : mFragments) {
                if (mFragment instanceof ILoginEvent) {
                    ((ILoginEvent) mFragment).login();
                }
            }
            //登录成功获取用户相关配置信息
            CommonHttpRequest.getInstance().getShareUrl();
        }
        if (eventBusObject.getCode() == EventBusCode.LOGIN_OUT) {
            for (Fragment mFragment : mFragments) {
                if (mFragment instanceof ILoginEvent) {
                    ((ILoginEvent) mFragment).loginOut();
                }
            }
            bottomLayout.showRed(0);
            if (homeFragment != null) {
                CommonHttpRequest.getInstance().setTeenagerDateByUerInfo(false, "");
                homeFragment.setTeenagerMode(false);
            }
        }
        if (eventBusObject.getCode() == EventBusCode.TEENAGER_MODE) {
            if (homeFragment != null) {
                homeFragment.setTeenagerMode((boolean) eventBusObject.getObj());
            }
        }
        if (eventBusObject.getCode() != EventBusCode.PUBLISH) return;

        switch (eventBusObject.getMsg()) {
            case EventBusCode.pb_start:
                if (dialog == null) {
                    dialog = new HomeProgressDialog(this);
                }
                dialog.show();
                isPublish = true;
                if (slipViewPager.getCurrentItem() != 0) {
                    slipViewPager.setCurrentItem(0, false);
                }
                break;
            case EventBusCode.pb_success:
                isPublish = false;
                if (homeFragment != null) {
                    MomentsDataBean dataBean = (MomentsDataBean) eventBusObject.getObj();
                    homeFragment.addPublishDate(dataBean);
                }
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
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
            case EventBusCode.pb_progress:
                if (dialog != null && dialog.isShowing()) {
                    int progress = (int) eventBusObject.getObj();
                    dialog.changeProgress(progress);
                }
                break;
            default:
                try {
                    dialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isPublish = false;
                break;
        }
    }

    /**
     * 直接用bugly的更新
     */
    public void requestVersion() {
        Beta.checkUpgrade(false, false);
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
            if (VideoViewManager.instance().onBackPressed()) {
                return true;
            }
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                ToastUtil.showShort("再按一次退出程序");
                firstTime = secondTime;
            } else {
                stopHandler();
                finish();
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 这两个方法用于跳转详情统计用
     * 获取main 底部的4个fragment 对象
     *
     * @return
     */
    public int getCurrentTab() {
        return slipViewPager.getCurrentItem();
    }

    /**
     * 获取首页fragment的viewpager
     *
     * @return
     */
    public int getHomeFragment() {
        return homeFragment.getViewpagerCurrentIndex();
    }

    @Override
    public void getLoadMoreDate(ILoadMore callBack) {
        if (homeFragment != null) {
            homeFragment.getLoadMoreDate(callBack);
        }
    }

    @Override
    public void tabSelector(int index) {
        releaseAllVideo();
    }

    List<Fragment> mFragments;

    private void initFragment() {
        mFragments = new ArrayList<>();
        homeFragment = new MainHomeNewFragment();
        mFragments.add(homeFragment);
//        mFragments.add(new FindFragment());
        mFragments.add(new DiscoverFragment());
        mFragments.add(new NoticeFragment());
        mFragments.add(new MineFragment());
        slipViewPager.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), mFragments));
        slipViewPager.setOffscreenPageLimit(3);
    }

    /**
     * 再次点击做刷新操作
     *
     * @param index
     */
    @Override
    public void tabSelectorDouble(int index) {
        if (mFragments == null || index > mFragments.size() - 1) return;
        Fragment fragment = mFragments.get(index);
        if (fragment instanceof ITabRefresh) {
            ((ITabRefresh) fragment).refreshDateByTab();
        }
    }

    @Override
    public int getBarColor() {
        return -111;
    }
}
