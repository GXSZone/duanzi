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
import com.caotu.duanzhi.view.widget.ExpandTextView;

public class TestActivity extends AppCompatActivity {

    String text = "Java 是一种跨平台的、解释型语言，Java 源代码编译成中间”字节码”存储于 class 文件中。Java 字节码中包括了很多源代码信息，如变量名、方法名，很容易被反编译成 Java 源代码。所以需要对java代码进行混淆。混淆就是对发布出去的程序进行重新组织和处理，混淆器将代码中的所有变量、函数、类的名称变为简短的英文字母代号，反编译后将难以阅读。同时混淆的时候会遍历代码以发现没有被调用的代码，从而将其在打包成apk时剔除，最终一定程度上降低了apk的大小，比如编译后 jar 文件体积大约能减少25% ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        initView();
    }

    private void initView() {
        ExpandTextView textView = findViewById(R.id.expand_text);
        textView.setText(text);
    }

    public void changeState(View view) {

    }
}
