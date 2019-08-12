package com.caotu.duanzhi.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.other.TextWatcherAdapter;
import com.caotu.duanzhi.utils.ToastUtil;

import java.util.Locale;


/**
 * 举报的弹窗
 */

public class ReportDialog extends Dialog implements View.OnClickListener {

    private RelativeLayout layout1;
    private RadioGroup layout2;
    private EditText editText;
    String contentId; //举报内容或者评论的ID
    int type; //举报的类型, type == 1 ? "2" : "1");//举报类型 1_作品 2_评论

    public ReportDialog(Context context) {
        super(context, R.style.customDialog);
    }

    public void setIdAndType(String contentId, int type) {
        this.contentId = contentId;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_report_dialog);
        layout1 = findViewById(R.id.rl_other);
        layout2 = findViewById(R.id.radio_group_first);
        TextView okBt = findViewById(R.id.ok_action);
        okBt.setOnClickListener(this);
        findViewById(R.id.iv_back_dialog).setOnClickListener(this);
        TextView textLength = findViewById(R.id.tv_text_length);
        editText = findViewById(R.id.et_report);
        editText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                int length = s.toString().length();
                if (length > 100) {
                    length = 100;
                }
                textLength.setText(String.format(Locale.CHINA, "%d/100", length));
            }
        });
        layout2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO: 2019-07-31 这里有个坑,dialog dismiss后再show这个checkedId 会往上叠加
                int index = (checkedId - 1) % 7;
                reportText = BaseConfig.REPORTITEMS[index];
                //其他操作
                if (TextUtils.equals(reportText, BaseConfig.REPORTITEMS[6])) {
                    layout2.setVisibility(View.GONE);
                    layout1.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private String reportText;

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        reportText = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_action:
                String otherCause = editText.getText().toString();

                if (TextUtils.isEmpty(reportText)) {
                    ToastUtil.showShort("请先选择举报类型");
                }

                CommonHttpRequest.getInstance().
                        requestReport(contentId, reportText, type, otherCause);
                dismiss();
                break;
            case R.id.iv_back_dialog:
                if (layout1.getVisibility() == View.VISIBLE) {
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    @Override
    public void show() {
        super.show();
        Window window = getWindow();
        if (window == null) return;
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setBackgroundDrawableResource(android.R.color.transparent);
    }
}