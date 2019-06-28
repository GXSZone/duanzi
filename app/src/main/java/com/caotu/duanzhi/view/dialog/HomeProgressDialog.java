package com.caotu.duanzhi.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.GlideUtils;
import com.caotu.duanzhi.utils.MySpUtils;
import com.ruffian.library.widget.RImageView;


public class HomeProgressDialog extends Dialog {


    private ProgressBar progressBar;

    public HomeProgressDialog(@NonNull Context context) {
        super(context, R.style.top_animation_dialog_style);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.layout_home_progress);
        RImageView userImage = findViewById(R.id.user_image);
        progressBar = findViewById(R.id.publish_progress);
        GlideUtils.loadImage(MySpUtils.getString(MySpUtils.SP_MY_AVATAR), userImage);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        Window window = getWindow();
        if (window != null) {
            //不阻挡activity的事件
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.TOP;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
        }
    }

    public void changeProgress(int progress) {
        if (progress > 100 || progress < 0) return;
        if (progressBar != null) {
            progressBar.setProgress(progress);
        }
    }
}
