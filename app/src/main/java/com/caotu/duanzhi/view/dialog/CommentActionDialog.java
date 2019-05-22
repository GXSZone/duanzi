package com.caotu.duanzhi.view.dialog;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.MyApplication;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.module.login.LoginHelp;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.ToastUtil;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * @author mac
 * @日期: 2018/11/2
 * @describe 不感兴趣和举报的弹窗
 */
public class CommentActionDialog extends BaseDialogFragment implements View.OnClickListener {

    private String commentId;
    private DialogListener callback;
    private boolean isMine = false;
    String mCopyText;

    public void setContentIdAndCallBack(String commentId, DialogListener listener, boolean isMe, String copyText) {
        this.commentId = commentId;
        callback = listener;
        isMine = isMe;
        mCopyText = copyText;
    }


    @Override
    public void initView(View inflate) {
        TextView leftButton = inflate.findViewById(R.id.bt_left);
        leftButton.setOnClickListener(this);

        if (TextUtils.isEmpty(mCopyText)) {
            leftButton.setVisibility(View.GONE);
        }
        TextView right = inflate.findViewById(R.id.bt_right);
        right.setOnClickListener(this);
        if (isMine) {
            Drawable Icon = DevicesUtils.getDrawable(R.mipmap.delete_pinlun);
            Icon.setBounds(0, 0, Icon.getMinimumWidth(), Icon.getMinimumHeight());
            right.setCompoundDrawables(null, Icon, null, null);
            right.setText("删除评论");
        }
        inflate.findViewById(R.id.tv_click_cancel).setOnClickListener(this);
        //设置背景透明，才能显示出layout中诸如圆角的布局，否则会有白色底（框）
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.TransparentBottomSheetStyle);
    }

    @Override
    public int getLayout() {
        return R.layout.layout_comment_action_dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_left:
                if (!TextUtils.isEmpty(mCopyText)) {
                    UmengHelper.event(UmengStatisticsKeyIds.copy_text);
                    ClipboardManager cm = (ClipboardManager) MyApplication.getInstance().
                            getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setText(mCopyText);
                    ToastUtil.showShort("复制成功");
                }
                break;
            case R.id.bt_right:
                if (isMine && callback != null) {
                    callback.deleteItem();
                } else if (!isMine && LoginHelp.isLoginAndSkipLogin()) {
                    if (callback != null) {
                        callback.report();
                    }
//                    showReportDialog();
                }
                break;
        }
        dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (callback != null) {
            callback = null;
        }
    }

    private String reportType;

    protected void showReportDialog() {
        new AlertDialog.Builder(MyApplication.getInstance().getRunningActivity())
                .setSingleChoiceItems(BaseConfig.REPORTITEMS, -1, (dialog, which) ->
                        reportType = BaseConfig.REPORTITEMS[which])
                .setTitle("举报")
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {

                    if (TextUtils.isEmpty(reportType)) {
                        ToastUtil.showShort("请选择举报类型");
                    } else {
                        dialog.dismiss();
                        CommonHttpRequest.getInstance().requestReport(commentId, reportType, 1);
                    }
                }).show();

    }

}
