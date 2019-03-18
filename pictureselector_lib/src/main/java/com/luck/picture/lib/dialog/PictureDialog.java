package com.luck.picture.lib.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import com.luck.picture.lib.R;

public class PictureDialog extends Dialog {
    public Context context;
    private ProgressBar loadingTV;

    public PictureDialog(Context context) {
        super(context, R.style.picture_alert_dialog);
        this.context = context;
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        window.setWindowAnimations(R.style.DialogWindowStyle);
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picture_alert_dialog);
//        loadingTV = findViewById(R.id.update_progressbar);
//        loadingTV.setMax(100);
    }

    public void setLoadingProgress(int progress) {
        if (loadingTV.getVisibility() != View.VISIBLE) {
            loadingTV.setVisibility(View.VISIBLE);
        }
        if (loadingTV != null) {
            loadingTV.setProgress(progress);
        }
    }
}