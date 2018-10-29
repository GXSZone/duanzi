package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.caotu.duanzhi.R;

public class MainBottomLayout extends FrameLayout {
    public MainBottomLayout(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public MainBottomLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        View rootView = inflate(context, R.layout.main_bottom_layout, null);


    }
}
