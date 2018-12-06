package com.caotu.duanzhi.view.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe 不感兴趣和举报的弹窗
 */
public class ActionDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    //    private TextView mBtReport;
//    private TextView mBtNoInterested;
//    private TextView mTvClickCancel;
    private String contentId;
    private DialogListener callback;
    private boolean hasReport = false;

    public void setContentIdAndCallBack(String contentId, DialogListener listener, boolean isOnlyOne) {
        this.contentId = contentId;
        callback = listener;
        hasReport = isOnlyOne;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.layout_more_action_dialog, container, false);
        initView(inflate);
        return inflate;
    }

    private void initView(View inflate) {
        inflate.findViewById(R.id.bt_report).setOnClickListener(this);
//        report.setVisibility(hasReport ? View.VISIBLE : View.GONE);
        View cannot = inflate.findViewById(R.id.bt_no_interested);
        cannot.setVisibility(hasReport ? View.VISIBLE : View.GONE);
        cannot.setOnClickListener(this);
        inflate.findViewById(R.id.tv_click_cancel).setOnClickListener(this);
        //设置背景透明，才能显示出layout中诸如圆角的布局，否则会有白色底（框）
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.TransparentBottomSheetStyle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_no_interested:
                if (LoginHelp.isLoginAndSkipLogin()) {
                    noInterested();
                }
                break;
            case R.id.bt_report:
                if (LoginHelp.isLoginAndSkipLogin()) {
                    showReportDialog();
                }
                break;
        }
        dismiss();
    }

    private void noInterested() {
        if (TextUtils.isEmpty(contentId)) {
            ToastUtil.showShort("没有传内容Id");
            return;
        }
        OkGo.<BaseResponseBean<String>>post(HttpApi.UNLIKE)
                .headers("OPERATE", "UNLIKE")
                .headers("VALUE", contentId)
                //可能需要传空json : {}
                .upJson("{}")
                .execute(new JsonCallback<BaseResponseBean<String>>() {
                    @Override
                    public void onSuccess(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort("将减少推荐此类内容");
                        if (callback != null) {
                            callback.deleteItem();
                        }
                    }

                    @Override
                    public void onError(Response<BaseResponseBean<String>> response) {
                        ToastUtil.showShort("请稍后重试");
                        super.onError(response);
                    }
                });
    }

    private String reportType;

    protected void showReportDialog() {
        new AlertDialog.Builder(MyApplication.getInstance().getRunningActivity())
                .setSingleChoiceItems(BaseConfig.REPORTITEMS, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        reportType = BaseConfig.REPORTITEMS[which];
                    }
                })
                .setTitle("举报")
//                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        reportType = null;
//                        dialog.dismiss();
//                    }
//                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (TextUtils.isEmpty(reportType)) {
                            ToastUtil.showShort("请选择举报类型");
                        } else {
                            dialog.dismiss();
                            CommonHttpRequest.getInstance().requestReport(contentId, reportType, 0);
//                            requestReport();
                        }
                    }
                }).show();

    }

    /**
     * 修改dialogfragment的bug
     * @param manager
     * @param tag
     */
    @Override
    public void show(FragmentManager manager, String tag) {
        //super.show(manager, tag);
        try {
            Class c = Class.forName("android.support.v4.app.DialogFragment");
            Constructor con = c.getConstructor();
            Object obj = con.newInstance();
            Field dismissed = c.getDeclaredField("mDismissed");
            dismissed.setAccessible(true);
            dismissed.set(obj, false);
            Field shownByMe = c.getDeclaredField("mShownByMe");
            shownByMe.setAccessible(true);
            shownByMe.set(obj, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();

    }

    @Override
    public void dismiss() {
        //super.dismiss();
        dismissAllowingStateLoss();
    }

    public interface DialogListener {
        void deleteItem();

        //显示举报弹窗
//        void showReport();
    }
}
