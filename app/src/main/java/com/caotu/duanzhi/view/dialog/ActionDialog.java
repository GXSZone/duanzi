package com.caotu.duanzhi.view.dialog;

import android.text.TextUtils;
import android.view.View;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.ToastUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe 不感兴趣和举报的弹窗
 */
public class ActionDialog extends BaseDialogFragment implements View.OnClickListener {

    private String contentId;
    private DialogListener callback;
    private boolean hasReport = false;

    public void setContentIdAndCallBack(String contentId, DialogListener listener, boolean isOnlyOne) {
        this.contentId = contentId;
        callback = listener;
        hasReport = isOnlyOne;
    }

    @Override
    public void initView(View inflate) {
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
    public int getLayout() {
        return R.layout.layout_more_action_dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_no_interested:
                UmengHelper.event(UmengStatisticsKeyIds.content_uninterest);
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
                .headers("LOC", CommonHttpRequest.getInstance().getRecommendType())
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

    protected void showReportDialog() {
        ReportDialog dialog = new ReportDialog(getContext());
        dialog.setIdAndType(contentId, 0);
        dialog.show();
    }
}
