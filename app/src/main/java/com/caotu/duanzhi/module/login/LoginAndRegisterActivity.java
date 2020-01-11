package com.caotu.duanzhi.module.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.RegistBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.base.MyFragmentAdapter;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.widget.SlipViewPager;
import com.luck.picture.lib.dialog.PictureDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weige.umenglib.AuthCallBack;
import weige.umenglib.ThirdPlatform;
import weige.umenglib.UmengLibHelper;
import weige.umenglib.UserAuthInfo;

public class LoginAndRegisterActivity extends BaseActivity implements View.OnClickListener {

    private SlipViewPager mViewpagerLoginRegister;

    private AuthCallBack authListener;
    private Map<String, String> regist = new HashMap<>();
    //用于给首页回调
    public static final int LOGIN_RESULT_CODE = 329;
    public static final int LOGIN_REQUEST_CODE = 330;
    private AMapLocationClient locationClient;
    boolean startAMap = false;

    @Override
    protected int getLayoutView() {
        fullScreen(this);
        return R.layout.activity_login_and_regist;
    }

    public SlipViewPager getViewPager() {
        return mViewpagerLoginRegister;
    }

    @Override
    protected void initView() {
        ImageView logo = findViewById(R.id.iv_logo);
        logo.setImageResource(BaseConfig.login_logo);
        mViewpagerLoginRegister = findViewById(R.id.viewpager_login_register);
        findViewById(R.id.include_login_login_qq_but).setOnClickListener(this);
        findViewById(R.id.include_login_login_weixin_but).setOnClickListener(this);
        findViewById(R.id.include_login_login_weibo_but).setOnClickListener(this);

        mViewpagerLoginRegister.setSlipping(false);
        mViewpagerLoginRegister.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), getFragmentList()));
        regist.put("device", Build.MODEL);//设备
        regist.put("devicetype", "AZ");//设备类型

        config();
        initStartAMapLocation();

    }

    private List<Fragment> getFragmentList() {
        List<Fragment> mFragments = new ArrayList<>();
        VerificationLoginFragment registFragment = new VerificationLoginFragment();
        mFragments.add(registFragment);
        PwdLoginFragment loginFragment = new PwdLoginFragment();
        mFragments.add(loginFragment);
        return mFragments;
    }

    /**
     * 开启定位
     */
    private void initStartAMapLocation() {
        locationClient = new AMapLocationClient(this);
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        locationClient.setLocationOption(option);
        //设置定位监听
        locationClient.setLocationListener(aMapLocation -> {
            startAMap = true;
            if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                String address = aMapLocation.getProvince();
                String city = aMapLocation.getCity();
                if (!TextUtils.isEmpty(address) && !TextUtils.isEmpty(city)) {
                    regist.put("regloc", address + "," + city);//注册地址 省市即可
                } else if (!TextUtils.isEmpty(city)) {
                    regist.put("regloc", city);
                }
            } else {
                regist.put("regloc", "");//注册地址 省市即可
            }
            if (regist.size() >= 10) {
                requestRegist(regist);
            }
        });
        startLocation();
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        if (locationClient == null || locationClient.isStarted()) return;
        locationClient.startLocation();
    }

    long startTime;

    @Override
    public void onClick(View view) {
        long secondTime = System.currentTimeMillis();
        if (secondTime - startTime >= 3000) {
            switch (view.getId()) {
                case R.id.include_login_login_qq_but:
                    boolean install = UmengLibHelper.isInstall(this, ThirdPlatform.qq);
                    if (install) {
                        UmengLibHelper.platLogin(this, ThirdPlatform.qq, authListener);
                    } else {
                        ToastUtil.showShort("请先安装QQ客户端");
                    }
                    break;
                case R.id.include_login_login_weixin_but:
                    boolean install1 = UmengLibHelper.isInstall(this, ThirdPlatform.weixin);
                    if (install1) {
                        UmengLibHelper.platLogin(this, ThirdPlatform.weixin, authListener);
                    } else {
                        ToastUtil.showShort("请先安装微信客户端");
                    }
                    break;
                case R.id.include_login_login_weibo_but:
                    boolean install2 = UmengLibHelper.isInstall(this, ThirdPlatform.sina);
                    if (install2) {
                        UmengLibHelper.platLogin(this, ThirdPlatform.sina, authListener);
                    } else {
                        ToastUtil.showShort("请先安装微博客户端");
                    }
                    break;
            }
            startTime = secondTime;
        }
    }

    public PictureDialog dialog;

    private void config() {
        authListener = new AuthCallBack() {

            @Override
            public void error(String s) {
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ToastUtil.showShort("授权失败:" + s);
            }

            @Override
            protected void cancle() {
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void complete(@ThirdPlatform int platform, UserAuthInfo umInfoBean) {
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                thirdLoginSeccess(platform, umInfoBean);
            }

            @Override
            public void start() {
                try {
                    if (dialog == null) {
                        dialog = new PictureDialog(LoginAndRegisterActivity.this);
                    }
                    dialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private void thirdLoginSeccess(int platform, UserAuthInfo umInfoBean) {
        regist.put("regheadurl", umInfoBean.getIconurl());//默认头像
        regist.put("regid", umInfoBean.getUid());//注册id(第三方)
        regist.put("regname", umInfoBean.getName());//第三方昵称
        regist.put("regsex", umInfoBean.getGender());//性别
        regist.put("regage", "");//年龄
        regist.put("regphone", "");//手机号
        switch (platform) {
            case ThirdPlatform.qq:
                regist.put("regtype", "QQ");//PH，WX，QQ，WB
                break;
            case ThirdPlatform.sina:
                regist.put("regtype", "WB");//PH，WX，QQ，WB
                break;
            case ThirdPlatform.weixin:
                regist.put("regtype", "WX");//PH，WX，QQ，WB
                break;
            default:
                break;
        }
        if (startAMap) {
            requestRegist(regist);
        } else {
            startLocation();
        }
    }

    private void requestRegist(Map<String, String> regist) {
        Map<String, String> map = getData();
        map.put("logintype", regist.get("regtype"));
        String stringBody = AESUtils.getRequestBodyAES(map);

        OkGo.<BaseResponseBean<RegistBean>>post(HttpApi.DO_REGIST)
                .upString(stringBody)
                .execute(new JsonCallback<BaseResponseBean<RegistBean>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<RegistBean>> response) {
                        //  isfirst 是否是第一次登陆  是否已经绑定过手机号 phuser
                        RegistBean data = response.body().getData();
                        String phuser = data.getPhuser();
                        loginSuccess(phuser);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<RegistBean>> response) {
                        ToastUtil.showShort("登录失败！");
                        super.onError(response);
                    }
                });

    }

    private void loginSuccess(String phuser) {
        MySpUtils.putBoolean(MySpUtils.SP_HAS_BIND_PHONE, "1".equals(phuser));
        MySpUtils.putBoolean(MySpUtils.SP_ISLOGIN, true);
        JPushManager.getInstance().loginSuccessAndSetJpushAlias();
        LoginHelp.getUserInfo(() -> {
            setResult(LOGIN_RESULT_CODE);
            finish();
        }, false);
    }


    public Map<String, String> getData() {
        return regist;
    }

    @Override
    protected void onDestroy() {
        UmengLibHelper.onDestroy(this);
        //销毁时，需要销毁定位client
        if (null != locationClient) {
            locationClient.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        UmengLibHelper.onSaveInstanceState(this, outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UmengLibHelper.onActivityResult(this, requestCode, resultCode, data);
    }
}
