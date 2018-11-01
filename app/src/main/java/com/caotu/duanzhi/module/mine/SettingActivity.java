package com.caotu.duanzhi.module.mine;

import android.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.EventBusHelp;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.MySpUtils;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void initView() {
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_click_user_agreement).setOnClickListener(this);
        TextView mTvVersion = findViewById(R.id.tv_version);
        findViewById(R.id.tv_click_login_out).setOnClickListener(this);
        mTvVersion.setText(DevicesUtils.getVerName());
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
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_click_user_agreement:
                WebActivity.openWeb("用户隐私协议",WebActivity.KEY_USER_AGREEMENT,false,null);
                break;
            case R.id.tv_click_login_out:
                // 创建退出对话框
                new AlertDialog.Builder(this)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            logout();
                            dialog.dismiss();
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss())
                        .setTitle("提示")
                        .setMessage("确定要退出吗?")
                        .show();
                break;
        }
    }

    public void logout() {
        MySpUtils.clearLogingType();
        JPushManager.getInstance().loginOutClearAlias();
        EventBusHelp.sendLoginOut();
//        App.getInstance().getIsPariseMap().clear();
        finish();
    }
}
