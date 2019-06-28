package com.caotu.duanzhi.view.fixTextClick;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;

public abstract class SimpeClickSpan extends ClickableSpan {
    int tagNormalColor = DevicesUtils.getColor(R.color.color_FF698F);


    public abstract void onSpanClick(View widget);

    @Override
    public final void onClick(@NonNull View widget) {
        if (ViewCompat.isAttachedToWindow(widget)) {
            onSpanClick(widget);
        }
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        //设置文本的颜色
        ds.setColor(tagNormalColor);
        //超链接形式的下划线，false 表示不显示下划线，true表示显示下划线
        ds.setUnderlineText(false);
    }
}
