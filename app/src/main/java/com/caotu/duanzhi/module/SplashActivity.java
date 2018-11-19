package com.caotu.duanzhi.module;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.json.JSONObject;

import java.util.HashMap;

public class SplashActivity extends BaseActivity {

    @Override
    protected void initView() {
        View skip = findViewById(R.id.iv_skip);
        skip.setOnClickListener(v -> {
            HashMap<String, String> params = CommonHttpRequest.getInstance().getHashMapParams();
            params.put("pagestr", "JUMP");
            OkGo.<String>post(HttpApi.COUNTNUMBER)
                    .upJson(new JSONObject(params))
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            //不关注结果
                        }
                    });
            goMain();
        });
        // TODO: 2018/11/19 false 直接跳过
        if (MySpUtils.getBoolean(MySpUtils.SP_ISFIRSTENTRY, true)) {
            skip.setVisibility(View.VISIBLE);
            ViewPager viewPager = findViewById(R.id.first_viewpager);
            initViewPager(viewPager);
        } else {
            skip.setVisibility(View.GONE);
            goMain();
        }
    }

    private void initViewPager(ViewPager viewPager) {
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
                return view == object;
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                ImageView imageView = new ImageView(container.getContext());
                if (position == 0) {
                    imageView.setImageResource(R.mipmap.yindao1);
                } else if (position == 1) {
                    imageView.setImageResource(R.mipmap.yindao2);
                } else {
                    imageView.setImageResource(R.mipmap.yindao3);
                    imageView.setOnClickListener(v -> {
                        MySpUtils.putBoolean(MySpUtils.SP_ISFIRSTENTRY, false);
                        goMain();
                    });
                }
                container.addView(imageView, ViewPager.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                return imageView;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                container.removeView((View) object);
            }
        });
    }

    private void goMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public int getBarColor() {
        return R.color.splash_bg;
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_splash;
    }
}
