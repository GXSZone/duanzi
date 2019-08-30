package com.caotu.duanzhi.view.widget.WeiboEditText;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class RObject {
    @LinkType
    private int mType;
    private String objectRule = "@";// 匹配规则
    private String objectText;// 高亮文本
    private String mId;

    public RObject(String objectText, String id, @LinkType int type) {
        this.objectText = objectRule + objectText+" ";
        mId = id;
        mType = type;
    }

    public String getObjectRule() {
        return objectRule;
    }

    public int getmType() {
        return mType;
    }

    public String getmId() {
        return mId;
    }

    public void setObjectRule(String objectRule) {
        this.objectRule = objectRule;
    }

    public String getObjectText() {
        return objectText;
    }

    public void setObjectText(String objectText) {
        this.objectText = objectText;
    }

    /**
     * 为了以后多定义更多的东西
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface LinkType {
        int at_user = 1;
        int emoji = 2;
        int other = 3;
    }
}
