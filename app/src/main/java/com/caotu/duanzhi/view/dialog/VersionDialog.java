package com.caotu.duanzhi.view.dialog;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.bean.VersionBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.NotificationUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.io.File;

public class VersionDialog extends Dialog implements View.OnClickListener {
    public View ivClose;
    private Context context;
    public boolean isMustUpdate = false;
    public String msg;
    public String url;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private static final int notifyId = 1023;

    //测试apk下载
//    public static String apk_url = "http://download.fir.im/v2/app/install/56dd4bb7e75e2d27f2000046?download_token=e415c0fd1ac3b7abcb65ebc6603c59d9&source=update";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除白色背景
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public VersionDialog(Context context, VersionBean bean) {
        super(context, R.style.customDialog);
        this.context = context;

        setContentView(R.layout.layout_version_dialog);
        TextView versionMsg = findViewById(R.id.tv_version_msg);
        versionMsg.setMovementMethod(ScrollingMovementMethod.getInstance());
        findViewById(R.id.iv_skip_download).setOnClickListener(this);
        ivClose = findViewById(R.id.iv_close_dialog);
        ivClose.setOnClickListener(this);
        if (bean == null) return;
        //强制更新
        if (bean.updateanversiondroid.value.compareToIgnoreCase(DevicesUtils.getVerName()) > 0) {
            ivClose.setVisibility(View.GONE);
            isMustUpdate = true;
            this.setCanceledOnTouchOutside(false);
            this.setCancelable(false);
            msg = bean.updateanversiondroid.message;
            url = bean.updateanversiondroid.linkurl;
        } else {
            isMustUpdate = false;
            ivClose.setVisibility(View.VISIBLE);
            this.setCanceledOnTouchOutside(true);
            this.setCancelable(true);
            msg = bean.newestversionandroid.message;
            url = bean.newestversionandroid.linkurl;
        }
        if (!TextUtils.isEmpty(msg)) {
            versionMsg.setVisibility(View.VISIBLE);
            String text = msg.replace(" ", "\n");
            versionMsg.setText(text);
        } else {
            versionMsg.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_skip_download:
                startDownload();
                if (!isMustUpdate) {
                    dismiss();
                }
                break;
            case R.id.iv_close_dialog:
                dismiss();
                break;
        }

    }

    ProgressDialog progressDialog;


    private void startDownload() {

        OkGo.<File>get(url)
                .execute(new FileCallback() {
                    @Override
                    public void onStart(Request<File, ? extends Request> request) {
                        readyStart();
                    }

                    @Override
                    public void onSuccess(Response<File> response) {
                        if (!isMustUpdate) {
                            if (mNotifyManager != null) {
                                mNotifyManager.cancel(notifyId);
                            }
                        } else {
                            if (progressDialog != null) {
                                progressDialog.dismiss();
                            }
                        }
                        String absolutePath = response.body().getAbsolutePath();
                        installApk(absolutePath);
                    }

                    @Override
                    public void downloadProgress(Progress progress) {
                        float fraction = progress.fraction;
                        changeProgress(fraction);
                    }

                    @Override
                    public void onError(Response<File> response) {
                        if (!isMustUpdate) {
                            mBuilder.setContentText("下载出错");
                            mNotifyManager.notify(notifyId, mBuilder.build());
                        } else {
                            progressDialog.setMessage("下载出错");
                            progressDialog.setCancelable(true);
                        }
                        super.onError(response);
                    }
                });
    }


    private void changeProgress(float fraction) {
        int pg = (int) (fraction * 100);
        if (isMustUpdate) {
            progressDialog.setProgress(pg);

        } else {
            boolean notificationEnable = NotificationUtil.notificationEnable(context);
            if (notificationEnable) {
                mBuilder.setProgress(100, pg, false);
                mNotifyManager.notify(notifyId, mBuilder.build());
            }
        }
    }

    private void readyStart() {
        //下载开始
        if (isMustUpdate) {
            progressDialog = new ProgressDialog(MyApplication.getInstance().getRunningActivity());
            progressDialog.setMax(100);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("正在下载...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.show();
        } else {
            boolean notificationEnable = NotificationUtil.notificationEnable(context);
            if (notificationEnable) {
                mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    //只在Android O之上需要渠道
                    NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(notifyId),
                            "name", NotificationManager.IMPORTANCE_HIGH);
                    //如果这里用IMPORTANCE_NOENE就需要在系统的设置里面开启渠道，
                    //通知才能正常弹出
                    mNotifyManager.createNotificationChannel(notificationChannel);
                }

                mBuilder = new NotificationCompat.Builder(context);
                mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                mBuilder.setContentTitle("版本更新");
                mBuilder.setContentText("正在下载...");
//                mBuilder.setDefaults(Notification.DEFAULT_ALL);
                mBuilder.setProgress(0, 0, false);
                Notification notification = mBuilder.build();
                notification.flags = Notification.FLAG_AUTO_CANCEL;
                mNotifyManager.notify(notifyId, notification);
            }
        }
    }


    private void installApk(String absolutePath) {
        //先判断有没有安装权限
        DevicesUtils.checkInstallPermission(MyApplication.getInstance().getRunningActivity(),
                () -> DevicesUtils.installApk(MyApplication.getInstance().getRunningActivity(),
                        absolutePath));
    }

}
