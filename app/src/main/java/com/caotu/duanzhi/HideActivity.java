package com.caotu.duanzhi;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.umeng.commonsdk.statistics.common.DeviceConfig;

/**
 * 隐藏给测试用,线上不开放
 */
public class HideActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private TextView deviceInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_layout);
        initView();
    }

    public void save(View view) {
        MySpUtils.putInt(MySpUtils.sp_test_http, httpType);
        MySpUtils.putInt(MySpUtils.sp_test_name, nameType);
        ToastUtil.showShort("保存成功,请退出APP后重新进生效");
        finish();
    }

    private void initView() {
        RadioGroup mRadioHttp = findViewById(R.id.radio_http);
        RadioGroup mRadioAppName = findViewById(R.id.radio_app_name);
        mRadioHttp.setOnCheckedChangeListener(this);
        mRadioAppName.setOnCheckedChangeListener(this);
        int anInt = MySpUtils.getInt(MySpUtils.sp_test_http, 0);
        mRadioHttp.check(anInt == 0 ? R.id.http_test : R.id.http_online);
        int name = MySpUtils.getInt(MySpUtils.sp_test_name, 0);
        mRadioAppName.check(name == 0 ? R.id.name_duanzi : R.id.name_duanyou);
        deviceInfo = findViewById(R.id.device_info);
    }

    int httpType = 0;
    int nameType = 0;

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int id = group.getId();
        switch (id) {
            case R.id.radio_http:
                if (checkedId == R.id.http_online) {
                    httpType = 1;
                } else if (checkedId == R.id.http_test) {
                    httpType = 0;
                }
                break;
            case R.id.radio_app_name:
                if (checkedId == R.id.name_duanyou) {
                    nameType = 1;
                } else if (checkedId == R.id.name_duanzi) {
                    nameType = 0;
                }
                break;
        }
    }

    public static String[] getTestDeviceInfo(Context context) {
        String[] deviceInfo = new String[2];
        try {
            if (context != null) {
                deviceInfo[0] = DeviceConfig.getDeviceIdForGeneral(context);
                deviceInfo[1] = DeviceConfig.getMac(context);
            }
        } catch (Exception e) {
        }
        return deviceInfo;
    }

    public void getInfo(View view) {
        //  添加测试设备的时候需要添加
        String[] testDeviceInfo = getTestDeviceInfo(this);
        String de = "{\"device_id\":\"862565041453522\",\"mac\":\"b4:cd:27:59:9f:45\"}";
        deviceInfo.setText("{\"device_id\":" + testDeviceInfo[0] + ",\"mac\":" + testDeviceInfo[1] + "}");
    }
}
