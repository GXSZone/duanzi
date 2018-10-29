package com.caotu.duanzhi.module.home.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.module.login.LoginHelp;

import cn.jpush.android.api.JPushInterface;

public class MainActivity extends AppCompatActivity {
    String download_url = "https://ctkj-1256675270.file.myqcloud.com/toutu_2.0.5.apk";
    private TextView viewById;
    private TextView rxtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        JPushInterface.requestPermission(this);
    }


    public void change_icon(View view) {
        LoginHelp.isLoginAndSkipLogin();

//        //3e6e148bca1e486cb6f34bfb8b564878
//        //13c37c494f254d4f8af51836d6b47b98
//        CommonHttpRequest.getInstance()
//                .<String>requestLikeOrUnlike("13c37c494f254d4f8af51836d6b47b98", "13c37c494f254d4f8af51836d6b47b98", true, new JsonCallback<BaseResponseBean<String>>() {
//                            @Override
//                            public void onSuccess(Response<BaseResponseBean<String>> response) {
//                                Log.i("okgoLwqiu", "onSuccess: ");
//                            }
//
//                            @Override
//                            public void onError(Response<BaseResponseBean<String>> response) {
//                                response.getException().getMessage();
//                                super.onError(response);
//                            }
//                        }
//                );
    }
}
