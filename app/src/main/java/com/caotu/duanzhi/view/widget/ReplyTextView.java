package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;


import com.ruffian.library.widget.RTextView;

public class ReplyTextView extends RTextView {

    public ReplyTextView(Context context) {
        super(context);
    }

    public ReplyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 处理删除事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 100;
            if (rect.contains(eventX, eventY)) {
                showReplyDialog(true);
            } else {
                showReplyDialog(false);
            }

        }
        return super.onTouchEvent(event);
    }

    BottomTextClick listener;

    public void setListener(BottomTextClick listener) {
        this.listener = listener;
    }

    public interface BottomTextClick {
        void showPop(boolean isShowListStr);
    }

    private void showReplyDialog(boolean b) {
        if (listener != null) {
            listener.showPop(b);
        }
    }
}
