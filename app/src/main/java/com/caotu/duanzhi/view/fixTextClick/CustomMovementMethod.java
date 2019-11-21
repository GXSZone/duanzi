package com.caotu.duanzhi.view.fixTextClick;

import android.text.Layout;
import android.text.Spannable;
import android.text.method.BaseMovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import com.caotu.duanzhi.module.detail.DetailActivity;
import com.caotu.duanzhi.module.detail_scroll.ContentNewDetailActivity;

/**
 * 这个是为了解决 LinkMovementMethod 在列表会有textview滑动导致有时候出现边缘被切割的情况,
 * 是因为textview 设置这个属性可以滑动
 * 这样内部处理可以少写textview的点击和长按事件,设置clickspan后再设置click和longclick也是可以的
 */
public class CustomMovementMethod extends BaseMovementMethod {
    private static CustomMovementMethod customMovementMethod;

    public static CustomMovementMethod getInstance() {
        if (customMovementMethod == null) {
            customMovementMethod = new CustomMovementMethod();
        }
        return customMovementMethod;
    }

    long downTime;

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
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
                if (action == MotionEvent.ACTION_UP) {
                    //除了点击事件，我们不要其他东西
                    link[0].onClick(widget);
                }
//                return true;
            } else if (action == MotionEvent.ACTION_UP) {
                //这么处理可以少写事件监听
                if (widget.getContext() instanceof DetailActivity || widget.getContext() instanceof ContentNewDetailActivity) {
                    if (widget.getParent() instanceof ViewGroup)
                        ((ViewGroup) widget.getParent()).performClick();
                } else {
                    widget.performClick();
                }
            }
        }
        //自认为是属于比较优雅的隔离textview 文本事件处理了
        if (action == MotionEvent.ACTION_DOWN) {
            downTime = System.currentTimeMillis();
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            long l = System.currentTimeMillis() - downTime;
            //这个长按的时间是系统的,不同手机不一样
            if (l > ViewConfiguration.getLongPressTimeout()) {
                if (widget.getParent() instanceof ViewGroup) {
                    ((ViewGroup) widget.getParent()).performLongClick();
                }
            }
        }
        return true;
    }

    private CustomMovementMethod() {

    }
}
