package com.caotu.duanzhi.module.mine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.DataCleanManager;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.store.CookieStore;

import okhttp3.HttpUrl;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private TextView cacheSize;

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_click_user_agreement).setOnClickListener(this);
        TextView mTvVersion = findViewById(R.id.tv_version);
        findViewById(R.id.tv_click_login_out).setOnClickListener(this);
        cacheSize = findViewById(R.id.tv_cache);
        String totalCacheSize = null;
        try {
            totalCacheSize = DataCleanManager.getTotalCacheSize(MyApplication.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
        cacheSize.setText(TextUtils.isEmpty(totalCacheSize) ? "0M" : totalCacheSize);
        findViewById(R.id.rl_clear_cache).setOnClickListener(this);


        Switch button = findViewById(R.id.wifi_auto_play);
        boolean wifi_auto_play = MySpUtils.getBoolean(MySpUtils.SP_WIFI_PLAY, true);
        button.setChecked(wifi_auto_play);
        button.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MySpUtils.putBoolean(MySpUtils.SP_WIFI_PLAY, isChecked);
            EventBusHelp.sendVideoIsAutoPlay();
        });

        Switch trafficButton = findViewById(R.id.liuliang_auto_play);
        boolean traffic_auto_play = MySpUtils.getBoolean(MySpUtils.SP_TRAFFIC_PLAY, false);
        trafficButton.setChecked(traffic_auto_play);
        trafficButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                ToastUtil.showShort("初始化会不会调用");
            MySpUtils.putBoolean(MySpUtils.SP_TRAFFIC_PLAY, isChecked);
            EventBusHelp.sendVideoIsAutoPlay();
        });

        Switch eyeMode = findViewById(R.id.eye_mode);
        boolean isEyeMode = MySpUtils.getBoolean(MySpUtils.SP_EYE_MODE, false);
        eyeMode.setChecked(isEyeMode);
        eyeMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MySpUtils.putBoolean(MySpUtils.SP_EYE_MODE, isChecked);
            EventBusHelp.sendNightMode(isChecked);
        });

        boolean hasEnter = MySpUtils.getBoolean(MySpUtils.SP_ENTER_SETTING, false);
        findViewById(R.id.iv_play_new_tip).setVisibility(!hasEnter ? View.VISIBLE : View.GONE);
        findViewById(R.id.iv_eye_new_tip).setVisibility(!hasEnter ? View.VISIBLE : View.GONE);

        findViewById(R.id.tv_click_community_convention).setOnClickListener(this);

        mTvVersion.setText(String.format("当前版本%s" + "\n" + "All Rights Reserved By 内含段子", DevicesUtils.getVerName()));
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_settinng;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.tv_click_community_convention:
                WebActivity.openWeb("社区公约", BaseConfig.community_convention, false);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_click_user_agreement:
                WebActivity.openWeb("用户隐私协议", BaseConfig.KEY_USER_AGREEMENT, false);
                break;
            case R.id.tv_click_login_out:
                // 创建退出对话框
                AlertDialog alertDialog = new AlertDialog.Builder(this)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            logout();
                            dialog.dismiss();
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                        .setTitle("提示")
                        .setMessage("确定要退出吗?")
                        .create();
                alertDialog.show();
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setTextColor(DevicesUtils.getColor(R.color.color_FF8787));
                alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE)
                        .setTextColor(Color.BLACK);
                break;
            case R.id.rl_clear_cache:
                // 创建退出对话框
                AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            DataCleanManager.clearAllCache(MyApplication.getInstance());
                            cacheSize.setText("0K");
                            dialog.dismiss();
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                        .setTitle("提示")
                        .setMessage("确定清除缓存吗?")
                        .create();
                alertDialog2.show();
                alertDialog2.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setTextColor(DevicesUtils.getColor(R.color.color_FF8787));
                alertDialog2.getButton(DialogInterface.BUTTON_NEGATIVE)
                        .setTextColor(Color.BLACK);
                break;
        }
    }

    public void logout() {
        MySpUtils.clearLogingType();
        JPushManager.getInstance().loginOutClearAlias();
        EventBusHelp.sendLoginOut();
        // TODO: 2018/11/12 清除本地cookie
        try {
            //#3902 java.io.EOFException   com.android.okhttp.okio.Buffer.clear(Buffer.java:764)
            HttpUrl httpUrl = HttpUrl.parse(BaseConfig.baseApi);
            CookieStore cookieStore = OkGo.getInstance().getCookieJar().getCookieStore();
            cookieStore.removeCookie(httpUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        MySpUtils.putBoolean(MySpUtils.SP_ENTER_SETTING, true);
        super.onDestroy();
    }
}
