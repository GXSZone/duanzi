package com.caotu.duanzhi.module;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.home.MainActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final MyApplication instance = MyApplication.getInstance();
        instance.getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(instance.getRunningActivity(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
    }
}
