package com.caotu.duanzhi.module.mine;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.caotu.duanzhi.HideActivity;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.login.BindPhoneAndForgetPwdActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DataCleanManager;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.dialog.BaseIOSDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.store.CookieStore;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import java.util.LinkedList;

import okhttp3.HttpUrl;

public class SettingActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView cacheSize;

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_click_user_agreement).setOnClickListener(this);
//        TextView mTvVersion = findViewById(R.id.tv_version);

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
        button.setOnCheckedChangeListener(this);


        Switch trafficButton = findViewById(R.id.liuliang_auto_play);
        boolean traffic_auto_play = MySpUtils.getBoolean(MySpUtils.SP_TRAFFIC_PLAY, false);
        trafficButton.setChecked(traffic_auto_play);
        trafficButton.setOnCheckedChangeListener(this);

        Switch eyeMode = findViewById(R.id.eye_mode);
        boolean isEyeMode = MySpUtils.getBoolean(MySpUtils.SP_EYE_MODE, false);
        eyeMode.setChecked(isEyeMode);
        eyeMode.setOnCheckedChangeListener(this);

        Switch videoAutoReplayMode = findViewById(R.id.video_auto_replay_mode);
        videoAutoReplayMode.setChecked(MySpUtils.getReplaySwitch());
        videoAutoReplayMode.setOnCheckedChangeListener(this);

        View noticeSetting = findViewById(R.id.tv_click_notice_setting);
        noticeSetting.setOnClickListener(this);
        View pswSetting = findViewById(R.id.tv_click_psw_setting);
        pswSetting.setOnClickListener(this);
        View loginOut = findViewById(R.id.tv_click_login_out);
        loginOut.setOnClickListener(this);
        findViewById(R.id.tv_click_community_convention).setOnClickListener(this);
        findViewById(R.id.rl_check_update).setOnClickListener(this);
        if (!LoginHelp.isLogin()) {
            noticeSetting.setVisibility(View.GONE);
            pswSetting.setVisibility(View.GONE);
            loginOut.setVisibility(View.GONE);
        }
//        mTvVersion.setText(String.format("当前版本%s\nAll Rights Reserved By %s", DevicesUtils.getVerName(), BaseConfig.appName));
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();
        if (upgradeInfo == null) {
            // TODO: 2019-05-27 这里判断检查更新的小红点问题

        }
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
            case R.id.tv_click_notice_setting:
                HelperForStartActivity.openNoticeSetting();
                break;
            case R.id.rl_check_update:
                Beta.checkUpgrade();
                break;
            case R.id.tv_click_psw_setting:
                //  第一层是绑定手机
                if (!MySpUtils.getBoolean(MySpUtils.SP_HAS_BIND_PHONE, false)) {
                    HelperForStartActivity.openBindPhoneOrPsw(BindPhoneAndForgetPwdActivity.BIND_TYPE);
                } else {
                    HelperForStartActivity.openBindPhoneOrPsw(BindPhoneAndForgetPwdActivity.SETTING_PWD);
                }
                break;
            case R.id.tv_click_community_convention:
                UmengHelper.event(UmengStatisticsKeyIds.community_onvention);
                WebActivity.openWeb("社区公约", BaseConfig.COMMUNITY_CONVENTION, false);
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_click_user_agreement:
                UmengHelper.event(UmengStatisticsKeyIds.user_agreement);
                WebActivity.openWeb("用户隐私协议", BaseConfig.KEY_USER_AGREEMENT, false);
                break;
            case R.id.tv_click_login_out:
                BaseIOSDialog baseIOSDialog = new BaseIOSDialog(this, new BaseIOSDialog.SimpleClickAdapter() {
                    @Override
                    public void okAction() {
                        UmengHelper.event(UmengStatisticsKeyIds.login_out);
                        logout();
                    }
                });
                baseIOSDialog.setTitleText("确定要退出吗?").show();
                break;
            case R.id.rl_clear_cache:
                BaseIOSDialog cacheDialog = new BaseIOSDialog(this, new BaseIOSDialog.SimpleClickAdapter() {
                    @Override
                    public void okAction() {
                        UmengHelper.event(UmengStatisticsKeyIds.clear_cache);
                        DataCleanManager.clearAllCache(MyApplication.getInstance());
                        cacheSize.setText("0K");
                    }
                });
                cacheDialog.setTitleText("确定清除缓存吗?").show();
                break;
        }
    }

    public void logout() {
//        //对应的用户文字水印文件也得删除,目前先保留,省事,反正用户也看不到,不然重新登录又得生成一遍
//        LanSongFileUtil.deleteFile(PathConfig.getUserImagePath());
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


    final static int COUNTS = 5;// 点击次数
    final static long DURATION = 1000;// 规定有效时间
    long[] mHits = new long[COUNTS];

    public void HttpChange(View view) {
        if (!BaseConfig.isTestMode) return;
        //每次点击时，数组向前移动一位
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        //为数组最后一位赋值
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - DURATION)) {
            mHits = new long[COUNTS];//重新初始化数组
            ToastUtil.showShort("连续点击了5次");
            startActivity(new Intent(this, HideActivity.class));
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.wifi_auto_play:
                UmengHelper.event(UmengStatisticsKeyIds.wifi_auto_play);
                MySpUtils.putBoolean(MySpUtils.SP_WIFI_PLAY, isChecked);
                EventBusHelp.sendVideoIsAutoPlay();
                break;
            case R.id.liuliang_auto_play:
                UmengHelper.event(UmengStatisticsKeyIds.mobile_auto_play);
                MySpUtils.putBoolean(MySpUtils.SP_TRAFFIC_PLAY, isChecked);
                EventBusHelp.sendVideoIsAutoPlay();
                break;
            case R.id.eye_mode:
                UmengHelper.event(UmengStatisticsKeyIds.eyecare);
                MySpUtils.putBoolean(MySpUtils.SP_EYE_MODE, isChecked);
                LinkedList<Activity> activities = MyApplication.activities;
                for (int i = activities.size() - 1; i >= 0; i--) {
                    Activity activity = activities.get(i);
                    if (activity instanceof BaseActivity) {
                        ((BaseActivity) activity).setBrightness(isChecked);
                    }
                }
                break;
            case R.id.video_auto_replay_mode:
                MySpUtils.setReplaySwitch(isChecked);
                break;
            default:
                break;
        }
    }
}
