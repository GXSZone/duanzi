package com.caotu.duanzhi.view.widget.WeiboEditText;

import android.content.Context;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.utils.DevicesUtils;
import com.caotu.duanzhi.utils.HelperForStartActivity;
import com.caotu.duanzhi.view.widget.EditTextLib.AtTextWatcher;
import com.caotu.duanzhi.view.widget.EditTextLib.DefaultKeyEventProxy;
import com.caotu.duanzhi.view.widget.EditTextLib.KeyEventProxy;
import com.ruffian.library.widget.REditText;

import java.util.ArrayList;
import java.util.List;

/**
 * 参考文章 link{https://www.jianshu.com/p/96f9136b76df}
 */
public class CopyWeChatEditText extends REditText {
    public CopyWeChatEditText(Context context) {
        super(context);
        init();
    }

    public CopyWeChatEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private KeyEventProxy mKeyEventProxy = new DefaultKeyEventProxy();

    private boolean handleKeyEvent(KeyEvent event) {
        return mKeyEventProxy != null && mKeyEventProxy.onKeyEvent(event, getText());
    }

    private void init() {

//        setEditableFactory(new NoCopySpanEditableFactory(new DirtySpanWatcher()));  //这个是为了不能复制粘贴
        setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
//                    return KeyDownHelper(getText());
//                }
//                return false;
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

    /**
     * 添加 @内容
     *
     * @param extra
     */
    public void addSpan(UserBean extra) {
        Editable editable = getText();
        String s = editable.toString();
        //如果是自动触发的@ 功能则需要删除已经在输入框里的@ 再整体插入
        if (!TextUtils.isEmpty(s)) {
            String substring = s.substring(s.length() - 1, length());
            if (TextUtils.equals(substring, "@")) {
                editable.delete(s.length() - 1, length());
            }
        }
        RObject rObject = new RObject(extra.username, extra.userid, RObject.LinkType.at_user);
        mRObjectsList.add(rObject);
        editable.insert(getSelectionEnd(), rObject.getObjectText());
        DataSpan myTextSpan = new DataSpan(DevicesUtils.getColor(R.color.color_FF698F));
        myTextSpan.bindDate(extra);
        editable.setSpan(myTextSpan, getSelectionEnd() - rObject.getObjectText().length(), getSelectionEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        setSelection(editable.length());
    }

    /**
     * 找到最后 Span 块
     *
     * @param text
     * @return
     */
    public boolean KeyDownHelper(Editable text) {
        int selectionEnd = Selection.getSelectionEnd(text);
        int selectionStart = Selection.getSelectionStart(text);
        DataSpan[] spans = text.getSpans(selectionStart, selectionEnd, DataSpan.class);
//        //如果光标起始和结束不在同一位置,删除
//        try {
//            if (selectionStart != selectionEnd) {
//                // 查询文本是否属于目标对象,若是移除列表数据
//                String tagetText = getText().toString().substring(
//                        selectionStart, selectionEnd);
//                for (int i = 0; i < mRObjectsList.size(); i++) {
//                    RObject object = mRObjectsList.get(i);
//                    if (tagetText.equals(object.getObjectText())) {
//                        mRObjectsList.remove(object);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        for (DataSpan span : spans) {
            if (span != null) {
                int spanStart = text.getSpanStart(span);
                int spanEnd = text.getSpanEnd(span);
                if (selectionEnd == spanEnd) {
                    Selection.setSelection(text, spanStart, spanEnd);
                    return false;
                }
            }
        }
        return false;
    }


    private List<RObject> mRObjectsList = new ArrayList<RObject>();// object集合

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (mRObjectsList == null || mRObjectsList.size() == 0) {
            return;
        }

        int startPosition = 0;
        int endPosition = 0;
        String objectText = "";
        for (int i = 0; i < mRObjectsList.size(); i++) {
            objectText = mRObjectsList.get(i).getObjectText();// 文本
            startPosition = getText().toString().indexOf(objectText);// 获取文本开始下标
            endPosition = startPosition + objectText.length();
            if (startPosition != -1 && selStart > startPosition
                    && selStart <= endPosition) {// 若光标处于话题内容中间则移动光标到话题结束位置
                setSelection(endPosition);
            }
        }

    }

    /**
     * 获取输入框里的@ 集合
     *
     * @return
     */
    public List<UserBean> getAtListBean() {
        ArrayList<UserBean> userBeans = new ArrayList<>();
        Editable text = getText();
        DataSpan[] spans = text.getSpans(0, text.length(), DataSpan.class);
        if (spans != null && spans.length > 0) {
            for (int i = 0; i < spans.length; i++) {
                userBeans.add(spans[i].getDate());
            }
        }
        return userBeans;
    }
//    class NoCopySpanEditableFactory extends Editable.Factory {
//
//        private NoCopySpan spans;
//
//        public NoCopySpanEditableFactory(NoCopySpan spans) {
//            this.spans = spans;
//        }
//
//        @Override
//        public Editable newEditable(CharSequence source) {
//            SpannableStringBuilder stringBuilder = new SpannableStringBuilder(source);
//            stringBuilder.setSpan(spans, 0, source.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
//            return stringBuilder;
//        }
//    }
//
//    class DirtySpanWatcher implements SpanWatcher {
//
//        @Override
//        public void onSpanAdded(Spannable text, Object what, int start, int end) {
//
//        }
//
//        @Override
//        public void onSpanRemoved(Spannable text, Object what, int start, int end) {
//
//        }
//
//        @Override
//        public void onSpanChanged(Spannable text, Object what, int ostart, int oend, int nstart, int nend) {
////            int spanEnd = text.getSpanEnd(what);
////            int spanStart = text.getSpanStart(what);
////            if (spanStart >= 0 && spanEnd >= 0 && what instanceof DataSpan) {
////                CharSequence charSequence = text.subSequence(spanStart, spanEnd);
////                if (!charSequence.toString().contains("@")) {
////                    DataSpan[] spans = text.getSpans(spanStart, spanEnd, DataSpan.class);
////                    for (DataSpan span : spans) {
////                        if (span != null) {
////                            text.removeSpan(span);
////                            break;
////                        }
////                    }
////                }
////            }
//        }
//    }
}
