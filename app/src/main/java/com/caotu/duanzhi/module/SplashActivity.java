package com.caotu.duanzhi.module;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.SplashBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.view.widget.CountDownTextView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;
import com.sunfusheng.progress.OnProgressListener;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SplashActivity extends BaseActivity {

    public static final String onlineTag = "android_pro";
    public static final String lineTag = "android_dev";
    private GlideImageView startView;
    private CountDownTextView timerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Set<String> tags = new HashSet<>();
        if (BaseConfig.isDebug) {
            tags.add(lineTag);
        } else {
            tags.add(onlineTag);
        }
        JPushManager.getInstance().setTags(MyApplication.getInstance(), tags);
    }

    @Override
    protected void initView() {
        View skip = findViewById(R.id.iv_skip);
        startView = findViewById(R.id.start_layout);
        timerView = findViewById(R.id.timer_skip);
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
            MySpUtils.putBoolean(MySpUtils.SP_ISFIRSTENTRY, false);
            goMain();
        });
        // TODO: 2018/11/19 false 直接跳过
        if (MySpUtils.getBoolean(MySpUtils.SP_ISFIRSTENTRY, true)) {
            MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startView.setVisibility(View.GONE);
                    skip.setVisibility(View.VISIBLE);
                    ViewPager viewPager = findViewById(R.id.first_viewpager);
                    initViewPager(viewPager);
                }
            }, 1500);
        } else {
            long longTime = MySpUtils.getLong(MySpUtils.SPLASH_SHOWED);
            if (!DevicesUtils.isToday(longTime) && NetWorkUtils.isNetworkConnected(this)) {
                MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goMain();
                    }
                }, 1500);
                dealSplashImage();
            }else {
                MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        goMain();
                    }
                }, 1500);
            }
        }
    }

    /**
     * 获取闪屏广告业
     */
    private void dealSplashImage() {
        Map<String, String> map = new HashMap<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        double param = new BigDecimal((float) dm.widthPixels / dm.heightPixels).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        map.put("param", String.valueOf(param));
        OkGo.<BaseResponseBean<SplashBean>>post(HttpApi.SPLASH)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<SplashBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<SplashBean>> response) {
                        SplashBean data = response.body().getData();
                        String thumbnail = data.getThumbnail();
                        if (TextUtils.isEmpty(thumbnail)) return;
                        //先取消跳转的延迟消息
                        MyApplication.getInstance().getHandler().removeCallbacksAndMessages(null);
                        if (startView != null) {
                            startView.load(thumbnail, R.mipmap.loding_bg, new OnProgressListener() {
                                @Override
                                public void onProgress(boolean isComplete, int percentage, long bytesRead, long totalBytes) {
                                    if (isComplete){
                                        startView.setClickable(true);
                                        startView.setFocusable(true);
                                        setSplashClick(data);
                                        dealTimer(data.getShowtime());
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<SplashBean>> response) {
                        super.onError(response);
                    }
                });

    }

    private void setSplashClick(SplashBean bean) {
        startView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean == null || TextUtils.isEmpty(bean.getWap_url())) return;
                Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
                Intent webIntent = new Intent(SplashActivity.this, WebActivity.class);
                webIntent.putExtra(WebActivity.KEY_URL, bean.getWap_url());
                Intent[] intents = new Intent[2];
                intents[0] = homeIntent;
                intents[1] = webIntent;
                startActivities(intents);
                finish();
            }
        });

    }

    private void dealTimer(String showtime) {
        MySpUtils.putLong(MySpUtils.SPLASH_SHOWED, System.currentTimeMillis());
        timerView.setNormalText("跳过 0S")
                .setCountDownText("跳过 ", "S")
                .setCloseKeepCountDown(false)//关闭页面保持倒计时开关
                .setCountDownClickable(true)//倒计时期间点击事件是否生效开关
                .setShowFormatTime(true)//是否格式化时间
                .setOnCountDownFinishListener(() -> goMain())
                .setOnClickListener(v -> goMain());
        timerView.setVisibility(View.VISIBLE);
        try {
            timerView.startCountDown(Long.parseLong(showtime));
        } catch (Exception e) {
            timerView.startCountDown(3000);
            e.printStackTrace();
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
                Log.i("viewpager", "instantiateItem: " + position);
                ImageView imageView = new ImageView(container.getContext());
                if (position == 0) {
                    imageView.setImageResource(R.mipmap.yindao1);
                } else if (position == 1) {
                    imageView.setImageResource(R.mipmap.yindao2);
                } else {
                    imageView.setImageResource(R.mipmap.yingdao3);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MySpUtils.putBoolean(MySpUtils.SP_ISFIRSTENTRY, false);
                            goMain();
                        }
                    });
                }
                imageView.setScaleType(ImageView.ScaleType.CENTER);
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
