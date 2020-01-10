package com.caotu.duanzhi.module;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.caotu.adlib.ADLibConfig;
import com.caotu.adlib.AdHelper;
import com.caotu.adlib.AdSplashListenerAdapter;
import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.SplashBean;
import com.caotu.duanzhi.Http.bean.UrlCheckBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.advertisement.ADConfig;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.home.MainActivity;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.other.AndroidInterface;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.RealmHelper;
import com.caotu.duanzhi.view.viewpagertranformer.PageTransformer3D;
import com.caotu.duanzhi.view.widget.TimerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.sunfusheng.GlideImageView;
import com.sunfusheng.util.BlurUtils;
import com.taobao.sophix.SophixManager;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.admob.admobgensdk.ad.splash.ADMobGenSplashView;

/**
 * 之前启动页的图会突然放大一下就是因为这个适配框架导致的
 */
public class SplashActivity extends AppCompatActivity {

    private GlideImageView startView;
    private TimerView timerView;
    long skipTime = 600;
    private FrameLayout frameLayout;
    private FrameLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdHelper.getInstance().fullScreen(this);
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

        if (!BaseConfig.isDebug) {
            initHotFix();
        }
        RealmHelper.putDateFromSp();
        BaseConfig.isSupportBlur = BlurUtils.checkRenderscriptSupport(this);
    }

    private void initViewStub() {
        View skip = findViewById(R.id.iv_skip);
        skip.setOnClickListener(v -> {
            CommonHttpRequest.getInstance().splashCount("JUMP");
            MySpUtils.putBoolean(MySpUtils.SP_ISFIRSTENTRY, false);
            UmengHelper.event(UmengStatisticsKeyIds.splash_guide_skip);
            goMain();
        });
        ViewPager viewPager = findViewById(R.id.first_viewpager);
        viewPager.setBackgroundColor(Color.WHITE);  //白色背景为了遮盖
        initViewPager(viewPager);
    }

    private void initHotFix() {
        List<String> tags = new ArrayList<>();
        if (BaseConfig.isDebug) {
            tags.add(BaseConfig.lineTag);
        } else {
            tags.add(BaseConfig.onlineTag);
        }
        // TODO: 2019-11-14 可以新加tag 来区分补丁下发策略
        //此处调用在queryAndLoadNewPatch()方法前
        SophixManager.getInstance().setTags(tags);
        // queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
        SophixManager.getInstance().queryAndLoadNewPatch();
    }

    protected void initView() {
        frameLayout = findViewById(R.id.splash_activity);
        adContainer = findViewById(R.id.fl_guide_splash);
        startView = findViewById(R.id.start_layout);
        timerView = findViewById(R.id.timer_skip);

        ImageView image = findViewById(R.id.app_logo);
        image.setImageResource(BaseConfig.app_logo);
        // TODO: 2018/11/19 false 直接跳过
//        if (MySpUtils.getBoolean(MySpUtils.SP_ISFIRSTENTRY, true)) {
//            ViewStub viewStub = findViewById(R.id.view_stub_first);
//            MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    viewStub.inflate();
//                    initViewStub();
//                }
//            }, skipTime);
//        } else {
//            getAppConfig();
//        }
        if (MySpUtils.getBoolean(MySpUtils.SP_ISFIRSTENTRY, true)) {
            MyApplication.getInstance().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MySpUtils.putBoolean(MySpUtils.SP_ISFIRSTENTRY, false);
                    goMain();
                }
            }, skipTime);
        } else {
            getAppConfig();
        }
        CommonHttpRequest.getInstance().getInterestingUsers(null);
    }


    /**
     * 获取闪屏广告业
     */
    private void getAppConfig() {
        Map<String, String> map = new HashMap<>();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        double param = new BigDecimal((float) dm.widthPixels / dm.heightPixels).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        map.put("param", String.valueOf(param));
        OkGo.<BaseResponseBean<SplashBean>>post(HttpApi.SPLASH)
                .upJson(new JSONObject(map))
                .execute(new JsonCallback<BaseResponseBean<SplashBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<SplashBean>> response) {
                        //都是后台不按正常出牌才有这一堆判断
                        SplashBean data = response.body().getData();
                        if (data == null || data.androidAd == null) {
                            goMain();
                            return;
                        }
                        AdHelper.getInstance().initAdOpenSwitch(
                                data.androidAd.loc_screem,
                                data.androidAd.loc_content,
                                data.androidAd.loc_comment,
                                data.androidAd.loc_banner,

                                data.androidAd.loc_table,
                                data.androidAd.loc_table_pic,
                                data.androidAd.loc_table_text,
                                data.androidAd.loc_table_video
                        );

                        // TODO: 2019-10-10 1 代表开启广告
                        if (ADLibConfig.AdOpenConfig.splashAdIsOpen) {
                            dealAD();

                        } else if (!TextUtils.isEmpty(data.thumbnail)) {
                            long longTime = MySpUtils.getLong(MySpUtils.SPLASH_SHOWED);
                            if (!DevicesUtils.isToday(longTime)) {
                                //先取消跳转的延迟消息
                                frameLayout.setVisibility(View.VISIBLE);
                                startView.load(data.thumbnail, R.color.transparent, (isComplete, percentage, bytesRead, totalBytes) -> {
                                    if (isComplete) {
                                        setSplashClick(data);
                                        dealTimer(data.showtime);
                                    }
                                });
                            } else {
                                goMain();
                            }
                        } else {
                            goMain();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<SplashBean>> response) {
                        super.onError(response);
                        goMain();
                    }
                });

    }

    private void setSplashClick(SplashBean bean) {
        startView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean == null || TextUtils.isEmpty(bean.wap_url)) return;
                startView.setEnabled(false);
                CommonHttpRequest.getInstance().splashCount("SCREEN");
                CommonHttpRequest.getInstance().checkUrl(bean.wap_url, new JsonCallback<BaseResponseBean<UrlCheckBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<UrlCheckBean>> response) {
                        // TODO: 2018/12/25 保存接口给的key,H5认证使用
                        UrlCheckBean data = response.body().getData();
                        WebActivity.H5_KEY = data.getReturnkey();
                        WebActivity.WEB_FROM_TYPE = AndroidInterface.type_splash;
                        Intent homeIntent = new Intent(SplashActivity.this, MainActivity.class);
                        Intent webIntent = new Intent(SplashActivity.this, WebActivity.class);
                        webIntent.putExtra(WebActivity.KEY_URL, bean.wap_url);
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


    private boolean needJumpMain = false;
    private boolean readyJump = false;
    ADMobGenSplashView splashAd;

    private void dealAD() {
        splashAd = AdHelper.getInstance().initSplashAd(this, new AdSplashListenerAdapter() {
            @Override
            public void onAdClose() {
                super.onAdClose();
                readyJump = true;
                next();
            }

            @Override
            public void onADReceiv() {
                ViewGroup viewGroup = (ViewGroup) adContainer.getParent();
                viewGroup.setAlpha(1.0f);
            }

            @Override
            public void onADFailed(String s) {
                super.onADFailed(s);
                goMain();
            }

            @Override
            public void onADClick() {
                super.onADClick();
                readyJump = true;
            }
        }, adContainer);
    }

    @Override
    protected void onPause() {
        super.onPause();
        needJumpMain = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        needJumpMain = true;
        next();
    }

    private void next() {
        if (needJumpMain && readyJump) {
            UmengHelper.event(ADConfig.splash_skip);
            goMain();
        }
    }

    private void dealTimer(String showtime) {
        timerView.setVisibility(View.VISIBLE);
        MySpUtils.putLong(MySpUtils.SPLASH_SHOWED, System.currentTimeMillis());
        timerView.setOnCountDownListener(new TimerView.OnCountDownListener() {
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
        Intent intent = new Intent(this, MainActivity.class);
//        Intent intent = new Intent(this, TestActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdHelper.getInstance().onSplashDestroy(splashAd);
    }

    /**
     * 开屏页一定要禁止用户对返回按钮的控制，否则将可能导致用户手动退出了App而广告无法正常曝光和计费
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
