package com.caotu.duanzhi.module.mine;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.MySpUtils;

public class NoticeSettingActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_click_user_agreement).setOnClickListener(this);
        TextView mTvVersion = findViewById(R.id.tv_version);
        findViewById(R.id.tv_click_login_out).setOnClickListener(this);
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
        findViewById(R.id.iv_notice_tip).setVisibility(hasEnter ? View.GONE : View.VISIBLE);
        findViewById(R.id.tv_click_notice_setting).setOnClickListener(this);

        findViewById(R.id.tv_click_community_convention).setOnClickListener(this);
        mTvVersion.setText(String.format("当前版本%s\nAll Rights Reserved By %s", DevicesUtils.getVerName(), BaseConfig.appName));
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

                break;
            case R.id.tv_click_community_convention:
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_click_user_agreement:
                break;
            case R.id.tv_click_login_out:


                break;
            case R.id.rl_clear_cache:

                break;
        }
    }



    @Override
    protected void onDestroy() {
        MySpUtils.putBoolean(MySpUtils.SP_ENTER_SETTING, true);
        super.onDestroy();
    }
}
