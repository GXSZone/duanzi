package com.caotu.duanzhi.other;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import androidx.lifecycle.GenericLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.caotu.duanzhi.Http.bean.WebShareBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.jpush.PushActivityHelper;
import com.caotu.duanzhi.module.base.BaseActivity;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.module.other.WebActivity;
import com.caotu.duanzhi.utils.AESUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.utils.MySpUtils;
import com.caotu.duanzhi.view.dialog.ReportDialog;
import com.caotu.duanzhi.view.dialog.ShareDialog;
import com.google.gson.Gson;
import com.luck.picture.lib.dialog.PictureDialog;

import org.json.JSONObject;

public class AndroidInterface {
    public static final String type_splash = "splash"; //闪屏
    public static final String type_banner = "banner"; //发现页banner
    public static final String type_recommend = "recommend"; //推荐列表
    public static final String type_notice = "notice"; //推送通知
    public static final String type_user = "user";  //个人中心页面
    public static final String type_mine_banner = "mine_banner";  //个人中心banner
    public static final String type_other_user = "other_user";
    public static final String type_other = "other";  //其他

    @JavascriptInterface
    public String callappforkey() {
        JSONObject jsonObject = new JSONObject();
        try {
            String myId = MySpUtils.getMyId();
            if (!TextUtils.isEmpty(WebActivity.USER_ID)) {
                myId = WebActivity.USER_ID;
            }
            jsonObject.put("userid", TextUtils.isEmpty(myId) ? "" : AESUtils.encode(myId));
            jsonObject.put("key", WebActivity.H5_KEY);
            jsonObject.put("apptype", "Android");
            jsonObject.put("apppage", WebActivity.WEB_FROM_TYPE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    @JavascriptInterface
    public void closeapp() {
        MyApplication.getInstance().getRunningActivity().finish();
    }

    PictureDialog dialog;

    @JavascriptInterface
    public void shareweb(String shareContent) {
        startVibrator(100);
        WebShareBean webShareBean = new Gson().fromJson(shareContent, WebShareBean.class);
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        ShareDialog shareDialog = ShareDialog.newInstance(webShareBean);
        shareDialog.setListener(new ShareDialog.SimperMediaCallBack() {
            @Override
            public void callback(WebShareBean bean) {
                if (bean.webType == 1) {
                    showDialog(runningActivity);
                    addLifeObServer(runningActivity);
                    ShareHelper.getInstance().shareWebPicture(bean, bean.url);
                } else {
                    ShareHelper.getInstance().shareFromWebView(bean);
                }
            }
        });

        if (runningActivity instanceof BaseActivity) {
            shareDialog.show(((BaseActivity) runningActivity).getSupportFragmentManager(), "share");
        }
    }

    private void showDialog(Activity runningActivity) {
        if (runningActivity == null) return;
        if (dialog == null) {
            dialog = new PictureDialog(runningActivity);
        }
        dialog.show();
    }

    @JavascriptInterface
    public void weblogin() {
        LoginHelp.goLogin();
    }

    /**
     * H5页面打开APP
     * 首页、发现、发布、消息、我、他人主页（比如段子哥、段子妹）、话题详情、内容详情
     * type: 0=首页  1=他人主页  2=内容详情  3=话题详情  4=发现 5=发布  6=消息  7=我 8=举报内容  9=举报评论
     */
    @JavascriptInterface
    public void webSkipApp(int type, String id) {
        if (type == 0 || type == 4 || type == 6 || type == 7) {
            closeapp();
        }
        if (type == 5) {
            HelperForStartActivity.openPublish();
        }
        if (type == 8 || type == 9) {
            Activity runningActivity = MyApplication.getInstance().getRunningActivity();
            if (runningActivity != null) {
                ReportDialog dialog = new ReportDialog(runningActivity);
                dialog.setIdAndType(id, type == 9 ? 1 : 0);
                dialog.show();
            }
            return;
        }
        PushActivityHelper.openApp(type, id);
    }

    /**
     * H5设置给app分享内容
     *
     * @param shareContent
     */
    @JavascriptInterface
    public void setShareContent(String shareContent) {
        WebShareBean webShareBean = new Gson().fromJson(shareContent, WebShareBean.class);
        Activity runningActivity = MyApplication.getInstance().getRunningActivity();
        if (runningActivity instanceof WebActivity) {
            runningActivity.runOnUiThread(() -> ((WebActivity) runningActivity).setShareBean(webShareBean));
        }
    }

    private void addLifeObServer(Activity activity) {
        if (activity == null) return;
        if (activity instanceof LifecycleOwner) {
            LifecycleOwner owner = (LifecycleOwner) activity;
            owner.getLifecycle().addObserver((GenericLifecycleObserver) (source, event) -> {
                if (event == Lifecycle.Event.ON_PAUSE) {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    private Vibrator mVibrator;

    /**
     * @param milliseconds 震动时间
     * @author Angle
     * 创建时间: 2018/11/4 13:04
     * 方法描述: 开启相应的震动
     */
    public void startVibrator(long milliseconds) {
        if (mVibrator == null) {
            initVibrator();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect vibrationEffect = VibrationEffect.createOneShot(milliseconds, 100);
            mVibrator.vibrate(vibrationEffect);
        } else {
            mVibrator.vibrate(milliseconds);
        }
    }

    /**
     * @author Angle
     * 创建时间: 2018/11/4 13:03
     * 方法描述: 初始化震动的对象
     */
    private void initVibrator() {
        mVibrator = (Vibrator) MyApplication.getInstance().getSystemService(Context.VIBRATOR_SERVICE);
    }
}
