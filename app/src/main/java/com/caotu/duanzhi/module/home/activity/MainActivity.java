package com.caotu.duanzhi.module.home.activity;

import android.content.Intent;
import android.view.View;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.publish.PublishActivity;
import com.caotu.duanzhi.view.widget.MainBottomLayout;

public class MainActivity extends BaseActivity implements MainBottomLayout.BottomClickListener {

    @Override
    protected void initView() {
        JPushManager.getInstance().requestPermission(this);
        MainBottomLayout bottomLayout = findViewById(R.id.my_tab_bottom);
        bottomLayout.setListener(this);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_main;
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

    @Override
    public void tabSelector(int index) {
        switch (index) {
            case 0:

                break;
            case 1:
                Intent intent = new Intent(this, PublishActivity.class);
                startActivity(intent);
                break;
            case 2:
                break;
            default:
                break;
        }
    }
}
