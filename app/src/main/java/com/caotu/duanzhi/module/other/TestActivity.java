package com.caotu.duanzhi.module.other;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;

public class TestActivity extends AppCompatActivity {

    private Button mBtChange;

    private RadioGroup mRadioGroup;
    boolean isCheck = false;
    private View viewById;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        initView();
    }

    private void initView() {

        mRadioGroup = (RadioGroup) findViewById(R.id.radio_group);
        RadioButton radio1 = findViewById(R.id.radio1);
        radio1.setVisibility(View.GONE);
        this.viewById = findViewById(R.id.base_moment_spl_like_iv);
//        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                // TODO: 2018/11/8 默认是-1
//                if (mRadioGroup.getCheckedRadioButtonId() == -1) {
//                    ToastUtil.showShort("    " + group.getCheckedRadioButtonId());
//                } else {
//                    ToastUtil.showShort("有未选中的");
//                }
//            }
//        });

        RadioButton viewById = findViewById(R.id.radio2);
//        viewById.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                boolean has = isChecked;
//            }
//        });
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewById.setChecked(!viewById.isChecked());

            }
        });


    }

    public void changeState(View view) {
        isCheck = !isCheck;
        viewById.setEnabled(isCheck);
        MySpUtils.putBoolean(MySpUtils.SP_ISLOGIN, isCheck);
        ToastUtil.showShort(isCheck ? "已登录" : "未登录");
//        mRadioGroup.clearCheck();
    }
}
