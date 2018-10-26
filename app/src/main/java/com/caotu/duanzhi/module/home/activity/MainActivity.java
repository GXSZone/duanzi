package com.caotu.duanzhi.module.home.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.R;

public class MainActivity extends AppCompatActivity {
    String download_url = "https://ctkj-1256675270.file.myqcloud.com/toutu_2.0.5.apk";
    private TextView viewById;
    private TextView rxtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewById = findViewById(R.id.tv_bottom);
        rxtext = findViewById(R.id.rxtext);
    }

    public void download_apk(View view) {

    }

    public void change_icon(View view) {
        viewById.setSelected(!viewById.isSelected());

        rxtext.setPressed(!rxtext.isPressed());
    }
}
