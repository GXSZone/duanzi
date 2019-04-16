package com.caotu.duanzhi.view.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.text.Selection;
import android.text.Spannable;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * #25403 java.lang.IndexOutOfBoundsException
 * setSpan (-1 ... -1) starts before 0
 * android.text.SpannableStringInternal.checkRange(SpannableStringInternal.java:434)
 * link {https://blog.csdn.net/u010232308/article/details/85048016}
 * 由于在recyclerview的头布局里的文本可以长按复制
 */
public class HackyTextView extends AppCompatTextView {
    public HackyTextView(Context context) {
        super(context);
    }

    public HackyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HackyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent event) {
        // FIXME simple workaround to https://code.google.com/p/android/issues/detail?id=191430
        int startSelection = getSelectionStart();
        int endSelection = getSelectionEnd();
        if (startSelection < 0 || endSelection < 0) {
            Selection.setSelection((Spannable) getText(), getText().length());
        } else if (startSelection != endSelection) {
            if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                final CharSequence text = getText();
                setText(null);
                setText(text);
            }
        }
        return super.dispatchTouchEvent(event);
    }

}
