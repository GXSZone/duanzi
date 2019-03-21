package com.caotu.duanzhi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioGroup;

import com.caotu.duanzhi.utils.ToastUtil;

/**
 * 指纹识别 代码参考:https://guolin.blog.csdn.net/article/details/81450114
 */
public class HideActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {


    private RadioGroup mRadioHttp;
    private RadioGroup mRadioAppName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hide_layout);
        initView();
    }


    public void save(View view) {

    }

    private void initView() {
        mRadioHttp = (RadioGroup) findViewById(R.id.radio_http);
        mRadioAppName = (RadioGroup) findViewById(R.id.radio_app_name);
        mRadioHttp.setOnCheckedChangeListener(this);
        mRadioAppName.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        int id = group.getId();
        switch (id) {
            case R.id.radio_http:
                if (checkedId == R.id.http_online) {
                    ToastUtil.showShort("线上环境");
                } else if (checkedId == R.id.http_test) {
                    ToastUtil.showShort("测试环境");
                }
                break;
            case R.id.radio_app_name:
                if (checkedId == R.id.name_duanyou) {
                    ToastUtil.showShort("内含段友");
                } else if (checkedId == R.id.name_duanzi) {
                    ToastUtil.showShort("内含段子");
                }
                break;

        }
    }
}
