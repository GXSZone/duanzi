package com.caotu.duanzhi.module.other;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;

public class TestActivity extends AppCompatActivity {

    private Button mBtChange;

    private RadioGroup mRadioGroup;
    boolean isCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        initView();
    }

    private void initView() {

        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO: 2018/11/8 默认是-1
                if (mRadioGroup.getCheckedRadioButtonId() == -1) {
                    ToastUtil.showShort("    " + group.getCheckedRadioButtonId());
                }else {
                    ToastUtil.showShort("有未选中的");
                }
            }
        });


    }

    public void changeState(View view) {
        isCheck = !isCheck;
        MySpUtils.putBoolean(MySpUtils.SP_ISLOGIN, isCheck);
        ToastUtil.showShort(isCheck ? "已登录" : "未登录");
//        mRadioGroup.clearCheck();
    }
}
