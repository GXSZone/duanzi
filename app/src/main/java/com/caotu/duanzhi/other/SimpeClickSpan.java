package com.caotu.duanzhi.other;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

public abstract class SimpeClickSpan extends ClickableSpan {

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
        ds.setColor(Color.parseColor("#FF698F"));
        //超链接形式的下划线，false 表示不显示下划线，true表示显示下划线
        ds.setUnderlineText(false);
    }
}
