package com.caotu.duanzhi.view.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caotu.duanzhi.Http.JsonCallback;
import com.caotu.duanzhi.Http.bean.BaseResponseBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.HttpApi;
import com.caotu.duanzhi.utils.ToastUtil;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

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

    public void setContentIdAndCallBack(String contentId, DialogListener listener) {
        this.contentId = contentId;
        callback = listener;
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
        inflate.findViewById(R.id.bt_no_interested).setOnClickListener(this);
        inflate.findViewById(R.id.tv_click_cancel).setOnClickListener(this);
        //设置背景透明，才能显示出layout中诸如圆角的布局，否则会有白色底（框）
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.TransparentBottomSheetStyle);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_no_interested:
                noInterested();
                break;
            case R.id.bt_report:
                if (callback != null) {
                    callback.showReport();
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

    public interface DialogListener {
        void deleteItem();

        //显示举报弹窗
        void showReport();
    }
}
