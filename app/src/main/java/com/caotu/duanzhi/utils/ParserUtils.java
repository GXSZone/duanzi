package com.caotu.duanzhi.utils;

import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import androidx.annotation.ColorInt;

import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.R;
import com.caotu.duanzhi.other.UmengHelper;
import com.caotu.duanzhi.other.UmengStatisticsKeyIds;
import com.caotu.duanzhi.view.fixTextClick.SimpeClickSpan;
import com.caotu.duanzhi.view.widget.EditTextLib.SpXEditText;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ParserUtils {
    private static final String regexHtml = "<(ct) type=[0-9] id=.*?>.+?</(ct)>";
    //    public static final String at_string = "@name 裘黎伟@name 123@name @name ";
//    private static final String regexAT = "@[^\\s]+\\s?";// @开始,空格结尾
    //    public static final String string = "<ct type=1 id=1111>###</ct>裘黎伟<ct type=1 id=2222>name</ct>中间字段<ct type=1 id=3333>qlw</ct>";
    public static final String string = "裘黎伟<ct type=1 id=1111>###</ct>123456<ct type=1 id=1111>㐇㐋</ct>qlw";


    /**
     * https://blog.csdn.net/pengpeng235/article/details/84011780
     * 该处理方式没有点击事件,只是文本转换,用于用户不需要标亮的地方
     *
     * @param content
     */
    public static String htmlToJustAtText(String content) {
        if (TextUtils.isEmpty(content)) return content;
        StringBuffer buffer = new StringBuffer();
        Pattern pattern = Pattern.compile(regexHtml);
        Matcher match = pattern.matcher(content);
        boolean hasHtml = false;
        while (match.find()) {
            hasHtml = true;
            String target = match.group();
            String name = target.substring(target.indexOf(">") + 1, target.indexOf("</ct>"));
            match.appendReplacement(buffer, "@".concat(name).concat(" "));
        }
        if (!hasHtml) {
            return content;
        }
        match.appendTail(buffer);
//        Log.i(TAG, "htmlToJustAtText: " + buffer.toString());
        return buffer.toString();
    }

    /**
     * 从原文本获取数据另外还得设置点击事件
     * 用于在列表和详情等地方展示用,还有这次改版的消息里用到,只是没有@
     *
     * @param content
     * @return
     */
    public static SpannableStringBuilder htmlToSpanText(String content, boolean hasAt) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (TextUtils.isEmpty(content)) return builder;
        Pattern pattern = Pattern.compile(regexHtml);
        Matcher match = pattern.matcher(content);
        int mend = 0;
        int i = 0;
        boolean hasAtUSer = false;
        while (match.find()) {
            hasAtUSer = true;
            String target = match.group();
            String id = target.substring(target.indexOf("id=") + 3, target.indexOf(">"));
//            String type = target.substring(target.indexOf("type=") + 5, target.indexOf("id="));
            String name = target.substring(target.indexOf(">") + 1, target.indexOf("</ct>"));
            int start = match.start();
            int end = match.end();
            String substring1;
            //@ 前后的字符串也得要,下次找到的头跟上次的尾做切割
            if (i == 0) {
                substring1 = content.substring(0, start);
            } else {
                substring1 = content.substring(mend, start);
            }
            mend = end;
            builder.append(substring1);
            // TODO: 2019-09-03 如果需要翻译成: @name
            if (hasAt) {
                builder.append("@".concat(name).concat(" "), new SimpeClickSpan() {
                    @Override
                    public void onSpanClick(View widget) {
                        UmengHelper.event(UmengStatisticsKeyIds.at_personal);
                        HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, id);
                    }
                }, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                builder.append(name, new SimpeClickSpan() {
                    @Override
                    public void onSpanClick(View widget) {
                        UmengHelper.event(UmengStatisticsKeyIds.at_personal);
                        HelperForStartActivity.openOther(HelperForStartActivity.type_other_user, id);
//                        ToastUtil.showShort("id :" + id + "   name: " + name);
                    }
                }, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            i++;
        }
        //没有匹配到直接返回原字段即可
        if (!hasAtUSer) {
            return new SpannableStringBuilder(content);
        }
        if (!content.endsWith("</ct>")) {
            builder.append(content.substring(mend));
        } else {
            //这个是为了更好的处理文本的点击事件,如果光是已@ 结尾后面的文本后面的点击事件都是它的
            builder.append(" ");
        }
        return builder;
    }

    /**
     * 这个是为了解析发布页的保存文本的解析
     *
     * @param content
     * @param editText
     */
    public static void htmlBindEditText(String content, SpXEditText editText) {
        if (TextUtils.isEmpty(content)) return;
        Pattern pattern = Pattern.compile(regexHtml);
        Editable editable = editText.getText();
        Matcher match = pattern.matcher(content);
        int mend = 0;
        int i = 0;
        boolean hasAtUSer = false;
        while (match.find()) {
            hasAtUSer = true;
            String target = match.group();
            String id = target.substring(target.indexOf("id=") + 3, target.indexOf(">"));
//            String type = target.substring(target.indexOf("type=") + 5, target.indexOf("id="));
            String name = target.substring(target.indexOf(">") + 1, target.indexOf("</ct>"));
            int start = match.start();
            int end = match.end();
            String substring1;
            //@ 前后的字符串也得要,下次找到的头跟上次的尾做切割
            if (i == 0) {
                substring1 = content.substring(0, start);
            } else {
                substring1 = content.substring(mend, start);
            }
            mend = end;
            int length = editable.length();
            editable.insert(length, substring1);
            UserBean bean = new UserBean();
            bean.username = name;
            bean.userid = id;
            editText.addSpan(bean);
            i++;
        }
        //没有匹配到直接返回原字段即可
        if (!hasAtUSer) {
            editText.setText(content);
            return;
        }
        if (!content.endsWith("</ct>")) {
            int length = editable.length();
            editable.insert(length, content.substring(mend));
        }
    }

    /**
     * 这个思路就是先用系统urlSpan找到有几个标签,再截取出来关键信息,重新设置另外的Span,可以使clickspan也可以是
     * 自定义的myurlSpan
     * 参考: https://github.com/zhe525069676/WeiBoLayout
     * 这个试用于不需要替换文本只是匹配就好的操作
     *
     * @param txt
     */
    public static String convertHtml(String txt, List<UserBean> beanList) {
        if (beanList == null || beanList.isEmpty() || TextUtils.isEmpty(txt)) return txt;
        Collections.sort(beanList, (o1, o2) -> o1.startIndex - o2.startIndex);
        StringBuilder builder = new StringBuilder();
        int size = beanList.size();
        for (int i = 0; i < size; i++) {
            UserBean bean = beanList.get(i);
            int startIndex = bean.startIndex;
            int endIndex = bean.endIndex;
            if (i == 0) {
                builder.append(txt.substring(0, startIndex));
            } else {
                UserBean bean1 = beanList.get(i - 1);
                builder.append(txt.substring(bean1.endIndex, startIndex));
            }
            builder.append("<ct type=1 id=")
                    .append(bean.userid)
                    .append(">")
                    .append(bean.username)
                    .append("</ct>");
            if (i == size - 1 && endIndex < txt.length()) {
                builder.append(txt.substring(endIndex));
            }
        }
        return builder.toString();
    }

    /**
     * 搜索标红设置
     *
     * @param oldString
     * @param markupText
     * @param color
     * @return
     */
    public static SpannableString setMarkupText(String oldString, String markupText, @ColorInt int color) {
        if (TextUtils.isEmpty(oldString)) return new SpannableString("");
        SpannableString spannableString = new SpannableString(oldString);
        if (TextUtils.isEmpty(oldString) || TextUtils.isEmpty(markupText)) return spannableString;
        boolean contains = oldString.contains(markupText);
        if (contains) {
            int start = 0;
            while (start < oldString.length()) {
                start = oldString.indexOf(markupText, start);
                if (start < 0) {
                    start = oldString.length();
                    continue;
                }
                int end = start + markupText.length();
                if (end > oldString.length()) {
                    start = oldString.length();
                    continue;
                }
                spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = end;
            }
        }
        return spannableString;
    }

    public static SpannableStringBuilder setMarkContentText(SpannableStringBuilder oldString, String markupText) {
        if (TextUtils.isEmpty(oldString) || TextUtils.isEmpty(markupText)) return null;
        String toString = oldString.toString();
        int color = DevicesUtils.getColor(R.color.color_FF698F);
        boolean contains = toString.contains(markupText);
        if (contains) {
            int start = 0;
            while (start < toString.length()) {
                start = toString.indexOf(markupText, start);
                if (start < 0) {
                    start = toString.length();
                    continue;
                }
                int end = start + markupText.length();
                if (end > toString.length()) {
                    start = toString.length();
                    continue;
                }

                oldString.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = end;
            }
        }
        return oldString;
    }
}
