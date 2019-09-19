package com.caotu.duanzhi.view.widget.EditTextLib;

import android.content.Context;
import android.text.Editable;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import androidx.appcompat.widget.AppCompatEditText;

import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.utils.HelperForStartActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunhapper on 2019/1/25 .
 * <p>
 * https://www.jianshu.com/p/96f9136b76df
 * https://blog.csdn.net/u014702653/article/details/52799715
 */
public class SpXEditText extends AppCompatEditText {
    private KeyEventProxy mKeyEventProxy = new DefaultKeyEventProxy();

    public SpXEditText(Context context) {
        super(context);
    }

    public SpXEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpXEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        List<NoCopySpan> watchers = new ArrayList<>();
        watchers.add(new SpanChangedWatcher());

        setEditableFactory(new SpXEditableFactory(watchers));
        setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return handleKeyEvent(event);
            }
        });
        addTextChangedListener(new AtTextWatcher() {
            @Override
            public void ByDealAt() {
                HelperForStartActivity.openSearch();
            }
        });
    }

    private boolean handleKeyEvent(KeyEvent event) {
        return mKeyEventProxy != null && mKeyEventProxy.onKeyEvent(event, getText());
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new CustomInputConnectionWrapper(super.onCreateInputConnection(outAttrs), true);
    }

    /**
     * 插入字符
     *
     * @param extra
     */
    public void addSpan(UserBean extra) {

        Spannable spanableString = new MentionUser(extra).getSpanableString();
        Editable editable = getText();
        if (editable == null) return;
        //如果是自动触发的@ 功能则需要删除已经在输入框里的@ 再整体插入
        String str = editable.toString();
        if (!TextUtils.isEmpty(str)) {
            String substring = str.substring(str.length() - 1, length());
            if (TextUtils.equals(substring, "@")) {
                editable.delete(str.length() - 1, length());
            }
        }
        int start = Selection.getSelectionStart(editable);
        int end = Selection.getSelectionEnd(editable);
        if (end < start) {
            int temp = start;
            start = end;
            end = temp;
        }
        editable.replace(start, end, spanableString);
        //插入后重新获取焦点设置光标
        postDelayed(new Runnable() {
            @Override
            public void run() {
                requestFocus();
            }
        }, 500);

    }

    public List<UserBean> getAtListBean() {
        Editable text = getText();
        ArrayList<UserBean> userBeans = new ArrayList<>();
        if (text != null) {
            MentionUser[] spans = text.getSpans(0, text.length(), MentionUser.class);
            if (spans != null && spans.length > 0) {
                for (int i = 0; i < spans.length; i++) {
                    userBeans.add(spans[i].bean);
                }
            }
        }
        return userBeans;
    }


    /**
     * 解决google输入法删除不走OnKeyListener()回调问题
     */
    private class CustomInputConnectionWrapper extends InputConnectionWrapper {


        /**
         * Initializes a wrapper.
         *
         * <p><b>Caveat:</b> Although the system can accept {@code (InputConnection) null} in some
         * places, you cannot emulate such a behavior by non-null {@link InputConnectionWrapper} that
         * has {@code null} in {@code target}.</p>
         *
         * @param target  the {@link InputConnection} to be proxied.
         * @param mutable set {@code true} to protect this object from being reconfigured to target
         *                another {@link InputConnection}.  Note that this is ignored while the target is {@code
         *                null}.
         */
        public CustomInputConnectionWrapper(InputConnection target, boolean mutable) {
            super(target, mutable);
        }


        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            if (beforeLength == 1 && afterLength == 0) {
                return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
                        KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
                        KeyEvent.KEYCODE_DEL));
            }
            return super.deleteSurroundingText(beforeLength, afterLength);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            return (handleKeyEvent(event))
                    || super.sendKeyEvent(event);
        }
    }

//    public void setKeyEventProxy(KeyEventProxy keyEventProxy) {
//        mKeyEventProxy = keyEventProxy;
//    }

}
