package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.TextWatcherAdapter;
import com.ruffian.library.widget.REditText;

public class EditTextWithDel extends REditText {

    private Drawable imgInable;

    public EditTextWithDel(Context context) {
        super(context);
        init();
    }

    public EditTextWithDel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    private void init() {
        imgInable = getContext().getResources().getDrawable(R.mipmap.close_icon);
        addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });
        setDrawable();
    }

    /**
     * 为了少走设置图片的逻辑
     */
    boolean isDelShow = false;

    // 设置删除图片
    private void setDrawable() {
        if (length() < 1 && isDelShow) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            isDelShow = false;
        } else if (length() >= 1 && !isDelShow) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, imgInable, null);
            isDelShow = true;
        }
    }

    // 处理删除事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (imgInable != null && event.getAction() == MotionEvent.ACTION_UP) {
            int eventX = (int) event.getRawX();
            int eventY = (int) event.getRawY();
            Rect rect = new Rect();
            getGlobalVisibleRect(rect);
            rect.left = rect.right - 100;
            if (rect.contains(eventX, eventY)){
                getText().clear();
//                setText("");
            }

        }
        return super.onTouchEvent(event);
    }
}
