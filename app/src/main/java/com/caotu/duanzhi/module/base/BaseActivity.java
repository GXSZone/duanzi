package com.caotu.duanzhi.module.base;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.caotu.duanzhi.Http.bean.EventBusObject;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusCode;
import com.caotu.duanzhi.other.HandleBackUtil;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.jzvd.Jzvd;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutView());
        if (MySpUtils.getBoolean(MySpUtils.SP_EYE_MODE, false)) {
            setBrightness(true);
        }
        EventBus.getDefault().register(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 全局处理页面的夜间模式
     *
     * @param eventBusObject
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventBus(EventBusObject eventBusObject) {
        if (EventBusCode.EYE_MODE == eventBusObject.getCode()) {
            boolean isNight = (boolean) eventBusObject.getObj();
            setBrightness(isNight);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        setStatusBar(getBarColor());
    }

    public int getBarColor() {
        return Color.WHITE;
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        setStatusBar(getBarColor());
    }

    /**
     * Android 6.0 以上设置状态栏颜色
     */
    protected void setStatusBar(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 设置状态栏底色颜色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(color);

            // 如果亮色，设置状态栏文字为黑色
            if (isLightColor(color)) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        } else {
            getWindow().setStatusBarColor(DevicesUtils.getColor(R.color.color_status_bar));
        }
    }

    /**
     * 判断颜色是不是亮色
     *
     * @param color
     * @return
     * @from https://stackoverflow.com/questions/24260853/check-if-color-is-dark-or-light-in-android
     */
    private boolean isLightColor(@ColorInt int color) {
        return ColorUtils.calculateLuminance(color) >= 0.5;
    }

    protected abstract void initView();

    protected abstract @LayoutRes
    int getLayoutView();

    private IntentFilter filter;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        //注册广播接收器，给广播接收器添加可以接收的广播Action
        if (filter == null) {
            filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        }
        registerReceiver(mReceiver, filter);
    }


    /**
     * 耳机的监听
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(action)) {
                BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                if (BluetoothProfile.STATE_DISCONNECTED == adapter.getProfileConnectionState(BluetoothProfile.HEADSET)) {
                    releaseAllVideo();
                }
                //屏幕锁定时，可以暂停视频播放或做其他事情
//                doSecondthing();
            } else if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
                //耳机拔出时，可以暂停视频播放或做其他事情
                ToastUtil.showShort("耳机拔出");
                releaseAllVideo();
            }
        }
    };

    /**
     * 处理耳机的线控问题,监听耳机的暂停播放按钮
     *
     * @param keyCode
     * @param event
     * @return
     */

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_HEADSETHOOK == keyCode && event.getRepeatCount() == 0) {
            //短按
            releaseAllVideo();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 只是暂停播放而不是释放播放资源
     */
    public void releaseAllVideo() {
        Jzvd.releaseAllVideos();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        unregisterReceiver(mReceiver);
        Jzvd.releaseAllVideos();
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

    public void closeSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }

    public void showKeyboard(EditText text) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(text, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 统一处理返回键问题
     */
    @Override
    public void onBackPressed() {
        if (!HandleBackUtil.handleBackPress(this)) {
            super.onBackPressed();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置屏幕亮度
     *
     * @param isNightMode
     */
    public void setBrightness(boolean isNightMode) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        if (isNightMode) {
            if (getSystemBrightness() < 0.1f) {
                return;
            }
            lp.screenBrightness = 0.1f;
        } else {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        }
        getWindow().setAttributes(lp);
    }

    /**
     * 获得系统亮度
     */
    private float getSystemBrightness() {
        float systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            systemBrightness = (systemBrightness + 0.0f) / 255f;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }
}
