package com.caotu.duanzhi.module.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.caotu.duanzhi.ContextProvider;
import com.caotu.duanzhi.HideActivity;
import com.caotu.duanzhi.Http.CommonHttpRequest;
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
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.view.dialog.BaseIOSDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cookie.store.CookieStore;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.UpgradeInfo;

import okhttp3.HttpUrl;

public class SettingActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView cacheSize;
    private TextView text_size;
    private TextView teenager;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_settinng;
    }

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_click_user_agreement).setOnClickListener(this);
        TextView mTvVersion = findViewById(R.id.tv_version);

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

        Switch collectionMode = findViewById(R.id.switch_show_collection);
        collectionMode.setChecked(MySpUtils.getBoolean(MySpUtils.SP_COLLECTION_SHOW, true));
        collectionMode.setOnCheckedChangeListener(this);

        View noticeSetting = findViewById(R.id.tv_click_notice_setting);
        noticeSetting.setOnClickListener(this);
        View pswSetting = findViewById(R.id.tv_click_psw_setting);
        pswSetting.setOnClickListener(this);
        View loginOut = findViewById(R.id.tv_click_login_out);
        loginOut.setOnClickListener(this);
        findViewById(R.id.tv_click_community_convention).setOnClickListener(this);
        View checkVersion = findViewById(R.id.rl_check_update);
        checkVersion.setOnClickListener(this);
        if (!LoginHelp.isLogin()) {
            noticeSetting.setVisibility(View.GONE);
            pswSetting.setVisibility(View.GONE);
            loginOut.setVisibility(View.GONE);
            ViewGroup parent = (ViewGroup) collectionMode.getParent();
            parent.setVisibility(View.GONE);

        }
        mTvVersion.setText(String.format("当前版本%s\nAll Rights Reserved By %s", DevicesUtils.getVerName(), BaseConfig.appName));
        ((TextView) findViewById(R.id.tv_version_msg)).setText(DevicesUtils.getVerName());


        findViewById(R.id.rl_text_size).setOnClickListener(this);
        text_size = findViewById(R.id.tv_text_size);
        String textSize = "中号";
        float aFloat = MySpUtils.getFloat(MySpUtils.SP_TEXT_SIZE);
        if (aFloat == 18) {
            textSize = "大号";
        } else if (aFloat == 14) {
            textSize = "小号";
        }
        text_size.setText(textSize);
        findViewById(R.id.rl_click_teenager_mode).setOnClickListener(this);
        teenager = findViewById(R.id.tv_teenager_mode);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        MySpUtils.putBoolean(MySpUtils.SP_ENTER_SETTING, true);
//    }

    @Override
    protected void onResume() {
        super.onResume();
        UpgradeInfo upgradeInfo = Beta.getUpgradeInfo();
        // TODO: 2019-05-27 这里判断检查更新的小红点问题
        View redView = findViewById(R.id.view_red);
        redView.setVisibility(upgradeInfo == null ? View.INVISIBLE : View.VISIBLE);
        //为了登陆后获取值的准确性,如果是在当前页面唤起登录因为有接口请求所以有延迟,就不用eventbus了,麻烦
        teenager.postDelayed(() -> {
            if (CommonHttpRequest.teenagerIsOpen) {
                teenager.setText("已开启");
            } else {
                teenager.setText("未开启");
            }
        }, 500);

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
                UmengHelper.event(UmengStatisticsKeyIds.checkUpdate);
                Beta.checkUpgrade();
                break;
            case R.id.tv_click_psw_setting:
                UmengHelper.event(UmengStatisticsKeyIds.set_password);
                HelperForStartActivity.openBindPhoneOrPsw(BindPhoneAndForgetPwdActivity.SETTING_PWD);
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
            case R.id.rl_text_size:
                //弹窗选择字号
                float aFloat = MySpUtils.getFloat(MySpUtils.SP_TEXT_SIZE);
                UmengHelper.event(UmengStatisticsKeyIds.text_size_switch);
                new AlertDialog.Builder(this)
                        .setSingleChoiceItems(BaseConfig.TEXT_SIZE, getCheckItem(aFloat), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String reportType = BaseConfig.TEXT_SIZE[which];
                                text_size.setText(reportType);
                                float tranlate = tranlate(reportType);
                                MySpUtils.putFloat(MySpUtils.SP_TEXT_SIZE, tranlate);
                                EventBusHelp.sendChangeTextSize(tranlate);
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.rl_click_teenager_mode:
                if (LoginHelp.isLoginAndSkipLogin()) {
                    HelperForStartActivity.openTeenager(CommonHttpRequest.teenagerIsOpen,
                            CommonHttpRequest.teenagerPsd);
                }
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
                ContextProvider.get().setBrightness(isChecked);
                MySpUtils.putBoolean(MySpUtils.SP_EYE_MODE, isChecked);
                //一种需要activity重启的方法实现夜间模式,上面的那种实现系统会重启,更有保障,结果一样
//                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case R.id.video_auto_replay_mode:
                if (isChecked) {
                    UmengHelper.event(UmengStatisticsKeyIds.video_replay_switch);
                }
                MySpUtils.setReplaySwitch(isChecked);
                break;
            case R.id.switch_show_collection:
                if (isChecked) {
                    UmengHelper.event(UmengStatisticsKeyIds.switch_collection);
                }
                CommonHttpRequest.getInstance().changeSwitchBySetting(isChecked);
                break;
            default:
                break;
        }
    }

    /**
     * 弹窗的默认选中项翻译
     *
     * @param aFloat
     * @return
     */
    private int getCheckItem(float aFloat) {
        int size = 1;
        if (aFloat == 18) {
            size = 2;
        } else if (aFloat == 14) {
            size = 0;
        }
        return size;
    }

    float tranlate(String size) {
        float textSize = 16;
        if (TextUtils.equals("大号", size)) {
            textSize = 18;
        } else if (TextUtils.equals("小号", size)) {
            textSize = 14;
        }
        return textSize;
    }
}
