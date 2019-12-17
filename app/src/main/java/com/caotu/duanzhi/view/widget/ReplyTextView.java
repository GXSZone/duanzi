package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.caotu.duanzhi.Http.CommonHttpRequest;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.utils.AppUtil;
import com.ruffian.library.widget.RTextView;

public class ReplyTextView extends RTextView {

    private boolean hasDate;

    public ReplyTextView(Context context) {
        super(context);
        init();
    }

    public ReplyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    int rightTouchSize;

    private void init() {
        hasDate = AppUtil.listHasDate(CommonHttpRequest.hotComments);
        if (hasDate) {
            Drawable right = getContext().getResources().getDrawable(R.drawable.ao_button);
            rightTouchSize = right.getIntrinsicWidth();
            setCompoundDrawablesWithIntrinsicBounds(null, null, right, null);
        }
    }

    // 处理删除事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!hasDate) {
                showReplyDialog(false);
            } else {
                int eventX = (int) event.getRawX();
                int eventY = (int) event.getRawY();
                Rect rect = new Rect();
                getGlobalVisibleRect(rect);
                rect.left = rect.right - rightTouchSize - getPaddingRight();
                if (rect.contains(eventX, eventY)) {
                    UmengHelper.event("rcan");
                    showReplyDialog(true);
                } else {
                    showReplyDialog(false);
                }
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
