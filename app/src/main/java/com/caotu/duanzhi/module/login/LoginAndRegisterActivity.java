package com.caotu.duanzhi.module.login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.caotu.duanzhi.view.widget.SlipViewPager;
import com.luck.picture.lib.dialog.PictureDialog;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.HashMap;
import java.util.Map;

public class LoginAndRegisterActivity extends FragmentActivity implements View.OnClickListener {

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_regist);
        initView();
        initStartAMapLocation();
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

    private void initView() {

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
        mViewpagerLoginRegister.setAdapter(null);
        regist.put("device", Build.MODEL);//设备
        regist.put("devicetype", "AZ");//设备类型
        //初始化状态
        mLlTab1.setSelected(true);
        mLlTab2.setSelected(false);
        config();
    }


    @Override
    public void onClick(View view) {

        UMShareAPI.get(this).release();
        switch (view.getId()) {
            case R.id.include_login_login_qq_but:
                //判断是否安装QQ客户端不准确
                mShareAPI.getPlatformInfo(this, SHARE_MEDIA.QQ, authListener);
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
        UMShareConfig config = new UMShareConfig();
        //设置每次登录拉取确认界面  目前SDK默认设置为在Token有效期内登录不进行二次授权，如果有需要每次登录都弹出授权页面
        config.isNeedAuthOnGetUserInfo(true);
        mShareAPI = UMShareAPI.get(this);
        mShareAPI.setShareConfig(config);
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

//        VolleyRequest.RequestPostJsonObjectText(this, HTTPAPI.DO_REGIST, stringBody, null, new VolleyJsonObjectInterface() {
//
//            @Override
//            public void onSuccess(JSONObject response) {
//
//                String code = response.optString("code");
//                if ("1000".equals(code)) {
//                    String data = response.optString("data");
//                    String phuser = "";
//                    String isfirst = "";
//                    try {
//                        JSONObject obj = new JSONObject(data);
//                        phuser = obj.optString("phuser");
//                        isfirst = obj.optString("isfirst");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    //是否是第一次登陆
//                    SPUtils.setEditorKeyValue(SPUtils.SP_ISFIRSTLOGINENTRY, "1".equals(isfirst));
//                    //是否绑定手机
//                    SPUtils.setEditorKeyValue(SPUtils.SP_HAS_BIND_PHONE, "1".equals(phuser));
//
//                    //第三方直接登录 绑定手机号延后提示
//                    SPUtils.setEditorKeyValue(SPUtils.SP_ISLOGIN, true);
//                    JPushManager.getInstance().loginSuccessAndSetJpushAlias();
//                    EventBusHelp.sendLoginEvent();
//                    ToastUtil.showShort(R.string.login_success);
//                    setResult(LoginAndRegisterActivity.LOGIN_RESULT_CODE);
//                    finish();
//                    return;
//                }
//                ToastUtil.showShort(R.string.login_failure);
//            }
//
//            @Override
//            public void onError(VolleyError error) {
//                ToastUtil.showShort(R.string.login_failure);
//            }
//        });
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
