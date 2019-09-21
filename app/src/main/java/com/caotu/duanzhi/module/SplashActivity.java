package com.caotu.duanzhi.module;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.SplashBean;
import com.caotu.duanzhi.Http.bean.UrlCheckBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.NetWorkUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.viewpagertranformer.PageTransformer3D;
import com.caotu.duanzhi.view.widget.TimerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;
import com.taobao.sophix.SophixManager;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.jessyan.autosize.internal.CancelAdapt;

/**
 * 之前启动页的图会突然放大一下就是因为这个适配框架导致的
 */
public class SplashActivity extends AppCompatActivity implements CancelAdapt {

    private GlideImageView startView;
    private TimerView timerView;
    long skipTime = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
    }

    /**
     * 该方法专门用来获取和设置一些初始化配置
     *
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Set<String> tags = new HashSet<>();
        if (BaseConfig.isDebug) {
            tags.add(BaseConfig.lineTag);
        } else {
            tags.add(BaseConfig.onlineTag);
        }
        JPushManager.getInstance().setTags(MyApplication.getInstance(), tags);
        //获取分享url
        CommonHttpRequest.getInstance().getShareUrl();
        //初始化从sp读取历史记录
        MyApplication.getInstance().setMap(MySpUtils.getHashMapData());
        initHotFix();
        HelperForStartActivity.startVideoService(false);
    }

    Runnable splashRunnable = this::goMain;

    protected void initView() {
        View skip = findViewById(R.id.iv_skip);
        startView = findViewById(R.id.start_layout);
        timerView = findViewById(R.id.timer_skip);
        skip.setOnClickListener(v -> {
            CommonHttpRequest.getInstance().splashCount("JUMP");
            MySpUtils.putBoolean(MySpUtils.SP_ISFIRSTENTRY, false);
            UmengHelper.event(UmengStatisticsKeyIds.splash_guide_skip);
            goMain();
        });
        // TODO: 2018/11/19 false 直接跳过
        if (MySpUtils.getBoolean(MySpUtils.SP_ISFIRSTENTRY, true)) {
            startView.postDelayed(() -> {
                skip.setVisibility(View.VISIBLE);
                ViewStub viewStub = findViewById(R.id.view_stub_first);
                ViewPager viewPager = (ViewPager) viewStub.inflate();
                viewPager.setBackgroundColor(Color.WHITE);  //白色背景为了遮盖
                initViewPager(viewPager);
            }, skipTime);
        } else {
            long longTime = MySpUtils.getLong(MySpUtils.SPLASH_SHOWED);
            if (!DevicesUtils.isToday(longTime) && NetWorkUtils.isNetworkConnected(this)) {
                startView.postDelayed(splashRunnable, skipTime);
                dealSplashImage();
            } else {
                goMain();
            }
        }
    }

    private void initHotFix() {
        List<String> tags = new ArrayList<>();
        if (BaseConfig.isDebug) {
            tags.add(BaseConfig.lineTag);
        } else {
            tags.add(BaseConfig.onlineTag);
        }
        //此处调用在queryAndLoadNewPatch()方法前
        SophixManager.getInstance().setTags(tags);
        // queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
        SophixManager.getInstance().queryAndLoadNewPatch();
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
                        if (data == null) return;
                        String thumbnail = data.getThumbnail();
                        if (TextUtils.isEmpty(thumbnail)) return;
                        //先取消跳转的延迟消息
                        startView.removeCallbacks(splashRunnable);
                        View parent = (View) startView.getParent();
                        parent.setBackgroundColor(DevicesUtils.getColor(R.color.splash_bg));
                        startView.setVisibility(View.VISIBLE);
                        startView.load(thumbnail, R.mipmap.loding_bg, (isComplete, percentage, bytesRead, totalBytes) -> {
                            if (isComplete) {
                                setSplashClick(data);
                                dealTimer(data.getShowtime());
                            }
                        });
                    }
                });

    }

    private void setSplashClick(SplashBean bean) {
        startView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean == null || TextUtils.isEmpty(bean.getWap_url())) return;
                startView.setEnabled(false);
                CommonHttpRequest.getInstance().splashCount("SCREEN");
                CommonHttpRequest.getInstance().checkUrl(bean.getWap_url(), new JsonCallback<BaseResponseBean<UrlCheckBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<UrlCheckBean>> response) {
                        // TODO: 2018/12/25 保存接口给的key,H5认证使用
                        UrlCheckBean data = response.body().getData();
                        WebActivity.H5_KEY = data.getReturnkey();
                        WebActivity.WEB_FROM_TYPE = AndroidInterface.type_splash;
                        Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
                        Intent webIntent = new Intent(SplashActivity.this, WebActivity.class);
                        webIntent.putExtra(WebActivity.KEY_URL, bean.getWap_url());
                        webIntent.putExtra(WebActivity.KEY_IS_SHOW_SHARE_ICON, TextUtils.equals("1", data.getIsshare()));
                        Intent[] intents = new Intent[2];
                        intents[0] = homeIntent;
                        intents[1] = webIntent;
                        startActivities(intents);
                        finish();
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<UrlCheckBean>> response) {
                        goMain();
                    }
                });
            }
        });

    }

    private void dealTimer(String showtime) {
        MySpUtils.putLong(MySpUtils.SPLASH_SHOWED, System.currentTimeMillis());
        timerView.setNormalText("跳过")
                .setCountDownText("跳过 ", "S")
                .setOnCountDownListener(new TimerView.OnCountDownListener() {
                    @Override
                    public void onClick() {
                        CommonHttpRequest.getInstance().splashCount("JUMPTIMER");
                        goMain();
                    }

                    @Override
                    public void onFinish() {
                        goMain();
                    }
                });
        timerView.setVisibility(View.VISIBLE);
        try {
            timerView.startCountDown(Long.parseLong(showtime));
        } catch (Exception e) {
            timerView.startCountDown(3);
            e.printStackTrace();
        }
    }

    /**
     * viewpager 动画可以参考
     * link{https://www.jianshu.com/p/ebbafdf99148}
     *
     * @param viewPager
     */
    private void initViewPager(ViewPager viewPager) {
        viewPager.setPageTransformer(true, new PageTransformer3D());
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
        // 进程存在
        if (timerView != null) {
            timerView.onDestroy();
        }
        startView.removeCallbacks(splashRunnable);
        Intent intent = new Intent(this, MainActivity.class);
//        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
        finish();
    }
}
