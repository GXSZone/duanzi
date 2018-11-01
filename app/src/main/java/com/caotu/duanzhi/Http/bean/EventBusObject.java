package com.caotu.duanzhi.Http.bean;

/**
 * @author zhushijun QQ:775158747
 * @class <类描述>
 * @time 2018/7/29 23:34
 */
public class EventBusObject {

    private int code;
    private Object obj;
    private String msg;
    private String tag;//防止自己的事件被自己响应了

    public EventBusObject(int code, Object obj, String msg, String tag) {

        this.code = code;
        this.obj = obj;
        this.msg = msg;
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "EventBusObject{" +
                "code=" + code +
                ", obj=" + obj +
                ", msg='" + msg + '\'' +
                '}';
    }
}
