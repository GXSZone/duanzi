package com.caotu.duanzhi.module.login;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.Http.bean.RegistBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.jpush.JPushManager;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.base.MyFragmentAdapter;
import com.caotu.duanzhi.other.ChangeUserPhotoServices;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.widget.SlipViewPager;
import com.luck.picture.lib.dialog.PictureDialog;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginAndRegisterActivity extends BaseActivity implements View.OnClickListener {

    private SlipViewPager mViewpagerLoginRegister;
    private TextView mLlTab1;
    private TextView mLlTab2;
    private UMShareAPI mShareAPI;
    private UMAuthListener authListener;
    private Map<String, String> regist = new HashMap<>();
    //用于给首页回调
    public static final int LOGIN_RESULT_CODE = 329;
    public static final int LOGIN_REQUEST_CODE = 330;
    private AMapLocationClient locationClient;
    boolean startAMap = false;

    @Override
    protected int getLayoutView() {
        return R.layout.activity_login_and_regist;
    }

    @Override
    protected void initView() {
        mViewpagerLoginRegister = findViewById(R.id.viewpager_login_register);
        findViewById(R.id.include_login_login_qq_but).setOnClickListener(this);
        findViewById(R.id.include_login_login_weixin_but).setOnClickListener(this);
        findViewById(R.id.include_login_login_weibo_but).setOnClickListener(this);
        mLlTab1 = findViewById(R.id.ll_tab_1);
        mLlTab2 = findViewById(R.id.ll_tab_2);
        mLlTab1.setOnClickListener(this);
        mLlTab2.setOnClickListener(this);
        mViewpagerLoginRegister.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mLlTab1.setSelected(true);
                    mLlTab2.setSelected(false);
                } else if (position == 1) {
                    mLlTab1.setSelected(false);
                    mLlTab2.setSelected(true);
                }
            }
        });

        mViewpagerLoginRegister.setAdapter(new MyFragmentAdapter(getSupportFragmentManager(), getFragmentList()));
        regist.put("device", Build.MODEL);//设备
        regist.put("devicetype", "AZ");//设备类型
        //初始化状态
        mLlTab1.setSelected(true);
        mLlTab2.setSelected(false);
        config();
        initStartAMapLocation();
    }

    private List<Fragment> getFragmentList() {
        List<Fragment> mFragments = new ArrayList<>();
        LoginNewFragment loginFragment = new LoginNewFragment();
        mFragments.add(loginFragment);
        RegistNewFragment registFragment = new RegistNewFragment();
        mFragments.add(registFragment);
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
        locationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                startAMap = true;
                if (aMapLocation.getErrorCode() == AMapLocation.LOCATION_SUCCESS) {
                    String address = aMapLocation.getProvince();
                    String city = aMapLocation.getCity();
                    regist.put("regloc", address + city);//注册地址 省市即可
                } else {
                    regist.put("regloc", "");//注册地址 省市即可
                }
                if (regist.size() >= 10) {
                    requestRegist(regist);
                }
            }
        });
        startLocation();
    }

    /**
     * 开始定位
     */
    private void startLocation() {
        if (null != locationClient) {
            //签到只需调用startLocation即可
            if (locationClient.isStarted()) {
                return;
            }
            locationClient.startLocation();
        }
    }

    public boolean isQQClientAvailable() {
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void onClick(View view) {

//        UMShareAPI.get(this).release();
        switch (view.getId()) {
            case R.id.include_login_login_qq_but:
                //判断是否安装QQ客户端不准确,只能通过这种方式判断,照理QQ有网页版的授权也没有
                if (isQQClientAvailable()) {
                    mShareAPI.getPlatformInfo(this, SHARE_MEDIA.QQ, authListener);
                } else {
                    ToastUtil.showShort("请先安装QQ客户端");
                }
                break;
            case R.id.include_login_login_weixin_but:
                if (mShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN)) {
                    mShareAPI.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, authListener);
                } else {
                    ToastUtil.showShort("请先安装微信客户端");
                }

                break;
            case R.id.include_login_login_weibo_but:
                mShareAPI.getPlatformInfo(this, SHARE_MEDIA.SINA, authListener);//调用授权接口UMShareAPI
                break;
            case R.id.ll_tab_1:
                mViewpagerLoginRegister.setCurrentItem(0);
                break;
            case R.id.ll_tab_2:
                mViewpagerLoginRegister.setCurrentItem(1);
                break;
        }
    }

    public PictureDialog dialog;

    private void config() {
//        UMShareConfig config = new UMShareConfig();
        //设置每次登录拉取确认界面  目前SDK默认设置为在Token有效期内登录不进行二次授权，如果有需要每次登录都弹出授权页面
//        config.isNeedAuthOnGetUserInfo(true);
        mShareAPI = UMShareAPI.get(this);
//        mShareAPI.setShareConfig(config);
        authListener = new UMAuthListener() {
            /**
             * @desc 授权开始的回调
             * @param platform 平台名称
             */
            @Override
            public void onStart(SHARE_MEDIA platform) {
                try {
                    if (dialog == null) {
                        dialog = new PictureDialog(MyApplication.getInstance().getRunningActivity());
                    }
                    dialog.show();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            /**
             * @desc 授权成功的回调
             * @param platform 平台名称
             * @param action 行为序号，开发者用不上
             * @param data 用户资料返回
             */
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                thirdLoginSeccess(platform, action, data);
            }


            /**
             * @desc 授权失败的回调
             * @param platform 平台名称
             * @param action 行为序号，开发者用不上
             * @param t 错误原因
             */
            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ToastUtil.showShort("授权失败");
            }

            /**
             * @desc 授权取消的回调
             * @param platform 平台名称
             * @param action 行为序号，开发者用不上
             */
            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                try {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                ToastUtil.showShort("取消了");
            }
        };
    }

    private void thirdLoginSeccess(SHARE_MEDIA platform, int action, Map<String, String> data) {
//        mShareAPI.deleteOauth(this, platform, authListener);
     /*  UShare封装
          后字段名	 QQ原始字段名	    微信原始字段名	        新浪原始字段名	            字段含义	            备注
            uid	       openid	             unionid	            uid	                 用户唯一标识	uid能否实现Android与iOS平台打通，目前QQ只能实现同APPID下用户ID匹配
           openid	    openid	            openid	                空	                 用户唯一标识	主要为微信和QQ使用
           unionid	    unionid	            unionid     	        uid	                 用户唯一标识	主要为微信和QQ使用，unionid主要用于微信、QQ用户系统打通
            usid	    openid	             openid     	        uid	                   用户唯一标识	用于U-Share 4.x/5.x 升级后保留原先使用形式
            name	    screen_name	        screen_name	        screen_name	                用户昵称
           gender	    gender	             gender	            gender	                    用户性别	该字段会直接返回男女
           iconurl	    profile_image_url	profile_image_url	profile_image_url	        用户头像*/
        String name = data.get("name");
        switch (platform) {
            case QQ:
                regist.put("regheadurl", data.get("iconurl"));//默认头像
                regist.put("regid", data.get("unionid"));//注册id(第三方)
                regist.put("regname", name);//第三方昵称
                regist.put("regtype", "QQ");//PH，WX，QQ，WB
                regist.put("regsex", "女".equals(data.get("gender")) ? "1" : "0");//性别
                regist.put("regage", "");//年龄
                regist.put("regphone", "");//手机号
                break;
            case WEIXIN:
                regist.put("regheadurl", data.get("iconurl"));//默认头像
                regist.put("regid", data.get("uid"));//注册id(第三方)
                regist.put("regname", name);//第三方昵称
                regist.put("regtype", "WX");//PH，WX，QQ，WB
                regist.put("regsex", "女".equals(data.get("gender")) ? "1" : "0");//性别
                regist.put("regage", "");//年龄
                regist.put("regphone", "");//手机号
                break;
            case SINA:
                regist.put("regheadurl", data.get("iconurl"));//默认头像
                regist.put("regid", data.get("uid"));//注册id(第三方)
                regist.put("regname", name);//第三方昵称
                regist.put("regtype", "WB");//PH，WX，QQ，WB
                regist.put("regsex", "女".equals(data.get("gender")) ? "1" : "0");//性别
                regist.put("regage", "");//年龄
                regist.put("regphone", "");//手机号
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
                        // true不需要更新 false需要更新 字符串
                        if (TextUtils.equals("false", data.isNotupload())) {
                            uploadUserPhoto(map.get("regheadurl"));
                        }
                        loginSuccess(phuser);
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<RegistBean>> response) {
                        ToastUtil.showShort(R.string.login_failure);
                        super.onError(response);
                    }
                });

    }

    private void loginSuccess(String phuser) {
        MySpUtils.putBoolean(MySpUtils.SP_HAS_BIND_PHONE, "1".equals(phuser));
        MySpUtils.putBoolean(MySpUtils.SP_ISLOGIN, true);
        ToastUtil.showShort(R.string.login_success);
        JPushManager.getInstance().loginSuccessAndSetJpushAlias();
        setResult(LOGIN_RESULT_CODE);
        finish();
    }

    /**
     * 上传到自己服务器的.用户头像问题
     *
     * @param regheadurl
     */
    private void uploadUserPhoto(String regheadurl) {
        Intent intent = new Intent(this, ChangeUserPhotoServices.class);
        intent.putExtra("photo", regheadurl);
        startService(intent);
    }

    public Map<String, String> getData() {
        return regist;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
        dismissAMapLocation();
    }

    /**
     * 取消监听定位
     */
    private void dismissAMapLocation() {
        //销毁时，需要销毁定位client
        if (null != locationClient) {
            locationClient.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        UMShareAPI.get(this).onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
