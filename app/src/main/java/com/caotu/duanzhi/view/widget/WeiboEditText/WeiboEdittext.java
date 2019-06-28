package com.caotu.duanzhi.view.widget.WeiboEditText;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.caotu.duanzhi.other.TextWatcherAdapter;
import com.ruffian.library.widget.REditText;

import java.util.ArrayList;
import java.util.List;

/**
 * 参考文章: link{https://blog.csdn.net/u014702653/article/details/52799715}
 */
public class WeiboEdittext extends REditText {
    // 默认,话题文本高亮颜色
    private static final int FOREGROUND_COLOR = Color.parseColor("#FF8787");
    // 默认,话题背景高亮颜色
    private static final int BACKGROUND_COLOR = Color.parseColor("#FF698F");

    /**
     * 开发者可设置内容
     */
    private int mForegroundColor = FOREGROUND_COLOR;// 话题文本高亮颜色
    private int mBackgroundColor = BACKGROUND_COLOR;// 话题背景高亮颜色
    private List<RObject> mRObjectsList = new ArrayList<RObject>();// object集合

    public WeiboEdittext(Context context) {
        this(context, null);
    }

    public WeiboEdittext(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 初始化设置
        initView();
    }

    /**
     * 监听光标的位置,若光标处于话题内容中间则移动光标到话题结束位置
     */
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
     * 初始化控件,一些监听
     */
    private void initView() {
        this.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("onFocusChange", "onFocusChange: " + hasFocus);
            }
        });
        /**
         * 输入框内容变化监听<br/>
         * 1.当文字内容产生变化的时候实时更新UI
         */
        addTextChangedListener(new TextWatcherAdapter(){
            @Override
            public void afterTextChanged(Editable s) {
                // 文字改变刷新UI
                refreshEditTextUI(s.toString());
            }
        });

        /**
         * 监听删除键 <br/>
         * 1.光标在话题后面,将整个话题内容删除 <br/>
         * 2.光标在普通文字后面,删除一个字符
         */
        this.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_DEL
                        && event.getAction() == KeyEvent.ACTION_DOWN) {

                    int selectionStart = getSelectionStart();
                    int selectionEnd = getSelectionEnd();

                    /**
                     * 如果光标起始和结束不在同一位置,删除文本
                     */
                    if (selectionStart != selectionEnd) {
                        // 查询文本是否属于目标对象,若是移除列表数据
                        String tagetText = getText().toString().substring(
                                selectionStart, selectionEnd);
                        for (int i = 0; i < mRObjectsList.size(); i++) {
                            RObject object = mRObjectsList.get(i);
                            if (tagetText.equals(object.getObjectText())) {
                                mRObjectsList.remove(object);
                            }
                        }
                        return false;
                    }

                    int lastPos = 0;
                    Editable editable = getText();
                    // 遍历判断光标的位置
                    for (int i = 0; i < mRObjectsList.size(); i++) {

                        String objectText = mRObjectsList.get(i).getObjectText();

                        lastPos = getText().toString().indexOf(objectText, lastPos);
                        if (lastPos != -1) {
                            if (selectionStart != 0
                                    && selectionStart >= lastPos
                                    && selectionStart <= (lastPos + objectText
                                    .length())) {
                                // 选中话题
                                setSelection(lastPos,
                                        lastPos + objectText.length());
                                // 设置背景色
                                editable.setSpan(new BackgroundColorSpan(
                                                mBackgroundColor), lastPos, lastPos
                                                + objectText.length(),
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                return true;
                            }
                        }
                        lastPos += objectText.length();
                    }
                }

                return false;
            }
        });

    }

    /**
     * EditText内容修改之后刷新UI
     *
     * @param content 输入框内容
     */
    private void refreshEditTextUI(String content) {

        /**
         * 内容变化时操作<br/>
         * 1.查找匹配所有话题内容 <br/>
         * 2.设置话题内容特殊颜色
         */

        if (mRObjectsList.size() == 0)
            return;

        if (TextUtils.isEmpty(content)) {
            mRObjectsList.clear();
            return;
        }

        /**
         * 重新设置span
         */
        Editable editable = getText();
        int findPosition = 0;
        for (int i = 0; i < mRObjectsList.size(); i++) {
            final RObject object = mRObjectsList.get(i);
            String objectText = object.getObjectText();// 文本
            findPosition = content.indexOf(objectText);// 获取文本开始下标

            if (findPosition != -1) {// 设置话题内容前景色高亮
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(
                        mForegroundColor);
                editable.setSpan(colorSpan, findPosition, findPosition
                                + objectText.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            }
        }

    }

    /**
     * 插入/设置话题
     *
     * @param object 话题对象
     */
    public void setObject(RObject object) {

        if (object == null)
            return;

        String objectRule = object.getObjectRule();
        String objectText = object.getObjectText();
        if (TextUtils.isEmpty(objectText) || TextUtils.isEmpty(objectRule))
            return;

        // 拼接字符# %s #,并保存
        objectText = objectRule + objectText;
        object.setObjectText(objectText);

        /**
         * 添加话题<br/>
         * 1.将话题内容添加到数据集合中<br/>
         * 2.将话题内容添加到EditText中展示
         */

        /**
         * 1.添加话题内容到数据集合
         */
        mRObjectsList.add(object);

        /**
         * 2.将话题内容添加到EditText中展示
         */
        int selectionStart = getSelectionStart();// 光标位置
        Editable editable = getText();// 原先内容

        if (selectionStart >= 0) {
            editable.insert(selectionStart, objectText);// 在光标位置插入内容
            editable.insert(getSelectionStart(), " ");// 话题后面插入空格,至关重要
            setSelection(getSelectionStart());// 移动光标到添加的内容后面
        }

    }

    /**
     * 获取object列表数据
     */
    public List<RObject> getObjects() {
        List<RObject> objectsList = new ArrayList<RObject>();
        // 由于保存时候文本内容添加了匹配字符#,此处去除,还原数据
        if (mRObjectsList != null && mRObjectsList.size() > 0) {
            for (int i = 0; i < mRObjectsList.size(); i++) {
                RObject object = mRObjectsList.get(i);
                String objectText = object.getObjectText();
                String objectRule = object.getObjectRule();
                object.setObjectText(objectText.replace(objectRule, ""));// 将匹配规则字符替换
                objectsList.add(object);
            }
        }
        return objectsList;
    }
}
