package com.caotu.duanzhi.view.widget.EditTextLib;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;

/**
 * Created by sunhapper on 2019/1/30 .
 * 使用三星输入法IntegratedSpan完整性不能保证，所以加上BreakableSpan使得@mention完整性被破坏时删除对应span
 */
public class MentionUser implements IntegratedSpan, BreakableSpan, DataSpan {

    private Object styleSpan;
    int color = DevicesUtils.getColor(R.color.color_FF698F);
    UserBean bean;

    public MentionUser(UserBean userBean) {
        bean = userBean;
    }

    public Spannable getSpanableString() {
        styleSpan = new ForegroundColorSpan(color);
        SpannableString spannableString = new SpannableString(getDisplayText());
        spannableString.setSpan(styleSpan, 0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(this, 0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private CharSequence getDisplayText() {
        if (bean != null) {
            return "@" + bean.username + " ";
        }
        return "@ ";
    }


    @Override
    public boolean isBreak(Spannable text) {
        int spanStart = text.getSpanStart(this);
        int spanEnd = text.getSpanEnd(this);
        boolean isBreak = spanStart >= 0 && spanEnd >= 0 && !text.subSequence(spanStart, spanEnd).toString().equals(
                getDisplayText());
        if (isBreak && styleSpan != null) {
            text.removeSpan(styleSpan);
            styleSpan = null;
        }
        return isBreak;
    }
}
