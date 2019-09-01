package com.caotu.duanzhi.utils;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;

import com.caotu.duanzhi.Http.bean.UserBean;
import com.caotu.duanzhi.config.BaseConfig;
import com.caotu.duanzhi.view.fixTextClick.SimpeClickSpan;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ParserUtils {
    static final String TAG = BaseConfig.TAG;
    private static final String regexHtml = "<(ct) type=[0-9] id=.*?>.+?</(ct)>";
    //    public static final String at_string = "@name 裘黎伟@name 123@name @name ";
    private static final String regexAT = "@[^\\s]+\\s?";// @开始,空格结尾
    public static final String string = "<ct type=1 id=123456>qzxd</ct>裘黎伟<ct type=1 id=123456>name</ct>123<ct type=1 id=123456>qlw</ct>";


    /**
     * 用于解析edittext 输入框的@ 转成接口形式
     *
     * @param string
     * @param list
     */
    public static String beanToHtml(String string, List<UserBean> list) {
        if (list == null || list.isEmpty()) return string;
        Pattern pattern = Pattern.compile(regexAT);
        Matcher match = pattern.matcher(string);
        int i = 0;
        int mend = 0;
        StringBuilder builder = new StringBuilder();
        while (match.find()) {
            String group = match.group();
//            Log.i(BaseConfig.TAG, Thread.currentThread().getName());
//            Log.i(BaseConfig.TAG, "validContent: " + match.groupCount());
//            Log.i(BaseConfig.TAG, "validContent: " + group);
            // TODO: 2019-08-30 找到之后还得去集合里匹配一下,没有匹配到也是不会传的
            boolean hasthisUser = false;
            for (UserBean userBean : list) {
                if (group.contains(userBean.username)) {
                    hasthisUser = true;
                }
            }
            // TODO: 2019-08-30  这里还有一种情况手动输入和选择输入 输入了一样的文本,这就越界了,需要处理一下
            if (!hasthisUser || i >= list.size()) {
                builder.append(group);
            } else {
                UserBean userBean = list.get(i);
                int start = match.start();
                int end = match.start() + group.length();
                String substring1;
                //@ 前后的字符串也得要,下次找到的头跟上次的尾做切割
                if (i == 0) {
                    substring1 = string.substring(0, start);
                } else {
                    substring1 = string.substring(mend, start);
                }
                mend = end;
                builder.append(substring1)
                        .append("<ct type=1 id=")
                        .append(userBean.userid)
                        .append(">")
                        .append(userBean.username)
                        .append("</ct>");
            }
            i++;
        }
        //如果是@name 结束的还好说,不是的话后面还得在拼上
        if (!string.endsWith(regexAT)) {
            builder.append(string.substring(mend));
        }
        Log.i(TAG, builder.toString());
        return builder.toString();
    }

    /**
     * 这里连点击事件都要处理好
     * https://blog.csdn.net/pengpeng235/article/details/84011780
     *
     * @param content
     */
    public static SpannableString htmlToSpanText(String content) {
        StringBuffer buffer = new StringBuffer();
        SpannableString spannable = new SpannableString(buffer);

        Pattern pattern = Pattern.compile(regexHtml);
        Matcher match = pattern.matcher(content);

        while (match.find()) {
            String target = match.group();
            String id = target.substring(target.indexOf("id=") + 3, target.indexOf(">"));
            String type = target.substring(target.indexOf("type=") + 5, target.indexOf("id="));
            String name = target.substring(target.indexOf(">") + 1, target.indexOf("</ct>"));
            match.appendReplacement(buffer, "@" + name + " ");
            int start = match.start();
            spannable.setSpan(new SimpeClickSpan() {
                @Override
                public void onSpanClick(View widget) {
                    ToastUtil.showShort("id :" + id + "   name:: " + name);
                }
            }, start, start + name.length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        match.appendTail(buffer);
        Log.i(TAG, ">>>> sb :    " + buffer.toString());
        return spannable;
    }

    /**
     * 这个思路就是先用系统urlSpan找到有几个标签,再截取出来关键信息,重新设置另外的Span,可以使clickspan也可以是
     * 自定义的myurlSpan
     * 参考: https://github.com/zhe525069676/WeiBoLayout
     *
     * @param txt
     */
    public static void convertNormalStringToSpannableString(String txt) {
        //hack to fix android imagespan bug,see http://stackoverflow.com/questions/3253148/imagespan-is-cut-off-incorrectly-aligned
        //if string only contains emotion tags,add a empty char to the end

        Pattern MENTION_URL = Pattern.compile(regexHtml);
        SpannableString value = SpannableString.valueOf(txt);

        Linkify.addLinks(value, MENTION_URL, "");

        URLSpan[] urlSpans = value.getSpans(0, value.length(), URLSpan.class);


        for (URLSpan urlSpan : urlSpans) {
            String url = urlSpan.getURL();
            Log.i(TAG, "convertNormalStringToSpannableString: " + url);
            //com.zheblog.weibo.at://<ct type=1 id=123456>name</ct>   log输出,前面那串是自己写的scheme

//            if (urlSpan.getURL().startsWith(WeiboPatterns.TOPIC_SCHEME)) {
//                String topic = urlSpan.getURL().substring(WeiboPatterns.TOPIC_SCHEME.length(), urlSpan.getURL().length());
//                //不识别空格话题和大于30字话题
//                String group = topic.substring(1, topic.length() - 1).trim();
//                if (1 > group.length() || group.length() > 30) {
//                    value.removeSpan(urlSpan);
//                    continue;
//                }
//            }
//            weiboSpan = new MyURLSpan(urlSpan.getURL(), mColor);
//            int start = value.getSpanStart(urlSpan);
//            int end = value.getSpanEnd(urlSpan);
//            value.removeSpan(urlSpan);
//            value.setSpan(weiboSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


    }

}
