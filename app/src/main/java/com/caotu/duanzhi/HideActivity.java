package com.caotu.duanzhi;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;

import weige.umenglib.UmengLibHelper;

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
        int checkId;
        switch (name) {
            case 0:
                checkId = R.id.name_duanzi;
                break;
            case 1:
                checkId = R.id.name_duanyou;
                break;
            default:
                checkId = R.id.name_neihan;
                break;
        }
        mRadioAppName.check(checkId);
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
                } else if (checkedId == R.id.name_neihan) {
                    nameType = 2;
                }
                break;
        }
    }


    public void getInfo(View view) {
        //  添加测试设备的时候需要添加
        String[] testDeviceInfo = UmengLibHelper.getTestDeviceInfo(this);
        String de = "{\"device_id\":\"862565041453522\",\"mac\":\"b4:cd:27:59:9f:45\"}";
        deviceInfo.setText("{\"device_id\":" + testDeviceInfo[0] + ",\"mac\":" + testDeviceInfo[1] + "}");
    }
}
