package com.caotu.duanzhi.view.fixTextClick;

import android.view.View;

public interface ITouchableSpan {
    void setPressed(boolean pressed);
    void onClick(View widget);
}
