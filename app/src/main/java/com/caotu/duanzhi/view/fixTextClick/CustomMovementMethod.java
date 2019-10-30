package com.caotu.duanzhi.view.fixTextClick;

import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.BaseMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

/**
 * 这个是为了解决 LinkMovementMethod 在列表会有textview滑动导致有时候出现边缘被切割的情况,
 * 是因为textview 设置这个属性可以滑动
 * 这样内部处理可以少写textview的点击和长按事件,设置clickspan后再设置click和longclick也是可以的
 */
public class CustomMovementMethod extends BaseMovementMethod {
    private long lastClickTime;

    private static final long CLICK_DELAY = 500l;

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                //  ============================修改的部分==============================
                if (action == MotionEvent.ACTION_UP) {
                    if (System.currentTimeMillis() - lastClickTime < CLICK_DELAY) {
                        link[0].onClick(widget);
                    } else if (System.currentTimeMillis() - lastClickTime > CLICK_DELAY) {
                        ViewParent parent = widget.getParent();//处理widget的父控件点击事件
                        if (parent instanceof ViewGroup) {
                            ((ViewGroup) parent).performLongClick();
                        }
                    }
                } else {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(link[0]),
                            buffer.getSpanEnd(link[0]));
                    lastClickTime = System.currentTimeMillis();
                }
                // ============================修改的部分==============================
                return true;
            } else {
                //内部自己处理点击事件和长按事件给父控件处理
                if (action == MotionEvent.ACTION_DOWN) {
                    lastClickTime = System.currentTimeMillis();
                } else {
                    if (System.currentTimeMillis() - lastClickTime < CLICK_DELAY) {
                        ViewParent parent = widget.getParent();//处理widget的父控件点击事件
                        if (parent instanceof ViewGroup) {
                            ((ViewGroup) parent).performClick();
                        }
                    } else if (System.currentTimeMillis() - lastClickTime > CLICK_DELAY) {
                        ViewParent parent = widget.getParent();//处理widget的父控件点击事件
                        if (parent instanceof ViewGroup) {
                            ((ViewGroup) parent).performLongClick();
                        }
                    }
                }
                Selection.removeSelection(buffer);
            }
        }
        return super.onTouchEvent(widget, buffer, event);
    }

    public static CustomMovementMethod getInstance() {
        if (null == sInstance) {
            sInstance = new CustomMovementMethod();
        }
        return sInstance;
    }

    private static CustomMovementMethod sInstance;
}
