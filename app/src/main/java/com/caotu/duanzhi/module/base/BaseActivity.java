package com.caotu.duanzhi.module.base;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.ColorInt;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.detail.DetailActivity;
import com.caotu.duanzhi.module.detail_scroll.ContentNewDetailActivity;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.other.OtherActivity;
import com.caotu.duanzhi.other.HandleBackUtil;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ScreenShotListenManager;
import com.caotu.duanzhi.utils.ToastUtil;
import com.dueeeke.videoplayer.player.BaseIjkVideoView;
import com.dueeeke.videoplayer.player.VideoViewManager;

public abstract class BaseActivity extends AppCompatActivity {

    /**
     * https://www.jianshu.com/p/d586c3406cfb
     * <p>
     * onPostResume 回调可以拿一些控件的宽高信息
     * <p>
     * onUserLeaveHint
     */
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        initView();
        doStart();
    }

    private ScreenShotListenManager manager;

    private void doStart() {
        manager = ScreenShotListenManager.newInstance(this);
        manager.setListener(imagePath -> {
                    // do something
                    UmengHelper.event(UmengStatisticsKeyIds.screenshots);
                    Log.i("@@@@", "onShot: " + imagePath);
                }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        new AsyncLayoutInflater(this)  新鲜玩意,异步加载布局
//                .inflate(getLayoutView(), null, (view, resid, parent) -> setContentView(view));
        setContentView(getLayoutView());
        if (MySpUtils.getBoolean(MySpUtils.SP_EYE_MODE, false)) {
            setBrightness(true);
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
        //给主页全屏使用,特殊标记
        if (this instanceof MainActivity ||
                this instanceof OtherActivity ||
                this instanceof ContentNewDetailActivity
                || this instanceof DetailActivity) return;
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
        //注册广播接收器，给广播接收器添加可以接收的广播Action
        if (filter == null) {
            filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        }
        registerReceiver(mReceiver, filter);
        if (manager != null) {
            manager.startListen();
        }
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
        BaseIjkVideoView videoView = VideoViewManager.instance().getCurrentVideoPlayer();
        if (videoView == null) return;
        VideoViewManager.instance().stopPlayback();
        VideoViewManager.instance().releaseVideoPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (manager != null) {
            manager.stopListen();
        }
        //简单了当的省去判断是否已经注册广播的操作
        try {
            unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        releaseAllVideo();
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

    public void turnToFragment(Fragment fragment, @IdRes int fragmentLayout) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(fragmentLayout, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void closeSoftKeyboard() {
        View view = getWindow().peekDecorView();
        if (view == null) return;
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

    public void fullScreen(Activity activity) {
        //5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int uiVisibility = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(uiVisibility | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
//        if (activity instanceof DetailActivity || activity instanceof ContentNewDetailActivity) {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                final View contentView = activity.getWindow().getDecorView();
//                contentView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        boolean hasCut = false;
//                        WindowInsets windowInsets = contentView.getRootWindowInsets();
//                        if (windowInsets != null) {
//                            DisplayCutout cutout = windowInsets.getDisplayCutout();
//                            if (cutout != null) {
//                                List<Rect> rects = cutout.getBoundingRects();
//                                hasCut = AppUtil.listHasDate(rects);
//                            }
//                            adaptCutoutAboveAndroidP(activity, hasCut);
//                        } else {
//                            adaptCutoutAboveAndroidP(activity, false);
//                        }
//                    }
//                });
//            } else {
//                adaptCutoutAboveAndroidP(activity, DevicesUtils.chinaPhone(activity));
//            }
//        }
    }

//    private void adaptCutoutAboveAndroidP(Activity activity, boolean b) {
//        if (activity instanceof IStatusBar) {
//            ((IStatusBar) activity).hasCut(b);
//        }
//    }
}
