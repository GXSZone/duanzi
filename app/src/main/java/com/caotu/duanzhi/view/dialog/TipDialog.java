package com.caotu.duanzhi.view.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.caotu.duanzhi.R;

/**
 * 提醒的弹窗
 */
public class TipDialog extends Dialog {

    boolean image = false;

    public TipDialog(Context context, boolean isDownload) {
        super(context);
        image = isDownload;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tip_dialog);
        ImageView tipImage = findViewById(R.id.iv_tip_image);

        tipImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //去除白色背景
        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}
